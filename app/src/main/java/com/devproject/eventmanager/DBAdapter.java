package com.devproject.eventmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
    Context c;
    SQLiteDatabase db;
    DBHelper helper;

    public DBAdapter(Context ctx){
        this.c = ctx;
        helper = new DBHelper(c);
    }

    //OPEN DB
    public DBAdapter openDB(){
        try{
            db = helper.getWritableDatabase();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public void close(){
        try{
            helper.close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    //INSERT DATA TO DB
    public long add(String name, String date, String category, String relation, String money) {
        try{
            ContentValues cv = new ContentValues();
            cv.put(Contants.NAME,name);
            cv.put(Contants.DATE,date);
            cv.put(Contants.CATEGORY,category);
            cv.put(Contants.RELATION,relation);
            cv.put(Contants.MOMEY,money);

            return db.insert(Contants.TB_NAME,Contants.ROW_ID,cv);
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    //RETRIEVE ALL LIST
    public Cursor getAllList(){
        String[] columns = {Contants.ROW_ID, Contants.NAME, Contants.DATE, Contants.CATEGORY, Contants.RELATION, Contants.MOMEY};
        return db.query(Contants.TB_NAME, columns, null, null, null, null, null);
    }

    public long UPDATE(int id, String name, String date, String category, String relation, String money)
    {
        try
        {
            ContentValues cv = new ContentValues();
            cv.put(Contants.NAME,name);
            cv.put(Contants.DATE,date);
            cv.put(Contants.CATEGORY,category);
            cv.put(Contants.RELATION,relation);
            cv.put(Contants.MOMEY,money);

            return db.update(Contants.TB_NAME,cv,Contants.ROW_ID+" =?", new String[]{String.valueOf(id)});
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    //DELETE
    public long Delete(int id)
    {
        try
        {
            return db.delete(Contants.TB_NAME,Contants.ROW_ID+" =?", new String[]{String.valueOf(id)});
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
}
