package com.devproject.eventmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddAdapter extends RecyclerView.Adapter<AddAdapter.ViewHolder>
        implements OnAddItemClickListener {

    ArrayList<AddList> items;
    OnAddItemClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView getNameData;
        public TextView getCalendarData;
        public TextView getCategoryData;
        public TextView getRelationData;
        public TextView getMoneyData;
        public ViewHolder(View itemView, final OnAddItemClickListener listener) {
            super(itemView);
            getNameData = itemView.findViewById(R.id.getNameData);
            getCalendarData = itemView.findViewById(R.id.getCalendarData);
            getCategoryData = itemView.findViewById(R.id.getCategoryData);
            getRelationData = itemView.findViewById(R.id.getRelationData);
            getMoneyData = itemView.findViewById(R.id.getMoneyData);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }
        public void setItem(AddList item) {
            getNameData.setText(item.getName());
            getCalendarData.setText(item.getDate());
            getCategoryData.setText(item.getCategory());
            getRelationData.setText(item.getRelation());
            getMoneyData.setText(item.getMoney());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.asform, viewGroup, false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        AddList item = items.get(position);
//        viewHolder.getNameData.setText(item.getName());
//        viewHolder.getCalendarData.setText(item.getDate());
//        viewHolder.getCategoryData.setText(item.getCategory());
//        viewHolder.getRelationData.setText(item.getRelation());
//        viewHolder.getMoneyData.setText(item.getMoney());
        viewHolder.setItem(item);
     }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    public void addItem(AddList item) {
        items.add(item);
    }
    public void setItems(ArrayList<AddList> items) {
        this.items = items;
    }
    public AddList getItem(int position) {
        return items.get(position);
    }
    public void setItem(int position, AddList item) {
        items.set(position, item);
    }
    public void setOnitemClickListener(OnAddItemClickListener listener){
        this.listener = listener;
    }

    public void removeData(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }
}
