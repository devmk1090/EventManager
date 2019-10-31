//package com.devproject.eventmanager;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//public class AddCursorAdapter extends CursorRecycleAdapter<AddCursorAdapter.ViewHolder> {
//
//    public AddCursorAdapter (Context context, Cursor cursor) {
//        super(context,cursor);
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView getNameData;
//        TextView getCalendarData;
//        TextView getCategoryData;
//        TextView getRelationData;
//        TextView getMoneyData;
//        public ViewHolder(View view) {
//            super(view);
//            getNameData = itemView.findViewById(R.id.getNameData);
//            getCalendarData = itemView.findViewById(R.id.getCalendarData);
//            getCategoryData = itemView.findViewById(R.id.getCategoryData);
//            getRelationData = itemView.findViewById(R.id.getRelationData);
//            getMoneyData = itemView.findViewById(R.id.getMoneyData);
//
//        }
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.asform, parent, false);
//        ViewHolder vh = new ViewHolder(itemView);
//        return  vh;
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
//        AddList addList = AddList.fromCursor(cursor);
//        viewHolder.getNameData.setText(addList.getName());
//        viewHolder.getCalendarData.setText(addList.getName());
//        viewHolder.getCategoryData.setText(addList.getName());
//        viewHolder.getRelationData.setText(addList.getName());
//        viewHolder.getMoneyData.setText(addList.getName());
//    }
//}
