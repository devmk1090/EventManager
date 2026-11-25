package com.devproject.eventmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 사람별 목록: 통계 탭에서 고른 기간 안에서 주고받은 합계가 큰 사람부터 보여 준다.
 * 이름으로 찾을 수 있고, 누르면 그 사람과 주고받은 내역(PersonActivity, 기간 구분 없이 전부)을 연다.
 */
public class PeopleActivity extends AppCompatActivity {

    private static final String EXTRA_YEAR = "year"; // 없으면 전체 기간

    private InOutDatabase database;
    private Integer year;
    private List<EntryStats.Person> people = new ArrayList<>();
    private PeopleAdapter adapter;
    private EditText searchInput;
    private TextView caption;
    private TextView emptyText;

    public static Intent newIntent(Context context, Integer year) {
        Intent intent = new Intent(context, PeopleActivity.class);
        if (year != null) {
            intent.putExtra(EXTRA_YEAR, year.intValue());
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_people);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        // 위는 상태 표시줄, 아래는 내비게이션 바와 키보드 중 큰 쪽만큼 띄운다
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.people_root), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            view.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
            return WindowInsetsCompat.CONSUMED;
        });

        database = InOutDatabase.getInstance(this);
        database.open();
        year = getIntent().hasExtra(EXTRA_YEAR) ? getIntent().getIntExtra(EXTRA_YEAR, 0) : null;

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        caption = findViewById(R.id.people_caption);
        emptyText = findViewById(R.id.people_empty);
        searchInput = findViewById(R.id.people_search);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                render();
            }
        });
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
            }
            return true;
        });

        RecyclerView list = findViewById(R.id.people_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PeopleAdapter(person -> startActivity(PersonActivity.newIntent(this, person.name)));
        list.setAdapter(adapter);
    }

    // 사람 화면에서 내역을 고치고 돌아오면 다시 읽는다
    @Override
    protected void onResume() {
        super.onResume();
        people = EntryStats.people(EntryStats.inYear(database.selectAll(), year));
        render();
    }

    private void render() {
        String query = searchInput.getText().toString().trim();
        String needle = query.toLowerCase(Locale.ROOT);
        List<EntryStats.Person> visible = new ArrayList<>();
        for (EntryStats.Person person : people) {
            if (person.name.toLowerCase(Locale.ROOT).contains(needle)) {
                visible.add(person);
            }
        }
        String period = year == null ? getString(R.string.stats_period_all) : getString(R.string.stats_period_year, year);
        caption.setText(getString(R.string.people_caption, period, people.size()));
        adapter.submit(visible);
        if (visible.isEmpty()) {
            emptyText.setText(people.isEmpty() ? getString(R.string.stats_empty) : getString(R.string.people_empty_search, query));
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    /** 사람 한 줄: 이름, 건수, 받은 돈(+, 청록)·나간 돈(−, 먹색). 목록 전체가 카드 하나로 보이게 배경을 고른다. */
    static final class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.Holder> {

        interface Listener {
            void onPersonClicked(EntryStats.Person person);
        }

        private final Listener listener;
        private List<EntryStats.Person> people = new ArrayList<>();

        PeopleAdapter(Listener listener) {
            this.listener = listener;
        }

        @SuppressLint("NotifyDataSetChanged")
        void submit(List<EntryStats.Person> people) {
            this.people = people;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return people.size();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_person, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.bind(people.get(position), position == 0, position == people.size() - 1);
        }

        final class Holder extends RecyclerView.ViewHolder {
            private final TextView avatar;
            private final TextView name;
            private final TextView count;
            private final TextView received;
            private final TextView sent;

            Holder(View view) {
                super(view);
                avatar = view.findViewById(R.id.person_avatar);
                name = view.findViewById(R.id.person_name);
                count = view.findViewById(R.id.person_count);
                received = view.findViewById(R.id.person_received);
                sent = view.findViewById(R.id.person_sent);
            }

            void bind(EntryStats.Person person, boolean first, boolean last) {
                Context context = itemView.getContext();
                avatar.setText(person.name.isEmpty() ? "?" : new String(Character.toChars(person.name.codePointAt(0))));
                name.setText(person.name);
                count.setText(context.getString(R.string.person_entries_count, person.count));
                received.setText(MoneyFormat.signedWon(person.received, true));
                received.setVisibility(person.received > 0 ? View.VISIBLE : View.GONE);
                // 둘 다 0원이면 나간 돈 자리에 0원을 보여 준다
                sent.setText(person.sent > 0 || person.received > 0 ? MoneyFormat.signedWon(person.sent, false) : MoneyFormat.won(0));
                sent.setVisibility(person.sent > 0 || person.received == 0 ? View.VISIBLE : View.GONE);

                if (first && last) {
                    itemView.setBackgroundResource(R.drawable.bg_card_single);
                } else if (first) {
                    itemView.setBackgroundResource(R.drawable.bg_card_top);
                } else if (last) {
                    itemView.setBackgroundResource(R.drawable.bg_card_bottom);
                } else {
                    itemView.setBackgroundResource(R.drawable.bg_card_middle);
                }
                float density = context.getResources().getDisplayMetrics().density;
                int horizontal = Math.round(16 * density);
                itemView.setPadding(horizontal, Math.round((first ? 16 : 12) * density),
                        horizontal, Math.round((last ? 16 : 12) * density));
                itemView.setOnClickListener(v -> listener.onPersonClicked(person));
            }
        }
    }
}
