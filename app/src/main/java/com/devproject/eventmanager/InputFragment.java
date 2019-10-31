package com.devproject.eventmanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;



public class InputFragment extends Fragment {

    RecyclerView rv;
    MyAdapter adapter;
    ArrayList<AddList> addLists = new ArrayList<>();

//    OnDatabaseCallback callback;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_input, container, false);

        //RECYCLER
        rv = (RecyclerView) v.findViewById(R.id.addRecyclerView);

        //SET ITS PROPS
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        //ADAPTER
        adapter = new MyAdapter(getContext(), addLists);
        retrieve();

        return v;
    }

    //RETRIEVE
    private void retrieve() {
        DBAdapter db = new DBAdapter(getContext());

        //OPEN
        db.openDB();
        addLists.clear();

        //SELECT
        Cursor c = db.getAllList();

        //LOOP THRU THE DATA ADDING TO ARRAYLIST
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String date = c.getString(2);
            String category = c.getString(3);
            String relation = c.getString(4);
            String money = c.getString(5);

            //CREATE LISTS
            AddList a = new AddList(name, date, category, relation, money, id);

            //ADD TO LISTS
            addLists.add(a);
        }

        //SET ADAPTER TO RV
        if(!(addLists.size()<1))
        {
            rv.setAdapter(adapter);
        }
    }
}
