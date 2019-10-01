package com.devproject.eventmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SubActivity extends AppCompatActivity {

    private String TAG = "SubtractActivity";
    private TextView calendarData, categoryData, relationData;
    private Button calendarButton, tenButton, fiftyButton, hundredButton, resetButton, saveButton;
    private Spinner categorySpinner, relationSpinner;
    private EditText moneyData, nameData;
    private int moneyTotal;
    private DatePickerDialog.OnDateSetListener dateSetListener;


    static boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        setTitle("Add output money");

        calendarButton = (Button) findViewById(R.id.calendarButton);
        calendarData = (TextView) findViewById(R.id.calendarData);

        nameData = (EditText) findViewById(R.id.nameData);

        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        categoryData =(TextView) findViewById(R.id.categoryData);

        relationSpinner = (Spinner) findViewById(R.id.relationSpinner);
        relationData =(TextView) findViewById(R.id.relationData);

        moneyData = (EditText) findViewById(R.id.moneyData);
        tenButton = (Button) findViewById(R.id.tenButton);
        fiftyButton = (Button) findViewById(R.id.fiftyButton);
        hundredButton = (Button) findViewById(R.id.hundredButton);
        resetButton = (Button) findViewById(R.id.resetButton);

        saveButton = (Button) findViewById(R.id.saveButton);

        Intent incomingIntent = getIntent();
        String date = incomingIntent.getStringExtra("date");
        calendarData.setText(date);

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
                running = true;
                Intent intent = new Intent(SubActivity.this, MainActivity.class);
                intent.putExtra("name", nameData.getText().toString());
                intent.putExtra("date", calendarData.getText().toString());
                intent.putExtra("category", categoryData.getText().toString());
                intent.putExtra("relation", relationData.getText().toString());
                intent.putExtra("money", moneyData.getText().toString());
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
}

