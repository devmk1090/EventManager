package com.devproject.eventmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


public class SubActivity extends AppCompatActivity {

    private String TAG = "AddActivity";
    private TextView calendarData, categoryData, relationData;
    private Button calendarButton, tenButton, fiftyButton, hundredButton, resetButton, saveButton;
    private Spinner categorySpinner, relationSpinner;
    private EditText moneyData, nameData;
    private int moneyTotal;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    SubDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // open database
        if(database != null) {
            database.close();
            database = null;
        }

        database = SubDatabase.getInstance(this);
        boolean isOpen = database.open();
        if(isOpen) {
            Log.d(TAG, "Book database is open");
        } else {
            Log.d(TAG, "Book database is not open");
        }

        calendarButton = (Button) findViewById(R.id.calendarButton);
        calendarData = (TextView) findViewById(R.id.calendarData);

        nameData = (EditText) findViewById(R.id.nameData);

        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        categoryData = (TextView) findViewById(R.id.categoryData);

        relationSpinner = (Spinner) findViewById(R.id.relationSpinner);
        relationData = (TextView) findViewById(R.id.relationData);

        moneyData = (EditText) findViewById(R.id.moneyData);
        tenButton = (Button) findViewById(R.id.tenButton);
        fiftyButton = (Button) findViewById(R.id.fiftyButton);
        hundredButton = (Button) findViewById(R.id.hundredButton);
        resetButton = (Button) findViewById(R.id.resetButton);

        saveButton = (Button) findViewById(R.id.saveButton);

        this.calendarListener();

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryData.setText("" + parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        relationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relationData.setText("" + parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyTotal += 10000;
                moneyData.setText(moneyTotal + "원");
            }
        });

        fiftyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyTotal += 50000;
                moneyData.setText(moneyTotal + "원");
            }
        });

        hundredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyTotal += 100000;
                moneyData.setText(moneyTotal + "원");
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyTotal = 0;
                moneyData.setText(moneyTotal + "원");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nameData.getText().toString();
                final String date = calendarData.getText().toString();
                final String category = categoryData.getText().toString();
                final String relation = relationData.getText().toString();
                final String money = moneyData.getText().toString();
                database.insertRecord(name, date, category, relation, money);
                Intent intent = new Intent(SubActivity.this, MainActivity.class);
                intent.putExtra("Out", true);
                startActivity(intent);
            }
        });
    }
    public void calendarListener() {
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendarData.setText(year + "/" + month + "/" + dayOfMonth);
            }
        };
    }
    public void OnClickHandler(View view){
        DatePickerDialog dialog = new DatePickerDialog(this, dateSetListener, 2019, 10, 01);
        dialog.show();
    }
    // close database
    protected void onDestroy(){
        if (database != null) {
            database.close();
            database = null;
        }
        super.onDestroy();
    }
}