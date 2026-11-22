package com.devproject.eventmanager;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class EntryListTest {

    private static Entry sent(int id, String name, String date) {
        return new Entry(id, Entry.Direction.SENT, name, date, " 결혼식 ", " 친구 ", "50000", "");
    }

    private static Entry received(int id, String name, String date) {
        return new Entry(id, Entry.Direction.RECEIVED, name, date, " 환갑 ", " 회사 ", "100000", "");
    }

    private static List<String> names(List<Entry> entries) {
        List<String> names = new ArrayList<>();
        for (Entry entry : entries) {
            names.add(entry.name);
        }
        return names;
    }

    @Test
    public void sortsByRealDateNotByText() {
        // 문자열로 정렬하면 2025/9/3이 2025/10/1, 2025/9/25보다 앞에 온다
        List<Entry> entries = new ArrayList<>(Arrays.asList(
                sent(1, "9월3일", "2025/9/3/Wednesday"),
                sent(2, "10월1일", "2025/10/1/Wednesday"),
                sent(3, "9월25일", "2025/9/25/Thursday")));

        EntryList.sort(entries);

        assertEquals(Arrays.asList("10월1일", "9월25일", "9월3일"), names(entries));
    }

    @Test
    public void putsEntriesWithoutDateLastAndNewerFirstOnTies() {
        List<Entry> entries = new ArrayList<>(Arrays.asList(
                sent(1, "날짜없음", ""),
                sent(2, "먼저", "2026/11/8/Sunday"),
                received(7, "나중", "2026/11/8/Sunday")));

        EntryList.sort(entries);

        assertEquals(Arrays.asList("나중", "먼저", "날짜없음"), names(entries));
    }

    @Test
    public void filtersByDirectionAndName() {
        List<Entry> entries = Arrays.asList(
                sent(1, "최서연", "2026/11/20/Friday"),
                received(2, "이도현", "2026/11/8/Sunday"),
                sent(3, "이준호", "2026/11/3/Tuesday"));

        assertEquals(Arrays.asList("이도현"), names(EntryList.filter(entries, EntryList.Filter.RECEIVED, "")));
        assertEquals(Arrays.asList("최서연", "이준호"), names(EntryList.filter(entries, EntryList.Filter.SENT, null)));
        assertEquals(Arrays.asList("이도현", "이준호"), names(EntryList.filter(entries, EntryList.Filter.ALL, " 이 ")));
    }

    @Test
    public void groupsByMonthAndMarksFirstAndLast() {
        List<Entry> sorted = Arrays.asList(
                sent(3, "C", "2026/11/20/Friday"),
                sent(2, "B", "2026/11/14/Saturday"),
                sent(1, "A", "2025/10/24/Friday"),
                sent(4, "D", ""));

        List<EntryList.Row> rows = EntryList.group(sorted, 2026);

        assertEquals(7, rows.size());
        assertEquals("11월", rows.get(0).header);
        assertTrue(rows.get(1).first);
        assertFalse(rows.get(1).last);
        assertTrue(rows.get(2).last);
        assertEquals("2025년 10월", rows.get(3).header);
        assertTrue(rows.get(4).first && rows.get(4).last);
        assertEquals("날짜 없음", rows.get(5).header);
        assertNull(rows.get(5).entry);
        assertEquals("D", rows.get(6).entry.name);
    }
}
