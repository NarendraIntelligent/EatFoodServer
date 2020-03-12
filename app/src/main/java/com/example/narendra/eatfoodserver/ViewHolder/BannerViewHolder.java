package com.example.narendra.eatfoodserver.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.narendra.eatfoodserver.Common.Common;
import com.example.narendra.eatfoodserver.R;

/**
 * Created by narendra on 3/29/2018.
 */

public class BannerViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    public TextView banner_name;
    public ImageView banner_image;



    public BannerViewHolder(View itemView) {
        super(itemView);
      banner_name=(TextView)itemView.findViewById(R.id.banner_name);
        banner_image=(ImageView)itemView.findViewById(R.id.banner_image);

        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    //menu for update,delete
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Select the action");
        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(),Common.DELETE);


    }
}

