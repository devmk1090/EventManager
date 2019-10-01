package com.devproject.eventmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SubListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SubList> subArrayList;

    TextView getNameData, getCalendarData, getCategoryData,
            getRelationData,getMoneyData;


    public SubListAdapter(Context context, ArrayList<SubList> subArrayList ) {
        this.context = context;
        this.subArrayList = subArrayList;

    }


    @Override
    public int getCount() { //리스트뷰가 몇개의 아이템을 가지고 있는지를 알려주는 함수
        return subArrayList.size();
    }

    @Override
    public Object getItem(int position) {   //현재 어떤 아이템인지를 알려주는 부분으로 우리는 arraylist 에 저장되어있는 객체중
        return subArrayList.get(position);  //position 에 해당하는 것을 가져올 것이므로
    }

    @Override
    public long getItemId(int position) {   //현재 어떤 포지션인지를 알려주는 부분
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.asform, null);
            getNameData = (TextView) convertView.findViewById(R.id.getNameData);
            getCalendarData = (TextView) convertView.findViewById(R.id.getCalendarData);
            getCategoryData = (TextView) convertView.findViewById(R.id.getCategoryData);
            getRelationData = (TextView) convertView.findViewById(R.id.getRelationData);
            getMoneyData = (TextView) convertView.findViewById(R.id.getMoneyData);
        }
        getNameData.setText(subArrayList.get(position).getName());
        getCalendarData.setText(subArrayList.get(position).getDate());
        getCategoryData.setText(subArrayList.get(position).getCategory());
        getRelationData.setText(subArrayList.get(position).getRelation());
        getMoneyData.setText(subArrayList.get(position).getMoney());

        return convertView;
    }
}
