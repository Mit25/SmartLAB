package com.example.mit25.smartlab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Devicelist extends AppCompatActivity {

    ArrayList<Device> list;
    RecyclerView recycle;
    RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicelist);

        list = (ArrayList<Device>) getIntent().getSerializableExtra("List");

        recycle = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerAdapter = new RecyclerAdapter(list,this);
        RecyclerView.LayoutManager recyce = new GridLayoutManager(this,1);
        recycle.setLayoutManager(recyce);
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);
    }

}
