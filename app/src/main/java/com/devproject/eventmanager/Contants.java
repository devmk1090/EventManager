package com.devproject.eventmanager;

public class Contants {

    //COLUMNS
    static final String ROW_ID = "id";
    static final String NAME = "name";
    static final String DATE = "date";
    static final String CATEGORY = "category";
    static final String RELATION = "relation";
    static final String MOMEY = "money";

    //DB PROPERTIES
    static final String DB_NAME = "b_DB";
    static final String TB_NAME = "b_TB";
    static final int DB_VERSION = '1';

    //CREATE TABLE
    static final String CREATE_TB = "CREATE TABLE b_TB(id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "name TEXT, date TEXT, category TEXT, relation TEXT, money TEXT);";
}
