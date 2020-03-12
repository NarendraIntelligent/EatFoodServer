package com.example.narendra.eatfoodserver;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.narendra.eatfoodserver.Common.Common;
import com.example.narendra.eatfoodserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    TextView edtPhone,edtPassword;
    Button btnSignIn;
    // Intiate firebase
    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        edtPassword=(TextView)findViewById(R.id.editpassword);
        edtPhone=(TextView)findViewById(R.id.editphoneNumber);
        btnSignIn=(Button)findViewById(R.id.btnSignIn);
        database=FirebaseDatabase.getInstance();
        // Connection with User in Firebase
        users=database.getReference("User");
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SignInUser method
                signInUser(edtPhone.getText().toString(),edtPassword.getText().toString());
            }
        });

    }

    private void signInUser(String phone, String password) {
        final ProgressDialog mdialog = new ProgressDialog(SignIn.this);
        mdialog.setMessage("Please Waiting...");
        mdialog.dismiss();
        final String localPhone = phone;
        final String localPassword = password;
        //addvaluelistener is an interface contains  2 methods
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // datasnapshot contains values when ever data changes this method is called
                // If phone exists in firebase
                if (dataSnapshot.child(localPhone).exists()) {
                   mdialog.dismiss();
//                    mdialog.show();
                    //Get the values from the firebase and store the values in user class and store in user reference
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    //set the phone to localphone
                    user.setPhone(localPhone);
                    //If user is IsStaff
                    if (Boolean.parseBoolean(user.getIsStaff())) {
                        // if user password and localpassword(what we have typed are equal)(Both passwords are equal)then move to home.this

                        if (user.getPassword().equals(localPassword)) {
                            Intent login=new Intent(SignIn.this,Home.class);
                            // user equal to  Common.currentuser
                            Common.currentuser=user;
                            startActivity(login);
                            finish();

                        } else
                            Toast.makeText(SignIn.this, "Wrong password !..", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(SignIn.this, "Please login with Staff Account", Toast.LENGTH_SHORT).show();
                } else {
                    mdialog.dismiss();
                    Toast.makeText(SignIn.this, "User not exists in Database", Toast.LENGTH_LONG).show();

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }}

