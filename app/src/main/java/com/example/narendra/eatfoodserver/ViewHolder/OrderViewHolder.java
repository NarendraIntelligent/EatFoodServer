package com.example.narendra.eatfoodserver.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.narendra.eatfoodserver.R;

/**
 * Created by narendra on 2/16/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress,txtOrderDate;
    public Button btnEdit,btnRemove,btnDetail,btnDirection;
//    private  ItemClickListener itemClickListener;


    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderId =  itemView.findViewById(R.id.order_name);
        txtOrderStatus =  itemView.findViewById(R.id.order_status);
        txtOrderPhone =  itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_ship_to);
        txtOrderDate=itemView.findViewById(R.id.order_date);

        btnEdit=itemView.findViewById(R.id.btnEdit);
        btnRemove=itemView.findViewById(R.id.btnRemove);
        btnDetail=itemView.findViewById(R.id.btnDetail);
        btnDirection=itemView.findViewById(R.id.btnDirection);

    }



}

