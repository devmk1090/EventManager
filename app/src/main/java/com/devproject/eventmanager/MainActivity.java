package com.devproject.eventmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINACTIVITY";
    private FragmentPagerAdapter fragmentPagerAdapter;
    private AdView adView;
    private DrawerLayout drawerLayout;
    private static final int MY_PERMISSION_STORAGE = 100;
    InOutDatabase database;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        database = InOutDatabase.getInstance(this);
        boolean isOpen = database.open();
        if(isOpen) {
            Log.d(TAG, "Book database is open");
        } else {
            Log.d(TAG, "Book database is not open");
        }

        relativeLayout = (RelativeLayout) findViewById(R.id.main_Relative);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(false);
                Intent intent = null;
                switch(item.getItemId()) {
                    case R.id.nav_excelWrite:
                        checkPermission();
                        break;
                    case R.id.nav_evaluation:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.devproject.eventmanager"));
                        startActivity(intent);
                        break;
                    case R.id.nav_app_movie:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.devkproject.movieinfo3"));
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //Set ViewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        fragmentPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        boolean In = intent.getBooleanExtra("In", false);
        if(In == true)
        {
            viewPager.setCurrentItem(1);
        }
    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림")
                    .setMessage("죵료하시겠습니까 ?")
                    .setIcon(R.drawable.ic_info_black_24dp)
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SpannableString spannableString = new SpannableString("도움말");
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.white)), 0, spannableString.length(), 0);
        menu.getItem(0).setTitle(spannableString);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, 3);
                builder.setTitle("도움말");
                builder.setIcon(R.drawable.ic_help_black_24dp);
                builder.setMessage("# '나간 돈'  '받은 돈'  탭 오른쪽 하단에 있는 파란 십자 아이콘을 터치하면 내역을 입력할 수 있습니다.\n\n" +
                        "# 등록된 내역을 터치하면 '수정' '삭제' 할 수 있습니다.\n\n" +
                        "# '설정' 탭의 '엑셀 파일 만들기' 버튼을 터치하면 등록된 내역을 엑셀 파일로 만들어 보관할 수 있습니다.\n\n" +
                        "# '엑셀 파일 만들기'를 눌러도 파일이 만들어지지 않는다면 '앱 정보'에 들어가서 '저장 권한'을 허용해야 합니다 \n");

                builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                android.app.AlertDialog dialog = builder.create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }
    public void checkPermission(){
        int externalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(externalStorage == PackageManager.PERMISSION_GRANTED) { //외부 저장소 퍼미션을 가지고 있는지 체크
            saveExcel();
        } else { //퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) { //사용자가 퍼미션 거부를 한 적이 있는 경우
                Snackbar.make(relativeLayout, "엑셀 파일을 저장하려면 외부 저장소 접근 권한이 필요합니다.", //사용자에게 퍼미션이 필요한 이유 설명
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //퍼미션 요청
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
                    }
                }).show();
            } else { //퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청이 바로 이루어짐
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }
    public void saveExcel(){
        Workbook wb = new HSSFWorkbook();
        Sheet sheet1 = wb.createSheet("나간 돈");
        Sheet sheet2 = wb.createSheet("받은 돈");

        Row row = sheet1.createRow(0);
        Row row2 = sheet2.createRow(0);

        Cell cell;

        cell = row.createCell(0);
        cell.setCellValue("이름");
        cell = row.createCell(1);
        cell.setCellValue("날짜");
        cell = row.createCell(2);
        cell.setCellValue("경조사");
        cell = row.createCell(3);
        cell.setCellValue("관계");
        cell = row.createCell(4);
        cell.setCellValue("금액");
        cell = row.createCell(5);
        cell.setCellValue("메모");

        cell = row2.createCell(0);
        cell.setCellValue("이름");
        cell = row2.createCell(1);
        cell.setCellValue("날짜");
        cell = row2.createCell(2);
        cell.setCellValue("경조사");
        cell = row2.createCell(3);
        cell.setCellValue("관계");
        cell = row2.createCell(4);
        cell.setCellValue("금액");
        cell = row2.createCell(5);
        cell.setCellValue("메모장");

        Cursor cursor = database.rawQuery("SELECT * FROM " + database.TABLE_OUT_INFO);
        for(int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            row = sheet1.createRow(i+1);

            cell = row.createCell(0);
            cell.setCellValue(cursor.getString(1));
            cell = row.createCell(1);
            cell.setCellValue(cursor.getString(2));
            cell = row.createCell(2);
            cell.setCellValue(cursor.getString(3));
            cell = row.createCell(3);
            cell.setCellValue(cursor.getString(4));
            cell = row.createCell(4);
            cell.setCellValue(cursor.getString(5));
            cell = row.createCell(5);
            cell.setCellValue(cursor.getString(6));
        }
        Cursor cursor2 = database.rawQuery("SELECT * FROM " + database.TABLE_IN_INFO);
        for(int i = 0; i < cursor2.getCount(); i++) {
            cursor2.moveToNext();
            row2 = sheet2.createRow(i+1);

            cell = row2.createCell(0);
            cell.setCellValue(cursor2.getString(1));
            cell = row2.createCell(1);
            cell.setCellValue(cursor2.getString(2));
            cell = row2.createCell(2);
            cell.setCellValue(cursor2.getString(3));
            cell = row2.createCell(3);
            cell.setCellValue(cursor2.getString(4));
            cell = row2.createCell(4);
            cell.setCellValue(cursor2.getString(5));
            cell = row2.createCell(5);
            cell.setCellValue(cursor2.getString(6));
        }

        String folderName = "/Download";
        String fileName = "경조사 엑셀 데이터.xls";
        File excelFile;

        if(!isExternalStorageWritable()) return;

        File filePath = new File(Environment.getExternalStorageDirectory() + folderName);

        excelFile = new File(filePath, fileName);
        if(excelFile.exists()){
            excelFile.delete();
        }
        try {
            FileOutputStream os = new FileOutputStream(excelFile);
            wb.write(os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "다운로드 폴더에 저장되었습니다", Toast.LENGTH_SHORT).show();
    }
}