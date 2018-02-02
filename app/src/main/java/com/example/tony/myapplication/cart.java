package com.example.tony.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.wallet.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class cart extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private TextView itemCost;
    private FloatingActionButton removeItem;

    private RecyclerView cart_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        firebaseAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User_Carts").child(firebaseAuth.getUid()).child("ItemsInCart");

        cart_list = (RecyclerView)findViewById(R.id.cart_items);
        removeItem = (FloatingActionButton) findViewById(R.id.remove_item);
        itemCost = (TextView) findViewById(R.id.cart_cost);
        cart_list.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));

        getSupportActionBar().setTitle("Cart");

        findViewById(R.id.go_shop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final RelativeLayout cartBottom = (RelativeLayout)findViewById(R.id.cart_bottom);

        FirebaseRecyclerOptions<CartUploadInfo> options =
                new FirebaseRecyclerOptions.Builder<CartUploadInfo>()
                        .setQuery(mDatabase, CartUploadInfo.class)
                        .build();

        final FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CartUploadInfo, BlogViewHolder>(options) {
            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cart_product_row, parent, false);


                return new BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder viewHolder, final int position, @NonNull CartUploadInfo model) {

                final String product_key = getRef(position).getKey() ;

                viewHolder.setTitle(model.getImageName());
                viewHolder.setPrice("\u20B9 " + model.getImagePrice());
                viewHolder.setImage(getApplicationContext(),model.getImageURL());

//                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        Intent productInfoIntent = new Intent(getApplicationContext(),ProductInfo.class);
//                        productInfoIntent.putExtra("productId",product_key);
//                        productInfoIntent.putExtra("category",category);
//                        startActivity(productInfoIntent);
//                    }
//                });

                viewHolder.mView.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            getRef(position).removeValue();
                        }
                        catch (Exception e){

                            Toast.makeText(cart.this, "Can't Delete Item ? Go back and try again", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            }
        };

        firebaseRecyclerAdapter.startListening();
        cart_list.setAdapter(firebaseRecyclerAdapter);

        findViewById(R.id.no_item).setVisibility(View.INVISIBLE);

        FirebaseDatabase.getInstance().getReference().child("User_Carts").child(firebaseAuth.getUid()).child("ItemsInCart")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int totalPrice = 0;

                        long childrenCount = dataSnapshot.getChildrenCount();
                        int childcount = (int) childrenCount;

//                        Toast.makeText(cart.this, String.valueOf(childcount), Toast.LENGTH_SHORT).show();

                        if (childcount == 0 ){
                            cartBottom.setVisibility(View.GONE);
                            FirebaseDatabase.getInstance().getReference().child("User_Carts").child(firebaseAuth.getUid()).child("Total").setValue("0");
                            findViewById(R.id.no_item).setVisibility(View.VISIBLE);
                            findViewById(R.id.cart_items).setVisibility(View.GONE);
                        }

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            int itemPrice = 0;

                            CartUploadInfo cartUploadInfo = snapshot.getValue(CartUploadInfo.class);

                            try {
                                itemPrice = Integer.parseInt(snapshot.getValue(CartUploadInfo.class).getImagePrice());
                            } catch (NumberFormatException nfe) {
                                System.out.println("Could not parse " + nfe);
                            }

                            totalPrice = itemPrice + totalPrice;
////
////                            try {
////                                myNum = Integer.parseInt(et.getText().toString());
////                            } catch(NumberFormatException nfe) {
////                                System.out.println("Could not parse " + nfe);
////                            }

                        }
                            FirebaseDatabase.getInstance().getReference().child("User_Carts").child(firebaseAuth.getUid()).child("Total").setValue(String.valueOf(totalPrice));
//
                        try {
                            FirebaseDatabase.getInstance().getReference().child("User_Carts").child(firebaseAuth.getUid()).child("Total").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    itemCost.setText("\u20B9 " + dataSnapshot.getValue(String.class));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        catch (Exception e){

                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        findViewById(R.id.checkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder checkOutDialog = new AlertDialog.Builder(cart.this); //Set up Alert Dialog for sign out
                final View customLayout = cart.this.getLayoutInflater().inflate(R.layout.checkout_text, null);

                checkOutDialog.setView(customLayout);
                final EditText checkout_address = (EditText) customLayout.findViewById(R.id.checkout_address);
                final String strUserName = checkout_address.getText().toString();

                checkOutDialog.setMessage("You are about to checkout")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                TextView totalCost = (TextView) findViewById(R.id.cart_cost);
                                String cost = totalCost.toString();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });

                final AlertDialog alert = checkOutDialog.create();
                alert.setTitle("Enter Delivery Address");
                alert.show();

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                checkout_address.addTextChangedListener(new TextWatcher() {
                    private void handleText() {
                        // Grab the button
                        final Button okButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                        if (checkout_address.getText().length() == 0) {
                            okButton.setEnabled(false);
                        } else if (checkout_address.getText().length() > 20){
                            okButton.setEnabled(true);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        handleText();
                    }
                });

            }
        });

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private TextView product_title;
        private TextView product_price;

        public BlogViewHolder(final View itemView) {
            super(itemView);

            product_title = itemView.findViewById(R.id.cart_display_title);
            product_price = itemView.findViewById(R.id.cart_display_price);
            mView = itemView;
        }

        public void setTitle(String title){

            product_title.setText(title);
        }

        public void setPrice(String price){

            product_price.setText(price);

        }

        public void setImage(Context ctx, String image){

            ImageView product_image =itemView.findViewById(R.id.cart_display_image);
            Picasso.with(ctx).load(image).into(product_image);

        }


    }
}
