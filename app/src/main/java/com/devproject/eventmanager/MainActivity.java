package com.devproject.eventmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MAINACTIVITY";
    private TextView getCalendarData, getNameData, getCategoryData, getRelationData, getMoneyData;
    private Button inputButton, outputButton, compareButton, settingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final LinearLayout mainFrame = (LinearLayout) findViewById(R.id.mainFrame);
        final RelativeLayout main = (RelativeLayout) findViewById(R.id.main);
        getCalendarData = (TextView) findViewById(R.id.getCalendarData);
        getNameData = (TextView) findViewById(R.id.getNameData);
        getCategoryData = (TextView) findViewById(R.id.getCategoryData);
        getRelationData = (TextView) findViewById(R.id.getRelationData);
        getMoneyData = (TextView) findViewById(R.id.getMoneyData);

        inputButton = (Button) findViewById(R.id.inputButton);
        outputButton = (Button) findViewById(R.id.outputButton);
        compareButton = (Button) findViewById(R.id.compareButton);
        settingButton = (Button) findViewById(R.id.settingButton);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("name");
        final String date = intent.getStringExtra("date");
        final String category = intent.getStringExtra("category");
        final String relation = intent.getStringExtra("relation");
        final String money = intent.getStringExtra("money");

        mainFrame.setVisibility(View.GONE);


//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        InputFragment inputFragment = new InputFragment();
//        final Bundle bundle = new Bundle();
//        bundle.putString("name", name);
//        bundle.putString("date", date);
//        bundle.putString("category", category);
//        bundle.putString("relation", relation);
//        bundle.putString("money", money);
//        inputFragment.setArguments(bundle);
//        transaction.add(R.id.main, inputFragment);
//        transaction.commit();

        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                InputFragment inputFragment = new InputFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("date", date);
                bundle.putString("category", category);
                bundle.putString("relation", relation);
                bundle.putString("money", money);
                inputFragment.setArguments(bundle);
                setTitle("인풋");
                transaction.replace(R.id.main, inputFragment);
                transaction.commit();
            }
        });

        outputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                OutputFragment outputFragment = new OutputFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("date", date);
                bundle.putString("category", category);
                bundle.putString("relation", relation);
                bundle.putString("money", money);
                outputFragment.setArguments(bundle);
                setTitle("아웃풋");
                transaction.replace(R.id.main, outputFragment);
                transaction.commit();
            }
        });

        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                CompareFragment compareFragment = new CompareFragment();
                setTitle("비교");
                transaction.replace(R.id.main, compareFragment);
                transaction.commit();
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                SettingFragment settingFragment = new SettingFragment();
                setTitle("세팅");
                transaction.replace(R.id.main, settingFragment);
                transaction.commit();
            }
        });
    }
}