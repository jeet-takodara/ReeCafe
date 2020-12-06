package com.parse.starter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class WorkerViewHolder extends RecyclerView.ViewHolder {

    TextView workerNameTextView;
    ImageButton deleteButton;


    public WorkerViewHolder(View itemView) {
        super(itemView);
        workerNameTextView = itemView.findViewById(R.id.workerNameTextView);
        deleteButton = itemView.findViewById(R.id.deleteButton);
    }
}
