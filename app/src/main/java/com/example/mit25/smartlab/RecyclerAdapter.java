package com.example.mit25.smartlab;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.connection.ConnectionsClient;

import java.io.Serializable;
import java.util.List;
/**
 * Created by mit25 on 6/6/18.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder>{

    List<Device> list;
    Context context;

    public RecyclerAdapter(List<Device> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview,parent,false);
        MyHolder myHolder = new MyHolder(view,context,list);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position){
        Device dev=list.get(position);
        holder.name.setText(dev.getName());
    }

    @Override
    public int getItemCount() {
        int arr = 0;
        try{
            if(list.size()==0){
                arr = 0;
            }
            else{
                arr=list.size();
            }
        }catch (Exception e){
        }
        return arr;
    }


    public static class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        List<Device> l;
        Context ctx;

        public MyHolder(View itemView,Context ctx,List<Device> list) {
            super(itemView);
            this.ctx = ctx;
            this.l = list;
            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.dName);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Device d=l.get(position);
            Intent i=new Intent(this.ctx,MainActivity.class);
            i.putExtra("Flag","true");
            i.putExtra("Name",d.getName());
            i.putExtra("ID",d.getID());
            this.ctx.startActivity(i);
        }
    }

}