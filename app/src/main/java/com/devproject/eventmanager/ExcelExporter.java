package com.devproject.eventmanager;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/** 모든 내역을 공용 Download 폴더에 .xls로 저장한다. 시트 구성(나간 돈·받은 돈)은 예전과 같다. */
final class ExcelExporter {

    private static final String TAG = "ExcelExporter";
    private static final String FILE_NAME = "경조사 엑셀 데이터.xls";
    private static final String[] HEADERS = {"이름", "날짜", "경조사", "관계", "금액", "메모"};

    private ExcelExporter() {}

    static void confirmAndExport(Context context) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.export_confirm_title)
                .setMessage(R.string.export_confirm_message)
                .setPositiveButton(R.string.action_export_confirm, (dialog, which) -> {
                    boolean saved = export(context);
                    Toast.makeText(context, saved ? R.string.export_done : R.string.export_failed, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    /**
     * 저장에 성공하면 true. 공용 폴더에 java.io.File로 쓰므로 Android 11(API 30) 이상에서만 권한 없이 된다.
     * 실패하면 false를 돌려주고 호출한 쪽이 실패 토스트를 띄운다.
     */
    static boolean export(Context context) {
        InOutDatabase database = InOutDatabase.getInstance(context);
        database.open();
        Workbook workbook = new HSSFWorkbook();
        try {
            fillSheet(workbook.createSheet("나간 돈"), database, InOutDatabase.TABLE_OUT_INFO);
            fillSheet(workbook.createSheet("받은 돈"), database, InOutDatabase.TABLE_IN_INFO);

            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloads.exists()) {
                downloads.mkdirs();
            }
            File file = new File(downloads, FILE_NAME);
            if (file.exists()) {
                file.delete();
            }
            try (FileOutputStream out = new FileOutputStream(file)) {
                workbook.write(out);
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "엑셀 파일 저장 실패", e);
            return false;
        } finally {
            try {
                workbook.close();
            } catch (IOException ignored) {
                // 닫기 실패는 저장 결과와 무관하다
            }
        }
    }

    private static void fillSheet(Sheet sheet, InOutDatabase database, String table) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            header.createCell(i).setCellValue(HEADERS[i]);
        }
        Cursor cursor = database.rawQuery("SELECT NAME, DATE, CATEGORY, RELATION, MONEY, MEMO FROM " + table);
        if (cursor == null) {
            return;
        }
        try {
            int rowIndex = 1;
            while (cursor.moveToNext()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(text(cursor, 0));
                row.createCell(1).setCellValue(text(cursor, 1));
                row.createCell(2).setCellValue(text(cursor, 2).trim());
                row.createCell(3).setCellValue(text(cursor, 3).trim());
                row.createCell(4).setCellValue((double) MoneyFormat.parse(text(cursor, 4)));
                row.createCell(5).setCellValue(text(cursor, 5));
            }
        } finally {
            cursor.close();
        }
    }

    private static String text(Cursor cursor, int index) {
        String value = cursor.getString(index);
        return value == null ? "" : value;
    }
}
