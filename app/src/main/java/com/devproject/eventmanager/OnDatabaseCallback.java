package com.devproject.eventmanager;

import java.util.ArrayList;

public interface OnDatabaseCallback {
//    public void insert(String name, String date, String category, String relation, String money);
    public ArrayList<AddList> selectAll();
}
