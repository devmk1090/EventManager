package com.devproject.eventmanager;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class CompareFragment extends Fragment {

    PieChart categoryPieChart, relationPieChart;
    TextView categoryText, relationText, outTotalMoney, inTotalMoney, inOutTotal;
    TextView categoryMarry, categoryFuneral, categoryFirstBirth, categorySixty, categoryBirthday, categoryETC;
    LinearLayout categoryLayout, relatioonLayout;
    InOutDatabase database;
    String TAG = "OutFragment";


    public CompareFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_compare, container, false);

        database = InOutDatabase.getInstance(getActivity());
        boolean isOpen = database.open();
        if(isOpen) {
            Log.d(TAG, "Book database is open");
        } else {
            Log.d(TAG, "Book database is not open");
        }

        categoryLayout = (LinearLayout) v.findViewById(R.id.categoryLayout);
        categoryMarry = (TextView) v.findViewById(R.id.categoryMarry);
        categoryFuneral = (TextView) v.findViewById(R.id.categoryFuneral);
        categoryFirstBirth = (TextView) v.findViewById(R.id.categoryFirstBirth);
        categorySixty = (TextView) v.findViewById(R.id.categorySixty);
        categoryBirthday = (TextView) v.findViewById(R.id.categoryBirthday);
        categoryETC = (TextView) v.findViewById(R.id.categoryETC);

        relatioonLayout = (LinearLayout) v.findViewById(R.id.relationLayout);

        categoryText = (TextView) v.findViewById(R.id.categoryText);
        relationText = (TextView) v.findViewById(R.id.relationText);
        outTotalMoney = (TextView) v.findViewById(R.id.outTotalMoney);
        inTotalMoney = (TextView) v.findViewById(R.id.inTotalMoney);
        inOutTotal = (TextView) v.findViewById(R.id.inOutTotal);
        categoryPieChart = (PieChart) v.findViewById(R.id.categoryPieChart);
        relationPieChart = (PieChart) v.findViewById(R.id.relationPieChart);

        outTotalMoney.setText(String.valueOf(database.getAllMoneyOut()));
        inTotalMoney.setText(String.valueOf(database.getAllMoneyIn()));
        int i = Integer.parseInt(outTotalMoney.getText().toString());
        int j = Integer.parseInt(inTotalMoney.getText().toString());
        int total = i - j;
        inOutTotal.setText(""+total);

        categoryPieChart.setVisibility(View.GONE);
        relationPieChart.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.GONE);
        relatioonLayout.setVisibility(View.GONE);

        categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relationPieChart.setVisibility(View.GONE);
                relatioonLayout.setVisibility(View.GONE);
                categoryPieChart.setVisibility(View.VISIBLE);
                categoryLayout.setVisibility(View.VISIBLE);

                categoryPieChart.setUsePercentValues(true);
                categoryPieChart.setHoleRadius(15);
                categoryPieChart.setTransparentCircleRadius(15);
                categoryPieChart.setEntryLabelColor(Color.BLACK);
                categoryPieChart.getLegend().setEnabled(false);

                ArrayList<PieEntry> pieItems = new ArrayList<PieEntry>();
                if(database.getCategoryMarryOutMoney() != 0) {
                    pieItems.add(new PieEntry(database.getCategoryMarryOutMoney(), "결혼식"));
                    categoryMarry.setText("결혼식 : " + String.valueOf(database.getCategoryMarryOutMoney()) + "원");
                }
                if(database.getCategoryFirstBirthOutMoney() != 0) {
                    pieItems.add(new PieEntry(database.getCategoryFirstBirthOutMoney(), "돌잔치"));
                    categoryFirstBirth.setText("돌잔치 : " + String.valueOf(database.getCategoryFirstBirthOutMoney()) + "원");
                }
                if(database.getCategoryFuneralOutMoney() != 0) {
                    pieItems.add(new PieEntry(database.getCategoryFuneralOutMoney(), "장례식"));
                    categoryFuneral.setText("장례식 : " + String.valueOf(database.getCategoryFuneralOutMoney()) + "원");
                }
                if(database.getCategorySixtyOutMoney() != 0) {
                    pieItems.add(new PieEntry(database.getCategorySixtyOutMoney(), "환갑"));
                    categorySixty.setText("환갑 : " + String.valueOf(database.getCategorySixtyOutMoney()) + "원");
                }
                if(database.getCategoryBirthdayOutMoney() != 0) {
                    pieItems.add(new PieEntry(database.getCategoryBirthdayOutMoney(), "생일"));
                    categoryBirthday.setText("생일 : " + String.valueOf(database.getCategoryBirthdayOutMoney()) + "원");
                }
                if(database.getCategoryETCOutMoney() != 0) {
                    pieItems.add(new PieEntry(database.getCategoryETCOutMoney(), "기타"));
                    categoryETC.setText("기타 : " + String.valueOf(database.getCategoryETCOutMoney()) + "원");
                }
                PieDataSet dataset = new PieDataSet(pieItems , "Category");
                PieData data = new PieData(dataset);

                dataset.setValueTextSize(20);
                dataset.setColors(ColorTemplate.JOYFUL_COLORS);
                categoryPieChart.setData(data);

            }
        });

        relationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPieChart.setVisibility(View.GONE);
                categoryLayout.setVisibility(View.GONE);
                relationPieChart.setVisibility(View.VISIBLE);
                relatioonLayout.setVisibility(View.VISIBLE);

                relationPieChart.setUsePercentValues(true);
                relationPieChart.setHoleRadius(15);
                relationPieChart.setTransparentCircleRadius(15);
                relationPieChart.setEntryLabelColor(Color.BLACK);
                relationPieChart.getLegend().setEnabled(false);

                ArrayList<PieEntry> pieItems = new ArrayList<PieEntry>();
                if(database.getRelationFriendOutMoney() != 0) {
                    pieItems.add(new PieEntry(database.getRelationFriendOutMoney(), "동네친구"));
                }
                if(database.getRelationRelativeOutMoney() != 0) {
                    pieItems.add(new PieEntry(database.getRelationRelativeOutMoney(), "친척"));
                }
                if(database.getRelationUniversityOutMoney() != 0) {
                    pieItems.add(new PieEntry(database.getRelationUniversityOutMoney(), "대학교"));
                }
                if(database.getRelationSchoolOutMoney() != 0) {
                    pieItems.add(new PieEntry(database.getRelationSchoolOutMoney(), "초중고"));
                }
                if(database.getRelationJobOutMoney() != 0) {
                    pieItems.add(new PieEntry(database.getRelationJobOutMoney(), "직장동료"));
                }
                PieDataSet dataset = new PieDataSet(pieItems , "Category");
                PieData data = new PieData(dataset);

                dataset.setValueTextSize(20);
                dataset.setColors(ColorTemplate.JOYFUL_COLORS);
                relationPieChart.setData(data);
            }
        });
        return v;
    }
}

