package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class UserMenuActivity extends AppCompatActivity {

    ParseFile file;
    RecyclerView userRecyclerView;
    List<UserItemData> getDataList,list;
    List<ParseObject> item;
    UserAdapter adapter;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        getDataList = new ArrayList<>();
        list = new ArrayList<>();
        item = new ArrayList<>();

        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setHasFixedSize(true);

        setRecyclerView();

    }

    public List<UserItemData> getData() {

        getDataList.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ItemDetails");
        query.whereEqualTo("status",true);
        query.orderByAscending("name");

        try {

            item = query.find();

        }catch(Exception e) {
            e.printStackTrace();
        }

        for(ParseObject o : item) {
            file = o.getParseFile("image");
            try {
                imageUrl = file.getUrl();
            } catch(Exception e) {
                e.printStackTrace();
            }
            getDataList.add(new UserItemData(o.getString("name"), o.getInt("price"), imageUrl));
        }

        return getDataList;

    }

    public void setRecyclerView() {

        list = getData();
        adapter = new UserAdapter(list,getApplicationContext());
        userRecyclerView.setAdapter(adapter);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_food_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.cartButton) {
            Intent intent = new Intent(this,CartActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}