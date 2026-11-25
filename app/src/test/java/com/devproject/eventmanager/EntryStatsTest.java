package com.devproject.eventmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class EntryStatsTest {

    private static Entry received(int id, String name, String date, String category, String relation, String money) {
        return new Entry(id, Entry.Direction.RECEIVED, name, date, category, relation, money, "");
    }

    private static Entry sent(int id, String name, String date, String category, String relation, String money) {
        return new Entry(id, Entry.Direction.SENT, name, date, category, relation, money, "");
    }

    private final List<Entry> entries = Arrays.asList(
            sent(1, "김하은", "2026/7/4/토요일", " 생일 ", " 가족 ", "100000"),
            sent(2, "이민재", "2025/8/29/Friday", " 결혼식 ", " 친구 ", "100000"),
            sent(3, " 김하은 ", "2026/11/21/토요일", " 결혼식 ", " 가족 ", "50000"),
            sent(4, "박준영", "", " 돌잔치 ", "", "30000"),
            sent(5, "정민지", "2026/3/2/월요일", "", " 지인 ", "0"),
            received(6, "김하은", "2024/5/16/토요일", " 결혼식 ", " 가족 ", "200000"),
            received(7, "오세린", "2026/11/8/일요일", " 환갑 ", " 친구 ", "50,000"));

    @Test
    public void yearsAreDistinctNewestFirstAndSkipUndated() {
        assertEquals(Arrays.asList(2026, 2025, 2024), EntryStats.years(entries));
    }

    @Test
    public void inYearKeepsOnlyThatYearAndNullMeansAll() {
        assertEquals(4, EntryStats.inYear(entries, 2026).size());
        assertEquals(entries.size(), EntryStats.inYear(entries, null).size());
        assertTrue(EntryStats.inYear(entries, 2023).isEmpty());
    }

    @Test
    public void sumsReadDigitsOnlyAndCountByDirection() {
        assertEquals(280000, EntryStats.sum(entries, Entry.Direction.SENT));
        assertEquals(250000, EntryStats.sum(entries, Entry.Direction.RECEIVED)); // "50,000"도 5만 원
        assertEquals(5, EntryStats.count(entries, Entry.Direction.SENT));
        assertEquals(2, EntryStats.count(entries, Entry.Direction.RECEIVED));
    }

    @Test
    public void categoriesAreTrimmedSortedAndZeroGroupsDropped() {
        List<EntryStats.GroupSum> byCategory = EntryStats.byCategory(entries, Entry.Direction.SENT);
        assertEquals(3, byCategory.size()); // 정민지의 0원(미지정)은 빠진다
        assertEquals("결혼식", byCategory.get(0).label);
        assertEquals(150000, byCategory.get(0).amount);
        assertEquals("생일", byCategory.get(1).label);
        assertEquals("돌잔치", byCategory.get(2).label);
    }

    @Test
    public void emptyRelationBecomesUnnamed() {
        List<EntryStats.GroupSum> byRelation = EntryStats.byRelation(entries, Entry.Direction.SENT);
        assertEquals("가족", byRelation.get(0).label);
        assertEquals(150000, byRelation.get(0).amount);
        assertEquals(EntryStats.UNNAMED, byRelation.get(byRelation.size() - 1).label);
    }

    @Test
    public void peopleGroupTrimmedNamesAcrossDirections() {
        List<EntryStats.Person> people = EntryStats.people(entries);
        EntryStats.Person first = people.get(0);
        assertEquals("김하은", first.name);
        assertEquals(200000, first.received);
        assertEquals(150000, first.sent);
        assertEquals(3, first.count);
        assertEquals(5, people.size()); // 김하은·이민재·오세린·박준영·정민지
        assertEquals("정민지", people.get(people.size() - 1).name); // 0원이라 맨 뒤
    }

    @Test
    public void byNameSumsOneDirection() {
        List<EntryStats.GroupSum> byName = EntryStats.byName(entries, Entry.Direction.SENT);
        assertEquals("김하은", byName.get(0).label);
        assertEquals(150000, byName.get(0).amount);
        assertEquals("이민재", byName.get(1).label);
    }

    @Test
    public void withNameIgnoresSurroundingSpaces() {
        assertEquals(3, EntryStats.withName(entries, " 김하은").size());
        assertTrue(EntryStats.withName(entries, "  ").isEmpty());
        assertTrue(EntryStats.withName(entries, "김하").isEmpty()); // 일부만 같으면 다른 사람
    }
}
