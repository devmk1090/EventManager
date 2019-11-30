package com.devproject.eventmanager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SettingFragment extends Fragment{

    Button helpButton, noticeButton, excelButton, linkButton;
    LinearLayout settingLayout;
    InOutDatabase database;
    private static final int MY_PERMISSION_STORAGE = 100;

    public SettingFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);

        database = InOutDatabase.getInstance(getActivity());
        boolean isOpen = database.open();
        if(isOpen) {
            Log.d(TAG, "Book database is open");
        } else {
            Log.d(TAG, "Book database is not open");
        }
        settingLayout = (LinearLayout) v.findViewById(R.id.settingLayout);
        excelButton = (Button) v.findViewById(R.id.excelButton);
        helpButton = (Button) v.findViewById(R.id.helpButton);
        linkButton = (Button) v.findViewById(R.id.linkButton);
        noticeButton = (Button) v.findViewById(R.id.noticeButton);

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
                builder.setTitle("도움말");
                builder.setIcon(R.drawable.ic_help_black_24dp);
                builder.setMessage("# '나간 돈'  '받은 돈'  탭 오른쪽 아래에 있는 파란 십자 아이콘을 터치하면 정보를 입력할 수 있습니다.\n\n" +
                        "# 등록된 정보를 터치하면 '수정' '삭제' 할 수 있습니다.\n\n" +
                        "# '설정' 탭의 '엑셀 파일 만들기' 버튼을 터치하면 등록된 정보를 엑셀 파일로 만들어 보관할 수 있습니다.\n\n" +
                        "# '엑셀 파일 만들기'를 눌러도 파일이 만들어지지 않는다면 '앱 정보'에 들어가서 '저장권한'을 허용해야 합니다 \n");

                builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        noticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
                builder.setTitle("도움말");
                builder.setIcon(R.drawable.ic_help_black_24dp);
                builder.setMessage("# 19/12/01 version 1.0.0 : 앱 출시\n\n" );

                builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        excelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/"));
                startActivity(intent);
            }
        });
        return v;
    }
    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }
    public void checkPermission2(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
    public void checkPermission(){
        int externalStorage = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(externalStorage == PackageManager.PERMISSION_GRANTED) { //외부 저장소 퍼미션을 가지고 있는지 체크
            saveExcel();
        } else { //퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) { //사용자가 퍼미션 거부를 한 적이 있는 경우
                Snackbar.make(settingLayout, "엑셀 파일을 저장하려면 외부 저장소 접근 권한이 필요합니다.", //사용자에게 퍼미션이 필요한 이유 설명
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //퍼미션 요청
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
                    }
                }).show();
            } else { //퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청이 바로 이루어짐
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_STORAGE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
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

        String folderName = "/간편한 경조사 관리";
        String fileName = "경조사 엑셀 데이터.xls";
        File excelFile;

        if(!isExternalStorageWritable()) return;

        File filePath = new File(Environment.getExternalStorageDirectory() + folderName);
        if(!filePath.exists())
            filePath.mkdirs();
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
        Toast.makeText(getActivity(), "'간편한 경조사 관리'폴더에 저장되었습니다", Toast.LENGTH_SHORT).show();
    }
}
