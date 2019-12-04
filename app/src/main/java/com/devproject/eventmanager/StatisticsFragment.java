package com.devproject.eventmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    PieChart categoryPieChart, relationPieChart, categoryInPieChart, relationInPieChart;
    TextView categoryText, relationText, outTotalMoney, inTotalMoney, inOutTotal;
    LinearLayout wholeLayout, categoryLayout, relationLayout, categoryLayout2, relationLayout2;
    InOutDatabase database;
    String TAG = "OutFragment";


    public StatisticsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_statistics, container, false);

        database = InOutDatabase.getInstance(getActivity());
        boolean isOpen = database.open();
        if (isOpen) {
            Log.d(TAG, "Book database is open");
        } else {
            Log.d(TAG, "Book database is not open");
        }

        wholeLayout = (LinearLayout) v.findViewById(R.id.wholeLayout);
        categoryLayout = (LinearLayout) v.findViewById(R.id.categoryLayout);
        categoryLayout2 = (LinearLayout) v.findViewById(R.id.categoryLayout2);
        TextView categoryMarry = new TextView(getActivity());
        categoryMarry.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView categoryFirstBirth = new TextView(getActivity());
        categoryFirstBirth.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView categoryFuneral = new TextView(getActivity());
        categoryFuneral.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView categorySixty = new TextView(getActivity());
        categorySixty.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView categorySeventy = new TextView(getActivity());
        categorySeventy.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView categoryBirthday = new TextView(getActivity());
        categoryBirthday.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView categoryETC = new TextView(getActivity());
        categoryETC.setTextAppearance(getContext(), R.style.CustomTextView);


        relationLayout = (LinearLayout) v.findViewById(R.id.relationLayout);
        relationLayout2 = (LinearLayout) v.findViewById(R.id.relationLayout2);
        TextView relationFriend = new TextView(getActivity());
        relationFriend.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView relationRelative = new TextView(getActivity());
        relationRelative.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView relationJob = new TextView(getActivity());
        relationJob.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView relationUniversity = new TextView(getActivity());
        relationUniversity.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView relationFamily = new TextView(getActivity());
        relationFamily.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView relationKnowing = new TextView(getActivity());
        relationKnowing.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView relationETC = new TextView(getActivity());
        relationETC.setTextAppearance(getContext(), R.style.CustomTextView);

        categoryText = (TextView) v.findViewById(R.id.categoryText);
        relationText = (TextView) v.findViewById(R.id.relationText);
        outTotalMoney = (TextView) v.findViewById(R.id.outTotalMoney);
        inTotalMoney = (TextView) v.findViewById(R.id.inTotalMoney);
        inOutTotal = (TextView) v.findViewById(R.id.inOutTotal);
        categoryPieChart = (PieChart) v.findViewById(R.id.categoryPieChart);
        relationPieChart = (PieChart) v.findViewById(R.id.relationPieChart);
        categoryInPieChart = (PieChart) v.findViewById(R.id.categoryInPieChart);
        relationInPieChart = (PieChart) v.findViewById(R.id.relationInPieChart);

        DecimalFormat formatter = new DecimalFormat("###,###");
        int in = database.getAllMoneyIn();
        int out = database.getAllMoneyOut();
        inTotalMoney.setText(formatter.format(in));
        outTotalMoney.setText(formatter.format(out));
        int total = in - out;
        inOutTotal.setText(formatter.format(total));


        categoryPieChart.setVisibility(View.GONE);
        relationPieChart.setVisibility(View.GONE);
        categoryInPieChart.setVisibility(View.GONE);
        relationInPieChart.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.GONE);
        relationLayout.setVisibility(View.GONE);
        categoryLayout2.setVisibility(View.GONE);
        relationLayout2.setVisibility(View.GONE);
        wholeLayout.setVisibility(View.GONE);

        final CharSequence[] categoryItems = {" 나간 돈 ", " 받은 돈 "};
        int [] color={Color.rgb(255,0,0), Color.rgb(255,165,0), Color.rgb(34,139,34),
                Color.rgb(255,255,0), Color.rgb(0,191,255), Color.rgb(127,255,212),
                Color.rgb(160,32,240)};
        categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
                builder.setTitle("경조사별 통계")
                        .setIcon(R.drawable.ic_insert_chart_black_24dp)
                        .setItems(categoryItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        relationText.setText(" 관계별 통계 ");
                                        categoryText.setText(" 경조사별" + categoryItems[0]);
                                        relationPieChart.setVisibility(View.GONE);
                                        relationInPieChart.setVisibility(View.GONE);
                                        categoryInPieChart.setVisibility(View.GONE);
                                        relationLayout.setVisibility(View.GONE);
                                        relationLayout2.setVisibility(View.GONE);
                                        wholeLayout.setVisibility(View.GONE);
                                        if (out > 0) {
                                            wholeLayout.setVisibility(View.VISIBLE);
                                            categoryPieChart.setVisibility(View.VISIBLE);
                                            categoryLayout.setVisibility(View.VISIBLE);
                                            categoryLayout2.setVisibility(View.VISIBLE);
                                        }
                                        if (categoryMarry.getParent() != null) {
                                            ((ViewGroup) categoryMarry.getParent()).removeView(categoryMarry);
                                        }
                                        if (categoryFirstBirth.getParent() != null) {
                                            ((ViewGroup) categoryFirstBirth.getParent()).removeView(categoryFirstBirth);
                                        }
                                        if (categoryFuneral.getParent() != null) {
                                            ((ViewGroup) categoryFuneral.getParent()).removeView(categoryFuneral);
                                        }
                                        if (categorySixty.getParent() != null) {
                                            ((ViewGroup) categorySixty.getParent()).removeView(categorySixty);
                                        }
                                        if (categorySeventy.getParent() != null) {
                                            ((ViewGroup) categorySeventy.getParent()).removeView(categorySeventy);
                                        }
                                        if (categoryBirthday.getParent() != null) {
                                            ((ViewGroup) categoryBirthday.getParent()).removeView(categoryBirthday);
                                        }
                                        if (categoryETC.getParent() != null) {
                                            ((ViewGroup) categoryETC.getParent()).removeView(categoryETC);
                                        }


                                        ArrayList<PieEntry> pieItems = new ArrayList<PieEntry>();
                                        if (database.getCategoryMarryOutMoney() != 0) {
                                            pieItems.add(new PieEntry((float) database.getCategoryMarryOutMoney(), "결혼식"));
                                            categoryLayout.addView(categoryMarry);
                                            categoryMarry.setText("결혼식 : " + formatter.format(database.getCategoryMarryOutMoney()) + "원");
                                        }
                                        if (database.getCategoryFirstBirthOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getCategoryFirstBirthOutMoney(), "돌잔치"));
                                            categoryLayout.addView(categoryFirstBirth);
                                            categoryFirstBirth.setText("돌잔치 : " + formatter.format(database.getCategoryFirstBirthOutMoney()) + "원");
                                        }
                                        if (database.getCategoryFuneralOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getCategoryFuneralOutMoney(), "장례식"));
                                            categoryLayout.addView(categoryFuneral);
                                            categoryFuneral.setText("장례식 : " + formatter.format(database.getCategoryFuneralOutMoney()) + "원");
                                        }
                                        if (database.getCategorySixtyOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getCategorySixtyOutMoney(), "환갑"));
                                            categoryLayout.addView(categorySixty);
                                            categorySixty.setText("환갑 : " + formatter.format(database.getCategorySixtyOutMoney()) + "원");
                                        }
                                        if (database.getCategorySeventyOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getCategorySeventyOutMoney(), "칠순"));
                                            categoryLayout2.addView(categorySeventy);
                                            categorySeventy.setText("칠순 : " + formatter.format(database.getCategorySeventyOutMoney()) + "원");
                                        }
                                        if (database.getCategoryBirthdayOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getCategoryBirthdayOutMoney(), "생일"));
                                            categoryLayout2.addView(categoryBirthday);
                                            categoryBirthday.setText("생일 : " + formatter.format(database.getCategoryBirthdayOutMoney()) + "원");
                                        }
                                        if (database.getCategoryETCOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getCategoryETCOutMoney(), "기타"));
                                            categoryLayout2.addView(categoryETC);
                                            categoryETC.setText("기타 : " + formatter.format(database.getCategoryETCOutMoney()) + "원");
                                        }
                                        PieDataSet dataset = new PieDataSet(pieItems, "Category");
                                        PieData data = new PieData(dataset);
                                        dataset.setValueTextSize(20);
                                        dataset.setColors(color);
                                        categoryPieChart.setUsePercentValues(true);
                                        categoryPieChart.setHoleRadius(25);
                                        categoryPieChart.setTransparentCircleRadius(15);
                                        categoryPieChart.setEntryLabelColor(Color.BLACK);
                                        categoryPieChart.getLegend().setEnabled(false);
                                        categoryPieChart.setCenterText("단위 : %");
                                        categoryPieChart.setCenterTextSize(15);
                                        categoryPieChart.setData(data);
                                        categoryPieChart.invalidate(); //refresh
                                        break;
                                    case 1:
                                        relationText.setText(" 관계별 통계 ");
                                        categoryText.setText(" 경조사별" + categoryItems[1]);
                                        relationPieChart.setVisibility(View.GONE);
                                        relationInPieChart.setVisibility(View.GONE);
                                        categoryPieChart.setVisibility(View.GONE);
                                        relationLayout.setVisibility(View.GONE);
                                        relationLayout2.setVisibility(View.GONE);
                                        wholeLayout.setVisibility(View.GONE);
                                        if (in > 0) {
                                            wholeLayout.setVisibility(View.VISIBLE);
                                            categoryInPieChart.setVisibility(View.VISIBLE);
                                            categoryLayout.setVisibility(View.VISIBLE);
                                            categoryLayout2.setVisibility(View.VISIBLE);
                                        }
                                        if (categoryMarry.getParent() != null) {
                                            ((ViewGroup) categoryMarry.getParent()).removeView(categoryMarry);
                                        }
                                        if (categoryFirstBirth.getParent() != null) {
                                            ((ViewGroup) categoryFirstBirth.getParent()).removeView(categoryFirstBirth);
                                        }
                                        if (categoryFuneral.getParent() != null) {
                                            ((ViewGroup) categoryFuneral.getParent()).removeView(categoryFuneral);
                                        }
                                        if (categorySixty.getParent() != null) {
                                            ((ViewGroup) categorySixty.getParent()).removeView(categorySixty);
                                        }
                                        if (categorySeventy.getParent() != null) {
                                            ((ViewGroup) categorySeventy.getParent()).removeView(categorySeventy);
                                        }
                                        if (categoryBirthday.getParent() != null) {
                                            ((ViewGroup) categoryBirthday.getParent()).removeView(categoryBirthday);
                                        }
                                        if (categoryETC.getParent() != null) {
                                            ((ViewGroup) categoryETC.getParent()).removeView(categoryETC);
                                        }

                                        ArrayList<PieEntry> pieItems2 = new ArrayList<PieEntry>();
                                        if (database.getCategoryMarryInMoney() != 0) {
                                            pieItems2.add(new PieEntry((float) database.getCategoryMarryInMoney(), "결혼식"));
                                            categoryLayout.addView(categoryMarry);
                                            categoryMarry.setText("결혼식 : " + formatter.format(database.getCategoryMarryInMoney()) + "원");
                                        }
                                        if (database.getCategoryFirstBirthInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getCategoryFirstBirthInMoney(), "돌잔치"));
                                            categoryLayout.addView(categoryFirstBirth);
                                            categoryFirstBirth.setText("돌잔치 : " + formatter.format(database.getCategoryFirstBirthInMoney()) + "원");
                                        }
                                        if (database.getCategoryFuneralInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getCategoryFuneralInMoney(), "장례식"));
                                            categoryLayout.addView(categoryFuneral);
                                            categoryFuneral.setText("장례식 : " + formatter.format(database.getCategoryFuneralInMoney()) + "원");
                                        }
                                        if (database.getCategorySixtyInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getCategorySixtyInMoney(), "환갑"));
                                            categoryLayout.addView(categorySixty);
                                            categorySixty.setText("환갑 : " + formatter.format(database.getCategorySixtyInMoney()) + "원");
                                        }
                                        if (database.getCategorySeventyInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getCategorySeventyInMoney(), "칠순"));
                                            categoryLayout2.addView(categorySeventy);
                                            categorySeventy.setText("칠순 : " + formatter.format(database.getCategorySeventyInMoney()) + "원");
                                        }
                                        if (database.getCategoryBirthdayInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getCategoryBirthdayInMoney(), "생일"));
                                            categoryLayout2.addView(categoryBirthday);
                                            categoryBirthday.setText("생일 : " + formatter.format(database.getCategoryBirthdayInMoney()) + "원");
                                        }
                                        if (database.getCategoryETCInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getCategoryETCInMoney(), "기타"));
                                            categoryLayout2.addView(categoryETC);
                                            categoryETC.setText("기타 : " + formatter.format(database.getCategoryETCInMoney()) + "원");
                                        }
                                        PieDataSet dataset2 = new PieDataSet(pieItems2, "Category");
                                        PieData data2 = new PieData(dataset2);
                                        dataset2.setValueTextSize(20);
                                        dataset2.setColors(color);
                                        categoryInPieChart.setUsePercentValues(true);
                                        categoryInPieChart.setHoleRadius(25);
                                        categoryInPieChart.setTransparentCircleRadius(15);
                                        categoryInPieChart.setEntryLabelColor(Color.BLACK);
                                        categoryInPieChart.getLegend().setEnabled(false);
                                        categoryInPieChart.setCenterText("단위 : %");
                                        categoryInPieChart.setCenterTextSize(15);
                                        categoryInPieChart.setData(data2);
                                        categoryInPieChart.invalidate(); //refresh
                                        break;
                                    default:
                                }
                            }
                        });
                builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        final CharSequence[] realtionItems = {" 나간 돈 ", " 받은 돈 "};
        relationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
                builder.setTitle("관계별 통계")
                        .setIcon(R.drawable.ic_insert_chart_black_24dp)
                        .setItems(realtionItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        categoryText.setText(" 경조사별 통계 ");
                                        relationText.setText(" 관계별" + realtionItems[0]);
                                        categoryPieChart.setVisibility(View.GONE);
                                        categoryInPieChart.setVisibility(View.GONE);
                                        relationInPieChart.setVisibility(View.GONE);
                                        categoryLayout.setVisibility(View.GONE);
                                        categoryLayout2.setVisibility(View.GONE);
                                        wholeLayout.setVisibility(View.GONE);
                                        if (out > 0) {
                                            wholeLayout.setVisibility(View.VISIBLE);
                                            relationPieChart.setVisibility(View.VISIBLE);
                                            relationLayout.setVisibility(View.VISIBLE);
                                            relationLayout2.setVisibility(View.VISIBLE);
                                        }
                                        if (relationFriend.getParent() != null) {
                                            ((ViewGroup) relationFriend.getParent()).removeView(relationFriend);
                                        }
                                        if (relationRelative.getParent() != null) {
                                            ((ViewGroup) relationRelative.getParent()).removeView(relationRelative);
                                        }
                                        if (relationJob.getParent() != null) {
                                            ((ViewGroup) relationJob.getParent()).removeView(relationJob);
                                        }
                                        if (relationUniversity.getParent() != null) {
                                            ((ViewGroup) relationUniversity.getParent()).removeView(relationUniversity);
                                        }
                                        if (relationFamily.getParent() != null) {
                                            ((ViewGroup) relationFamily.getParent()).removeView(relationFamily);
                                        }
                                        if (relationKnowing.getParent() != null) {
                                            ((ViewGroup) relationKnowing.getParent()).removeView(relationKnowing);
                                        }
                                        if (relationETC.getParent() != null) {
                                            ((ViewGroup) relationETC.getParent()).removeView(relationETC);
                                        }

                                        ArrayList<PieEntry> pieItems = new ArrayList<PieEntry>();
                                        if (database.getRelationFriendOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationFriendOutMoney(), "친구"));
                                            relationLayout.addView(relationFriend);
                                            relationFriend.setText("친구 : " + formatter.format(database.getRelationFriendOutMoney()) + "원");
                                        }
                                        if (database.getRelationRelativeOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationRelativeOutMoney(), "친척"));
                                            relationLayout.addView(relationRelative);
                                            relationRelative.setText("친척 : " + formatter.format(database.getRelationRelativeOutMoney()) + "원");
                                        }
                                        if (database.getRelationJobOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationJobOutMoney(), "회사"));
                                            relationLayout.addView(relationJob);
                                            relationJob.setText("회사 : " + formatter.format(database.getRelationJobOutMoney()) + "원");
                                        }
                                        if (database.getRelationUniversityOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationUniversityOutMoney(), "대학교"));
                                            relationLayout.addView(relationUniversity);
                                            relationUniversity.setText("대학교 : " + formatter.format(database.getRelationUniversityOutMoney()) + "원");
                                        }
                                        if (database.getRelationFamilyOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationFamilyOutMoney(), "가족"));
                                            relationLayout2.addView(relationFamily);
                                            relationFamily.setText("가족 : " + formatter.format(database.getRelationFamilyOutMoney()) + "원");
                                        }
                                        if (database.getRelationKnowingOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationKnowingOutMoney(), "지인"));
                                            relationLayout2.addView(relationKnowing);
                                            relationKnowing.setText("지인 : " + formatter.format(database.getRelationKnowingOutMoney()) + "원");
                                        }
                                        if (database.getRelationETCOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationETCOutMoney(), "기타"));
                                            relationLayout2.addView(relationETC);
                                            relationETC.setText("기타 : " + formatter.format(database.getRelationETCOutMoney()) + "원");
                                        }
                                        PieDataSet dataset = new PieDataSet(pieItems, "Category");
                                        PieData data = new PieData(dataset);
                                        dataset.setValueTextSize(20);
                                        dataset.setColors(color);
                                        relationPieChart.setUsePercentValues(true);
                                        relationPieChart.setHoleRadius(25);
                                        relationPieChart.setTransparentCircleRadius(15);
                                        relationPieChart.setEntryLabelColor(Color.BLACK);
                                        relationPieChart.getLegend().setEnabled(false);
                                        relationPieChart.setCenterText("단위 : %");
                                        relationPieChart.setCenterTextSize(15);
                                        relationPieChart.setData(data);
                                        relationPieChart.invalidate();
                                        break;
                                    case 1:
                                        categoryText.setText(" 경조사별 통계 ");
                                        relationText.setText(" 관계별" + realtionItems[1]);
                                        categoryPieChart.setVisibility(View.GONE);
                                        categoryInPieChart.setVisibility(View.GONE);
                                        relationPieChart.setVisibility(View.GONE);
                                        categoryLayout.setVisibility(View.GONE);
                                        categoryLayout2.setVisibility(View.GONE);
                                        wholeLayout.setVisibility(View.GONE);
                                        if (in > 0) {
                                            wholeLayout.setVisibility(View.VISIBLE);
                                            relationInPieChart.setVisibility(View.VISIBLE);
                                            relationLayout.setVisibility(View.VISIBLE);
                                            relationLayout2.setVisibility(View.VISIBLE);
                                        }
                                        if (relationFriend.getParent() != null) {
                                            ((ViewGroup) relationFriend.getParent()).removeView(relationFriend);
                                        }
                                        if (relationRelative.getParent() != null) {
                                            ((ViewGroup) relationRelative.getParent()).removeView(relationRelative);
                                        }
                                        if (relationJob.getParent() != null) {
                                            ((ViewGroup) relationJob.getParent()).removeView(relationJob);
                                        }
                                        if (relationUniversity.getParent() != null) {
                                            ((ViewGroup) relationUniversity.getParent()).removeView(relationUniversity);
                                        }
                                        if (relationFamily.getParent() != null) {
                                            ((ViewGroup) relationFamily.getParent()).removeView(relationFamily);
                                        }
                                        if (relationKnowing.getParent() != null) {
                                            ((ViewGroup) relationKnowing.getParent()).removeView(relationKnowing);
                                        }
                                        if (relationETC.getParent() != null) {
                                            ((ViewGroup) relationETC.getParent()).removeView(relationETC);
                                        }

                                        ArrayList<PieEntry> pieItems2 = new ArrayList<PieEntry>();
                                        if (database.getRelationFriendInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationFriendInMoney(), "친구"));
                                            relationLayout.addView(relationFriend);
                                            relationFriend.setText("친구 : " + formatter.format(database.getRelationFriendInMoney()) + "원");
                                        }
                                        if (database.getRelationRelativeInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationRelativeInMoney(), "친척"));
                                            relationLayout.addView(relationRelative);
                                            relationRelative.setText("친척 : " + formatter.format(database.getRelationRelativeInMoney()) + "원");
                                        }
                                        if (database.getRelationJobInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationJobInMoney(), "회사"));
                                            relationLayout.addView(relationJob);
                                            relationJob.setText("회사 : " + formatter.format(database.getRelationJobInMoney()) + "원");
                                        }
                                        if (database.getRelationUniversityInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationUniversityInMoney(), "대학교"));
                                            relationLayout.addView(relationUniversity);
                                            relationUniversity.setText("대학교 : " + formatter.format(database.getRelationUniversityInMoney()) + "원");
                                        }
                                        if (database.getRelationFamilyInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationFamilyInMoney(), "가족"));
                                            relationLayout2.addView(relationFamily);
                                            relationFamily.setText("가족 : " + formatter.format(database.getRelationFamilyInMoney()) + "원");
                                        }
                                        if (database.getRelationKnowingInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationKnowingInMoney(), "지인"));
                                            relationLayout2.addView(relationKnowing);
                                            relationKnowing.setText("지인 : " + formatter.format(database.getRelationKnowingInMoney()) + "원");
                                        }
                                        if (database.getRelationETCInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationETCInMoney(), "기타"));
                                            relationLayout2.addView(relationETC);
                                            relationETC.setText("기타 : " + formatter.format(database.getRelationETCInMoney()) + "원");
                                        }
                                        PieDataSet dataset2 = new PieDataSet(pieItems2, "Category");
                                        PieData data2 = new PieData(dataset2);
                                        dataset2.setValueTextSize(20);
                                        dataset2.setColors(color);
                                        relationInPieChart.setUsePercentValues(true);
                                        relationInPieChart.setHoleRadius(25);
                                        relationInPieChart.setTransparentCircleRadius(15);
                                        relationInPieChart.setEntryLabelColor(Color.BLACK);
                                        relationInPieChart.getLegend().setEnabled(false);
                                        relationInPieChart.setCenterText("단위 : %");
                                        relationInPieChart.setCenterTextSize(15);
                                        relationInPieChart.setData(data2);
                                        relationInPieChart.invalidate();
                                        break;
                                    default:
                                }
                            }
                        });
                builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return v;
    }
}