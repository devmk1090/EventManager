package com.devproject.eventmanager;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

/** 통계 탭: 기간(전체·연도별)을 골라 나간 돈/받은 돈 합계와 경조사별·관계별·사람별 막대를 보여 준다. */
public class StatisticsFragment extends Fragment {

    /** 사람별 막대로 보여 줄 사람 수. 나머지는 ‘전체 보기’(PeopleActivity)에서 본다. */
    private static final int PEOPLE_PREVIEW = 5;

    private InOutDatabase database;
    private Entry.Direction direction = Entry.Direction.SENT;
    /** null이면 전체 기간 */
    private Integer year;
    private List<Integer> years = new ArrayList<>();

    private MaterialButton periodButton;
    private TextView caption;
    private TextView total;
    private TextView compare;
    private LinearLayout categoryBars;
    private LinearLayout relationBars;
    private LinearLayout personBars;
    private View peopleAll;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        database = InOutDatabase.getInstance(requireContext());
        database.open();

        periodButton = view.findViewById(R.id.stats_period);
        caption = view.findViewById(R.id.stats_caption);
        total = view.findViewById(R.id.stats_total);
        compare = view.findViewById(R.id.stats_compare);
        categoryBars = view.findViewById(R.id.bars_category);
        relationBars = view.findViewById(R.id.bars_relation);
        personBars = view.findViewById(R.id.bars_person);
        peopleAll = view.findViewById(R.id.stats_people_all);

        periodButton.setOnClickListener(this::showPeriodMenu);
        peopleAll.setOnClickListener(v -> startActivity(PeopleActivity.newIntent(requireContext(), year)));

        ChipGroup filter = view.findViewById(R.id.stats_filter);
        filter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            direction = checkedIds.get(0) == R.id.chip_stats_received ? Entry.Direction.RECEIVED : Entry.Direction.SENT;
            reload();
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            reload();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            reload();
        }
    }

    /** 전체 기간과 내역이 있는 연도(최근부터)를 고르는 메뉴 */
    private void showPeriodMenu(View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor, Gravity.END);
        Menu menu = popup.getMenu();
        menu.add(Menu.NONE, 0, 0, R.string.stats_period_all);
        for (int i = 0; i < years.size(); i++) {
            menu.add(Menu.NONE, i + 1, i + 1, periodLabel(years.get(i)));
        }
        menu.setGroupCheckable(Menu.NONE, true, true);
        menu.findItem(year == null ? 0 : years.indexOf(year) + 1).setChecked(true);
        popup.setOnMenuItemClickListener(item -> {
            year = item.getItemId() == 0 ? null : years.get(item.getItemId() - 1);
            reload();
            return true;
        });
        popup.show();
    }

    private String periodLabel(Integer periodYear) {
        return periodYear == null ? getString(R.string.stats_period_all) : getString(R.string.stats_period_year, periodYear);
    }

    private void reload() {
        if (caption == null) {
            return;
        }
        List<Entry> all = database.selectAll();
        years = EntryStats.years(all);
        if (year != null && !years.contains(year)) {
            year = null; // 고른 해의 내역이 모두 지워졌으면 전체 기간으로
        }
        String period = periodLabel(year);
        periodButton.setText(period);
        List<Entry> entries = EntryStats.inYear(all, year);

        boolean sent = direction == Entry.Direction.SENT;
        long mine = EntryStats.sum(entries, direction);
        long other = EntryStats.sum(entries, sent ? Entry.Direction.RECEIVED : Entry.Direction.SENT);
        String mineLabel = getString(sent ? R.string.sent_money : R.string.received_money);
        String otherLabel = getString(sent ? R.string.received_money : R.string.sent_money);

        caption.setText(getString(R.string.stats_caption, mineLabel, period, EntryStats.count(entries, direction)));
        total.setText(MoneyFormat.won(mine));
        if (mine == 0 && other == 0) {
            compare.setText(R.string.stats_empty);
        } else if (mine == other) {
            compare.setText(getString(R.string.stats_same_as, otherLabel));
        } else {
            String diff = MoneyFormat.won(Math.abs(mine - other));
            compare.setText(getString(mine > other ? R.string.stats_more_than : R.string.stats_less_than, otherLabel, diff));
        }

        showBars(categoryBars, EntryStats.byCategory(entries, direction), mine);
        showBars(relationBars, EntryStats.byRelation(entries, direction), mine);
        List<EntryStats.GroupSum> people = EntryStats.byName(entries, direction);
        showBars(personBars, people.subList(0, Math.min(PEOPLE_PREVIEW, people.size())), mine);
        peopleAll.setVisibility(entries.isEmpty() ? View.GONE : View.VISIBLE);
    }

    /** 막대 길이는 보이는 항목 중 가장 큰 것 기준, 비율(%)은 그 방향의 합계(base) 기준 */
    private void showBars(LinearLayout container, List<EntryStats.GroupSum> sums, long base) {
        container.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        if (sums.isEmpty() || base <= 0) {
            TextView empty = new TextView(requireContext(), null, 0, R.style.Text_Caption);
            empty.setText(R.string.stats_empty);
            int padding = Math.round(6 * getResources().getDisplayMetrics().density);
            empty.setPadding(0, padding, 0, padding);
            container.addView(empty);
            return;
        }
        long max = 0;
        for (EntryStats.GroupSum item : sums) {
            max = Math.max(max, item.amount);
        }
        List<TextView> amountViews = new ArrayList<>();
        float widestAmount = 0;
        for (EntryStats.GroupSum item : sums) {
            View row = inflater.inflate(R.layout.item_stat_bar, container, false);
            ((TextView) row.findViewById(R.id.bar_label)).setText(item.label);
            // 아주 작은 항목도 막대가 보이도록 최소 2%는 채운다
            ((LinearProgressIndicator) row.findViewById(R.id.bar_progress))
                    .setProgress((int) Math.max(20, item.amount * 1000 / max));
            TextView amount = row.findViewById(R.id.bar_amount);
            amount.setText(MoneyFormat.won(item.amount));
            widestAmount = Math.max(widestAmount, amount.getPaint().measureText(amount.getText().toString()));
            amountViews.add(amount);
            ((TextView) row.findViewById(R.id.bar_percent)).setText(percentText(item.amount * 100.0 / base));
            container.addView(row);
        }
        // 금액 칸 너비를 가장 긴 금액에 맞춰 통일해야 막대 길이가 줄마다 같아진다
        int amountWidth = (int) Math.ceil(widestAmount);
        for (TextView amount : amountViews) {
            ViewGroup.LayoutParams params = amount.getLayoutParams();
            params.width = Math.max(amount.getMinWidth(), amountWidth + amount.getPaddingLeft() + amount.getPaddingRight());
            amount.setLayoutParams(params);
        }
    }

    private String percentText(double percent) {
        if (percent > 0 && percent < 0.5) {
            return getString(R.string.percent_under_one);
        }
        return getString(R.string.percent, Math.round(percent));
    }
}
