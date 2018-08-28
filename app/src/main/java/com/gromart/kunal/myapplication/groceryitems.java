package com.example.tony.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import static com.example.tony.myapplication.ListAdapter.KEY_MEAL;

//import com.example.tony.myapplication.MovieBoardAdapter;
//import com.coderefer.firebasedatabaseexample.fragments.AddMovieFragment;

public class groceryitems extends AppCompatActivity {

    Intent myIntent = getIntent();

    private FirebaseAuth firebaseAuth;

    private RecyclerView mproductlist;

    private DatabaseReference mDatabase;

    SearchView searchView;

    String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceryitems);

        firebaseAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products").child(getIntent().getStringExtra("category"));

        setTitle(getIntent().getStringExtra("category"));

        mproductlist = (RecyclerView)findViewById(R.id.product_list);
        mproductlist.setLayoutManager(new LinearLayoutManager(this));

        //Toast.makeText(this, getIntent().getStringExtra("category"), Toast.LENGTH_SHORT).show();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.logo4);
        getSupportActionBar().setDisplayShowTitleEnabled(true); //optional

    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseSearch(searchText);

//        final String category = getIntent().getStringExtra("category");
//
//        FirebaseRecyclerOptions<ImageUploadInfo> options =
//                new FirebaseRecyclerOptions.Builder<ImageUploadInfo>()
//                        .setQuery(mDatabase, ImageUploadInfo.class)
//                        .build();
//
//        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ImageUploadInfo, BlogViewHolder>(options) {
//            @Override
//            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.product_row, parent, false);
//
//                return new BlogViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull BlogViewHolder viewHolder, int position, @NonNull ImageUploadInfo model) {
//
//                final String product_key = getRef(position).getKey() ;
//
//                viewHolder.setTitle(model.getImageName());
//                viewHolder.setPrice("\u20B9 " + model.getImagePrice());
//                viewHolder.setImage(getApplicationContext(),model.getImageURL());
//
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
//
//            }
//        };
//        firebaseRecyclerAdapter.startListening();
//        mproductlist.setAdapter(firebaseRecyclerAdapter);


    }

    private void firebaseSearch(String search){


        final String category = getIntent().getStringExtra("category");

        Query query = mDatabase.orderByChild("imageName").startAt(search).endAt(search + "\uf8ff");

        FirebaseRecyclerOptions<ImageUploadInfo> options =
                new FirebaseRecyclerOptions.Builder<ImageUploadInfo>()
                        .setQuery(query, ImageUploadInfo.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ImageUploadInfo, BlogViewHolder>(options) {
            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_row, parent, false);

                return new BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BlogViewHolder viewHolder, int position, @NonNull ImageUploadInfo model) {

                final String product_key = getRef(position).getKey() ;

                viewHolder.setTitle(model.getImageName());
                viewHolder.setPrice("\u20B9 " + model.getImagePrice());
                viewHolder.setImage(getApplicationContext(),model.getImageURL());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent productInfoIntent = new Intent(getApplicationContext(),ProductInfo.class);
                        productInfoIntent.putExtra("productId",product_key);
                        productInfoIntent.putExtra("category",category);
                        startActivity(productInfoIntent);
                    }
                });

            }
        };
        firebaseRecyclerAdapter.startListening();
        mproductlist.setAdapter(firebaseRecyclerAdapter);



    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private TextView product_title;
        private TextView product_price;

        public BlogViewHolder(final View itemView) {
            super(itemView);

            product_title = itemView.findViewById(R.id.product_display_title);
            product_price = itemView.findViewById(R.id.product_display_price);
            mView = itemView;
        }

        public void setTitle(String title){

            product_title.setText(title);
        }

        public void setPrice(String price){

            product_price.setText(price);

        }

        public void setImage(Context ctx,String image){

            ImageView product_image =itemView.findViewById(R.id.product_display_image);
            Picasso.with(ctx).load(image).into(product_image);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.listpagemenu,menu);
        MenuItem cartbtn = menu.findItem(R.id.cart);

        if(firebaseAuth.getCurrentUser() != null){
            cartbtn.setVisible(true);
        }

        final String category = getIntent().getStringExtra("category");

        MenuItem menuSearch = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(menuSearch);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query.toUpperCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText.toUpperCase());
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent mIntent = new Intent(getApplicationContext(), PostActivity.class);
        mIntent.putExtra("category",KEY_MEAL);

        if (item.getItemId()==R.id.add){

            startActivity(mIntent);

        }
        if (item.getItemId()==R.id.cart){
            startActivity(new Intent(getApplicationContext(), cart.class));
        }

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
}


