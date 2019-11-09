package com.devproject.eventmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.ViewHolder>
        implements OnSubItemClickListener {

    ArrayList<SubList> items = new ArrayList<SubList>();
    OnSubItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.asform, viewGroup, false);

        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        SubList item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void addItem(SubList item) {
        items.add(item);
    }
    public void setItems(ArrayList<SubList> items) {
        this.items = items;
    }
    public SubList getItem(int position) {
        return items.get(position);
    }
    public void setItem(int position, SubList item) {
        items.set(position, item);
    }
    public void setOnitemClickListener(OnSubItemClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView getNameData;
        TextView getCalendarData;
        TextView getCategoryData;
        TextView getRelationData;
        TextView getMoneyData;
        public ViewHolder(View itemView, final OnSubItemClickListener listener) {
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
        public void setItem(SubList item) {
            getNameData.setText(item.getName());
            getCalendarData.setText(item.getDate());
            getCategoryData.setText(item.getCategory());
            getRelationData.setText(item.getRelation());
            getMoneyData.setText(item.getMoney());
        }
    }
    public void removeData(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }
}
