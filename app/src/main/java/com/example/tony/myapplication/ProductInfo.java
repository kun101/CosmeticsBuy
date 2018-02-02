package com.example.tony.myapplication;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProductInfo extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference userCartRef;

    StorageReference storageReference;

    private ImageView product_display_image;
    private TextView product_display_name;
    private TextView product_display_weight;
    private TextView product_display_features;
    private TextView product_display_price;
    private TextView product_display_disclaimer;
    private TextView product_display_ingreients;
    private TextView product_display_packaging;
    private String productURL;

    private FirebaseAuth firebaseAuth;

    private FloatingActionButton addtocart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_info);

        String product_key = getIntent().getStringExtra("productId");
        String product_category = getIntent().getStringExtra("category");

        addtocart = findViewById(R.id.add_to_cart);

        firebaseAuth = FirebaseAuth.getInstance();

        product_display_name = (TextView)findViewById(R.id.product_info_name);
        product_display_weight = (TextView)findViewById(R.id.product_info_weight);
        product_display_features = (TextView)findViewById(R.id.product_info_features);
        product_display_price = (TextView)findViewById(R.id.product_info_price);
        product_display_disclaimer = (TextView)findViewById(R.id.product_info_disclaimer);
        product_display_ingreients = (TextView)findViewById(R.id.product_info_ingredients);
        product_display_packaging = (TextView)findViewById(R.id.product_info_packaging);
        product_display_image = (ImageView)findViewById(R.id.product_info_image);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(product_category);
        storageReference = FirebaseStorage.getInstance().getReference("User_Cart").child(product_category);

        mDatabase.child(product_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String product_name = (String) dataSnapshot.child("imageName").getValue();
                String product_weight = (String) dataSnapshot.child("imageWeight").getValue();
                String product_ingredients = (String) dataSnapshot.child("imageIngredients").getValue();
                String product_features = (String) dataSnapshot.child("imageFeatures").getValue();
                String product_price = (String) dataSnapshot.child("imagePrice").getValue();
                String product_packaging = (String) dataSnapshot.child("imagePacking").getValue();
                String product_disclaimer = (String) dataSnapshot.child("imageDisclaimer").getValue();
                String product_image = (String) dataSnapshot.child("imageURL").getValue();

                product_display_name.setText(product_name);
                product_display_weight.setText(product_weight);
                product_display_ingreients.setText(product_ingredients);
                product_display_features.setText(product_features);
                product_display_price.setText(product_price);
                product_display_packaging.setText(product_packaging);
                product_display_disclaimer.setText(product_disclaimer);

                Picasso.with(getApplicationContext()).load(product_image).into(product_display_image);
                setTitle(product_display_name.getText().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(firebaseAuth.getCurrentUser()!=null){

                    userCartRef = FirebaseDatabase.getInstance().getReference().child("User_Carts").child(firebaseAuth.getUid()).child("ItemsInCart");
                    addItemToCart();
                    Toast.makeText(ProductInfo.this, "Item Added To Your Cart", Toast.LENGTH_SHORT).show();

                }


                FirebaseRecyclerOptions<ImageUploadInfo> options =
                        new FirebaseRecyclerOptions.Builder<ImageUploadInfo>()
                                .setQuery(mDatabase, ImageUploadInfo.class)
                                .build();


            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addItemToCart(){
        final String product_key = getIntent().getStringExtra("productId");

        mDatabase.child(product_key).child("imageURL").addValueEventListener(new ValueEventListener() {

            final String productName = product_display_name.getText().toString();
            final String productWeight = product_display_weight.getText().toString();
            final String productPrice = product_display_price.getText().toString();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String productURL = (String) dataSnapshot.getValue(String.class);

                CartUploadInfo imageUploadInfo = new CartUploadInfo(productName,productURL,productWeight,productPrice);

                // Adding image upload id s child element into databaseReference.
                userCartRef.child(product_key).setValue(imageUploadInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
