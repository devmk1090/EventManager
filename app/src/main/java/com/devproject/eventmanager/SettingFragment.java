package com.devproject.eventmanager;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
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


public class SettingFragment extends Fragment{

    Button excelButton;
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
                cell.setCellValue("메모장이다");

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
            }
        });
        return v;
    }
}
