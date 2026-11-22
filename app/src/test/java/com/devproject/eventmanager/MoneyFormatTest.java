package com.devproject.eventmanager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoneyFormatTest {

    @Test
    public void parsesDigitsOnly() {
        assertEquals(100000L, MoneyFormat.parse("100,000"));
        assertEquals(50000L, MoneyFormat.parse("50000"));
        assertEquals(0L, MoneyFormat.parse(""));
        assertEquals(0L, MoneyFormat.parse(null));
    }

    @Test
    public void handlesAmountsBeyondIntRange() {
        // 예전에는 Integer.parseInt라 int 범위를 넘으면 목록에서 앱이 죽었다
        assertEquals(999999999999L, MoneyFormat.parse("999999999999"));
        assertEquals("999,999,999,999원", MoneyFormat.won(999999999999L));
    }

    @Test
    public void formatsWonWithSign() {
        assertEquals("100,000원", MoneyFormat.won(100000));
        assertEquals("+50,000원", MoneyFormat.signedWon(50000, true));
        assertEquals("−50,000원", MoneyFormat.signedWon(50000, false));
    }

    @Test
    public void readsAmountInKoreanUnits() {
        assertEquals("10만 원", MoneyFormat.korean(100000));
        assertEquals("3만 5,000원", MoneyFormat.korean(35000));
        assertEquals("5,000원", MoneyFormat.korean(5000));
        assertEquals("1억 원", MoneyFormat.korean(100000000));
        assertEquals("1억 2,000만 원", MoneyFormat.korean(120000000));
        assertEquals("0원", MoneyFormat.korean(0));
    }
}
