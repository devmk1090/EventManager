package com.devproject.eventmanager;

/**
 * 받은 돈·나간 돈 내역 한 건. DB의 IN_INFO(받은 돈) 또는 OUT_INFO(나간 돈) 한 행에 대응한다.
 * 문자열 값은 DB에 저장된 그대로 둔다(경조사·관계는 앞뒤 공백 포함, 날짜는 "yyyy/M/d/요일").
 */
public class Entry {

    public enum Direction {
        RECEIVED, // 받은 돈 (IN_INFO)
        SENT      // 나간 돈 (OUT_INFO)
    }

    public final int id;
    public final Direction direction;
    public final String name;
    public final String date;
    public final String category;
    public final String relation;
    public final String money;
    public final String memo;
    public final EntryDate parsedDate; // 날짜가 없거나 형식이 다르면 null

    public Entry(int id, Direction direction, String name, String date, String category,
                 String relation, String money, String memo) {
        this.id = id;
        this.direction = direction;
        this.name = orEmpty(name);
        this.date = orEmpty(date);
        this.category = orEmpty(category);
        this.relation = orEmpty(relation);
        this.money = orEmpty(money);
        this.memo = orEmpty(memo);
        this.parsedDate = EntryDate.parse(this.date);
    }

    public boolean isReceived() {
        return direction == Direction.RECEIVED;
    }

    public long amount() {
        return MoneyFormat.parse(money);
    }

    public String categoryLabel() {
        return category.trim();
    }

    public String relationLabel() {
        return relation.trim();
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }
}
