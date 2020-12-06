package com.parse.starter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorkerListActivity extends AppCompatActivity {

    SwipeRefreshLayout workerListSwipeRefreshLayout;
    RecyclerView workerListRecyclerView;
    List<WorkerData> getDataList,dataList;
    List<ParseUser> list;
    WorkerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_list);

        workerListSwipeRefreshLayout = findViewById(R.id.workerListSwipeRefreshLayout);
        workerListRecyclerView = findViewById(R.id.workerListRecyclerView);

        workerListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRecyclerView();
                workerListSwipeRefreshLayout.setRefreshing(false);
            }
        });

        list = new ArrayList<>();
        getDataList = new ArrayList<>();
        dataList = new ArrayList<>();

        setRecyclerView();

    }

    public List<WorkerData> getData() {

        getDataList.clear();
        list.clear();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("Label","worker");

        try {
            list = query.find();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(ParseUser user : list) {
            getDataList.add(new WorkerData(user.getString("Name")));
        }

        Collections.sort(getDataList, new Comparator<WorkerData>() {
            @Override
            public int compare(WorkerData workerData, WorkerData t1) {
                return workerData.workerName.compareTo(t1.workerName);
            }
        });

        return getDataList;
    }

    public void setRecyclerView() {
        dataList = getData();

        adapter = new WorkerAdapter(dataList,getApplicationContext());

        workerListRecyclerView.setAdapter(adapter);
        workerListRecyclerView.setLayoutManager(new LinearLayoutManager(WorkerListActivity.this));
    }

}