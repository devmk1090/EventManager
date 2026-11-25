package com.devproject.eventmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 내역 목록: 맨 위 합계 카드, 필터 칩, 그 아래 월 제목과 내역(같은 달은 카드 하나로 보이게).
 * 사람별 내역(PersonActivity)에서는 필터 칩 없이 쓴다.
 */
class EntryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    interface Listener {
        void onFilterChanged(EntryList.Filter filter);

        void onEntryClicked(Entry entry);
    }

    private static final int TYPE_SUMMARY = 0;
    private static final int TYPE_FILTERS = 1;
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ENTRY = 3;
    private static final int TYPE_EMPTY = 4;

    private final Listener listener;
    private final boolean showFilters;
    private final int fixedItems; // 합계 카드 (+ 필터 칩)
    private long receivedTotal;
    private long sentTotal;
    private EntryList.Filter filter = EntryList.Filter.ALL;
    private List<EntryList.Row> rows = new ArrayList<>();
    private String emptyMessage = "";

    EntryAdapter(Listener listener) {
        this(listener, true);
    }

    EntryAdapter(Listener listener, boolean showFilters) {
        this.listener = listener;
        this.showFilters = showFilters;
        this.fixedItems = showFilters ? 2 : 1;
    }

    @SuppressLint("NotifyDataSetChanged")
    void submit(long receivedTotal, long sentTotal, EntryList.Filter filter, List<EntryList.Row> rows, String emptyMessage) {
        this.receivedTotal = receivedTotal;
        this.sentTotal = sentTotal;
        this.filter = filter;
        this.rows = rows;
        this.emptyMessage = emptyMessage;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return fixedItems + (rows.isEmpty() ? 1 : rows.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_SUMMARY;
        }
        if (showFilters && position == 1) {
            return TYPE_FILTERS;
        }
        if (rows.isEmpty()) {
            return TYPE_EMPTY;
        }
        return rows.get(position - fixedItems).isHeader() ? TYPE_HEADER : TYPE_ENTRY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_SUMMARY:
                return new SummaryHolder(inflater.inflate(R.layout.item_history_summary, parent, false));
            case TYPE_FILTERS:
                return new FiltersHolder(inflater.inflate(R.layout.item_history_filters, parent, false));
            case TYPE_HEADER:
                return new TextHolder(inflater.inflate(R.layout.item_month_header, parent, false));
            case TYPE_EMPTY:
                return new TextHolder(inflater.inflate(R.layout.item_empty, parent, false));
            default:
                return new EntryHolder(inflater.inflate(R.layout.item_entry, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SummaryHolder) {
            ((SummaryHolder) holder).bind(receivedTotal, sentTotal);
        } else if (holder instanceof FiltersHolder) {
            ((FiltersHolder) holder).bind(filter);
        } else if (holder instanceof EntryHolder) {
            ((EntryHolder) holder).bind(rows.get(position - fixedItems));
        } else if (holder instanceof TextHolder) {
            String text = rows.isEmpty() ? emptyMessage : rows.get(position - fixedItems).header;
            ((TextHolder) holder).text.setText(text);
        }
    }

    static final class SummaryHolder extends RecyclerView.ViewHolder {
        private final TextView received;
        private final TextView sent;
        private final TextView difference;

        SummaryHolder(View view) {
            super(view);
            received = view.findViewById(R.id.summary_received);
            sent = view.findViewById(R.id.summary_sent);
            difference = view.findViewById(R.id.summary_difference);
        }

        void bind(long receivedTotal, long sentTotal) {
            received.setText(MoneyFormat.won(receivedTotal));
            sent.setText(MoneyFormat.won(sentTotal));
            long diff = receivedTotal - sentTotal;
            difference.setText(diff == 0 ? MoneyFormat.won(0) : MoneyFormat.signedWon(Math.abs(diff), diff > 0));
        }
    }

    final class FiltersHolder extends RecyclerView.ViewHolder {
        private final ChipGroup group;
        private boolean binding;

        FiltersHolder(View view) {
            super(view);
            group = view.findViewById(R.id.filter_group);
            group.setOnCheckedStateChangeListener((chipGroup, checkedIds) -> {
                if (binding || checkedIds.isEmpty()) {
                    return;
                }
                int id = checkedIds.get(0);
                EntryList.Filter selected = id == R.id.chip_received ? EntryList.Filter.RECEIVED
                        : id == R.id.chip_sent ? EntryList.Filter.SENT
                        : EntryList.Filter.ALL;
                listener.onFilterChanged(selected);
            });
        }

        void bind(EntryList.Filter filter) {
            binding = true;
            int id = filter == EntryList.Filter.RECEIVED ? R.id.chip_received
                    : filter == EntryList.Filter.SENT ? R.id.chip_sent
                    : R.id.chip_all;
            group.check(id);
            binding = false;
        }
    }

    static final class TextHolder extends RecyclerView.ViewHolder {
        final TextView text;

        TextHolder(View view) {
            super(view);
            text = (TextView) view;
        }
    }

    final class EntryHolder extends RecyclerView.ViewHolder {
        private final TextView avatar;
        private final TextView name;
        private final TextView meta;
        private final TextView memo;
        private final TextView amount;
        private final TextView direction;

        EntryHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.entry_avatar);
            name = view.findViewById(R.id.entry_name);
            meta = view.findViewById(R.id.entry_meta);
            memo = view.findViewById(R.id.entry_memo);
            amount = view.findViewById(R.id.entry_amount);
            direction = view.findViewById(R.id.entry_direction);
        }

        void bind(EntryList.Row row) {
            Entry entry = row.entry;
            Context context = itemView.getContext();
            boolean received = entry.isReceived();
            int toneColor = ContextCompat.getColor(context, received ? R.color.accent : R.color.ink);

            avatar.setText(initial(entry.name));
            avatar.setBackgroundResource(received ? R.drawable.bg_avatar_received : R.drawable.bg_avatar_sent);
            avatar.setTextColor(ContextCompat.getColor(context, received ? R.color.accent : R.color.text_chip));
            name.setText(entry.name);
            meta.setText(metaText(entry));
            String memoText = entry.memo.trim();
            memo.setVisibility(memoText.isEmpty() ? View.GONE : View.VISIBLE);
            memo.setText(memoText);
            amount.setText(MoneyFormat.signedWon(entry.amount(), received));
            amount.setTextColor(toneColor);
            direction.setText(received ? R.string.direction_received : R.string.direction_sent);

            // 같은 달 내역이 흰 카드 하나로 이어 보이도록 위/아래 모서리와 여백을 정한다
            if (row.first && row.last) {
                itemView.setBackgroundResource(R.drawable.bg_card_single);
            } else if (row.first) {
                itemView.setBackgroundResource(R.drawable.bg_card_top);
            } else if (row.last) {
                itemView.setBackgroundResource(R.drawable.bg_card_bottom);
            } else {
                itemView.setBackgroundResource(R.drawable.bg_card_middle);
            }
            float density = context.getResources().getDisplayMetrics().density;
            int horizontal = Math.round(16 * density);
            itemView.setPadding(horizontal, Math.round((row.first ? 16 : 12) * density),
                    horizontal, Math.round((row.last ? 16 : 12) * density));

            itemView.setOnClickListener(v -> listener.onEntryClicked(entry));
        }

        private String initial(String text) {
            String trimmed = text.trim();
            return trimmed.isEmpty() ? "?" : new String(Character.toChars(trimmed.codePointAt(0)));
        }

        private String metaText(Entry entry) {
            StringBuilder text = new StringBuilder();
            append(text, entry.categoryLabel());
            append(text, entry.relationLabel());
            if (entry.parsedDate != null) {
                append(text, entry.parsedDate.shortText());
            }
            return text.toString();
        }

        private void append(StringBuilder text, String part) {
            if (part.isEmpty()) {
                return;
            }
            if (text.length() > 0) {
                text.append(" · ");
            }
            text.append(part);
        }
    }
}
