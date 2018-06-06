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

    List<Device> list =  new ArrayList<>();
    RecyclerView recycle;
    RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicelist);

        recycle = (RecyclerView) findViewById(R.id.recyclerview);
        for(int i=0;i<5;i++){
            Device d=new Device(Integer.toString(i),Integer.toString(i));
            Log.d("name:",d.getName());
            Log.d("ID:",d.getID());
            list.add(d);
        }
        recyclerAdapter = new RecyclerAdapter(list,this);
        RecyclerView.LayoutManager recyce = new GridLayoutManager(this,1);
        recycle.setLayoutManager(recyce);
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);
    }

}
