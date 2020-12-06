package com.parse.starter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserItemViewHolder extends RecyclerView.ViewHolder {

    ImageView itemImage;
    TextView itemName;
    TextView itemPrice;
    Button addToCartButton;

    public UserItemViewHolder(View itemView) {
        super(itemView);
        itemImage = itemView.findViewById(R.id.itemImage);
        itemName = itemView.findViewById(R.id.itemName);
        itemPrice = itemView.findViewById(R.id.itemPrice);
        addToCartButton = itemView.findViewById(R.id.addToCartButton);
    }
}
