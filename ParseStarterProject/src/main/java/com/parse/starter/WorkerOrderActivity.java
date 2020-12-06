package com.parse.starter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class WorkerOrderActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView workerOrderRecyclerView;
    WorkerOrderAdapter adapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_order);

        swipeRefreshLayout = findViewById(R.id.workerOrderSwipeRefreshLayout);
        workerOrderRecyclerView = findViewById(R.id.workerOrderRecyclerView);
        context = getApplicationContext();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRecyclerView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        setRecyclerView();

    }

    public void setRecyclerView() {
        refreshOrders();
        adapter = new WorkerOrderAdapter(getApplicationContext(),WorkerActivity.orders);
        workerOrderRecyclerView.setAdapter(adapter);
        workerOrderRecyclerView.setLayoutManager(new LinearLayoutManager(WorkerOrderActivity.this));
    }

    private void refreshOrders() {

        WorkerActivity.orders.clear();

        try {
            SQLiteDatabase database = SQLiteDatabase.openDatabase(context.getDatabasePath("Orders").getPath(), null, SQLiteDatabase.OPEN_READONLY);

            Cursor c = database.rawQuery("SELECT * FROM Details", null);

            int id = c.getColumnIndex("id");
            int nameId = c.getColumnIndex("name");
            int orderId = c.getColumnIndex("list");

            c.moveToFirst();

            while(!c.isAfterLast()){
                WorkerActivity.orders.add(c.getString(orderId)+"@"+c.getString(nameId)+"@"+c.getString(id));
                c.moveToNext();
            }

            c.close();
            database.close();

        } catch(Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "No orders!", Toast.LENGTH_SHORT).show();
        }
    }

}