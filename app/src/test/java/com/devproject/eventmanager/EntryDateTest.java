package com.devproject.eventmanager;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EntryDateTest {

    @Test
    public void parsesStoredDateInAnyWeekdayLanguage() {
        EntryDate english = EntryDate.parse("2026/11/20/Friday");
        EntryDate korean = EntryDate.parse("2026/11/20/금요일");

        assertEquals(20261120, english.dayKey());
        assertEquals(20261120, korean.dayKey());
    }

    @Test
    public void returnsNullWhenDateIsMissingOrBroken() {
        assertNull(EntryDate.parse(""));
        assertNull(EntryDate.parse(null));
        assertNull(EntryDate.parse("날짜"));
        assertNull(EntryDate.parse("2026/13/1/Monday"));
    }

    @Test
    public void recalculatesWeekdayInsteadOfTrustingStoredText() {
        // 저장된 요일이 틀려도(예: 영어, 오래된 계산) 화면에는 날짜로 계산한 요일을 쓴다
        EntryDate date = EntryDate.parse("2026/11/20/Sunday");

        assertEquals("11.20 (금)", date.shortText());
        assertEquals("2026년 11월 20일 (금)", date.longText());
    }

    @Test
    public void keepsLegacyStorageFormat() {
        EntryDate date = EntryDate.of(2026, 11, 3);

        assertEquals("2026/11/3/화요일", date.toStorage(Locale.KOREA));
        assertEquals("2026/11/3/Tuesday", date.toStorage(Locale.US));
    }

    @Test
    public void monthLabelShowsYearOnlyForOtherYears() {
        assertEquals("11월", EntryDate.of(2026, 11, 3).monthLabel(2026));
        assertEquals("2025년 11월", EntryDate.of(2025, 11, 3).monthLabel(2026));
    }
}
