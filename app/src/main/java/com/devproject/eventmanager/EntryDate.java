package com.devproject.eventmanager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * DB의 DATE 문자열("2026/11/20/Friday", "2026/11/20/금요일", 또는 빈 문자열)을 다룬다.
 * 저장 형식은 예전 버전과 같게 유지하고, 화면에 보여 줄 때는 요일을 날짜로 다시 계산한다
 * (저장된 요일은 기기 언어에 따라 영어일 수도 있기 때문).
 */
public final class EntryDate {

    private static final String[] WEEKDAYS_KO = {"일", "월", "화", "수", "목", "금", "토"};

    public final int year;
    public final int month; // 1-12
    public final int day;

    private EntryDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public static EntryDate of(int year, int month, int day) {
        return new EntryDate(year, month, day);
    }

    /** 저장된 문자열을 읽는다. 날짜가 없거나 형식이 다르면 null. */
    public static EntryDate parse(String stored) {
        if (stored == null) {
            return null;
        }
        String[] parts = stored.trim().split("/");
        if (parts.length < 3) {
            return null;
        }
        try {
            int year = Integer.parseInt(parts[0].trim());
            int month = Integer.parseInt(parts[1].trim());
            int day = Integer.parseInt(parts[2].trim());
            if (year < 1 || month < 1 || month > 12 || day < 1 || day > 31) {
                return null;
            }
            return new EntryDate(year, month, day);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 예전 버전과 같은 저장 형식. 요일은 기기 언어를 따른다: "2026/11/20/금요일" */
    public String toStorage(Locale locale) {
        String weekday = new SimpleDateFormat("EEEE", locale).format(calendar().getTime());
        return year + "/" + month + "/" + day + "/" + weekday;
    }

    /** 목록용: "11.20 (금)" */
    public String shortText() {
        return month + "." + day + " (" + weekdayKo() + ")";
    }

    /** 입력 화면용: "2026년 11월 20일 (금)" */
    public String longText() {
        return year + "년 " + month + "월 " + day + "일 (" + weekdayKo() + ")";
    }

    /** 목록의 월 제목: 올해면 "11월", 아니면 "2025년 11월" */
    public String monthLabel(int currentYear) {
        return year == currentYear ? month + "월" : year + "년 " + month + "월";
    }

    public int monthKey() {
        return year * 100 + month;
    }

    public int dayKey() {
        return year * 10000 + month * 100 + day;
    }

    public String weekdayKo() {
        return WEEKDAYS_KO[calendar().get(Calendar.DAY_OF_WEEK) - 1];
    }

    private Calendar calendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, day);
        return calendar;
    }
}
