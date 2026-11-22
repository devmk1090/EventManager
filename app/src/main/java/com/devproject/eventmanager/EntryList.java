package com.devproject.eventmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** 내역 목록의 정렬·필터·월별 묶음. 화면과 상관없는 순수 로직이라 단위 테스트로 확인한다. */
public final class EntryList {

    public enum Filter { ALL, RECEIVED, SENT }

    /** 목록 한 줄: 월 제목(header)이거나 내역 한 건(entry). */
    public static final class Row {
        public final String header;
        public final Entry entry;
        public final boolean first; // 같은 달 묶음의 첫 내역
        public final boolean last;  // 같은 달 묶음의 마지막 내역

        private Row(String header, Entry entry, boolean first, boolean last) {
            this.header = header;
            this.entry = entry;
            this.first = first;
            this.last = last;
        }

        public boolean isHeader() {
            return header != null;
        }
    }

    private EntryList() {}

    /**
     * 날짜 최신순. DATE는 문자열이라 DB에서 정렬하면 "2025/9/3"이 "2025/10/1"보다 앞에 오므로 여기서 정렬한다.
     * 날짜가 같으면 나중에 추가한 것이 먼저, 날짜가 없는 내역은 맨 뒤.
     */
    public static void sort(List<Entry> entries) {
        Collections.sort(entries, (a, b) -> {
            int byDate = Integer.compare(dayKey(b), dayKey(a));
            if (byDate != 0) {
                return byDate;
            }
            return Integer.compare(b.id, a.id);
        });
    }

    /** 방향과 이름 검색어로 거른다. 검색은 예전처럼 이름에 들어 있는지(대소문자 무시)로 본다. */
    public static List<Entry> filter(List<Entry> entries, Filter filter, String query) {
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        List<Entry> result = new ArrayList<>();
        for (Entry entry : entries) {
            if (filter == Filter.RECEIVED && !entry.isReceived()) {
                continue;
            }
            if (filter == Filter.SENT && entry.isReceived()) {
                continue;
            }
            if (!needle.isEmpty() && !entry.name.toLowerCase(Locale.ROOT).contains(needle)) {
                continue;
            }
            result.add(entry);
        }
        return result;
    }

    /** 정렬된 내역을 월별로 묶는다. 날짜가 없는 내역은 "날짜 없음" 묶음이 된다. */
    public static List<Row> group(List<Entry> sorted, int currentYear) {
        List<Row> rows = new ArrayList<>();
        int start = 0;
        while (start < sorted.size()) {
            Entry firstEntry = sorted.get(start);
            int month = monthKey(firstEntry);
            int end = start;
            while (end < sorted.size() && monthKey(sorted.get(end)) == month) {
                end++;
            }
            String label = firstEntry.parsedDate == null
                    ? "날짜 없음"
                    : firstEntry.parsedDate.monthLabel(currentYear);
            rows.add(new Row(label, null, false, false));
            for (int i = start; i < end; i++) {
                rows.add(new Row(null, sorted.get(i), i == start, i == end - 1));
            }
            start = end;
        }
        return rows;
    }

    private static int dayKey(Entry entry) {
        return entry.parsedDate == null ? -1 : entry.parsedDate.dayKey();
    }

    private static int monthKey(Entry entry) {
        return entry.parsedDate == null ? -1 : entry.parsedDate.monthKey();
    }
}
