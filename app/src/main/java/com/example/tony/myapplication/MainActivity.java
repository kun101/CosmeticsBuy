package com.example.tony.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private LinearLayout mDrawerBody;
    private TabLayout mtabs;
    public Button btn;
    private boolean mLoginStatus;
    private static final String TAG = "Tabs";
    private MenuItem loginbtn;
    private MenuItem logoutbtn;
    private NavigationView navigationView;
    private Menu drawer_menu;
    TextView nameObject;
    String username;
    TextView NavUsername;
    TextView NavUsermail;
    private SectionsPageAdapter mSectionsPageAdapter;
    private FirebaseAuth firebaseAuth;
    private ViewPager mViewPager;

    private FirebaseUser user;
    private String name;
    private String email;

    private static final int RC_SIGN_IN = 123;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference();
    DatabaseReference searchRef = database.getReference().child("Products");


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        //for adding users to database


        navigationView = (NavigationView) findViewById(R.id.NavView);
        final View headerView = navigationView.getHeaderView(0);
        NavUsername = (TextView) headerView.findViewById(R.id.navUsername);
        NavUsermail = (TextView)headerView.findViewById(R.id.navUseremail);

        navigationView.getMenu().getItem(0).setChecked(true);

        mLoginStatus = true;

        final AlertDialog.Builder signOutDialog = new AlertDialog.Builder(this); //Set up Alert Dialog for sign out

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem){ //When Drawer Item is Clicked
                int id = menuItem.getItemId();

                MenuItem db = (MenuItem)findViewById(R.id.db);

                menuItem.setChecked(true);

                if (id == R.id.db_logout){

                    signOutDialog.setMessage("Do you want to Sign Out?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    usersignout();
                                    hideLogoutButton();
                                    NavUsername.setText("Welcome!");
                                    NavUsermail.setText("You are not logged in");
                                    mLoginStatus=false;
                                    invalidateOptionsMenu();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                    navigationView.getMenu().getItem(0).setChecked(true);
                                }
                            });


                    AlertDialog alert = signOutDialog.create();
                    alert.setTitle("Sign Out");
                    alert.show();

                    mDrawerLayout.closeDrawers();
                    invalidateOptionsMenu();

                }

                switch (id) {
                    case R.id.db:
                        mDrawerLayout.closeDrawers();
                        break;
                }

                return false;
            }
        });



        if (firebaseAuth.getCurrentUser() == null){
            mLoginStatus = false;
        }


        if (mLoginStatus){
            user=firebaseAuth.getCurrentUser();
            name = user.getDisplayName();
            email = user.getEmail();
        }

        invalidateOptionsMenu();

        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerBody = (LinearLayout) findViewById(R.id.drawerbody);
        mtabs = (TabLayout) findViewById(R.id.tabs);

       mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close) {

            public void onDrawerClosed(View view) {
                ViewCompat.setTranslationZ(mDrawerBody,0);
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
            }
       };

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (mLoginStatus){

            navigationView.getMenu().getItem(3).setVisible(true);

            NavUsername.setText(user.getDisplayName());
            NavUsermail.setText(user.getEmail());


            Toast.makeText(this, " Hi " +  name, Toast.LENGTH_SHORT).show();

        }

        //NavigationView navigationView = (NavigationView) findViewById(R.id.NavView);


    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        userRef.child("users").child(userId).setValue(user);
    }

    private void hideLogoutButton() {
        navigationView.getMenu().getItem(3).setVisible(false);
    }

    private void showLogoutButton() {
        navigationView.getMenu().getItem(3).setVisible(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolopts, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem loginbtn = menu.findItem(R.id.login);
        MenuItem cartbtn = menu.findItem(R.id.cart);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (!mLoginStatus){
            loginbtn.setVisible(true);
        }else {
            cartbtn.setVisible(true);
            loginbtn.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);

    } //Toolbar Creation

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (mToggle.onOptionsItemSelected(item)){
            ViewCompat.setTranslationZ(mDrawerBody,1);
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.login) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    RC_SIGN_IN);
            return true;
        }

        if (id==R.id.cart){
            startActivity(new Intent(getApplicationContext(), cart.class));
        }



        return super.onOptionsItemSelected(item);
    } //Toolbar Selection

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == ResultCodes.OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(this,"Signed In Successfully", Toast.LENGTH_SHORT).show();
                showLogoutButton();
                writeNewUser(firebaseAuth.getCurrentUser().getUid(),
                        firebaseAuth.getCurrentUser().getDisplayName(),
                        firebaseAuth.getCurrentUser().getEmail());


                // ...
            } else {
                Toast.makeText(this,"Sign In Failed", Toast.LENGTH_SHORT).show();
            }
        }


    }//Sign in pass/fail

    private void setupViewPager(ViewPager viewPager){

        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabFragment1(), "GROCERY");
        adapter.addFragment(new TabFragment2(),"STATIONERY");
        viewPager.setAdapter(adapter);

    } //Create Tabs

    private void showloginbtn(){
        loginbtn.setVisible(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    private void usersignout(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });

    }

}