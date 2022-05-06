package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickAction{
    List<Users> users_list;
    DataBaseHandler db;
    UserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DataBaseHandler(this);
        users_list = db.getContacts();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.users_recycler_view);
        adapter = new UserRecyclerAdapter(users_list,this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void openPosts(Users user) {
        Intent i = new Intent(getApplicationContext(),PostActivities.class);
        Bundle bundle = new Bundle();
        bundle.putString("username",user.getUsername());
        Log.e("User name", user.getUsername());
        i.putExtras(bundle);
        startActivity(i);

    }
}