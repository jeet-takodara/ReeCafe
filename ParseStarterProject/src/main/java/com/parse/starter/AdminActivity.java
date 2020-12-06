package com.parse.starter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    static RelativeLayout adminLayout;
    FloatingActionButton addItem;
    Intent gotoaddItem;
    MyAdapter adapter;
    RecyclerView recyclerView;
    static List<ItemData> list,getDataList;
    static List<Boolean> status,getStatusList;
    List<ParseObject> objects;
    String code;
    ParseObject deleteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        adminLayout = findViewById(R.id.adminLayout);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        addItem = findViewById(R.id.addItemFloatingActionButton);

        gotoaddItem = new Intent(getApplicationContext(),AddItemActivity.class);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(gotoaddItem);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRecyclerView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy > 0) {
                    if(addItem.isShown())
                        addItem.hide();
                }

                else if(dy < 0) {
                    if(!addItem.isShown())
                        addItem.show();
                }
            }
        });

        list = new ArrayList<>();
        status = new ArrayList<>();
        getDataList = new ArrayList<>();
        objects = new ArrayList<>();
        getStatusList = new ArrayList<>();

        setRecyclerView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.admin_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                break;

            case R.id.workers:
                Intent gotoWorkerList = new Intent(getApplicationContext(),WorkerListActivity.class);
                startActivity(gotoWorkerList);
                break;

            case R.id.addWorker:
                Intent gotoAddWorker = new Intent(getApplicationContext(),AddWorker.class);
                startActivity(gotoAddWorker);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        if(isNetworkAvailable()) {
            ParseUser.logOut();
            Intent backtoLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(backtoLogin);
            finish();
        } else {
            Toast.makeText(this, "Internet Connection Not Available!", Toast.LENGTH_SHORT).show();
        }
    }

    public List<ItemData> getData() {

        getDataList.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ItemDetails");
        query.orderByAscending("name");

        try {
            objects = query.find();
        } catch(Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < objects.size(); i++) {
            getDataList.add(new ItemData(objects.get(i).getString("name")));
        }

        return getDataList;
    }

    public List<Boolean> getStatus() {
        getStatusList.clear();

            for (int i = 0; i < objects.size(); i++) {
                getStatusList.add(objects.get(i).getBoolean("status"));
            }

        return getStatusList;

    }

    public void setRecyclerView() {
        list = getData();
        status = getStatus();
        adapter = new MyAdapter(list,status,getApplicationContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminActivity.this));
    }


    ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {

        Drawable background;
        Drawable xMark;
        int xMarkMargin;
        boolean initiated;

        public void init() {
            background = new ColorDrawable(Color.RED);
            xMark = ContextCompat.getDrawable(AdminActivity.this , R.drawable.bin);
            xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            xMarkMargin = 15;
            initiated = true;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            ParseQuery<ParseObject> delete = ParseQuery.getQuery("ItemDetails");
            delete.whereEqualTo("name",list.get(viewHolder.getAdapterPosition()).itemName);
            try {
                deleteItem = delete.getFirst();
                deleteItem.delete();
            } catch(Exception e) {
                e.printStackTrace();
            }

            list.remove(viewHolder.getAdapterPosition());
            status.remove(viewHolder.getAdapterPosition());

            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());

        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            View itemView = viewHolder.itemView;

            if(viewHolder.getAdapterPosition() == -1) {
                return;
            }

            if(!initiated){
                init();
            }

            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            int itemHeight = itemView.getBottom() - itemView.getTop();
            int intrinsicWidth = xMark.getIntrinsicWidth();
            int intrinsicHeight = xMark.getIntrinsicWidth();

            int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
            int xMarkRight = itemView.getRight() - xMarkMargin;
            int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
            int xMarkBottom = xMarkTop + intrinsicHeight;
            xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

            xMark.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
