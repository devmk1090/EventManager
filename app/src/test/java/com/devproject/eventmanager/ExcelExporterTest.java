package com.devproject.eventmanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Calendar;

public class ExcelExporterTest {

    @Test
    public void fileNameUsesZeroPaddedDate() {
        Calendar day = Calendar.getInstance();
        day.clear();
        day.set(2026, Calendar.JANUARY, 9);
        assertEquals("경조사 내역 2026-01-09.xls", ExcelExporter.fileName(day));
    }
}
