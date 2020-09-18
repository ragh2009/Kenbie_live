package com.kenbie.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kenbie.R;
import com.kenbie.adapters.SortAdapter;
import com.kenbie.model.Option;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SortFragment extends BaseFragment {
    private ArrayList<Option> sortArray;
    private int type = 1;

    public SortFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            type = getArguments().getInt("Type", 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sort, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (type == 1)
            sortArray = bindSortData();
        else
            sortArray = bindTypeData();

        ListView sortList = (ListView) view.findViewById(R.id.sort_list);
        SortAdapter sortAdapter = new SortAdapter(getActivity(), sortArray, type == 1 ? mActivity.sortBy : mActivity.position);
        sortList.setAdapter(sortAdapter);
        sortList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type == 1)
                    mActivity.sortBy = position + 1;
                else {
                    mActivity.uType = position + 1;
                    mActivity.position = position;
                }
                mActivity.onBackPressed();
            }
        });
    }

    private ArrayList<Option> bindTypeData() {
        String[] name = {"All", "Model", "Agency", "Photographer"};
        ArrayList<Option> values = new ArrayList<>();
        for (int i = 0; i < name.length; i++) {
            Option op = new Option();
            op.setId(i);
            op.setTitle(name[i]);
            values.add(op);
        }

        return values;
    }

    private ArrayList<Option> bindSortData() {
        String[] name = {"Online", "Offline", "All"};
        ArrayList<Option> values = new ArrayList<>();
        for (int i = 0; i < name.length; i++) {
            Option op = new Option();
            op.setId(i);
            op.setTitle(name[i]);
            values.add(op);
        }

        return values;
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(33, type == 1?"SORT BY":"SELECT USER TYPE", true, false, false);
        super.onResume();
    }
}
