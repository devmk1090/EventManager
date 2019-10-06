package com.devproject.eventmanager;
import android.database.Cursor;
import android.view.View;

public interface OnAddItemClickListener {
    public void onItemClick(AddAdapter.ViewHolder holder, View view, int position);

}
