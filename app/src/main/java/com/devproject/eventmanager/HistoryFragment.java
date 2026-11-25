package com.devproject.eventmanager;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/** 내역 탭: 받은 돈·나간 돈을 한 목록에 날짜 최신순·월별로 보여 준다. */
public class HistoryFragment extends Fragment implements EntryAdapter.Listener {

    private InOutDatabase database;
    private EntryAdapter adapter;
    private View searchBar;
    private EditText searchInput;

    private EntryList.Filter filter = EntryList.Filter.ALL;
    private List<Entry> entries = new ArrayList<>();
    private long receivedTotal;
    private long sentTotal;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        database = InOutDatabase.getInstance(requireContext());
        database.open();

        searchBar = view.findViewById(R.id.search_bar);
        searchInput = view.findViewById(R.id.search_input);
        view.findViewById(R.id.btn_search).setOnClickListener(v -> openSearch());
        view.findViewById(R.id.btn_search_close).setOnClickListener(v -> closeSearch());
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
            hideKeyboard();
            return true;
        });

        RecyclerView list = view.findViewById(R.id.entry_list);
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new EntryAdapter(this);
        list.setAdapter(adapter);

        view.findViewById(R.id.fab_add).setOnClickListener(v -> {
            Entry.Direction direction = filter == EntryList.Filter.RECEIVED
                    ? Entry.Direction.RECEIVED : Entry.Direction.SENT;
            startActivity(EntryFormActivity.newEntryIntent(requireContext(), direction));
        });
        return view;
    }

    // 입력·수정 화면에서 돌아오면 다시 읽는다
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
        entries = database.selectAll();
        EntryList.sort(entries);
        receivedTotal = EntryStats.sum(entries, Entry.Direction.RECEIVED);
        sentTotal = EntryStats.sum(entries, Entry.Direction.SENT);
        render();
    }

    private void render() {
        if (adapter == null) {
            return;
        }
        String query = searchInput.getText().toString().trim();
        List<Entry> visible = EntryList.filter(entries, filter, query);
        String emptyMessage;
        if (entries.isEmpty()) {
            emptyMessage = getString(R.string.empty_history);
        } else if (!query.isEmpty()) {
            emptyMessage = getString(R.string.empty_search, query);
        } else {
            emptyMessage = getString(R.string.empty_filtered);
        }
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        adapter.submit(receivedTotal, sentTotal, filter, EntryList.group(visible, currentYear), emptyMessage);
    }

    @Override
    public void onFilterChanged(EntryList.Filter newFilter) {
        filter = newFilter;
        render();
    }

    @Override
    public void onEntryClicked(Entry entry) {
        startActivity(EntryFormActivity.editIntent(requireContext(), entry));
    }

    private void openSearch() {
        searchBar.setVisibility(View.VISIBLE);
        searchInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /** 검색창이 열려 있으면 닫고 true를 돌려준다. 뒤로가기에서 쓴다. */
    public boolean closeSearch() {
        if (searchBar == null || searchBar.getVisibility() != View.VISIBLE) {
            return false;
        }
        hideKeyboard();
        searchBar.setVisibility(View.GONE);
        searchInput.setText("");
        return true;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        }
        searchInput.clearFocus();
    }
}
