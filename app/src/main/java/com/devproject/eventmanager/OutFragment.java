package com.devproject.eventmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class OutFragment extends Fragment implements TextWatcher {

    RecyclerView recyclerView;
    OutAdapter adapter;
    String TAG = "OutFragment";
    InOutDatabase database;
    EditText searchEdit;

    public OutFragment(){}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton floatingButton = (FloatingActionButton) getView().findViewById(R.id.outFab);
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

        searchEdit = v.findViewById(R.id.searchEdit);
        searchEdit.addTextChangedListener(this);
        database = InOutDatabase.getInstance(getActivity());
        boolean isOpen = database.open();
        if(isOpen) {
            Log.d(TAG, "Book database is open");
        } else {
            Log.d(TAG, "Book database is not open");
        }

        recyclerView = (RecyclerView) v.findViewById(R.id.outRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<OutList> result = database.selectAllOut();
        adapter = new OutAdapter(getContext(), result);
        recyclerView.setAdapter(adapter);
        adapter.setItems(result);

        adapter.setOnitemClickListener(new OutItemClickListener() {
            @Override
            public void onItemClick(OutAdapter.ViewHolder holder, View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), 3);
                builder.setTitle("알림");
                builder.setMessage("수정하거나 삭제할 수 있습니다.");
                builder.setIcon(R.drawable.ic_info_black_24dp);
                builder.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), OutDetailActivity.class);
                        int i = database.getItemIdOut(position);
                        Log.d(TAG, String.valueOf(i));
                        Log.d(TAG, String.valueOf(position));
                        intent.putExtra("ID", i);
                        intent.putExtra("NAME", database.getNameOut(position));
                        intent.putExtra("DATE", database.getDateOut(position));
                        intent.putExtra("CATEGORY", database.getCategoryOut(position));
                        intent.putExtra("RELATION", database.getRelationOut(position));
                        intent.putExtra("MONEY", database.getMoneyOut(position));
                        intent.putExtra("MEMO", database.getMemoOut(position));
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
                                Toast.makeText(getActivity(), "나간 돈 내역이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                int i = database.getItemIdOut(position);
                                Log.d(TAG, String.valueOf(i));
                                Log.d(TAG, String.valueOf(position));
                                database.deleteDataOut(i);
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

    }
    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        adapter.getFilter().filter(charSequence);
    }
    @Override
    public void afterTextChanged(Editable editable) {

    }
}