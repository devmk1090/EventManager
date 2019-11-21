package com.devproject.eventmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InActivity extends AppCompatActivity {

    private String TAG = "InActivity";
    private TextView calendarData, categoryData, relationData;
    private Button calendarButton, tenButton, fiftyButton, hundredButton, resetButton, saveButton;
    private EditText moneyData, nameData, memoData;
    private int moneyTotal;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    InOutDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inout);

        database = InOutDatabase.getInstance(this);
        boolean isOpen = database.open();
        if(isOpen) {
            Log.d(TAG, "Book database is open");
        } else {
            Log.d(TAG, "Book database is not open");
        }

        calendarButton = (Button) findViewById(R.id.calendarButton);
        calendarData = (TextView) findViewById(R.id.calendarData);

        nameData = (EditText) findViewById(R.id.nameData);
        categoryData = (TextView) findViewById(R.id.categoryData);
        relationData = (TextView) findViewById(R.id.relationData);

        moneyData = (EditText) findViewById(R.id.moneyData);
        tenButton = (Button) findViewById(R.id.tenButton);
        fiftyButton = (Button) findViewById(R.id.fiftyButton);
        hundredButton = (Button) findViewById(R.id.hundredButton);
        resetButton = (Button) findViewById(R.id.resetButton);

        memoData = (EditText) findViewById(R.id.memoData);
        saveButton = (Button) findViewById(R.id.saveButton);

        this.calendarListener();

        final CharSequence[] categoryItems = {" 결혼식 ", " 돌잔치 ", " 장례식 ", " 기타 "};
        categoryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InActivity.this);
                builder.setTitle("경조사를 선택하세요")
                        .setItems(categoryItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        categoryData.setText(categoryItems[0]);
                                        break;
                                    case 1:
                                        categoryData.setText(categoryItems[1]);
                                        break;
                                    case 2:
                                        categoryData.setText(categoryItems[2]);
                                        break;
                                    case 3:
                                        categoryData.setText(categoryItems[3]);
                                        break;
                                    default:
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        final CharSequence[] relationItems = {" 동네친구 ", " 친척 ", " 직장동료 ", " 대학교 ", " 초중고 ", " 가족 ", " 기타 "};
        relationData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InActivity.this);
                builder.setTitle("관계를 선택하세요")
                        .setItems(relationItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        relationData.setText(relationItems[0]);
                                        break;
                                    case 1:
                                        relationData.setText(relationItems[1]);
                                        break;
                                    case 2:
                                        relationData.setText(relationItems[2]);
                                        break;
                                    case 3:
                                        relationData.setText(relationItems[3]);
                                        break;
                                    case 4:
                                        relationData.setText(relationItems[4]);
                                        break;
                                    case 5:
                                        relationData.setText(relationItems[5]);
                                        break;
                                    case 6:
                                        relationData.setText(relationItems[6]);
                                        break;
                                    default:
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
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
                final String memo = memoData.getText().toString();
                database.insertRecordIn(name, date, category, relation, money, memo);
                Intent intent = new Intent(InActivity.this, MainActivity.class);
                intent.putExtra("In", true);
                startActivity(intent);
            }
        });
    }
    public void calendarListener() {
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendarData.setText(year + "/" + (month +1) + "/" + dayOfMonth);
            }
        };
    }
    public void OnClickHandler(View view){
        DatePickerDialog dialog = new DatePickerDialog(this, dateSetListener, 2019, 12, 01);
        dialog.show();
    }
}