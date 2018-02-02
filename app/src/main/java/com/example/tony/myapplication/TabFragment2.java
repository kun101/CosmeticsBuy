package com.example.tony.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabFragment2 extends Fragment {

    private static final String TAG = "Tab 2";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.listRecyclerView2);

        ListAdapter2 listAdapter = new ListAdapter2();

        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);;
        recyclerView.setLayoutManager(layoutManager);

        return view;

    }
}
