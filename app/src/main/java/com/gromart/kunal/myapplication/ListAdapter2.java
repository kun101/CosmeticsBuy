package com.example.tony.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by Tony on 1/1/2018.
 */

public class ListAdapter2 extends RecyclerView.Adapter {

    public static String KEY_MEAL = "meal";

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item2, parent,false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((listViewHolder) holder).bindView(position);

    }

    @Override
    public int getItemCount() {
        return OurData2.title.length;
    }

    private class listViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mItemText;
        private ImageView mItemImage;

        public listViewHolder(View itemView){

            super(itemView);

            mItemText = itemView.findViewById(R.id.itemText1);
            mItemImage = itemView.findViewById(R.id.itemImage1);
            itemView.setOnClickListener(this);

            mItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    KEY_MEAL = OurData2.title[getAdapterPosition()];

                    Intent intent = new Intent(view.getContext(), stationeryitems.class);

                    intent.putExtra("category",KEY_MEAL);

                    startActivity(view.getContext(), intent, Bundle.EMPTY);

                }
            });

        }

        public void bindView(int position){

            mItemText.setText(OurData2.title[position]);
            mItemImage.setImageResource(OurData2.picturePath[position]);

        }

        public void onClick(View view){

            KEY_MEAL = OurData2.title[getAdapterPosition()];

//            switch(getAdapterPosition()){
//                case 0:
//                    KEY_MEAL = OurData2.title[getAdapterPosition()];
//                case 1:
//                    KEY_MEAL = OurData2.title[getAdapterPosition()];
//                case 2:
//                    KEY_MEAL = OurData2.title[getAdapterPosition()];
//                case 3:
//                    KEY_MEAL = OurData2.title[getAdapterPosition()];
//            }

            Intent intent = new Intent(view.getContext(), stationeryitems.class);

            intent.putExtra("category",KEY_MEAL);

            startActivity(view.getContext(), intent, Bundle.EMPTY);

        }

    }


}
