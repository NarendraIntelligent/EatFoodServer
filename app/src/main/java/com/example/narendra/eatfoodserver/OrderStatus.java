package com.example.narendra.eatfoodserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.narendra.eatfoodserver.Common.Common;
import com.example.narendra.eatfoodserver.Model.DataMessage;
import com.example.narendra.eatfoodserver.Model.MyResponse;
import com.example.narendra.eatfoodserver.Model.Request;
import com.example.narendra.eatfoodserver.Model.Token;
import com.example.narendra.eatfoodserver.Remote.APIService;
import com.example.narendra.eatfoodserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {
   public RecyclerView recyclerView;
   public RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;
    FirebaseDatabase db;
    DatabaseReference requests;
    MaterialSpinner spinner,shiperspinner;
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
//Init firebase
        db=FirebaseDatabase.getInstance();
        requests=db.getReference("Requests");
        //INIT Service
        mService=Common.getFCMClient();
        //Init
        recyclerView=(RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//loadOrders();
        loadOrders(Common.currentuser.getPhone());//load all orders
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


    private void loadOrders(String phone) {

        FirebaseRecyclerOptions<Request> options=new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requests,Request.class)
                .build();

adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_layout,parent,false);
        return new OrderViewHolder(itemView);
    }
    @Override
    protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull final Request model) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));

                //New Event button
                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(position).getKey(),adapter.getItem(position));

                    }
                });
                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(position).getKey(),adapter.getItem(position));
                    }
                });
                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail=new Intent(OrderStatus.this,OrderDetail.class);
                         Common.currentRequest=model;
                         orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                         startActivity(orderDetail);



                    }
                });
                viewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trackingOrder=new Intent(OrderStatus.this,TrackingOrder.class);
                        Common.currentRequest=model;
                        startActivity(trackingOrder);

                    }
                });

    }

};
adapter.startListening();
adapter.notifyDataSetChanged();

recyclerView.setAdapter(adapter);
    }


    private void deleteOrder(String key,Request item) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    private void showUpdateDialog(final String key, final Request item) {
//       final AlertDialog.Builder alertDialog=new AlertDialog.Builder(OrderStatus.this);
//       alertDialog.setTitle("Update Order");
//       alertDialog.setMessage("Please Choose Status");
//        LayoutInflater inflater=this.getLayoutInflater();
//
//    final  View view=inflater.inflate(R.layout.update_order_layout,null);
//        spinner=view.findViewById(R.id.statusSpinner);
//        spinner.setItems("Placed","On my Way","Shipped");
//        spinner=view.findViewById(R.id.recycler_shippers);
//        alertDialog.setView(view);
////        final String localKey=key;
//        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//               item.setStatus(String.valueOf(spinner.getSelectedIndex()));
//                requests.child(key).setValue(item);
//                adapter.notifyDataSetChanged();// Add to update item size
//                sendOrderStatusToUser(key,item);
//
//            }
//        });
//        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();;
//
//            }
//        });
//        alertDialog.show();
//        }
        final AlertDialog.Builder alert = new AlertDialog.Builder(OrderStatus.this);
        alert.setTitle("Update request");
        alert.setMessage("Please select a status :");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);

        spinner = view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed", "On my way", "Shipping");
        shiperspinner=(MaterialSpinner)view.findViewById(R.id.shiperSpinner);
        // load all shipper phone to spinner
        final List<String> shipperList=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.SHIPPERS_TABLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot shipperSnapShot:dataSnapshot.getChildren())
                            shipperList.add(shipperSnapShot.getKey());
                        shiperspinner.setItems(shipperList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        alert.setView(view);

        alert.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
if(item.getStatus().equals("2"))
{
    FirebaseDatabase.getInstance().getReference(Common.ORDER_NEED_SHIP_TABLE)
            .child(shiperspinner.getItems().get(shiperspinner.getSelectedIndex()).toString())
            .child(key)
            .setValue(item);
    requests.child(key).setValue(item);
    adapter.notifyDataSetChanged();
    sendOrderStatusToUser(key, item);
    sendOrderShipRequestToShipper(shiperspinner.getItems().get(shiperspinner.getSelectedIndex()).toString(),item);
}
else {
    requests.child(key).setValue(item);
    adapter.notifyDataSetChanged();
    sendOrderStatusToUser(key, item);
}
            }
        });

        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void sendOrderShipRequestToShipper(String shipperPhone, Request item) {
        DatabaseReference tokens=db.getReference("Tokens");
        tokens.child(shipperPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {


                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       if(dataSnapshot.exists()){

                           Token token=dataSnapshot.getValue(Token.class);
                           Map<String,String> datasend=new HashMap<>();
                           datasend.put("title","EatFood");
                           datasend.put("message","You have new order need ship");
                           DataMessage dataMessage=new DataMessage(token.getToken(),datasend);

                           mService.sendNotification(dataMessage)
                                   .enqueue(new Callback<MyResponse>() {
                                       @Override
                                       public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                           if (response.code() == 200) {
                                               if (response.body().success == 1) {
                                                   Toast.makeText(OrderStatus.this, "Sent to Shippers", Toast.LENGTH_LONG).show();
                                               } else {
                                                   Toast.makeText(OrderStatus.this, "failed to send notification", Toast.LENGTH_LONG).show();
                                               }
                                           }
                                       }

                                       @Override
                                       public void onFailure(Call<MyResponse> call, Throwable t) {
                                           Log.e("ERROR",t.getMessage());

                                       }
                                   });
                       }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(OrderStatus.this, "Database Error", Toast.LENGTH_SHORT).show();

                    }
                });
        
    }

    private void sendOrderStatusToUser(final String key,final Request item) {
        DatabaseReference tokens=db.getReference("Tokens");
        tokens.child(item.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            Token token=dataSnapshot.getValue(Token.class);
                            Map<String,String> datasend=new HashMap<>();
                            datasend.put("title","EatFood");
                            datasend.put("message","Your order "+key+" was updated");
                            DataMessage dataMessage=new DataMessage(token.getToken(),datasend);

                            mService.sendNotification(dataMessage)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success == 1) {
                                                    Toast.makeText(OrderStatus.this, "Order was Updated !", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(OrderStatus.this, "Order was Updated but failed to send notification", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(OrderStatus.this, "Database Error", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
