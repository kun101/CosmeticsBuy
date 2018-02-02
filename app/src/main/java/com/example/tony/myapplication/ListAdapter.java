package com.example.tony.myapplication;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import static android.support.v4.content.ContextCompat.startActivities;
import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by Tony on 1/1/2018.
 */

public class ListAdapter extends RecyclerView.Adapter {

    public static String bakery = "babooshka";
    public static String cookie = "cookie";
    public static String cream = "cream";
    public static String basket = "basket";
    public static String KEY_MEAL = "meal";


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent,false);
        return new listViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((listViewHolder) holder).bindView(position);

    }

    @Override
    public int getItemCount() {
        return OurData.title.length;
    }

    private class listViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mItemText;
        private ImageView mItemImage;
        private RelativeLayout relativeLayout;

        public listViewHolder(View itemView){

            super(itemView);

            mItemText = (TextView) itemView.findViewById(R.id.itemText);
            mItemImage = (ImageView)itemView.findViewById(R.id.itemImage);
            relativeLayout = itemView.findViewById(R.id.item_category);

            itemView.setOnClickListener(this);

        }

        public void bindView(int position){

            mItemText.setText(OurData.title[position]);
            mItemImage.setImageResource(OurData.picturePath[position]);

        }

        public void onClick(View view) {

            switch(getAdapterPosition()){
                case 0:
                    KEY_MEAL = OurData.title[getAdapterPosition()];
                case 1:
                    KEY_MEAL = OurData.title[getAdapterPosition()];
                case 2:
                    KEY_MEAL = OurData.title[getAdapterPosition()];
                case 3:
                    KEY_MEAL = OurData.title[getAdapterPosition()];
            }

            Intent intent = new Intent(view.getContext(), groceryitems.class);

            intent.putExtra("category",KEY_MEAL);

            startActivity(view.getContext(), intent, Bundle.EMPTY);

        }
    }

}
