package com.devproject.eventmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.github.mikephil.charting.charts.Chart.LOG_TAG;


public class SettingFragment extends Fragment{

    Button helpButton, excelButton, linkButton;
    InOutDatabase database;

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

        excelButton = (Button) v.findViewById(R.id.excelButton);
        helpButton = (Button) v.findViewById(R.id.helpButton);
        linkButton = (Button) v.findViewById(R.id.linkButton);

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
                builder.setTitle("도움말");
                builder.setIcon(R.drawable.ic_help_black_24dp);
                builder.setMessage("# '나간 돈'  '받은 돈'  탭 오른쪽 아래에 있는 파란 십자 아이콘을 터치하면 정보를 입력할 수 있습니다.\n\n" +
                        "# 등록된 정보를 터치하면 '수정' '삭제' 할 수 있습니다.\n\n" +
                        "# '설정' 탭의 '엑셀 파일 만들기' 버튼을 터치하면 등록된 정보를 엑셀 파일로 만들어 보관할 수 있습니다.\n\n" +
                        "# 앱을 삭제하면 모든 정보가 날아갑니다.\n");

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
//                if(!isExternalStorageWritable() || isExternalStorageAvailable()) {
//                    Log.w("FileUtils", "Storage not available or read only");
//                    return ;
//                }
//                boolean success = false;
                String fileName = "test1.xls";
                FileOutputStream os;
                try
                {
                    os = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
                    wb.write(os);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), fileName + " 저장되었습니다", Toast.LENGTH_SHORT).show();
//                File file = new File(getContext().getExternalFilesDir(null), "excelTest.xls");
//                FileOutputStream os = null;
//                try {
//                    os =new FileOutputStream(file);
//                    wb.write(os);
//                    Log.w("FileUtils", "Writing file" + file);
//                } catch (IOException e){
//                    Log.w("FileUtils", "Error writing" + file, e);
//                } catch (Exception e) {
//                    Log.w("FuleUtils", "Failed to save file", e);
//                } finally {
//                    try {
//                        if(null != os)
//                            os.close();
//                    } catch (Exception ex) {
//
//                    }
//                }
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
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    public File getExcelFileStorageDir(String excelFile) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), excelFile);
        if(!file.mkdir()){
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }
}
