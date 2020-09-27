package com.devproject.eventmanager;

public class OutList {

    String name;
    String date;
    String category;
    String relation;
    String money;
    String memo;

    public OutList(String name, String date, String category, String relation, String money, String memo) {
        this.name = name;
        this.date = date;
        this.category = category;
        this.relation = relation;
        this.money = money;
        this.memo = memo;

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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}