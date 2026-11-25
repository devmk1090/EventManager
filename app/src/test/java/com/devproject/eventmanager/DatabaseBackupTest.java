package com.devproject.eventmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;

public class DatabaseBackupTest {

    @Test
    public void fileNameUsesZeroPaddedDate() {
        Calendar day = Calendar.getInstance();
        day.clear();
        day.set(2026, Calendar.MARCH, 5);
        assertEquals("경조사 백업 2026-03-05.db", DatabaseBackup.fileName(day));
    }

    @Test
    public void recognizesSqliteHeader() {
        byte[] head = Arrays.copyOf("SQLite format 3\u0000".getBytes(StandardCharsets.US_ASCII), 100);
        assertTrue(DatabaseBackup.hasSqliteHeader(head));
    }

    @Test
    public void rejectsOtherFilesAndShortHeaders() {
        assertFalse(DatabaseBackup.hasSqliteHeader("PK\u0003\u0004 엑셀이나 압축 파일".getBytes(StandardCharsets.UTF_8)));
        assertFalse(DatabaseBackup.hasSqliteHeader("SQLite format".getBytes(StandardCharsets.US_ASCII)));
        assertFalse(DatabaseBackup.hasSqliteHeader(new byte[0]));
        assertFalse(DatabaseBackup.hasSqliteHeader(null));
    }

    @Test
    public void latestSkipsEmptyAndUnreadableDates() {
        EntryDate latest = DatabaseBackup.latest(Arrays.asList(
                "2025/3/1/Saturday", "", null, "2026/11/21/토요일", "날짜 없음", "2026/1/9/금요일"));
        assertEquals(2026, latest.year);
        assertEquals(11, latest.month);
        assertEquals(21, latest.day);
    }

    @Test
    public void latestIsNullWhenNoEntryHasADate() {
        assertNull(DatabaseBackup.latest(Arrays.asList("", null, "  ")));
    }
}
