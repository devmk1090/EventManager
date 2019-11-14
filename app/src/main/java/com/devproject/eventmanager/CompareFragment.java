package com.devproject.eventmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CompareFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    InOutDatabase database;
    String TAG = "OutFragment";


    public CompareFragment() {}

    public static CompareFragment newInstance(String param1, String param2) {
        CompareFragment fragment = new CompareFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


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

        final TextView categoryText = (TextView) v.findViewById(R.id.categoryText);
        final TextView outTotalMoney = (TextView) v.findViewById(R.id.outTotalMoney);
        final TextView inTotalMoney = (TextView) v.findViewById(R.id.inTotalMoney);
        final TextView inOutTotal = (TextView) v.findViewById(R.id.inOutTotal);

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
                                        categoryText.setText(items[1]);
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

