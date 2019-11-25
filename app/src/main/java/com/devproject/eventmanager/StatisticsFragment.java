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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    PieChart categoryPieChart, relationPieChart, categoryInPieChart, relationInPieChart;
    TextView categoryText, relationText, outTotalMoney, inTotalMoney, inOutTotal;
    LinearLayout categoryLayout, relatioonLayout;
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
        categoryLayout = (LinearLayout) v.findViewById(R.id.categoryLayout);
        TextView categoryMarry = new TextView(getActivity());
        categoryMarry.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView categoryFirstBirth = new TextView(getActivity());
        categoryFirstBirth.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView categoryFuneral = new TextView(getActivity());
        categoryFuneral.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView categorySixty = new TextView(getActivity());
        categorySixty.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView categoryBirthday = new TextView(getActivity());
        categoryBirthday.setTextAppearance(getContext(), R.style.CustomTextView);

        TextView categoryETC = new TextView(getActivity());
        categoryETC.setTextAppearance(getContext(), R.style.CustomTextView);


        relatioonLayout = (LinearLayout) v.findViewById(R.id.relationLayout);
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

        outTotalMoney.setText(String.valueOf(database.getAllMoneyOut()));
        inTotalMoney.setText(String.valueOf(database.getAllMoneyIn()));
        int i = Integer.parseInt(outTotalMoney.getText().toString());
        int j = Integer.parseInt(inTotalMoney.getText().toString());
        int total = i - j;
        inOutTotal.setText("" + total);

        categoryPieChart.setVisibility(View.GONE);
        relationPieChart.setVisibility(View.GONE);
        categoryInPieChart.setVisibility(View.GONE);
        relationInPieChart.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.GONE);
        relatioonLayout.setVisibility(View.GONE);

        final CharSequence[] categoryItems = {" 나간 돈 ", " 받은 돈 "};

        categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("경조사별 통계")
                        .setItems(categoryItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        relationText.setText(" 관계별 통계 ");
                                        categoryText.setText(" 경조사별" + categoryItems[0]);
                                        relationPieChart.setVisibility(View.GONE);
                                        relationInPieChart.setVisibility(View.GONE);
                                        relatioonLayout.setVisibility(View.GONE);
                                        categoryInPieChart.setVisibility(View.GONE);
                                        categoryPieChart.setVisibility(View.VISIBLE);
                                        categoryLayout.setVisibility(View.VISIBLE);

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
                                        if (categoryBirthday.getParent() != null) {
                                            ((ViewGroup) categoryBirthday.getParent()).removeView(categoryBirthday);
                                        }
                                        if (categoryETC.getParent() != null) {
                                            ((ViewGroup) categoryETC.getParent()).removeView(categoryETC);
                                        }

                                        categoryPieChart.setUsePercentValues(true);
                                        categoryPieChart.setHoleRadius(25);
                                        categoryPieChart.setTransparentCircleRadius(15);
                                        categoryPieChart.setEntryLabelColor(Color.BLACK);
                                        categoryPieChart.getLegend().setEnabled(false);
                                        categoryPieChart.setCenterText("단위 : %");
                                        categoryPieChart.setCenterTextSize(15);

                                        ArrayList<PieEntry> pieItems = new ArrayList<PieEntry>();
                                        if (database.getCategoryMarryOutMoney() != 0) {
                                            pieItems.add(new PieEntry((float) database.getCategoryMarryOutMoney(), "결혼식"));
                                            categoryLayout.addView(categoryMarry);
                                            categoryMarry.setText("결혼식 : " + String.valueOf(database.getCategoryMarryOutMoney()) + "원");
                                        }
                                        if (database.getCategoryFirstBirthOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getCategoryFirstBirthOutMoney(), "돌잔치"));
                                            categoryLayout.addView(categoryFirstBirth);
                                            categoryFirstBirth.setText("돌잔치 : " + String.valueOf(database.getCategoryFirstBirthOutMoney()) + "원");
                                        }
                                        if (database.getCategoryFuneralOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getCategoryFuneralOutMoney(), "장례식"));
                                            categoryLayout.addView(categoryFuneral);
                                            categoryFuneral.setText("장례식 : " + String.valueOf(database.getCategoryFuneralOutMoney()) + "원");
                                        }
                                        if (database.getCategorySixtyOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getCategorySixtyOutMoney(), "환갑"));
                                            categoryLayout.addView(categorySixty);
                                            categorySixty.setText("환갑 : " + String.valueOf(database.getCategorySixtyOutMoney()) + "원");
                                        }
                                        if (database.getCategoryBirthdayOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getCategoryBirthdayOutMoney(), "생일"));
                                            categoryLayout.addView(categoryBirthday);
                                            categoryBirthday.setText("생일 : " + String.valueOf(database.getCategoryBirthdayOutMoney()) + "원");
                                        }
                                        if (database.getCategoryETCOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getCategoryETCOutMoney(), "기타"));
                                            categoryLayout.addView(categoryETC);
                                            categoryETC.setText("기타 : " + String.valueOf(database.getCategoryETCOutMoney()) + "원");
                                        }
                                        PieDataSet dataset = new PieDataSet(pieItems, "Category");
                                        PieData data = new PieData(dataset);
                                        dataset.setValueFormatter(new PercentFormatter());

                                        dataset.setValueTextSize(20);
                                        dataset.setColors(ColorTemplate.JOYFUL_COLORS);
                                        categoryPieChart.setData(data);
                                        categoryPieChart.invalidate(); //refresh
                                        break;
                                    case 1:
                                        relationText.setText(" 관계별 통계 ");
                                        categoryText.setText(" 경조사별" + categoryItems[1]);
                                        relationPieChart.setVisibility(View.GONE);
                                        relationInPieChart.setVisibility(View.GONE);
                                        relatioonLayout.setVisibility(View.GONE);
                                        categoryPieChart.setVisibility(View.GONE);
                                        categoryInPieChart.setVisibility(View.VISIBLE);
                                        categoryLayout.setVisibility(View.VISIBLE);

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
                                        if (categoryBirthday.getParent() != null) {
                                            ((ViewGroup) categoryBirthday.getParent()).removeView(categoryBirthday);
                                        }
                                        if (categoryETC.getParent() != null) {
                                            ((ViewGroup) categoryETC.getParent()).removeView(categoryETC);
                                        }
                                        categoryInPieChart.setUsePercentValues(true);
                                        categoryInPieChart.setHoleRadius(25);
                                        categoryInPieChart.setTransparentCircleRadius(15);
                                        categoryInPieChart.setEntryLabelColor(Color.BLACK);
                                        categoryInPieChart.getLegend().setEnabled(false);
                                        categoryInPieChart.setCenterText("단위 : %");
                                        categoryInPieChart.setCenterTextSize(15);

                                        ArrayList<PieEntry> pieItems2 = new ArrayList<PieEntry>();
                                        if (database.getCategoryMarryInMoney() != 0) {
                                            pieItems2.add(new PieEntry((float) database.getCategoryMarryInMoney(), "결혼식"));
                                            categoryLayout.addView(categoryMarry);
                                            categoryMarry.setText("결혼식 : " + String.valueOf(database.getCategoryMarryInMoney()) + "원");
                                        }
                                        if (database.getCategoryFirstBirthInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getCategoryFirstBirthInMoney(), "돌잔치"));
                                            categoryLayout.addView(categoryFirstBirth);
                                            categoryFirstBirth.setText("돌잔치 : " + String.valueOf(database.getCategoryFirstBirthInMoney()) + "원");
                                        }
                                        if (database.getCategoryFuneralInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getCategoryFuneralInMoney(), "장례식"));
                                            categoryLayout.addView(categoryFuneral);
                                            categoryFuneral.setText("장례식 : " + String.valueOf(database.getCategoryFuneralInMoney()) + "원");
                                        }
                                        if (database.getCategorySixtyInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getCategorySixtyInMoney(), "환갑"));
                                            categoryLayout.addView(categorySixty);
                                            categorySixty.setText("환갑 : " + String.valueOf(database.getCategorySixtyInMoney()) + "원");
                                        }
                                        if (database.getCategoryBirthdayInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getCategoryBirthdayInMoney(), "생일"));
                                            categoryLayout.addView(categoryBirthday);
                                            categoryBirthday.setText("생일 : " + String.valueOf(database.getCategoryBirthdayInMoney()) + "원");
                                        }
                                        if (database.getCategoryETCInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getCategoryETCInMoney(), "기타"));
                                            categoryLayout.addView(categoryETC);
                                            categoryETC.setText("기타 : " + String.valueOf(database.getCategoryETCInMoney()) + "원");
                                        }
                                        PieDataSet dataset2 = new PieDataSet(pieItems2, "Category");
                                        PieData data2 = new PieData(dataset2);

                                        dataset2.setValueTextSize(20);
                                        dataset2.setColors(ColorTemplate.JOYFUL_COLORS);
                                        categoryInPieChart.setData(data2);
                                        categoryInPieChart.invalidate(); //refresh
                                        break;
                                        default:
                                }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("관계별 통계")
                        .setItems(realtionItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        categoryText.setText(" 경조사별 통계 ");
                                        relationText.setText(" 관계별" + realtionItems[0]);
                                        categoryPieChart.setVisibility(View.GONE);
                                        categoryInPieChart.setVisibility(View.GONE);
                                        categoryLayout.setVisibility(View.GONE);
                                        relationInPieChart.setVisibility(View.GONE);
                                        relationPieChart.setVisibility(View.VISIBLE);
                                        relatioonLayout.setVisibility(View.VISIBLE);

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

                                        relationPieChart.setUsePercentValues(true);
                                        relationPieChart.setHoleRadius(25);
                                        relationPieChart.setTransparentCircleRadius(15);
                                        relationPieChart.setEntryLabelColor(Color.BLACK);
                                        relationPieChart.getLegend().setEnabled(false);
                                        relationPieChart.setCenterText("단위 : %");
                                        relationPieChart.setCenterTextSize(15);

                                        ArrayList<PieEntry> pieItems = new ArrayList<PieEntry>();
                                        if (database.getRelationFriendOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationFriendOutMoney(), "친구"));
                                            relatioonLayout.addView(relationFriend);
                                            relationFriend.setText("친구 : " + String.valueOf(database.getRelationFriendOutMoney()) + "원");
                                        }
                                        if (database.getRelationRelativeOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationRelativeOutMoney(), "친척"));
                                            relatioonLayout.addView(relationRelative);
                                            relationRelative.setText("친척 : " + String.valueOf(database.getRelationRelativeOutMoney()) + "원");
                                        }
                                        if (database.getRelationJobOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationJobOutMoney(), "직장동료"));
                                            relatioonLayout.addView(relationJob);
                                            relationJob.setText("직장동료 : " + String.valueOf(database.getRelationJobOutMoney()) + "원");
                                        }
                                        if (database.getRelationUniversityOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationUniversityOutMoney(), "대학교"));
                                            relatioonLayout.addView(relationUniversity);
                                            relationUniversity.setText("대학교 : " + String.valueOf(database.getRelationUniversityOutMoney()) + "원");
                                        }
                                        if (database.getRelationFamilyOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationFamilyOutMoney(), "가족"));
                                            relatioonLayout.addView(relationFamily);
                                            relationFamily.setText("가족 : " + String.valueOf(database.getRelationFamilyOutMoney()) + "원");
                                        }
                                        if (database.getRelationKnowingOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationKnowingOutMoney(), "지인"));
                                            relatioonLayout.addView(relationKnowing);
                                            relationKnowing.setText("지인 : " + String.valueOf(database.getRelationKnowingOutMoney()) + "원");
                                        }
                                        if (database.getRelationETCOutMoney() != 0) {
                                            pieItems.add(new PieEntry(database.getRelationETCOutMoney(), "기타"));
                                            relatioonLayout.addView(relationETC);
                                            relationETC.setText("기타 : " + String.valueOf(database.getRelationETCOutMoney()) + "원");
                                        }
                                        PieDataSet dataset = new PieDataSet(pieItems, "Category");
                                        PieData data = new PieData(dataset);
                                        data.setValueFormatter(new PercentFormatter());

                                        dataset.setValueTextSize(20);
                                        dataset.setColors(ColorTemplate.JOYFUL_COLORS);
                                        relationPieChart.setData(data);
                                        relationPieChart.invalidate();
                                        break;
                                    case 1:
                                        categoryText.setText(" 경조사별 통계 ");
                                        relationText.setText(" 관계별" + realtionItems[1]);
                                        categoryPieChart.setVisibility(View.GONE);
                                        categoryInPieChart.setVisibility(View.GONE);
                                        categoryLayout.setVisibility(View.GONE);
                                        relationPieChart.setVisibility(View.GONE);
                                        relationInPieChart.setVisibility(View.VISIBLE);
                                        relatioonLayout.setVisibility(View.VISIBLE);

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

                                        relationInPieChart.setUsePercentValues(true);
                                        relationInPieChart.setHoleRadius(25);
                                        relationInPieChart.setTransparentCircleRadius(15);
                                        relationInPieChart.setEntryLabelColor(Color.BLACK);
                                        relationInPieChart.getLegend().setEnabled(false);
                                        relationInPieChart.setCenterText("단위 : %");
                                        relationInPieChart.setCenterTextSize(15);

                                        ArrayList<PieEntry> pieItems2 = new ArrayList<PieEntry>();
                                        if (database.getRelationFriendInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationFriendInMoney(), "친구"));
                                            relatioonLayout.addView(relationFriend);
                                            relationFriend.setText("친구 : " + String.valueOf(database.getRelationFriendInMoney()) + "원");
                                        }
                                        if (database.getRelationRelativeInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationRelativeInMoney(), "친척"));
                                            relatioonLayout.addView(relationRelative);
                                            relationRelative.setText("친척 : " + String.valueOf(database.getRelationRelativeInMoney()) + "원");
                                        }
                                        if (database.getRelationJobInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationJobInMoney(), "직장동료"));
                                            relatioonLayout.addView(relationJob);
                                            relationJob.setText("직장동료 : " + String.valueOf(database.getRelationJobInMoney()) + "원");
                                        }
                                        if (database.getRelationUniversityInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationUniversityInMoney(), "대학교"));
                                            relatioonLayout.addView(relationUniversity);
                                            relationUniversity.setText("대학교 : " + String.valueOf(database.getRelationUniversityInMoney()) + "원");
                                        }
                                        if (database.getRelationFamilyInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationFamilyInMoney(), "가족"));
                                            relatioonLayout.addView(relationFamily);
                                            relationFamily.setText("가족 : " + String.valueOf(database.getRelationFamilyInMoney()) + "원");
                                        }
                                        if (database.getRelationKnowingInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationKnowingInMoney(), "지인"));
                                            relatioonLayout.addView(relationKnowing);
                                            relationKnowing.setText("지인 : " + String.valueOf(database.getRelationKnowingInMoney()) + "원");
                                        }
                                        if (database.getRelationETCInMoney() != 0) {
                                            pieItems2.add(new PieEntry(database.getRelationETCInMoney(), "기타"));
                                            relatioonLayout.addView(relationETC);
                                            relationETC.setText("기타 : " + String.valueOf(database.getRelationETCInMoney()) + "원");
                                        }
                                        PieDataSet dataset2 = new PieDataSet(pieItems2, "Category");
                                        PieData data2 = new PieData(dataset2);

                                        dataset2.setValueTextSize(20);
                                        dataset2.setColors(ColorTemplate.JOYFUL_COLORS);
                                        relationInPieChart.setData(data2);
                                        relationInPieChart.invalidate();
                                        break;
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