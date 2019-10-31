package com.devproject.eventmanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> {
    Context c;
    ArrayList<AddList> addLists;

    public MyAdapter(Context ctx, ArrayList<AddList> addLists) {
        this.c = ctx;
        this.addLists = addLists;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //VIEW OBJ FROM XML
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.asform, null);
        MyHolder holder = new MyHolder(v);
        return holder;
    }

    //BIND DATA TO VIEWS
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.getNameData.setText(addLists.get(position).getName());
        holder.getCalendarData.setText(addLists.get(position).getName());
        holder.getCategoryData.setText(addLists.get(position).getName());
        holder.getRelationData.setText(addLists.get(position).getName());
        holder.getMoneyData.setText(addLists.get(position).getName());

        //HANDLE ITEMCLICKS
        holder.setItemClickListener(new OnAddItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //OPEN DETAIL ACTIVITY
                //PASS DATA

                //CREATE INTENT
                Intent i = new Intent(c, DetailActivity.class);

                //LOAD DATA
                i.putExtra("NAME", addLists.get(pos).getName());
                i.putExtra("DATE", addLists.get(pos).getDate());
                i.putExtra("CATEGORY", addLists.get(pos).getCategory());
                i.putExtra("RELATION", addLists.get(pos).getRelation());
                i.putExtra("MONEY", addLists.get(pos).getMoney());
                i.putExtra("ID", addLists.get(pos).getId());

                //START ACTIVITY
                c.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addLists.size();
    }
}
