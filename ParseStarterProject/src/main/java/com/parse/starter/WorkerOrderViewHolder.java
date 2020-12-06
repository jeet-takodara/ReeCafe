package com.parse.starter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WorkerOrderViewHolder extends RecyclerView.ViewHolder {

    TextView customerNameTextView;
    TextView customerOrderTextView;
    Button doneButton;


    public WorkerOrderViewHolder(View itemView) {
        super(itemView);
        customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
        customerOrderTextView = itemView.findViewById(R.id.customerOrderTextView);
        doneButton = itemView.findViewById(R.id.doneButton);
    }
}
