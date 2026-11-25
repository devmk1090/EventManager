package com.devproject.eventmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

/**
 * 한 사람과 주고받은 내역: 받은 돈·나간 돈·차액 카드와 월별 목록(기간 구분 없이 전부).
 * 통계의 사람별 목록과 입력 화면의 ‘이 분과 주고받은 내역’에서 연다. 이름은 앞뒤 공백을 빼고 같으면 같은 사람으로 본다.
 */
public class PersonActivity extends AppCompatActivity implements EntryAdapter.Listener {

    private static final String EXTRA_NAME = "name";

    private InOutDatabase database;
    private EntryAdapter adapter;
    private String name;

    public static Intent newIntent(Context context, String name) {
        return new Intent(context, PersonActivity.class).putExtra(EXTRA_NAME, name.trim());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_person);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(true);

        RecyclerView list = findViewById(R.id.person_list);
        View fab = findViewById(R.id.fab_add);
        float density = getResources().getDisplayMetrics().density;
        // 위는 상태 표시줄, 아래는 내비게이션 바만큼 띄우고 목록 끝은 추가 버튼에 가리지 않게 한다
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.person_root), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, 0);
            list.setPadding(0, 0, 0, bars.bottom + Math.round(88 * density));
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
            params.bottomMargin = bars.bottom + Math.round(16 * density);
            fab.setLayoutParams(params);
            return WindowInsetsCompat.CONSUMED;
        });

        database = InOutDatabase.getInstance(this);
        database.open();
        String extra = getIntent().getStringExtra(EXTRA_NAME);
        name = extra == null ? "" : extra.trim();

        ((TextView) findViewById(R.id.person_title)).setText(name);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntryAdapter(this, false);
        list.setAdapter(adapter);

        fab.setOnClickListener(v ->
                startActivity(EntryFormActivity.newEntryIntent(this, Entry.Direction.SENT, name)));
    }

    // 내역을 고치거나 추가하고 돌아오면 다시 읽는다
    @Override
    protected void onResume() {
        super.onResume();
        List<Entry> entries = EntryStats.withName(database.selectAll(), name);
        EntryList.sort(entries);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        adapter.submit(EntryStats.sum(entries, Entry.Direction.RECEIVED), EntryStats.sum(entries, Entry.Direction.SENT),
                EntryList.Filter.ALL, EntryList.group(entries, currentYear), getString(R.string.person_empty));
    }

    @Override
    public void onFilterChanged(EntryList.Filter filter) {
        // 필터 칩을 보여 주지 않는다
    }

    @Override
    public void onEntryClicked(Entry entry) {
        startActivity(EntryFormActivity.editIntent(this, entry));
    }
}
