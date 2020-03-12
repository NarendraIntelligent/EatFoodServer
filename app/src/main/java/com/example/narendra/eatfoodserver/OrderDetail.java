package com.example.narendra.eatfoodserver;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.example.narendra.eatfoodserver.Common.Common;
import com.example.narendra.eatfoodserver.ViewHolder.OrderDetailAdapter;

/**
 * Created by narendra on 3/7/2018.
 */

public class OrderDetail extends AppCompatActivity {
    TextView order_id,order_phone,order_total,order_address,order_comment;
    String order_id_value="";
    RecyclerView lstFoods;
    RecyclerView.LayoutManager layoutManager;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        order_id=findViewById(R.id.order_id);
        order_phone=findViewById(R.id.order_phone);
        order_address=findViewById(R.id.order_address);
        order_comment=findViewById(R.id.order_comment);
        order_total=findViewById(R.id.order_total);


        lstFoods=findViewById(R.id.lstFoods);
        lstFoods.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        lstFoods.setLayoutManager(layoutManager);
        if(getIntent()!=null)
            order_id_value=getIntent().getStringExtra("OrderId");
        //set value
        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_address.setText(Common.currentRequest.getAddress());
        order_comment.setText(Common.currentRequest.getComment());

        OrderDetailAdapter adapter=new OrderDetailAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        lstFoods.setAdapter(adapter);


    }



}
