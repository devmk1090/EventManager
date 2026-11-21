package com.devproject.eventmanager;

public class InList {

    int id;
    String name;
    String date;
    String category;
    String relation;
    String money;
    String memo;

    public InList(int id, String name, String date, String category, String relation, String money, String memo) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.category = category;
        this.relation = relation;
        this.money = money;
        this.memo = memo;
    }

    // DB의 _id. 수정·삭제할 행을 정확히 가리키는 데 쓴다.
    public int getId() {
        return id;
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