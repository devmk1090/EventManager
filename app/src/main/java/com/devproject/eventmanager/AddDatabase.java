//package com.devproject.eventmanager;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//import java.util.ArrayList;
//
//public class AddDatabase {
//
//    public static final String TAG = "AddDatabase";
//
//    // singleton instance
//    private static AddDatabase database;
//
//    // database name
//    public static String DATABASE_NAME = "Add.db";
//
//    // table name for ADD_INFO
//    public static String TABLE_ADD_INFO = "ADD_INFO";
//
//    public static int DATABASE_VERSION = 1;
//
//    // helper class defined
//    private DatabaseHelper dbHelper;
//
//    // database object
//    private SQLiteDatabase db;
//
//    private Context context;
//
//    private Cursor cursor;
//
//    private AddDatabase(Context context){
//        this.context = context;
//    }
//
//    // singleton 패턴으로 구현 . 여러번 호출하더라도 최초에 생성된 객체를 반환하는 디자인 패턴
//    public static AddDatabase getInstance(Context context){
//        if (database == null) {
//            database = new AddDatabase(context);
//        }
//        return database;
//    }
//
//    // open database
//    public boolean open(){
//        println("opening database [" + DATABASE_NAME + "].");
//
//        dbHelper = new DatabaseHelper(context);
//        db = dbHelper.getWritableDatabase();
//
//        return true;
//    }
//
//    // close database
//    public void close(){
//        println("closing database [" + DATABASE_NAME + "].");
//        db.close();
//        database = null;
//    }
//
//    public Cursor rawQuery(String SQL) {
//        println("\nexecuteQuery called.\n");
//
//        Cursor c1 = null;
//        try{
//            c1 = db.rawQuery(SQL, null);
//            println("cursor count : " + c1.getCount());
//        } catch (Exception ex) {
//            Log.e(TAG, "Exception in executeQuery", ex);
//        }
//        return c1;
//    }
//
//    public boolean execSQL(String SQL) {
//        println("\nexecute called.\n");
//
//        try{
//            Log.d(TAG, "SQL : "+ SQL);
//            db.execSQL(SQL);
//        } catch (Exception ex) {
//            Log.e(TAG, "Exception in executeQuery", ex);
//            return false;
//        }
//        return true;
//    }
//
//
//    // Delete column
//    public boolean deleteData (long id) {
//        return db.delete(TABLE_ADD_INFO, " _id = " + (id +1), null) > 0;
//    }
//    public void resetData(long id) {
//        ContentValues contentValues = new ContentValues();
//
//        for(long i = 1; i < getCount() + 1; i++ ) {
//            contentValues.put("_id =", i);
//            db.update(TABLE_ADD_INFO, contentValues, "_id =" + (i + 1), null);
//        }
//    }
//    public int getCount(){
//        Cursor cursor = db.rawQuery("select * from " + TABLE_ADD_INFO, null);
//        if(cursor == null)
//            return 0;
//        else return cursor.getCount();
//    }
//
//    private class DatabaseHelper extends SQLiteOpenHelper {
//        public DatabaseHelper (Context context){
//            super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        }
//        public void onCreate(SQLiteDatabase _db) {
//            // TABLE_ADD_INFO
//            println("creating table [" + TABLE_ADD_INFO + "].");
//
//            // drop existing table
//            String DROP_SQL = "drop table if exists " + TABLE_ADD_INFO;
//            try{
//                _db.execSQL(DROP_SQL);
//            } catch (Exception ex) {
//                Log.e(TAG, "Exception in DROP_SQL", ex);
//            }
//
//            // create table
//            String CREATE_SQL = "create table " + TABLE_ADD_INFO + "("
//                    + "  _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
//                    + "  NAME TEXT, "
//                    + "  DATE TEXT, "
//                    + "  CATEGORY TEXT, "
//                    + "  RELATION TEXT, "
//                    + "  MONEY TEXT, "
//                    + "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
//                    + ")";
//            try {
//                _db.execSQL(CREATE_SQL);
//            } catch (Exception ex) {
//                Log.e(TAG, "Exception in CREATE_SQL", ex);
//            }
//        }
//
//        public void onOpen(SQLiteDatabase db) {
//            println("opened database [" + DATABASE_NAME + "].");
//        }
//
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");
//            if(oldVersion < 2) {
//
//            }
//        }
//
//        private void insertRecord(SQLiteDatabase _db, String name, String date, String category, String relation, String money) {
//            try {
//                _db.execSQL( "insert into " + TABLE_ADD_INFO + "(NAME, DATE, CATEGORY, RELATION, MONEY) values ('" + name + "','" + date + "','" + category +"','" + relation +"','" + money + "');" );
//            } catch (Exception ex){
//                Log.e(TAG, "Exception in executing insert SQL.", ex);
//            }
//        }
//    }
//
//    public void insertRecord (String name, String date, String category, String relation, String money){
//        try {
//            db.execSQL( "insert into " + TABLE_ADD_INFO + "(NAME, DATE, CATEGORY, RELATION, MONEY) values ('" + name + "','" + date + "','" + category +"','" + relation +"','" + money + "');" );
//        } catch (Exception ex){
//            Log.e(TAG, "Exception in executing insert SQL.", ex);
//        }
//    }
//
//    public ArrayList<AddList> selectAll() {
//        ArrayList<AddList> result = new ArrayList<AddList>();
//
//        try{
//            Cursor cursor = db.rawQuery("select NAME, DATE, CATEGORY, RELATION, MONEY from " + TABLE_ADD_INFO, null);
//            for(int i = 0; i < cursor.getCount(); i++) {
//                cursor.moveToNext();
//                String name = cursor.getString(0);
//                String date = cursor.getString(1);
//                String category = cursor.getString(2);
//                String relation = cursor.getString(3);
//                String money = cursor.getString(4);
//
//                AddList info = new AddList(name, date, category, relation, money);
//                result.add(info);
//            }
//        } catch (Exception ex) {
//            Log.e(TAG, "Exception in executing insert SQL.", ex);
//        }
//        return result;
//    }
//
//    private void println(String msg) {
//        Log.d(TAG, msg);
//    }
//}
