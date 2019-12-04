package com.devproject.eventmanager;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class OutDetailActivity extends AppCompatActivity {

    private String TAG = "OutDetailActivity";
    private TextView calendarData, categoryData, relationData;
    private Button calendarButton, tenButton, fiftyButton, hundredButton, resetButton, saveButton;
    private EditText moneyData, nameData, memoData;
    private int moneyTotal;
    private DatePickerDialog datePickerDialog;
    Calendar calendar;
    int year, month, dayOfMonth;
    String dayOfWeek;
    InOutDatabase database;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inout);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        database = InOutDatabase.getInstance(this);
        boolean isOpen = database.open();
        if (isOpen) {
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

        Intent intent = getIntent();

        final int id = intent.getExtras().getInt("ID");
        final String name = intent.getExtras().getString("NAME");
        final String date = intent.getExtras().getString("DATE");
        final String category = intent.getExtras().getString("CATEGORY");
        final String relation = intent.getExtras().getString("RELATION");
        final String money = intent.getExtras().getString("MONEY");
        final String memo = intent.getExtras().getString("MEMO");

        nameData.setText(name);
        calendarData.setText(date);
        categoryData.setText(category);
        relationData.setText(relation);
        moneyData.setText(money);
        memoData.setText(memo);

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year =calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);


                datePickerDialog = new DatePickerDialog(OutDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
                        Date date = new Date(year, month, dayOfMonth-1);
                        dayOfWeek = simpleDateFormat.format(date);
                        calendarData.setText(year + "/" + (month + 1) + "/" + dayOfMonth + "/" + dayOfWeek);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        final CharSequence[] categoryItems = {" 결혼식 ", " 돌잔치 ", " 장례식 ", " 환갑 ", " 칠순 ", " 생일 ", " 기타 "};
        categoryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OutDetailActivity.this, 3);
                builder.setTitle("경조사를 선택하세요")
                        .setIcon(R.drawable.ic_insert_drive_file_black_24dp)
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
                                    case 4:
                                        categoryData.setText(categoryItems[4]);
                                        break;
                                    case 5:
                                        categoryData.setText(categoryItems[5]);
                                        break;
                                    case 6:
                                        categoryData.setText(categoryItems[6]);
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

        final CharSequence[] relationItems = {" 친구 ", " 친척 ", " 회사 ", " 대학교 ", " 가족 ", " 지인 ", " 기타 "};
        relationData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OutDetailActivity.this, 3);
                builder.setTitle("관계를 선택하세요")
                        .setIcon(R.drawable.ic_insert_drive_file_black_24dp)
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
                builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
                moneyData.setText(String.valueOf(moneyTotal));
            }
        });
        fiftyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyTotal += 50000;
                moneyData.setText(String.valueOf(moneyTotal));
            }
        });
        hundredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyTotal += 100000;
                moneyData.setText(String.valueOf(moneyTotal));
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moneyTotal = 0;
                moneyData.setText(String.valueOf(moneyTotal));
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameData.getText().toString();
                String date = calendarData.getText().toString();
                String category = categoryData.getText().toString();
                String relation = relationData.getText().toString();
                String money = moneyData.getText().toString();
                String memo = memoData.getText().toString();
                if (name.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OutDetailActivity.this, 3);
                    builder.setTitle("알림")
                            .setIcon(R.drawable.ic_info_black_24dp)
                            .setMessage("이름을 입력해주세요")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (category.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OutDetailActivity.this, 3);
                    builder.setTitle("알림")
                            .setIcon(R.drawable.ic_info_black_24dp)
                            .setMessage("경조사를 선택해주세요")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (relation.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OutDetailActivity.this, 3);
                    builder.setTitle("알림")
                            .setIcon(R.drawable.ic_info_black_24dp)
                            .setMessage("관계를 선택해주세요")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (money.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(OutDetailActivity.this, 3);
                    builder.setTitle("알림")
                            .setIcon(R.drawable.ic_info_black_24dp)
                            .setMessage("금액을 입력해주세요")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "나간 돈 내역이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    database.updateOut(id, name, date, category, relation, money, memo);
                    Intent intent = new Intent(OutDetailActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
