package com.parse.starter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    TextView itemName;
    Switch switchItem;
    ImageButton menuButton;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemName = itemView.findViewById(R.id.nameTextView);
        switchItem = itemView.findViewById(R.id.switchState);
        menuButton = itemView.findViewById(R.id.menuButton);
    }
}
