package com.example.narendra.eatfoodserver.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.narendra.eatfoodserver.Model.Order;
import com.example.narendra.eatfoodserver.R;

import java.util.List;

/**
 * Created by narendra on 3/6/2018.
 */
class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView name,quantity,price,discount;
    public MyViewHolder(View itemView) {
        super(itemView);
        name=(TextView)itemView.findViewById(R.id.product_name);
        quantity=(TextView)itemView.findViewById(R.id.product_quantity);
        price=(TextView)itemView.findViewById(R.id.product_price);
        discount=(TextView)itemView.findViewById(R.id.product_discount);
    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {
    List<Order> myOrders;
    public OrderDetailAdapter(List<Order> myOrders){
        this.myOrders=myOrders;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order order=myOrders.get(position);
        holder.name.setText(String.format("Name : %s",order.getProductName()));
        holder.quantity.setText(String.format("Quantity : %s",order.getQuantity()));
        holder.price.setText(String.format("Price : %s",order.getPrice()));
        holder.discount.setText(String.format("Discount : %s",order.getDiscount()));
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
