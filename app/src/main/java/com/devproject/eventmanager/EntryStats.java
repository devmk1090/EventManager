package com.devproject.eventmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 통계 탭과 사람별 화면의 계산. 화면과 상관없는 순수 로직이라 단위 테스트로 확인한다.
 * 금액은 MoneyFormat.parse(숫자만 읽기)로 더하고, 경조사·관계·이름은 앞뒤 공백을 빼고 묶는다
 * (경조사·관계는 저장값에 앞뒤 공백이 붙어 있다).
 */
public final class EntryStats {

    /** 묶을 값이 비었을 때 보여 주는 이름 */
    static final String UNNAMED = "미지정";

    /** 경조사별·관계별·사람별 합계 한 줄 */
    public static final class GroupSum {
        public final String label;
        public final long amount;

        GroupSum(String label, long amount) {
            this.label = label;
            this.amount = amount;
        }
    }

    /** 한 사람(앞뒤 공백을 뺀 이름)과 주고받은 합계 */
    public static final class Person {
        public final String name;
        public final long received;
        public final long sent;
        public final int count;

        Person(String name, long received, long sent, int count) {
            this.name = name;
            this.received = received;
            this.sent = sent;
            this.count = count;
        }

        public long total() {
            return received + sent;
        }
    }

    private enum Key { CATEGORY, RELATION, NAME }

    private EntryStats() {}

    /** 내역에 있는 연도, 최근 연도부터. 날짜 없는 내역은 세지 않는다. */
    public static List<Integer> years(List<Entry> entries) {
        TreeSet<Integer> years = new TreeSet<>(Collections.reverseOrder());
        for (Entry entry : entries) {
            if (entry.parsedDate != null) {
                years.add(entry.parsedDate.year);
            }
        }
        return new ArrayList<>(years);
    }

    /** year가 null이면 전체, 아니면 그해 날짜의 내역만. 날짜 없는 내역은 전체에만 들어간다. */
    public static List<Entry> inYear(List<Entry> entries, Integer year) {
        if (year == null) {
            return new ArrayList<>(entries);
        }
        List<Entry> result = new ArrayList<>();
        for (Entry entry : entries) {
            if (entry.parsedDate != null && entry.parsedDate.year == year) {
                result.add(entry);
            }
        }
        return result;
    }

    public static long sum(List<Entry> entries, Entry.Direction direction) {
        long total = 0;
        for (Entry entry : entries) {
            if (entry.direction == direction) {
                total += entry.amount();
            }
        }
        return total;
    }

    public static int count(List<Entry> entries, Entry.Direction direction) {
        int count = 0;
        for (Entry entry : entries) {
            if (entry.direction == direction) {
                count++;
            }
        }
        return count;
    }

    /** 경조사별 합계, 금액 큰 순. 빈 값은 "미지정"으로 묶고 0원 묶음은 뺀다. */
    public static List<GroupSum> byCategory(List<Entry> entries, Entry.Direction direction) {
        return groupBy(entries, direction, Key.CATEGORY);
    }

    /** 관계별 합계, 금액 큰 순. 빈 값은 "미지정"으로 묶고 0원 묶음은 뺀다. */
    public static List<GroupSum> byRelation(List<Entry> entries, Entry.Direction direction) {
        return groupBy(entries, direction, Key.RELATION);
    }

    /** 사람별 합계(한 방향), 금액 큰 순 */
    public static List<GroupSum> byName(List<Entry> entries, Entry.Direction direction) {
        return groupBy(entries, direction, Key.NAME);
    }

    /** 사람별 받은 돈·나간 돈·건수. 주고받은 합계가 큰 사람부터, 같으면 이름순. */
    public static List<Person> people(List<Entry> entries) {
        Map<String, long[]> totals = new LinkedHashMap<>(); // 받은 돈, 나간 돈, 건수
        for (Entry entry : entries) {
            String name = label(entry, Key.NAME);
            long[] total = totals.get(name);
            if (total == null) {
                total = new long[3];
                totals.put(name, total);
            }
            total[entry.isReceived() ? 0 : 1] += entry.amount();
            total[2]++;
        }
        List<Person> result = new ArrayList<>();
        for (Map.Entry<String, long[]> item : totals.entrySet()) {
            long[] total = item.getValue();
            result.add(new Person(item.getKey(), total[0], total[1], (int) total[2]));
        }
        Collections.sort(result, (a, b) -> {
            int byTotal = Long.compare(b.total(), a.total());
            return byTotal != 0 ? byTotal : a.name.compareTo(b.name);
        });
        return result;
    }

    /** 이름이 같은(앞뒤 공백 무시) 내역만. 이름이 비었으면 빈 목록. */
    public static List<Entry> withName(List<Entry> entries, String name) {
        List<Entry> result = new ArrayList<>();
        String target = name == null ? "" : name.trim();
        if (target.isEmpty()) {
            return result;
        }
        for (Entry entry : entries) {
            if (entry.name.trim().equals(target)) {
                result.add(entry);
            }
        }
        return result;
    }

    private static List<GroupSum> groupBy(List<Entry> entries, Entry.Direction direction, Key key) {
        Map<String, Long> totals = new LinkedHashMap<>();
        for (Entry entry : entries) {
            if (entry.direction != direction) {
                continue;
            }
            String label = label(entry, key);
            Long total = totals.get(label);
            totals.put(label, (total == null ? 0 : total) + entry.amount());
        }
        List<GroupSum> result = new ArrayList<>();
        for (Map.Entry<String, Long> item : totals.entrySet()) {
            if (item.getValue() > 0) {
                result.add(new GroupSum(item.getKey(), item.getValue()));
            }
        }
        Collections.sort(result, (a, b) -> {
            int byAmount = Long.compare(b.amount, a.amount);
            return byAmount != 0 ? byAmount : a.label.compareTo(b.label);
        });
        return result;
    }

    private static String label(Entry entry, Key key) {
        String value = key == Key.CATEGORY ? entry.category : key == Key.RELATION ? entry.relation : entry.name;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? UNNAMED : trimmed;
    }
}
