package com.example.narendra.eatfoodserver;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.narendra.eatfoodserver.Common.Common;
import com.example.narendra.eatfoodserver.Model.DataMessage;
import com.example.narendra.eatfoodserver.Model.MyResponse;
import com.example.narendra.eatfoodserver.Remote.APIService;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessage extends AppCompatActivity {
    MaterialEditText edtMessage,edtTitle;
    Button btnSend;
    APIService mService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        mService= Common.getFCMClient();

        edtMessage=(MaterialEditText)findViewById(R.id.editMessage);
        edtTitle=(MaterialEditText)findViewById(R.id.editTitle);
        btnSend=(Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create message
                Map<String,String> dataSend = new HashMap<>();
                dataSend.put("title",edtTitle.getText().toString());
                dataSend.put("message",edtMessage.getText().toString());
                DataMessage dataMessage = new DataMessage(new StringBuilder("/topics/").append(Common.topicName).toString(),dataSend);
                mService.sendNotification(dataMessage).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.isSuccessful())
                            Toast.makeText(SendMessage.this, "Message sent!", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(SendMessage.this,Home.class);
                        startActivity(intent);
                    }


                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(SendMessage.this,""+t.getMessage(),Toast.LENGTH_LONG).show();

                            }
                        });


            }
        });
    }
}
