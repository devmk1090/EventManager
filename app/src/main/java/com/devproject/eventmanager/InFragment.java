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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;



public class InFragment extends Fragment {

    RecyclerView recyclerView;
    InAdapter adapter;
    String TAG = "InFragment";
    InOutDatabase database;

    public InFragment(){}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton floatingButton = (FloatingActionButton) getView().findViewById(R.id.inFab);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_in, container, false);

        database = InOutDatabase.getInstance(getActivity());
        boolean isOpen = database.open();
        if(isOpen) {
            Log.d(TAG, "Book database is open");
        } else {
            Log.d(TAG, "Book database is not open");
        }

        recyclerView = (RecyclerView) v.findViewById(R.id.inRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new InAdapter();
        recyclerView.setAdapter(adapter);
        ArrayList<InList> result = database.selectAllIn();
        adapter.setItems(result);

        adapter.setOnitemClickListener(new InItemClickListener() {
            @Override
            public void onItemClick(InAdapter.ViewHolder holder, View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
                builder.setTitle("알림.");
                builder.setMessage("수정하거나 삭제할 수 있습니다.");
                builder.setIcon(R.drawable.ic_info_black_24dp);
                builder.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), InDetailActivity.class);
                        int i = database.getItemIdIn(position);
                        intent.putExtra("ID", i);
                        intent.putExtra("NAME", database.getNameIn(position));
                        intent.putExtra("DATE", database.getDateIn(position));
                        intent.putExtra("CATEGORY", database.getCategoryIn(position));
                        intent.putExtra("RELATION", database.getRelationIn(position));
                        intent.putExtra("MONEY", database.getMoneyIn(position));
                        intent.putExtra("MEMO", database.getMemoIn(position));
                        startActivity(intent);
                    }
                });
                builder.setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
                        builder.setTitle("알림");
                        builder.setMessage("정말 삭제하시겠습니까 ?");
                        builder.setIcon(R.drawable.ic_warning_black_24dp);
                        builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "받은 돈 내역이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                int i = database.getItemIdIn(position);
                                Log.d(TAG, String.valueOf(i));
                                Log.d(TAG, String.valueOf(position));
                                database.deleteDataIn(i);
                                adapter.removeData(position);
                            }
                        });
                        builder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialog2 = builder.create();
                        dialog2.show();
                    }
                });
                builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return v;
    }
}