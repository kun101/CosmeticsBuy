package com.example.tony.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class myorders extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;

    private RecyclerView myOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorders);

        myOrders = (RecyclerView) findViewById(R.id.myOrders);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User_Orders").child(firebaseAuth.getUid()).child("ordered_items");
        //mDatabase = FirebaseDatabase.getInstance().getReference().child("User_Carts").child(firebaseAuth.getUid()).child("ItemsInCart");
        myOrders.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        getSupportActionBar().setTitle("My Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.go_shop1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<CartUploadInfo> options =
                new FirebaseRecyclerOptions.Builder<CartUploadInfo>()
                        .setQuery(mDatabase, CartUploadInfo.class)
                        .build();

        final FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CartUploadInfo, BlogViewHolder>(options){

            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_product_row, parent, false);


                return new BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final BlogViewHolder holder, final int position, @NonNull final CartUploadInfo model) {

                holder.setTitle(model.getImageName());
                holder.setPrice("\u20B9 " + model.getImagePrice());
                holder.setImage(getApplicationContext(),model.getImageURL());
                holder.setQuantity(model.getImageQuantity());
                holder.setDateTime(model.getDateTime());

                    holder.mView.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                final AlertDialog.Builder quantityDialog = new AlertDialog.Builder(myorders.this);


                                quantityDialog.setMessage("Do you want to delete this item from your order ?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                getRef(position).removeValue();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //  Action for 'NO' Button
                                                dialog.cancel();
                                            }
                                        });

                                final AlertDialog alert2 = quantityDialog.create();
                                alert2.setTitle("Cancel Order ");
                                alert2.show();
                            }
                            catch (Exception e){

                                Toast.makeText(getApplicationContext(), "Can't Delete Ordered Item ? Go back and try again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
        };

        firebaseRecyclerAdapter.startListening();
        myOrders.setAdapter(firebaseRecyclerAdapter);

        findViewById(R.id.no_item1).setVisibility(View.INVISIBLE);

        FirebaseDatabase.getInstance().getReference().child("User_Orders").child(firebaseAuth.getUid()).child("ordered_items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long childrenCount = dataSnapshot.getChildrenCount();

                int childcount = (int) childrenCount;

                if (childcount == 0 ){

                    findViewById(R.id.no_item1).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private TextView product_title;
        private TextView product_price;
        private TextView product_quantity;
        private TextView product_datetime;

        public BlogViewHolder(final View itemView) {
            super(itemView);

            product_title = itemView.findViewById(R.id.cart_display_title);
            product_price = itemView.findViewById(R.id.cart_display_price);
            product_quantity = itemView.findViewById(R.id.cart_display_quantity);
            product_datetime = itemView.findViewById(R.id.cart_display_datetime);
            mView = itemView;

//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    itemView.findViewById(R.id.remove_item).setVisibility(View.VISIBLE);
//                    return true;
//                }
//            });
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    itemView.findViewById(R.id.remove_item).setVisibility(View.INVISIBLE);
//                }
//            });
        }

        public void setTitle(String title){
            //title = title.substring(0,1).toUpperCase() + title.substring(1).toLowerCase();
            product_title.setText(title);
        }

        public void setPrice(String price){

            product_price.setText(price);

        }

        public void setQuantity(String quantity){

            product_quantity.setText(quantity);

        }

        public void setDateTime(String dateTime){
            product_datetime.setText(dateTime);
        }

        public void setImage(Context ctx, String image){

            ImageView product_image =itemView.findViewById(R.id.cart_display_image);
            Picasso.with(ctx).load(image).into(product_image);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
