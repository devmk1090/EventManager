package com.devproject.eventmanager;

import java.util.Locale;

/** 금액 문자열 읽기와 표시. DB의 MONEY는 숫자만 든 TEXT다. */
public final class MoneyFormat {

    private MoneyFormat() {}

    /** 숫자만 골라 읽는다. 비었거나 읽을 수 없으면 0 (목록이 죽지 않도록). */
    public static long parse(String text) {
        if (text == null) {
            return 0;
        }
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch >= '0' && ch <= '9') {
                digits.append(ch);
            }
        }
        if (digits.length() == 0) {
            return 0;
        }
        try {
            return Long.parseLong(digits.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** 100000 → "100,000" */
    public static String digits(long amount) {
        return String.format(Locale.KOREA, "%,d", amount);
    }

    /** 100000 → "100,000원" */
    public static String won(long amount) {
        return digits(amount) + "원";
    }

    /** 받은 돈은 "+100,000원", 나간 돈은 "−100,000원" */
    public static String signedWon(long amount, boolean received) {
        return (received ? "+" : "−") + won(amount);
    }

    /** 입력 실수를 막기 위한 한글 단위 표시: 100000 → "10만 원", 35000 → "3만 5,000원" */
    public static String korean(long amount) {
        if (amount <= 0) {
            return "0원";
        }
        long eok = amount / 100_000_000L;
        long man = (amount % 100_000_000L) / 10_000L;
        long rest = amount % 10_000L;
        StringBuilder text = new StringBuilder();
        if (eok > 0) {
            text.append(digits(eok)).append("억");
        }
        if (man > 0) {
            if (text.length() > 0) {
                text.append(' ');
            }
            text.append(digits(man)).append("만");
        }
        if (rest > 0) {
            if (text.length() > 0) {
                text.append(' ');
            }
            text.append(digits(rest)).append("원");
        } else {
            text.append(" 원");
        }
        return text.toString();
    }
}
