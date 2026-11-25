package com.devproject.eventmanager;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 모든 내역을 엑셀(.xls)로 저장한다. 시트 구성(나간 돈·받은 돈)과 칸은 예전과 같다.
 * 저장 위치는 설정에서 시스템 파일 창(SAF)으로 사용자가 고르므로, 저장소 권한 없이 모든 Android 버전에서 된다.
 */
final class ExcelExporter {

    private static final String TAG = "ExcelExporter";
    static final String MIME_TYPE = "application/vnd.ms-excel";
    private static final String[] HEADERS = {"이름", "날짜", "경조사", "관계", "금액", "메모"};

    private static final Executor IO = Executors.newSingleThreadExecutor();

    private ExcelExporter() {}

    /** 기본 파일 이름: "경조사 내역 2026-11-26.xls" */
    static String fileName(Calendar day) {
        return String.format(Locale.US, "경조사 내역 %04d-%02d-%02d.xls",
                day.get(Calendar.YEAR), day.get(Calendar.MONTH) + 1, day.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 모든 내역을 target에 저장한다. DB는 다른 화면처럼 메인 스레드에서 읽고, 엑셀 만들기와 쓰기는 백그라운드에서 한다.
     * 결과는 메인 스레드로 돌려준다.
     */
    static void export(Context context, Uri target, DatabaseBackup.Callback<Boolean> callback) {
        Context app = context.getApplicationContext();
        InOutDatabase database = InOutDatabase.getInstance(app);
        database.open();
        List<Entry> entries = database.selectAll();
        IO.execute(() -> {
            boolean saved = false;
            try (Workbook workbook = new HSSFWorkbook();
                 OutputStream out = DatabaseBackup.openForWrite(app, target)) {
                fillSheet(workbook.createSheet("나간 돈"), entries, Entry.Direction.SENT);
                fillSheet(workbook.createSheet("받은 돈"), entries, Entry.Direction.RECEIVED);
                workbook.write(out);
                saved = true;
            } catch (IOException | RuntimeException e) {
                Log.e(TAG, "엑셀 파일 저장 실패", e);
            }
            boolean result = saved;
            DatabaseBackup.postToMain(() -> callback.done(result));
        });
    }

    /** 저장된 순서대로 한 줄씩. 경조사·관계는 앞뒤 공백을 빼고, 금액은 숫자 칸으로 쓴다. */
    private static void fillSheet(Sheet sheet, List<Entry> entries, Entry.Direction direction) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            header.createCell(i).setCellValue(HEADERS[i]);
        }
        int rowIndex = 1;
        for (Entry entry : entries) {
            if (entry.direction != direction) {
                continue;
            }
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(entry.name);
            row.createCell(1).setCellValue(entry.date);
            row.createCell(2).setCellValue(entry.categoryLabel());
            row.createCell(3).setCellValue(entry.relationLabel());
            row.createCell(4).setCellValue((double) entry.amount());
            row.createCell(5).setCellValue(entry.memo);
        }
    }
}
