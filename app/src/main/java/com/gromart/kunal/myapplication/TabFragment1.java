package com.example.tony.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.GridLayoutManager;

public class TabFragment1 extends Fragment {

    private static final String TAG = "Tab 1";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.listRecyclerView);

        ListAdapter listAdapter = new ListAdapter();

        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);;
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }
}
