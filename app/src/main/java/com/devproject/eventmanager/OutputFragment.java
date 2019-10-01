package com.devproject.eventmanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.devproject.eventmanager.SubActivity.running;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OutputFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OutputFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OutputFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public OutputFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OutputFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OutputFragment newInstance(String param1, String param2) {
        OutputFragment fragment = new OutputFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ListView listView;
    private SubListAdapter subListAdapter;
    static  ArrayList<SubList> subArrayList = new ArrayList<>();
    private String TAG = "OutputFragment";
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton floatingButton = (FloatingActionButton)getView().findViewById(R.id.fab_sub);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SubActivity.class);
                startActivity(intent);
            }
        });
        listView = (ListView) getView().findViewById(R.id.subListView);
        subListAdapter = new SubListAdapter(getContext().getApplicationContext(), subArrayList);
        listView.setAdapter(subListAdapter);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_output, container, false);

        Bundle bundle = getArguments();
        String name = bundle.getString("name");
        String date = bundle.getString("date");
        String category = bundle.getString("category");
        String relation = bundle.getString("relation");
        String money = bundle.getString("money");

        if(running == true) {
            SubList subList = new SubList(name, date, category, relation, money);
            subArrayList.add(subList);
            running = false;
            return v;
        } else {
            return v;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
