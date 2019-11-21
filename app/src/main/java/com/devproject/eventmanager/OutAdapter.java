package com.devproject.eventmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OutAdapter extends RecyclerView.Adapter<OutAdapter.ViewHolder>
        implements OutItemClickListener {

    ArrayList<OutList> items;
    OutItemClickListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView getNameData;
        public TextView getCalendarData;
        public TextView getCategoryData;
        public TextView getRelationData;
        public TextView getMoneyData;
        public TextView getMemoData;

        public ViewHolder(View itemView, final OutItemClickListener listener) {
            super(itemView);
            getNameData = itemView.findViewById(R.id.getNameData);
            getCalendarData = itemView.findViewById(R.id.getCalendarData);
            getCategoryData = itemView.findViewById(R.id.getCategoryData);
            getRelationData = itemView.findViewById(R.id.getRelationData);
            getMoneyData = itemView.findViewById(R.id.getMoneyData);
            getMemoData = itemView.findViewById(R.id.getMemoData);
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
        public void setItem(OutList item) {
            getNameData.setText(item.getName());
            getCalendarData.setText(item.getDate());
            getCategoryData.setText(item.getCategory());
            getRelationData.setText(item.getRelation());
            getMoneyData.setText(item.getMoney());
            getMemoData.setText(item.getMemo());
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
        OutList item = items.get(position);
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

    public void addItem(OutList item) {
        items.add(item);
    }
    public void setItems(ArrayList<OutList> items) {
        this.items = items;
    }
    public OutList getItem(int position) {
        return items.get(position);
    }
    public void setItem(int position, OutList item) {
        items.set(position, item);
    }
    public void setOnitemClickListener(OutItemClickListener listener){
        this.listener = listener;
    }

    public void removeData(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }
}
