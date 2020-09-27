package com.devproject.eventmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class OutFragment extends Fragment implements TextWatcher {

    private static final String TAG = "OutFragment";
    private InOutDatabase database;
    private OutAdapter adapter;
    private ArrayList<OutList> result;
    private RecyclerView recyclerView;
    private EditText edit_search;

    public OutFragment(){}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton floatingButton = getView().findViewById(R.id.outFab);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OutActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_out, container, false);

        edit_search = v.findViewById(R.id.out_edit_search);
        edit_search.addTextChangedListener(this);
        database = InOutDatabase.getInstance(getActivity());
        boolean isOpen = database.open();
        if(isOpen) {
            Log.d(TAG, "Book database is open");
        } else {
            Log.d(TAG, "Book database is not open");
        }

        recyclerView = v.findViewById(R.id.outRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        result = database.selectAllOut();
        adapter = new OutAdapter(getContext(), result);
        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {}
}