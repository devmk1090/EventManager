package com.devproject.eventmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OutAdapter extends RecyclerView.Adapter<OutAdapter.ViewHolder>
        implements Filterable {

    private static final String TAG = "OutAdapter";

    InOutDatabase database;
    Context context;
    ArrayList<OutList> items;
    ArrayList<OutList> filteredList;

    public OutAdapter (Context context, ArrayList<OutList> outLists) {
        super();
        this.context = context;
        this.items = outLists;
        this.filteredList = outLists;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    filteredList = items;
                } else {
                    ArrayList<OutList> filteringList = new ArrayList<>();
                    for(OutList item : items) {
                        if(item.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(item);
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<OutList>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView getNameData;
        private TextView getCalendarData;
        private TextView getCategoryData;
        private TextView getRelationData;
        private TextView getMoneyData;
        private TextView getMemoData;

        public ViewHolder(View itemView) {
            super(itemView);
            getNameData = itemView.findViewById(R.id.getNameData);
            getCalendarData = itemView.findViewById(R.id.getCalendarData);
            getCategoryData = itemView.findViewById(R.id.getCategoryData);
            getRelationData = itemView.findViewById(R.id.getRelationData);
            getMoneyData = itemView.findViewById(R.id.getMoneyData);
            getMemoData = itemView.findViewById(R.id.getMemoData);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) {
                        return;
                    }
                    OutList clickedItem = filteredList.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, 3);
                    builder.setTitle("알림")
                            .setMessage("수정하거나 삭제할 수 있습니다")
                            .setIcon(R.drawable.ic_info_black_24dp)
                            .setNegativeButton("수정", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 누른 항목의 _id로 수정할 행을 정한다
                                    int i = clickedItem.getId();
                                    Intent intent = new Intent(context, OutDetailActivity.class);
                                    intent.putExtra("ID", i);
                                    intent.putExtra("NAME", clickedItem.getName());
                                    intent.putExtra("DATE", clickedItem.getDate());
                                    intent.putExtra("CATEGORY", clickedItem.getCategory());
                                    intent.putExtra("RELATION", clickedItem.getRelation());
                                    intent.putExtra("MONEY", database.getMoneyOut(i));
                                    intent.putExtra("MEMO", clickedItem.getMemo());
                                    context.startActivity(intent);
                                }
                            })
                            .setNeutralButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context, 3);
                                    builder.setTitle("알림");
                                    builder.setMessage("정말 삭제하시겠습니까 ?");
                                    builder.setIcon(R.drawable.ic_warning_black_24dp);
                                    builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // 누른 항목의 _id로 지운다. 같은 (이름, 날짜, 경조사, 관계)가 둘 있어도 누른 행만 지워진다.
                                            if (database.deleteDataOut(clickedItem.getId())) {
                                                Toast.makeText(context, "나간 돈 내역이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                // 화면 목록에서는 누른 항목 객체를 지운다. DB 조회 순서의 위치를 쓰면
                                                // 목록(DATE desc 정렬)과 순서가 달라 다른 항목이 사라진다.
                                                removeItem(clickedItem);
                                            } else {
                                                Toast.makeText(context, "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    builder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).create().show();
                                }
                            })
                            .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            })
                            .create()
                            .show();
                }
            });
        }
        @SuppressLint("SetTextI18n")
        public void setItem(OutList item) {
            getNameData.setText(item.getName());

            getCategoryData.setText(item.getCategory());
            getRelationData.setText(item.getRelation());
            getCalendarData.setText(item.getDate());

            getMemoData.setText(item.getMemo());
            DecimalFormat formatter = new DecimalFormat("###,###");
            int moneyformat = Integer.parseInt(item.getMoney());
            getMoneyData.setText(formatter.format(moneyformat) + "원");
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.asform_rev, viewGroup, false);
        database = InOutDatabase.getInstance(context);
        boolean isOpen = database.open();
        if(isOpen) {
            Log.d(TAG, "Book database is open");
        } else {
            Log.d(TAG, "Book database is not open");
        }
        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        OutList item = filteredList.get(position);
        viewHolder.setItem(item);
    }
    @Override
    public int getItemCount() {
        return filteredList.size();
    }
    public void addItem(OutList item) {
        items.add(item);
    }
    public void setItems(ArrayList<OutList> items) {
        this.items = items;
    }
    public OutList getItem(int position) {
        return items.get(position);
    }
    public void setItem(int position, OutList item) {
        items.set(position, item);
    }

    private void removeItem(OutList item) {
        int position = ListUtils.removeItem(items, filteredList, item);
        if (position >= 0) {
            notifyItemRemoved(position);
        } else {
            notifyDataSetChanged();
        }
    }
}