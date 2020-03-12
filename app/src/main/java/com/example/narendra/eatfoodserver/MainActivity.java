package com.example.narendra.eatfoodserver;

import android.content.Intent;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button btnsign;
    TextView txtslogan;
    //Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnsign = (Button) findViewById(R.id.btnSignIns);
        txtslogan = (TextView) findViewById(R.id.slogan);
        //fonts get from assets
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        txtslogan.setTypeface(face);
        btnsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SignIn.class);
                startActivity(intent);
            }
        });
    }
}
