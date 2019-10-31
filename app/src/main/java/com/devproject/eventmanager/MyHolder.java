package com.devproject.eventmanager;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView getNameData;
    TextView getCalendarData;
    TextView getCategoryData;
    TextView getRelationData;
    TextView getMoneyData;
    OnAddItemClickListener itemClickListener;

    public MyHolder(View itemView) {
        super(itemView);
        getNameData = itemView.findViewById(R.id.getNameData);
        getCalendarData = itemView.findViewById(R.id.getCalendarData);
        getCategoryData = itemView.findViewById(R.id.getCategoryData);
        getRelationData = itemView.findViewById(R.id.getRelationData);
        getMoneyData = itemView.findViewById(R.id.getMoneyData);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v, getLayoutPosition());
    }

    public void setItemClickListener(OnAddItemClickListener ic) {
        this.itemClickListener = ic;
    }
}
