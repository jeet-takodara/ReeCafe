package com.parse.starter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CartItemViewHolder extends RecyclerView.ViewHolder {

    TextView cartItemName;
    TextView cartItemQuantity;
    Button add;
    Button subtract;


    public CartItemViewHolder(View itemView) {
        super(itemView);

        cartItemName = itemView.findViewById(R.id.cartItemName);
        cartItemQuantity = itemView.findViewById(R.id.cartItemQuantity);
        add = itemView.findViewById(R.id.add);
        subtract = itemView.findViewById(R.id.subtract);

    }
}
