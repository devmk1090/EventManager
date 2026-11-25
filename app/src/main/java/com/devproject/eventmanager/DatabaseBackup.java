package com.devproject.eventmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 백업·복원. DB 파일(InOut.db)을 통째로 백업 파일로 저장하고, 고른 백업 파일로 DB 파일을 바꾼다.
 *
 * <p>저장하거나 불러올 위치는 사용자가 시스템 파일 창(SAF)에서 고른다. Google 드라이브도 그 창에 나오므로
 * 앱에는 로그인이나 저장소 권한이 필요 없다. 백업 파일 이름은 사용자가 바꿔도 되고, 불러올 때 내용으로 검사한다.
 *
 * <p>스레드: 백업 파일 읽기·쓰기(드라이브는 내려받기가 걸릴 수 있다)는 백그라운드에서 하고,
 * DB 연결을 닫고 파일을 복사·교체하는 일은 메인 스레드에서 한다. 앱의 DB 읽기·쓰기가 모두 메인 스레드라
 * 그 사이에 끼어들 수 없다.
 */
final class DatabaseBackup {

    private static final String TAG = "DatabaseBackup";

    /** 백업 파일 MIME. 저장소가 확장자를 바꾸지 않도록 일반 바이너리로 둔다. */
    static final String MIME_TYPE = "application/octet-stream";

    private static final byte[] SQLITE_HEADER = "SQLite format 3\u0000".getBytes(StandardCharsets.US_ASCII);
    private static final String[] TABLES = {InOutDatabase.TABLE_IN_INFO, InOutDatabase.TABLE_OUT_INFO};
    private static final String[] REQUIRED_COLUMNS = {"_ID", "NAME", "DATE", "CATEGORY", "RELATION", "MONEY", "MEMO"};

    private static final Executor IO = Executors.newSingleThreadExecutor();

    /** 불러올 수 없는 까닭 */
    enum Problem { NOT_BACKUP, NEWER_VERSION, DAMAGED, READ_FAILED }

    /** 백업 파일 내용 요약. 불러오기 확인 창에 보여 준다. */
    static final class Summary {
        final long received;
        final long sent;
        /** 날짜가 있는 내역 중 가장 최근 날짜. 없으면 null */
        final EntryDate latest;

        Summary(long received, long sent, EntryDate latest) {
            this.received = received;
            this.sent = sent;
            this.latest = latest;
        }
    }

    /** 고른 파일의 검사 결과. summary와 problem 중 하나만 있다. */
    static final class Inspection {
        /** 앱 캐시에 복사해 둔 백업 파일 */
        final File candidate;
        final Summary summary;
        final Problem problem;

        Inspection(File candidate, Summary summary, Problem problem) {
            this.candidate = candidate;
            this.summary = summary;
            this.problem = problem;
        }

        /** 불러오지 않기로 했을 때 캐시의 복사본을 지운다 */
        void discard() {
            candidate.delete();
        }
    }

    interface Callback<T> {
        void done(T result);
    }

    private DatabaseBackup() {}

    /** 기본 백업 파일 이름: "경조사 백업 2026-11-25.db" */
    static String fileName(Calendar day) {
        return String.format(Locale.US, "경조사 백업 %04d-%02d-%02d.db",
                day.get(Calendar.YEAR), day.get(Calendar.MONTH) + 1, day.get(Calendar.DAY_OF_MONTH));
    }

    /** 파일 앞부분이 SQLite DB 머리글인지 */
    static boolean hasSqliteHeader(byte[] head) {
        if (head == null || head.length < SQLITE_HEADER.length) {
            return false;
        }
        for (int i = 0; i < SQLITE_HEADER.length; i++) {
            if (head[i] != SQLITE_HEADER[i]) {
                return false;
            }
        }
        return true;
    }

    /** 저장된 DATE 문자열 중 가장 최근 날짜. 비었거나 읽을 수 없는 값은 건너뛴다. */
    static EntryDate latest(Iterable<String> storedDates) {
        EntryDate latest = null;
        for (String stored : storedDates) {
            EntryDate date = EntryDate.parse(stored);
            if (date != null && (latest == null || date.dayKey() > latest.dayKey())) {
                latest = date;
            }
        }
        return latest;
    }

    // ---- 백업 ----

    /** 지금 DB를 target에 저장한다. 메인 스레드에서 부르고, 결과도 메인 스레드로 돌려준다. */
    static void save(Context context, Uri target, Callback<Boolean> callback) {
        Context app = context.getApplicationContext();
        File snapshot = new File(app.getCacheDir(), "backup-snapshot.db");
        try {
            InOutDatabase.getInstance(app).withClosedConnection(dbFile -> copy(dbFile, snapshot));
        } catch (IOException e) {
            Log.e(TAG, "백업할 DB를 복사하지 못함", e);
            snapshot.delete();
            callback.done(false);
            return;
        }
        IO.execute(() -> {
            boolean saved = false;
            try (InputStream in = new FileInputStream(snapshot);
                 OutputStream out = openForWrite(app, target)) {
                copy(in, out);
                saved = true;
            } catch (IOException | RuntimeException e) {
                Log.e(TAG, "백업 파일 저장 실패", e);
            } finally {
                snapshot.delete();
            }
            boolean result = saved;
            postToMain(() -> callback.done(result));
        });
    }

    private static OutputStream openForWrite(Context context, Uri uri) throws IOException {
        OutputStream out;
        try {
            out = context.getContentResolver().openOutputStream(uri, "wt");
        } catch (FileNotFoundException | IllegalArgumentException e) {
            // "wt"(덮어쓰기)를 받지 않는 저장소가 있다. 방금 만든 파일이라 "w"로도 충분하다.
            out = context.getContentResolver().openOutputStream(uri, "w");
        }
        if (out == null) {
            throw new IOException("저장 위치를 열 수 없음: " + uri);
        }
        return out;
    }

    // ---- 불러오기 ----

    /** 고른 파일을 앱 캐시로 복사해 검사한다. 메인 스레드에서 부르고, 결과도 메인 스레드로 돌려준다. */
    static void inspect(Context context, Uri source, Callback<Inspection> callback) {
        Context app = context.getApplicationContext();
        IO.execute(() -> {
            File candidate = new File(app.getCacheDir(), "restore-candidate.db");
            Inspection result;
            try (InputStream in = app.getContentResolver().openInputStream(source)) {
                if (in == null) {
                    throw new IOException("파일을 열 수 없음: " + source);
                }
                try (OutputStream out = new FileOutputStream(candidate)) {
                    copy(in, out);
                }
                result = inspectFile(candidate);
            } catch (IOException | RuntimeException e) {
                Log.e(TAG, "백업 파일 읽기 실패", e);
                result = new Inspection(candidate, null, Problem.READ_FAILED);
            }
            if (result.problem != null) {
                result.discard();
            }
            Inspection done = result;
            postToMain(() -> callback.done(done));
        });
    }

    private static Inspection inspectFile(File file) {
        byte[] head = new byte[SQLITE_HEADER.length];
        try (InputStream in = new FileInputStream(file)) {
            if (readFully(in, head) < head.length || !hasSqliteHeader(head)) {
                return new Inspection(file, null, Problem.NOT_BACKUP);
            }
        } catch (IOException e) {
            return new Inspection(file, null, Problem.READ_FAILED);
        }
        SQLiteDatabase db;
        try {
            db = SQLiteDatabase.openDatabase(file.getPath(), null,
                    SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        } catch (SQLiteException e) {
            return new Inspection(file, null, Problem.DAMAGED);
        }
        try {
            int version = db.getVersion();
            if (version > InOutDatabase.DATABASE_VERSION) {
                return new Inspection(file, null, Problem.NEWER_VERSION);
            }
            if (version < 1 || !hasTables(db)) {
                return new Inspection(file, null, Problem.NOT_BACKUP);
            }
            if (!isIntact(db)) {
                return new Inspection(file, null, Problem.DAMAGED);
            }
            long received = DatabaseUtils.queryNumEntries(db, InOutDatabase.TABLE_IN_INFO);
            long sent = DatabaseUtils.queryNumEntries(db, InOutDatabase.TABLE_OUT_INFO);
            return new Inspection(file, new Summary(received, sent, latest(dates(db))), null);
        } catch (SQLiteException e) {
            return new Inspection(file, null, Problem.DAMAGED);
        } finally {
            db.close();
        }
    }

    private static boolean hasTables(SQLiteDatabase db) {
        for (String table : TABLES) {
            Set<String> columns = new HashSet<>();
            try (Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null)) {
                int name = cursor.getColumnIndexOrThrow("name");
                while (cursor.moveToNext()) {
                    columns.add(cursor.getString(name).toUpperCase(Locale.ROOT));
                }
            }
            for (String required : REQUIRED_COLUMNS) {
                if (!columns.contains(required)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isIntact(SQLiteDatabase db) {
        try (Cursor cursor = db.rawQuery("PRAGMA quick_check", null)) {
            return cursor.moveToFirst() && "ok".equalsIgnoreCase(cursor.getString(0));
        }
    }

    private static List<String> dates(SQLiteDatabase db) {
        List<String> dates = new ArrayList<>();
        for (String table : TABLES) {
            try (Cursor cursor = db.rawQuery("select DATE from " + table, null)) {
                while (cursor.moveToNext()) {
                    dates.add(cursor.getString(0));
                }
            }
        }
        return dates;
    }

    /**
     * 검사를 마친 백업으로 DB를 바꾼다. 바꾸기 전 DB는 앱 안에 보관해 두어 undo로 되돌릴 수 있다.
     * 메인 스레드에서 부른다. 실패하면 DB는 원래대로 남는다.
     */
    static boolean apply(Context context, Inspection inspection) {
        Context app = context.getApplicationContext();
        File kept = keptFile(app);
        try {
            InOutDatabase.getInstance(app).withClosedConnection(dbFile -> {
                if (dbFile.exists()) {
                    copy(dbFile, kept);
                } else {
                    kept.delete();
                }
                replace(dbFile, inspection.candidate, kept.exists() ? kept : null);
            });
            return true;
        } catch (IOException e) {
            Log.e(TAG, "불러오기 실패", e);
            return false;
        } finally {
            inspection.discard();
        }
    }

    /** 불러오기 전 DB로 되돌린다. 메인 스레드에서 부른다. */
    static boolean undo(Context context) {
        Context app = context.getApplicationContext();
        File kept = keptFile(app);
        if (!kept.exists()) {
            return false;
        }
        File current = new File(app.getCacheDir(), "undo-current.db");
        try {
            InOutDatabase.getInstance(app).withClosedConnection(dbFile -> {
                copy(dbFile, current);
                replace(dbFile, kept, current);
            });
            kept.delete();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "되돌리기 실패", e);
            return false;
        } finally {
            current.delete();
        }
    }

    /** 불러오기 직전 DB를 보관하는 곳. Android 자동 백업에는 넣지 않는다. */
    private static File keptFile(Context context) {
        return new File(context.getNoBackupFilesDir(), "before-restore.db");
    }

    /** dbFile을 source 내용으로 바꾼다. 복사하다 실패하면 fallback(원래 내용)으로 되돌린다. */
    private static void replace(File dbFile, File source, File fallback) throws IOException {
        deleteSidecars(dbFile);
        try {
            copy(source, dbFile);
        } catch (IOException e) {
            if (fallback != null) {
                try {
                    copy(fallback, dbFile);
                } catch (IOException restoreFailed) {
                    Log.e(TAG, "원래 DB로 되돌리기 실패", restoreFailed);
                }
            }
            throw e;
        }
    }

    /** 다른 DB 파일에 남은 저널이 새 파일에 적용되지 않도록 지운다 */
    private static void deleteSidecars(File dbFile) {
        for (String suffix : new String[]{"-journal", "-wal", "-shm"}) {
            new File(dbFile.getPath() + suffix).delete();
        }
    }

    private static void copy(File from, File to) throws IOException {
        try (InputStream in = new FileInputStream(from);
             FileOutputStream out = new FileOutputStream(to)) {
            copy(in, out);
            out.getFD().sync();
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[64 * 1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        out.flush();
    }

    private static int readFully(InputStream in, byte[] buffer) throws IOException {
        int total = 0;
        while (total < buffer.length) {
            int read = in.read(buffer, total, buffer.length - total);
            if (read == -1) {
                break;
            }
            total += read;
        }
        return total;
    }

    private static void postToMain(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
