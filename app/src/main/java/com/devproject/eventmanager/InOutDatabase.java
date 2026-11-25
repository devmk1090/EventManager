package com.devproject.eventmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InOutDatabase {

    public static final String TAG = "InOutDatabase";

    // singleton instance
    private static InOutDatabase database;

    // database name
    public static String DATABASE_NAME = "InOut.db";

    // IN_INFO = 받은 돈, OUT_INFO = 나간 돈. 두 테이블의 스키마는 같다.
    public static String TABLE_IN_INFO = "IN_INFO";
    public static String TABLE_OUT_INFO = "OUT_INFO";

    public static int DATABASE_VERSION = 1;

    /** 경조사별·관계별 합계 한 줄 */
    public static class GroupSum {
        public final String label;
        public final long amount;

        GroupSum(String label, long amount) {
            this.label = label;
            this.amount = amount;
        }
    }

    // helper class defined
    private InOutDatabase.DatabaseHelper dbHelper;

    // database object
    private SQLiteDatabase db;

    private final Context context;

    private InOutDatabase(Context context){
        this.context = context;
    }

    // singleton 패턴으로 구현 . 여러번 호출하더라도 최초에 생성된 객체를 반환하는 디자인 패턴
    public static InOutDatabase getInstance(Context context){
        if (database == null) {
            database = new InOutDatabase(context.getApplicationContext());
        }
        return database;
    }

    // open database (이미 열려 있으면 그대로 쓴다)
    public boolean open(){
        if (db != null && db.isOpen()) {
            return true;
        }
        println("opening database [" + DATABASE_NAME + "].");

        dbHelper = new InOutDatabase.DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return true;
    }

    // close database
    public void close(){
        println("closing database [" + DATABASE_NAME + "].");
        db.close();
        database = null;
    }

    /**
     * 백업·복원에서 DB 파일을 직접 복사하거나 바꾸는 동안만 연결을 닫아 둔다. 작업이 끝나면(실패해도) 다시 연다.
     * close()와 달리 싱글톤 객체는 그대로 두므로 화면들이 들고 있는 참조를 계속 쓸 수 있다.
     * DB 읽기·쓰기와 엇갈리지 않도록 메인 스레드에서만 부른다.
     */
    public void withClosedConnection(FileTask task) throws IOException {
        if (dbHelper != null) {
            dbHelper.close();
        }
        db = null;
        try {
            task.run(context.getDatabasePath(DATABASE_NAME));
        } finally {
            open();
        }
    }

    /** withClosedConnection 안에서 DB 파일을 다루는 작업 */
    public interface FileTask {
        void run(File databaseFile) throws IOException;
    }
    public Cursor rawQuery(String SQL) {
        println("\nexecuteQuery called.\n");

        Cursor c1 = null;
        try{
            c1 = db.rawQuery(SQL, null);
            println("cursor count : " + c1.getCount());
        } catch (Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
        }
        return c1;
    }
    public boolean execSQL(String SQL) {
        println("\nexecute called.\n");

        try{
            Log.d(TAG, "SQL : "+ SQL);
            db.execSQL(SQL);
        } catch (Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
            return false;
        }
        return true;
    }
    private static final String CREATE_TABLE_IN_INFO = "create table " + TABLE_IN_INFO + "("
            + "  _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + "  NAME TEXT, "
            + "  DATE TEXT, "
            + "  CATEGORY TEXT, "
            + "  RELATION TEXT, "
            + "  MONEY TEXT, "
            + "  MEMO TEXT, "
            + "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
            + ")";
    private static final String CREATE_TABLE_OUT_INFO = "create table " + TABLE_OUT_INFO + "("
            + "  _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + "  NAME TEXT, "
            + "  DATE TEXT, "
            + "  CATEGORY TEXT, "
            + "  RELATION TEXT, "
            + "  MONEY TEXT, "
            + "  MEMO TEXT, "
            + "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
            + ")";

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper (Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase _db) {
            try {
                _db.execSQL(CREATE_TABLE_IN_INFO);
                _db.execSQL(CREATE_TABLE_OUT_INFO);
            } catch (Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }
        }
        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + DATABASE_NAME + "].");
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");
            if(oldVersion < 2) {

            }
        }
    }

    private static String table(Entry.Direction direction) {
        return direction == Entry.Direction.RECEIVED ? TABLE_IN_INFO : TABLE_OUT_INFO;
    }

    /** 받은 돈·나간 돈 내역 전부. 순서는 EntryList.sort로 정한다. */
    public List<Entry> selectAll() {
        List<Entry> result = new ArrayList<>();
        readInto(result, Entry.Direction.RECEIVED);
        readInto(result, Entry.Direction.SENT);
        return result;
    }

    private void readInto(List<Entry> out, Entry.Direction direction) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select _id, NAME, DATE, CATEGORY, RELATION, MONEY, MEMO from " + table(direction), null);
            while (cursor.moveToNext()) {
                out.add(new Entry(cursor.getInt(0), direction, cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
            }
        } catch (Exception ex) {
            Log.e(TAG, "Exception in executing select SQL.", ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // 값을 SQL 문자열에 이어 붙이지 않고 ContentValues로 넘긴다(이름·메모에 ' 가 있어도 저장되도록).
    // 저장되는 컬럼과 값(모두 TEXT)은 이전 방식과 같다.
    public boolean insert(Entry.Direction direction, String name, String date, String category, String relation, String money, String memo) {
        try {
            return db.insertOrThrow(table(direction), null, values(name, date, category, relation, money, memo)) != -1;
        } catch (Exception ex){
            Log.e(TAG, "Exception in executing insert SQL.", ex);
            return false;
        }
    }

    public boolean update(Entry.Direction direction, int id, String name, String date, String category, String relation, String money, String memo) {
        try {
            return db.update(table(direction), values(name, date, category, relation, money, memo), "_id=" + id, null) > 0;
        } catch (Exception ex) {
            Log.e(TAG, "Exception in executing update SQL.", ex);
            return false;
        }
    }

    public boolean delete(Entry.Direction direction, int id) {
        try {
            return db.delete(table(direction), " _id = " + id, null) > 0;
        } catch (Exception ex) {
            Log.e(TAG, "Exception in executing delete SQL.", ex);
            return false;
        }
    }

    private static ContentValues values(String name, String date, String category, String relation, String money, String memo) {
        ContentValues cv = new ContentValues();
        cv.put("NAME", name);
        cv.put("DATE", date);
        cv.put("CATEGORY", category);
        cv.put("RELATION", relation);
        cv.put("MONEY", money);
        cv.put("MEMO", memo);
        return cv;
    }

    /** 추가할 때: 같은 (이름, 날짜, 경조사, 관계) 내역이 이미 있는지 */
    public boolean exists(Entry.Direction direction, String name, String date, String category, String relation) {
        return hasOtherItem(direction, -1, name, date, category, relation);
    }

    /** 수정할 때: 자기 자신(id)을 뺀 다른 행에 같은 (이름, 날짜, 경조사, 관계)가 있는지 */
    public boolean hasOtherItem(Entry.Direction direction, int id, String name, String date, String category, String relation) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select _id from " + table(direction) + " where _id <> " + id
                            + " and NAME = ? and DATE = ? and CATEGORY = ? and RELATION = ? limit 1",
                    new String[]{name, date, category, relation});
            return cursor.moveToFirst();
        } catch (Exception ex) {
            Log.e(TAG, "Exception in executing select SQL.", ex);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /** 합계 금액 */
    public long sum(Entry.Direction direction) {
        return queryLong("select SUM(MONEY) from " + table(direction));
    }

    /** 내역 수 */
    public long count(Entry.Direction direction) {
        return queryLong("select COUNT(*) from " + table(direction));
    }

    /** 경조사별 합계, 금액 큰 순 (0원 항목은 뺀다) */
    public List<GroupSum> sumByCategory(Entry.Direction direction) {
        return sumBy(direction, "CATEGORY");
    }

    /** 관계별 합계, 금액 큰 순 (0원 항목은 뺀다) */
    public List<GroupSum> sumByRelation(Entry.Direction direction) {
        return sumBy(direction, "RELATION");
    }

    // 저장값에 앞뒤 공백이 있으므로(" 결혼식 ") trim해서 묶는다. column은 상수만 넘어온다.
    private List<GroupSum> sumBy(Entry.Direction direction, String column) {
        List<GroupSum> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select trim(" + column + ") as label, SUM(MONEY) as total from " + table(direction)
                    + " group by label having total > 0 order by total desc", null);
            while (cursor.moveToNext()) {
                String label = cursor.isNull(0) || cursor.getString(0).isEmpty() ? "미지정" : cursor.getString(0);
                result.add(new GroupSum(label, cursor.getLong(1)));
            }
        } catch (Exception ex) {
            Log.e(TAG, "Exception in executing select SQL.", ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    private long queryLong(String sql) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            return cursor.moveToFirst() && !cursor.isNull(0) ? cursor.getLong(0) : 0;
        } catch (Exception ex) {
            Log.e(TAG, "Exception in executing select SQL.", ex);
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void println(String msg) {
        Log.d(TAG, msg);
    }
}
