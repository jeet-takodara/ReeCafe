package com.parse.starter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerViewHolder> {

    List<WorkerData> workerNameList;
    Context context;

    public WorkerAdapter (List<WorkerData> workerNameList, Context context) {
        this.workerNameList = workerNameList;
        this.context = context;
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.worker_list,parent,false);

        return new WorkerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final WorkerViewHolder holder, int position) {
        holder.workerNameTextView.setText(workerNameList.get(position).workerName);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("Name",workerNameList.get(holder.getAdapterPosition()).workerName);
                try{

                    ParseUser user = query.getFirst();
                    HashMap<String,String> params = new HashMap<>();
                    params.put("userId",user.getObjectId());

                    ParseCloud.callFunctionInBackground("deleteUserWithId", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {

                            if(e == null) {
                                    workerNameList.remove(holder.getAdapterPosition());
                                    notifyItemRemoved(holder.getAdapterPosition());
                                    Toast.makeText(context, "Worker Deleted!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    params.clear();

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return workerNameList.size();
    }
}
