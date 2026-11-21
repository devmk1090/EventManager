package com.devproject.eventmanager;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ListUtilsTest {

    private static int nextId = 1;

    private static OutList item(String name, String date) {
        return new OutList(nextId++, name, date, " 결혼식 ", " 친구 ", "50000", "");
    }

    // DB에는 A, B, C 순서로 저장됐지만 목록은 DATE desc 문자열 정렬이라 C, B, A로 보이는 상황.
    // 이전 코드는 DB 조회 순서의 위치(C → 2)를 써서 화면에서 A를 지웠다.
    @Test
    public void removesClickedItemWhenNotFiltered() {
        OutList a = item("A", "2026/11/1/일요일");
        OutList b = item("B", "2026/11/10/화요일");
        OutList c = item("C", "2026/11/20/금요일");
        List<OutList> items = new ArrayList<>(Arrays.asList(c, b, a));

        int position = ListUtils.removeItem(items, items, c);

        assertEquals(0, position);
        assertEquals(Arrays.asList(b, a), items);
    }

    @Test
    public void removesFromBothListsWhenFiltered() {
        OutList a = item("A", "2026/11/1/일요일");
        OutList b = item("B", "2026/11/10/화요일");
        OutList c = item("C", "2026/11/20/금요일");
        List<OutList> items = new ArrayList<>(Arrays.asList(c, b, a));
        List<OutList> filtered = new ArrayList<>(Arrays.asList(b));

        int position = ListUtils.removeItem(items, filtered, b);

        assertEquals(0, position);
        assertTrue(filtered.isEmpty());
        assertEquals(Arrays.asList(c, a), items);
    }

    @Test
    public void removesOnlyTheClickedObjectWhenFieldsAreEqual() {
        OutList first = item("A", "2026/11/1/일요일");
        OutList second = item("A", "2026/11/1/일요일");
        List<OutList> items = new ArrayList<>(Arrays.asList(first, second));

        int position = ListUtils.removeItem(items, items, second);

        assertEquals(1, position);
        assertEquals(1, items.size());
        assertSame(first, items.get(0));
    }

    @Test
    public void returnsMinusOneWhenItemIsNotDisplayed() {
        OutList a = item("A", "2026/11/1/일요일");
        OutList b = item("B", "2026/11/10/화요일");
        List<OutList> items = new ArrayList<>(Arrays.asList(b, a));
        List<OutList> filtered = new ArrayList<>(Arrays.asList(b));

        int position = ListUtils.removeItem(items, filtered, a);

        assertEquals(-1, position);
        assertEquals(Arrays.asList(b), items);
        assertEquals(Arrays.asList(b), filtered);
    }
}
