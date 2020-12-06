package com.parse.starter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class WorkerOrderAdapter extends RecyclerView.Adapter<WorkerOrderViewHolder> {

    Context context;
    List<String> orders;

    public WorkerOrderAdapter(Context context, List<String> orders) {
        this.context = context;
        this.orders = orders;
    }


    @NonNull
    @Override
    public WorkerOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.worker_order_list,parent,false);

        return new WorkerOrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final WorkerOrderViewHolder holder, final int position) {
        final String[] value = orders.get(position).split("@");
        holder.customerNameTextView.setText(String.format("%s @ %s", value[1], value[2]));
        holder.customerOrderTextView.setText(value[0]);
        holder.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery<ParseObject> object = ParseQuery.getQuery("TransactionDetails");
                String[] s = holder.customerNameTextView.getText().toString().split("@");
                object.getInBackground("" + s[1].trim(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if(e == null) {

                            if(object != null) {
                                object.put("status","complete");
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null) {
                                            WorkerActivity.orders.remove(WorkerActivity.orders.get(position));
                                            updateDatabase(value[2]);
                                            notifyItemRemoved(holder.getAdapterPosition());
                                        } else {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.i("ErrorSave",e.getMessage());
                                        }
                                    }
                                });
                            }

                        } else {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.i("Error",e.getMessage());
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private void updateDatabase(String id) {
        try {
            SQLiteDatabase database = SQLiteDatabase.openDatabase(context.getDatabasePath("Orders").getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            SQLiteStatement statement = database.compileStatement("DELETE FROM Details where id = ?");
            statement.bindString(1,id);
            statement.executeUpdateDelete();
        } catch(Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
