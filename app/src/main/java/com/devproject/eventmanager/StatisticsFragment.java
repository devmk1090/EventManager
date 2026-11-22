package com.devproject.eventmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

/** 통계 탭: 나간 돈/받은 돈 합계와 경조사별·관계별 막대. */
public class StatisticsFragment extends Fragment {

    private InOutDatabase database;
    private Entry.Direction direction = Entry.Direction.SENT;

    private TextView caption;
    private TextView total;
    private TextView compare;
    private LinearLayout categoryBars;
    private LinearLayout relationBars;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        database = InOutDatabase.getInstance(requireContext());
        database.open();

        caption = view.findViewById(R.id.stats_caption);
        total = view.findViewById(R.id.stats_total);
        compare = view.findViewById(R.id.stats_compare);
        categoryBars = view.findViewById(R.id.bars_category);
        relationBars = view.findViewById(R.id.bars_relation);

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

    private void reload() {
        if (caption == null) {
            return;
        }
        boolean sent = direction == Entry.Direction.SENT;
        long mine = database.sum(direction);
        long other = database.sum(sent ? Entry.Direction.RECEIVED : Entry.Direction.SENT);
        String mineLabel = getString(sent ? R.string.sent_money : R.string.received_money);
        String otherLabel = getString(sent ? R.string.received_money : R.string.sent_money);

        caption.setText(getString(R.string.stats_caption, mineLabel, database.count(direction)));
        total.setText(MoneyFormat.won(mine));
        if (mine == 0 && other == 0) {
            compare.setText(R.string.stats_empty);
        } else if (mine == other) {
            compare.setText(getString(R.string.stats_same_as, otherLabel));
        } else {
            String diff = MoneyFormat.won(Math.abs(mine - other));
            compare.setText(getString(mine > other ? R.string.stats_more_than : R.string.stats_less_than, otherLabel, diff));
        }

        showBars(categoryBars, database.sumByCategory(direction));
        showBars(relationBars, database.sumByRelation(direction));
    }

    /** 막대 길이는 가장 큰 항목 기준, 비율(%)은 전체 합계 기준 */
    private void showBars(LinearLayout container, List<InOutDatabase.GroupSum> sums) {
        container.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        if (sums.isEmpty()) {
            TextView empty = new TextView(requireContext(), null, 0, R.style.Text_Caption);
            empty.setText(R.string.stats_empty);
            int padding = Math.round(6 * getResources().getDisplayMetrics().density);
            empty.setPadding(0, padding, 0, padding);
            container.addView(empty);
            return;
        }
        long max = 0;
        long sum = 0;
        for (InOutDatabase.GroupSum item : sums) {
            max = Math.max(max, item.amount);
            sum += item.amount;
        }
        List<TextView> amountViews = new ArrayList<>();
        float widestAmount = 0;
        for (InOutDatabase.GroupSum item : sums) {
            View row = inflater.inflate(R.layout.item_stat_bar, container, false);
            ((TextView) row.findViewById(R.id.bar_label)).setText(item.label);
            // 아주 작은 항목도 막대가 보이도록 최소 2%는 채운다
            ((LinearProgressIndicator) row.findViewById(R.id.bar_progress))
                    .setProgress((int) Math.max(20, item.amount * 1000 / max));
            TextView amount = row.findViewById(R.id.bar_amount);
            amount.setText(MoneyFormat.won(item.amount));
            widestAmount = Math.max(widestAmount, amount.getPaint().measureText(amount.getText().toString()));
            amountViews.add(amount);
            ((TextView) row.findViewById(R.id.bar_percent)).setText(percentText(item.amount * 100.0 / sum));
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
