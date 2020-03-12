package com.example.narendra.eatfoodserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.narendra.eatfoodserver.Common.Common;
import com.example.narendra.eatfoodserver.Interface.ItemClickListener;
import com.example.narendra.eatfoodserver.Model.Foods;
import com.example.narendra.eatfoodserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class FoodList extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RelativeLayout rootLayout;
    FloatingActionButton fab;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId="";
    FirebaseRecyclerAdapter<Foods,FoodViewHolder> adapter;
    MaterialEditText edtFoodName,edtDescription,edtPrice,edtDiscount;
    Button btnSelect,btnupload;
    Foods newFood;
    Uri saveUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        db=FirebaseDatabase.getInstance();
        foodList=db.getReference("Foods");
        //Firebase Storage
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        //Init
        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        rootLayout=(RelativeLayout) findViewById(R.id.rootLayout);
        fab=(FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
        @Override
       public void onClick(View v) {
            showAddFoodDialog();

    }
});
        if(getIntent()!=null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId != null){
            loadListFood(categoryId);
        }


    }
    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    private void loadFood(String category_id) {
        Query query = foodList.orderByChild("category_id").equalTo(category_id);

        FirebaseRecyclerOptions<Foods> options = new FirebaseRecyclerOptions.Builder<Foods>()
                .setQuery(query, Foods.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Foods, FoodViewHolder>(options) {
            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Foods model) {
                holder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.food_image);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }



    private void showAddFoodDialog() {
        AlertDialog.Builder alertdialog=new AlertDialog.Builder(FoodList.this);
        alertdialog.setTitle("Add new Food");
        alertdialog.setMessage("Please fill full Information");
        //Layout inflater for alertdialog
        LayoutInflater inflater=this.getLayoutInflater();
        //add_menu_layout is reference for inflator for View class
        View add_menu_layout=inflater.inflate(R.layout.add_new_food,null);

        edtFoodName=add_menu_layout.findViewById(R.id.edtName);
        edtDescription=add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice=add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount=add_menu_layout.findViewById(R.id.edtDiscount);

        btnSelect=add_menu_layout.findViewById(R.id.btnSelects);
        btnupload=add_menu_layout.findViewById(R.id.btnUploads);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();

            }
        });
        //set alert alertDialof for view
        alertdialog.setView(add_menu_layout);
        alertdialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int which) {
                dialoginterface.dismiss();
                // Here just create a new Category
                if(newFood !=null)
                {
                    //Add the new category Value for positive button
                   foodList.push().setValue(newFood);
                    Snackbar.make(rootLayout,"New Category"+newFood.getName()+" was added",Snackbar.LENGTH_LONG).show();
                }


            }
        });
        alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int which) {
                dialoginterface.dismiss();


            }
        });
        alertdialog.show();

    }
    private void uploadImage() {
        if (saveUri !=null){
            final ProgressDialog mDialog=new ProgressDialog(this);
            mDialog.setMessage("Uploading....");
            mDialog.show();;
            //image name must be random uuid
            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=  storageReference.child("images/"+ imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded Successfully!!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //Set value for new category if image uploaded and we can get downloaded link
                                    newFood = new Foods(
                                           categoryId,
                                            edtFoodName.getText().toString(),
                                            uri.toString(),
                                            edtDescription.getText().toString(),
                                            edtPrice.getText().toString(),
                                           edtDiscount.getText().toString()
                                    );
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this,""+e.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploaded "+progress+"%");
                }
            });

        }

    }
    private void chooseImage() {
        Intent intent=new Intent();
        //uri path
        intent.setType("images/");
        //user want something based on mime type
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);

    }

    private void loadListFood(String categoryId) {
        Query listFoodByCategoryId=foodList.orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions<Foods> options=new FirebaseRecyclerOptions.Builder<Foods>()
                .setQuery(listFoodByCategoryId,Foods.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Foods, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Foods model) {
               viewHolder.food_name.setText(model.getName());
                    Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
                     viewHolder.setItemClickListener(new ItemClickListener() {
                         @Override
                         public void onClick(View view, int position, boolean isLongClick) {

                        }
                     });

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);
                return new FoodViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        onStop();

//              adapter=new FirebaseRecyclerAdapter<Foods, FoodViewHolder>(
//                      Foods.class,R.layout.food_item,FoodViewHolder.class,foodList.orderByChild("menuId").equalTo(categoryId)
//
//
//
//              ) {
//                  @Override
//                  protected void populateViewHolder(FoodViewHolder viewHolder, Foods model, int position) {
//                      viewHolder.food_name.setText(model.getName());
//                      Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
//                      viewHolder.setItemClickListener(new ItemClickListener() {
//                          @Override
//                          public void onClick(View view, int position, boolean isLongClick) {
//
//                          }
//                      });
//
//                  }
//              };


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if image is selectedg
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode==RESULT_OK
                && data!=null && data.getData()!=null)
        {
            //get data and save in uri
            saveUri=data.getData();
            btnSelect.setText("Image Selected! ");

        }


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDialogFood(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE)){
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {
        foodList.child(key).removeValue();
    }

    private void showUpdateDialogFood(final String key, final Foods item) {
        AlertDialog.Builder alertdialog=new AlertDialog.Builder(FoodList.this);
        alertdialog.setTitle("Edit Food");
        alertdialog.setMessage("Please fill full Information");
        //Layout inflater for alertdialog
        LayoutInflater inflater=this.getLayoutInflater();
        //add_menu_layout is reference for inflator for View class
        View add_menu_layout=inflater.inflate(R.layout.add_new_food,null);

        edtFoodName=add_menu_layout.findViewById(R.id.edtName);
        edtDescription=add_menu_layout.findViewById(R.id.edtDescription);
        edtPrice=add_menu_layout.findViewById(R.id.edtPrice);
        edtDiscount=add_menu_layout.findViewById(R.id.edtDiscount);

        //set Default value for view
        edtFoodName.setText(item.getName());
        edtDescription.setText(item.getDescription());
        edtPrice.setText(item.getPrice());
        edtDiscount.setText(item.getDiscount());




        btnSelect=add_menu_layout.findViewById(R.id.btnSelects);
        btnupload=add_menu_layout.findViewById(R.id.btnUploads);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);

            }
        });
        //set alert alertDialof for view
        alertdialog.setView(add_menu_layout);
        alertdialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int which) {
                dialoginterface.dismiss();
                // Here just create a new Category


                     //update Inforamtion
                    item.setName( edtFoodName.getText().toString());
                    item.setName(edtDescription.getText().toString());
                    item.setName(edtDiscount.getText().toString());
                    item.setName(edtPrice.getText().toString());

                    foodList.child(key).setValue(item);

                    Snackbar.make(rootLayout,"Food"+item.getName()+" was edited",Snackbar.LENGTH_LONG).show();



            }
        });
        alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoginterface, int which) {
                dialoginterface.dismiss();


            }
        });
        alertdialog.show();

    }
    private void changeImage(final Foods item) {
        if (saveUri !=null){
            final ProgressDialog mDialog=new ProgressDialog(this);
            mDialog.setMessage("Uploading....");
            mDialog.show();;
            //image name must be random uuid
            final String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=  storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded Successfully!!!", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //Set value for new category if image uploaded and we can get downloaded link
                                    item.setImage(uri.toString());

                                }
                            });

                        }


                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(FoodList.this,""+e.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("Uploaded "+progress+"%");
                }
            });

        }

    }


}
