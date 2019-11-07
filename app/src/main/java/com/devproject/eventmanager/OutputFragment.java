package com.devproject.eventmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;



public class OutputFragment extends Fragment {

    RecyclerView recyclerView;
    SubAdapter adapter;

    String TAG = "OutputFragment";

    SubDatabase database;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton floatingButton = (FloatingActionButton) getView().findViewById(R.id.subfab);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SubActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_output, container, false);

        if(database != null) {
            database.close();
            database = null;
        }

        database = SubDatabase.getInstance(getActivity());
        boolean isOpen = database.open();
        if(isOpen) {
            Log.d(TAG, "Book database is open");
        } else {
            Log.d(TAG, "Book database is not open");
        }

        recyclerView = (RecyclerView) v.findViewById(R.id.subRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new SubAdapter();
        recyclerView.setAdapter(adapter);
        ArrayList<SubList> result = database.selectAll();
        adapter.setItems(result);

        adapter.setOnitemClickListener(new OnSubItemClickListener() {
            @Override
            public void onItemClick(SubAdapter.ViewHolder holder, View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choice ?");
                builder.setMessage("Delete or Revise");
                builder.setIcon(android.R.drawable.ic_dialog_alert);

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int i = database.getItemId(position);
                        Log.d(TAG, String.valueOf(i));
                        database.deleteData(i);
                    }
                });
                builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("Revise", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), SubDetailActivity.class);
                        int i = database.getItemId(position);
                        intent.putExtra("ID", i);
                        intent.putExtra("NAME", database.getName(position));
                        intent.putExtra("DATE", database.getDate(position));
                        intent.putExtra("CATEGORY", database.getCategory(position));
                        intent.putExtra("RELATION", database.getRelation(position));
                        intent.putExtra("MONEY", database.getMoney(position));
                        startActivity(intent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return v;
    }
}