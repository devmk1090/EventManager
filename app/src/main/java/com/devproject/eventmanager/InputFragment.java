package com.devproject.eventmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;



public class InputFragment extends Fragment {

    RecyclerView recyclerView;
    AddAdapter adapter;

    OnDatabaseCallback callback;
    String TAG = "InputFragment";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton floatingButton = (FloatingActionButton) getView().findViewById(R.id.fab);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callback = (OnDatabaseCallback) getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_input, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.addRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AddAdapter();
        recyclerView.setAdapter(adapter);

        ArrayList<AddList> result = callback.selectAll();
        adapter.setItems(result);
//        Bundle bundle = getArguments();
//        String name = bundle.getString("name");
//        String date = bundle.getString("date");
//        String category = bundle.getString("category");
//        String relation = bundle.getString("relation");
//        String money = bundle.getString("money");

//        if(running == true) {
//            AddList addList = new AddList(name, date, category, relation, money);
//            addArrayList.add(addList);
//            running = false;
//            return v;
//        } else {
//            return v;
//        }
        adapter.setOnitemClickListener(new OnAddItemClickListener() {
            @Override
            public void onItemClick(AddAdapter.ViewHolder holder, View view, int position) {
                AddList item = adapter.getItem(position);

                Toast.makeText(getContext(), "아이템 선택됨 : " + item.getName(), Toast.LENGTH_LONG).show();
            }
        });
//        Button button = v.findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ArrayList<AddList> result = callback.selectAll();
//                adapter.setItems(result);
//                adapter.notifyDataSetChanged();
//            }
//        });
        return v;
    }
}
