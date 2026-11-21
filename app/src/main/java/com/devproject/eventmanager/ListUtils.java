package com.devproject.eventmanager;

import java.util.List;

final class ListUtils {

    private ListUtils() {}

    /**
     * 전체 목록(items)과 화면에 보이는 목록(filteredList)에서 target 객체 자체를 지운다.
     * 검색 필터가 꺼져 있으면 두 목록은 같은 객체다.
     *
     * @return 지우기 전 filteredList에서의 위치. 화면 목록에 없으면 -1
     */
    static <T> int removeItem(List<T> items, List<T> filteredList, T target) {
        int displayedPosition = indexOfSame(filteredList, target);
        int itemsPosition = indexOfSame(items, target);
        if (itemsPosition >= 0) {
            items.remove(itemsPosition);
        }
        if (filteredList != items && displayedPosition >= 0) {
            filteredList.remove(displayedPosition);
        }
        return displayedPosition;
    }

    // equals()가 아니라 같은 객체인지로 찾는다. 필드가 같은 항목이 둘 있어도 누른 항목만 지우기 위해서다.
    private static <T> int indexOfSame(List<T> list, T target) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == target) {
                return i;
            }
        }
        return -1;
    }
}
