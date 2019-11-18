package com.devproject.eventmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class CompareFragment extends Fragment {

    PieChart pieChart;
    TextView categoryText, outTotalMoney, inTotalMoney, inOutTotal, categoryOutMoney, categoryInMoney, categorySum;
    LinearLayout categoryLayout;
    InOutDatabase database;
    String TAG = "OutFragment";


    public CompareFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_compare, container, false);

        if(database != null) {
            database.close();
            database = null;
        }
        database = InOutDatabase.getInstance(getActivity());
        boolean isOpen = database.open();
        if(isOpen) {
            Log.d(TAG, "Book database is open");
        } else {
            Log.d(TAG, "Book database is not open");
        }

        categoryText = (TextView) v.findViewById(R.id.categoryText);
        outTotalMoney = (TextView) v.findViewById(R.id.outTotalMoney);
        inTotalMoney = (TextView) v.findViewById(R.id.inTotalMoney);
        inOutTotal = (TextView) v.findViewById(R.id.inOutTotal);
        categoryOutMoney = (TextView) v.findViewById(R.id.categoryOutMoney);
        categoryInMoney = (TextView) v.findViewById(R.id.categoryInMoney);
        categorySum = (TextView) v.findViewById(R.id.categorySum);
        categoryLayout = (LinearLayout) v.findViewById(R.id.categoryLayout);

        pieChart = (PieChart) v.findViewById(R.id.pieChart);

        int s = database.getCategoryOutMoney();

        pieChart.setUsePercentValues(true);
        Description description = new Description();
        description.setText("This is a pie chart");
        description.setTextSize(20f);
        pieChart.setDescription(description);

        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleRadius(25f);

        ArrayList<PieEntry> pieItems = new ArrayList<PieEntry>();
        pieItems.add(new PieEntry(50f, "결혼식"));
        pieItems.add(new PieEntry(50f, "장례식"));

        PieDataSet dataset = new PieDataSet(pieItems , "Category");
        PieData data = new PieData(dataset);

        pieChart.setData(data);
        dataset.setColors(ColorTemplate.JOYFUL_COLORS);

        categoryLayout.setVisibility(View.GONE);

        final CharSequence[] items = {" 전체 ", " 결혼식 ", " 돌잔치 ", " 장례식 "};
        categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("경조사를 선택하세요")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:
                                        categoryText.setText(items[0]);
                                        outTotalMoney.setText(String.valueOf(database.getAllMoneyOut()));
                                        inTotalMoney.setText(String.valueOf(database.getAllMoneyIn()));
                                        int i = Integer.parseInt(outTotalMoney.getText().toString());
                                        int j = Integer.parseInt(inTotalMoney.getText().toString());
                                        int total = i - j;
                                        inOutTotal.setText(""+total);
                                        break;
                                    case 1:
                                        categoryLayout.setVisibility(View.VISIBLE);
                                        categoryText.setText(items[1]);
                                        categoryOutMoney.setText(String.valueOf(database.getCategoryOutMoney()));
                                        categoryInMoney.setText(String.valueOf(database.getCategoryInMoney()));
                                        int categotyi = Integer.parseInt(categoryOutMoney.getText().toString());
                                        int categotyj = Integer.parseInt(categoryInMoney.getText().toString());
                                        int categoryTotal = categotyi - categotyj;
                                        categorySum.setText(""+categoryTotal);
                                        break;
                                    case 2:
                                        categoryText.setText(items[2]);
                                        break;
                                    case 3:
                                        categoryText.setText(items[3]);
                                        default:
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return v;
    }
}

