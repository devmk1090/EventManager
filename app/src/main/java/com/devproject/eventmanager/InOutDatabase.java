package com.devproject.eventmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class InOutDatabase {


    public static final String TAG = "InOutDatabase";

    // singleton instance
    private static InOutDatabase database;

    // database name
    public static String DATABASE_NAME = "InOut.db";

    // table name for ADD_INFO
    public static String TABLE_IN_INFO = "IN_INFO";
    public static String TABLE_OUT_INFO = "OUT_INFO";

    public static int DATABASE_VERSION = 1;

    // helper class defined
    private InOutDatabase.DatabaseHelper dbHelper;

    // database object
    private SQLiteDatabase db;

    private Context context;

    private InOutDatabase(Context context){
        this.context = context;
    }

    // singleton 패턴으로 구현 . 여러번 호출하더라도 최초에 생성된 객체를 반환하는 디자인 패턴
    public static InOutDatabase getInstance(Context context){
        if (database == null) {
            database = new InOutDatabase(context);
        }
        return database;
    }

    // open database
    public boolean open(){
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
    public void insertRecordIn (String name, String date, String category, String relation, String money, String memo){
        try {
            db.execSQL( "insert into " + TABLE_IN_INFO + "(NAME, DATE, CATEGORY, RELATION, MONEY, MEMO) values ('" + name + "','" + date + "','" + category +"','" + relation +"','" + money + "','" + memo + "');" );
        } catch (Exception ex){
            Log.e(TAG, "Exception in executing insert SQL.", ex);
        }
    }
    public void insertRecordOut (String name, String date, String category, String relation, String money, String memo){
        try {
            db.execSQL( "insert into " + TABLE_OUT_INFO + "(NAME, DATE, CATEGORY, RELATION, MONEY, MEMO) values ('" + name + "','" + date + "','" + category +"','" + relation +"','" + money + "','" + memo + "');" );
        } catch (Exception ex){
            Log.e(TAG, "Exception in executing insert SQL.", ex);
        }
    }

    public ArrayList<InList> selectAllIn() {
        ArrayList<InList> result = new ArrayList<InList>();
        try{
            Cursor cursor = db.rawQuery("select NAME, DATE, CATEGORY, RELATION, MONEY, MEMO from " + TABLE_IN_INFO, null);
            for(int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String name = cursor.getString(0);
                String date = cursor.getString(1);
                String category = cursor.getString(2);
                String relation = cursor.getString(3);
                String money = cursor.getString(4);
                String memo = cursor.getString(5);

                InList info = new InList(name, date, category, relation, money, memo);
                result.add(info);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Exception in executing insert SQL.", ex);
        }
        return result;
    }
    public ArrayList<OutList> selectAllOut() {
        ArrayList<OutList> result = new ArrayList<OutList>();
        try{
            Cursor cursor = db.rawQuery("select NAME, DATE, CATEGORY, RELATION, MONEY, MEMO from " + TABLE_OUT_INFO, null);
            for(int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                String name = cursor.getString(0);
                String date = cursor.getString(1);
                String category = cursor.getString(2);
                String relation = cursor.getString(3);
                String money = cursor.getString(4);
                String memo = cursor.getString(5);

                OutList info = new OutList(name, date, category, relation, money, memo);
                result.add(info);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Exception in executing insert SQL.", ex);
        }
        return result;
    }

    private void println(String msg) {
        Log.d(TAG, msg);
    }

    public String getMoneyIn(int position) {
        String money = null;
        Cursor cursor = db.rawQuery("select MONEY from " + TABLE_IN_INFO + " WHERE _id='" + position + "'", null);
        while (cursor.moveToNext()) {
            money = cursor.getString(0);
        }
        return money;
    }
    public String getMoneyOut(int position) {
        String money = null;
        Cursor cursor = db.rawQuery("select MONEY from " + TABLE_OUT_INFO + " WHERE _id='" + position + "'", null);
        while (cursor.moveToNext()) {
            money = cursor.getString(0);
        }
        return money;
    }
    public int getAllMoneyIn(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO, null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getAllMoneyOut(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO, null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategoryMarryOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE CATEGORY=' 결혼식 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategoryMarryInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE CATEGORY=' 결혼식 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategoryFirstBirthOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE CATEGORY=' 돌잔치 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategoryFirstBirthInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE CATEGORY=' 돌잔치 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategoryFuneralOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE CATEGORY=' 장례식 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategoryFuneralInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE CATEGORY=' 장례식 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategorySixtyOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE CATEGORY=' 환갑 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategorySixtyInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE CATEGORY=' 환갑 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategorySeventyOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE CATEGORY=' 칠순 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategorySeventyInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE CATEGORY=' 칠순 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategoryBirthdayOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE CATEGORY=' 생일 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategoryBirthdayInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE CATEGORY=' 생일 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategoryETCOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE CATEGORY=' 기타 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getCategoryETCInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE CATEGORY=' 기타 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }

    public int getRelationFriendOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE RELATION=' 친구 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationFriendInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE RELATION=' 친구 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationRelativeOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE RELATION=' 친척 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationRelativeInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE RELATION=' 친척 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationJobOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE RELATION=' 회사 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationJobInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE RELATION=' 회사 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationUniversityOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE RELATION=' 대학교 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationUniversityInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE RELATION=' 대학교 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationFamilyOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE RELATION=' 가족 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationFamilyInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE RELATION=' 가족 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationKnowingOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE RELATION=' 지인 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationKnowingInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE RELATION=' 지인 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationETCOutMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_OUT_INFO + " WHERE RELATION=' 기타 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public int getRelationETCInMoney(){
        int total = 0;
        Cursor cursor = db.rawQuery("select SUM(MONEY) from " + TABLE_IN_INFO + " WHERE RELATION=' 기타 ' ", null);
        if(cursor.moveToFirst())
            total = cursor.getInt(0);
        return total;
    }
    public boolean deleteDataIn (long id) {
        return db.delete(TABLE_IN_INFO, " _id = " + id, null) > 0;
    }
    public boolean deleteDataOut (long id) {
        return db.delete(TABLE_OUT_INFO, " _id = " + id, null) > 0;
    }
    public boolean updateIn(int id, String name, String date, String category, String relation, String money, String memo) {
        ContentValues cv = new ContentValues();
        cv.put("NAME", name);
        cv.put("DATE", date);
        cv.put("CATEGORY", category);
        cv.put("RELATION", relation);
        cv.put("MONEY", money);
        cv.put("MEMO", memo);

        return db.update(TABLE_IN_INFO, cv, "_id=" + id, null) > 0;
    }
    public boolean updateOut (int id, String name, String date, String category, String relation, String money, String memo) {

        ContentValues cv = new ContentValues();
        cv.put("NAME", name);
        cv.put("DATE", date);
        cv.put("CATEGORY", category);
        cv.put("RELATION", relation);
        cv.put("MONEY", money);
        cv.put("MEMO", memo);

        return db.update(TABLE_OUT_INFO, cv, "_id=" + id, null) > 0;
    }
    public boolean checkItemIn(String name, String date, String category, String relation) {
        Cursor cursor = db.rawQuery("select _id, NAME, DATE, CATEGORY, RELATION from " + TABLE_IN_INFO, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(name) && cursor.getString(2).equals(date)
                    && cursor.getString(3).equals(category) && cursor.getString(4).equals(relation)) {
                return false;
            }
        }
        return true;
    }
    public boolean checkItemOut(String name, String date, String category, String relation) {
        Cursor cursor = db.rawQuery("select _id, NAME, DATE, CATEGORY, RELATION from " + TABLE_OUT_INFO, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(name) && cursor.getString(2).equals(date)
                    && cursor.getString(3).equals(category) && cursor.getString(4).equals(relation)) {
                return false;
            }
        }
        return true;
    }
    public int getSearchItemIdIn(String name, String date, String category, String relation) {
        Cursor cursor = db.rawQuery("select _id, NAME, DATE, CATEGORY, RELATION, MONEY from " + TABLE_IN_INFO, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(name) && cursor.getString(2).equals(date)
                    && cursor.getString(3).equals(category) && cursor.getString(4).equals(relation)) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }
    public int getSearchItemIdOut(String name, String date, String category, String relation) {
        Cursor cursor = db.rawQuery("select _id, NAME, DATE, CATEGORY, RELATION, MONEY from " + TABLE_OUT_INFO, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(name) && cursor.getString(2).equals(date)
                    && cursor.getString(3).equals(category) && cursor.getString(4).equals(relation)) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }
    public int getDeleteItemIn(String name, String date, String category, String relation) {
        Cursor cursor = db.rawQuery("select _id, NAME, DATE, CATEGORY, RELATION, MONEY from " + TABLE_IN_INFO, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(name) && cursor.getString(2).equals(date)
                    && cursor.getString(3).equals(category) && cursor.getString(4).equals(relation)) {
                return cursor.getPosition();
            }
        }
        return 0;
    }
    public int getDeleteItemOut(String name, String date, String category, String relation) {
        Cursor cursor = db.rawQuery("select _id, NAME, DATE, CATEGORY, RELATION, MONEY from " + TABLE_OUT_INFO, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(name) && cursor.getString(2).equals(date)
                    && cursor.getString(3).equals(category) && cursor.getString(4).equals(relation)) {
                return cursor.getPosition();
            }
        }
        return 0;
    }
}
