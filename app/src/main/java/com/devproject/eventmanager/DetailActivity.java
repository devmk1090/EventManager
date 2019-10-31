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

import com.google.android.material.snackbar.Snackbar;

public class DetailActivity extends AppCompatActivity {

    private String TAG = "DetailActivity";
    private TextView calendarData, categoryData, relationData;
    private Button calendarButton, tenButton, fiftyButton, hundredButton, resetButton, reviseButton, deleteButton;
    private Spinner categorySpinner, relationSpinner;
    private EditText moneyData, nameData;
    private int moneyTotal;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //RECEIVE DATA
        Intent i = getIntent();

        final String name = i.getExtras().getString("NAME");
        final String date = i.getExtras().getString("DATE");
        final String category = i.getExtras().getString("CATEGORY");
        final String relation = i.getExtras().getString("RELATION");
        final String money = i.getExtras().getString("MONEY");
        final int id = i.getExtras().getInt("ID");

        nameData.setText(name);
        calendarData.setText(date);
        categoryData.setText(category);
        relationData.setText(relation);
        moneyData.setText(money);

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

        reviseButton = (Button) findViewById(R.id.reviseButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);

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
        reviseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(id, nameData.getText().toString(), calendarData.getText().toString(),
                        categoryData.getText().toString(), relationData.getText().toString(), moneyData.getText().toString());
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(id);
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

    private void update(int id, String newName, String newData, String newCategory, String newRelation, String newMoney){
        DBAdapter db = new DBAdapter(this);
        db.openDB();
        long result = db.UPDATE(id, newName, newData, newCategory, newRelation, newMoney);

        if(result > 0)
        {
            nameData.setText(newName);
            calendarData.setText(newData);
            categoryData.setText(newCategory);
            relationData.setText(newRelation);
            moneyData.setText(newMoney);
            Snackbar.make(nameData, "Updated Successfully", Snackbar.LENGTH_SHORT).show();
        }else
        {
            Snackbar.make(nameData, "Unable to Update", Snackbar.LENGTH_SHORT).show();
        }

        db.close();
    }

    //DELETE
    private void delete(int id){
        DBAdapter db = new DBAdapter(this);
        db.openDB();
        long result = db.Delete(id);

        if(result > 0)
        {
            this.finish();
        }else
        {
            Snackbar.make(nameData, "Unable to Update", Snackbar.LENGTH_SHORT).show();
        }

        db.close();
    }
}
