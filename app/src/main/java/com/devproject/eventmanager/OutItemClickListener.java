package com.devproject.eventmanager;
import android.view.View;

public interface OutItemClickListener {
    public void onItemClick(OutAdapter.ViewHolder holder, View view, int position);
}