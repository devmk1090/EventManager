package com.devproject.eventmanager;

import android.database.Cursor;

public class AddList {

    private String name, date, category, relation, money;
    private int id;

    public AddList(String name, String date, String category, String relation, String money, int id) {
        this.name = name;
        this.date = date;
        this.category = category;
        this.relation = relation;
        this.money = money;
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    @Override
//    public String toString() {
//        return "AddList{" +
//                "name='" + name + '\'' +
//                ", date='" + date + '\'' +
//                ", category='" + category + '\'' +
//                ", relation='" + relation + '\'' +
//                ", money='" + money + '\'' +
//                '}';
//
//    }
}
