package com.devproject.eventmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINACTIVITY";
    private TextView getCalendarData, getNameData, getCategoryData, getRelationData, getMoneyData;
    private Button inputButton, outputButton, compareButton, settingButton;
    AddDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        // open database
//        if(database != null) {
//            database.close();
//            database = null;
//        }
//        database = AddDatabase.getInstance(this);
//        boolean isOpen = database.open();
//        if(isOpen) {
//            Log.d(TAG, "Book database is open");
//        } else {
//            Log.d(TAG, "Book database is not open");
//        }

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
                transaction.replace(R.id.main, inputFragment);
                transaction.commit();
            }
        });

        outputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                OutputFragment outputFragment = new OutputFragment();
                transaction.replace(R.id.main, outputFragment);
                transaction.commit();
            }
        });

        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                CompareFragment compareFragment = new CompareFragment();
                transaction.replace(R.id.main, compareFragment);
                transaction.commit();
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                SettingFragment settingFragment = new SettingFragment();
                transaction.replace(R.id.main, settingFragment);
                transaction.commit();
            }
        });
    }

    // close database
    protected void onDestroy(){
        if (database != null) {
            database.close();
            database = null;
        }
        super.onDestroy();
    }
//    @Override
//    public ArrayList<AddList> selectAll() {
//        ArrayList<AddList> result = database.selectAll();
//        return result;
//    }
//    @Override
//    public void deleteData (int id) {
//
//    }
}
