package com.devproject.eventmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class AddActivity extends AppCompatActivity {

    private String TAG = "AddActivity";
    private TextView calendarData, categoryData, relationData;
    private Button calendarButton, tenButton, fiftyButton, hundredButton, resetButton, saveButton;
    private Spinner categorySpinner, relationSpinner;
    private EditText moneyData, nameData;
    private int moneyTotal;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    ArrayList<AddList> addLists = new ArrayList<>();
    MyAdapter adapter;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

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
                 save(name, date, category, relation, money);
                 Intent intent = new Intent(AddActivity.this, MainActivity.class);
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

    private void save(String name, String date, String category, String relation, String money){
        DBAdapter db = new DBAdapter(this);

        //OPEN
        db.openDB();

        //INSERT
        long result = db.add(name, date, category, relation, money);

        if(result > 0)
        {
            nameData.setText("");
            calendarData.setText("");
            categoryData.setText("");
            relationData.setText("");
            moneyData.setText("");
        } else
        {
            Snackbar.make(nameData, "Unable To Insert", Snackbar.LENGTH_SHORT).show();
        }

        //CLOSE
        db.close();

        //REFRESH
    }



}