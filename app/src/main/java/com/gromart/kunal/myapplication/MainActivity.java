package com.example.tony.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

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
    private String phone;

    private static final int RC_SIGN_IN = 123;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("users");
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
        NavUsermail = (TextView) headerView.findViewById(R.id.navUseremail);

        NavUsermail.setText("Order your favourite items !");

        navigationView.getMenu().getItem(0).setChecked(true);

        mLoginStatus = true;

        final AlertDialog.Builder signOutDialog = new AlertDialog.Builder(this); //Set up Alert Dialog for sign out

        if (firebaseAuth.getCurrentUser() == null) {
            mLoginStatus = false;
        }

        if (mLoginStatus) {
            user = firebaseAuth.getCurrentUser();
            name = user.getDisplayName();
            email = user.getEmail();
            phone = user.getPhoneNumber();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) { //When Drawer Item is Clicked
                int id = menuItem.getItemId();

                MenuItem db = (MenuItem) findViewById(R.id.db);

                menuItem.setChecked(true);

                if (id == R.id.db_logout) {

                    signOutDialog.setMessage("Do you want to Sign Out?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    usersignout();
                                    hideLogoutButton();
                                    NavUsername.setText("Welcome to Gromart");
                                    NavUsermail.setText("You are not logged in");
                                    mLoginStatus = false;
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
                if (firebaseAuth.getCurrentUser() != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().build();

                    firebaseAuth.getCurrentUser().updateProfile(profileUpdates);
                }

                switch (id) {
                    case R.id.db:
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.db_settings:
                        mDrawerLayout.closeDrawers();
                        if (!mLoginStatus) {
                            Toast.makeText(MainActivity.this, "You are not logged in, please log in", Toast.LENGTH_SHORT).show();
                        }else {
                            startActivity(new Intent(getApplicationContext(), settings.class));
                        }
                        break;
                    case R.id.db_about:
                        mDrawerLayout.closeDrawers();
                        if (!mLoginStatus) {
                            Toast.makeText(MainActivity.this, "You are not logged in, please log in", Toast.LENGTH_SHORT).show();
                        }else {
                            startActivity(new Intent(getApplicationContext(), myorders.class));
                        }
                        break;
                }

                return false;
            }
        });

        /*Certificate fingerprint (SHA-256): DD:57:47:CA:3E:0B:10:18:D4:1F:85:B8:18:9F:85:
            7B:BE:92:E7:33:3D:AD:5F:84:CE:D1:0F:18:69:6C:70:21*/

        //         SHA256: AC:65:14:0F:75:68:E8:5C:9D:BE:AB:CA:12:86:72:67:40:58:3A:3D:04:37:77:86:05:35:9C:C1:69:E5:CC:0F

            FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("address").getValue() == null) {

                        userRef.child(firebaseAuth.getCurrentUser().getUid()).child("address").setValue("No Address");
                        if (name != null) {
                            NavUsername.setText(name);
                        }

                    }
                    if (dataSnapshot.child("username").getValue() != null) {
                        String nme = dataSnapshot.child("username").getValue().toString();
                    }

                    if (dataSnapshot.child("username").getValue() != null || dataSnapshot.child("username").getValue() != "" || dataSnapshot.child("username").getValue() != " "){
                        NavUsermail.setText(dataSnapshot.child("username").getValue().toString());

                    }

                    if (dataSnapshot.child("phone").getValue() == null){
                        userRef.child(firebaseAuth.getCurrentUser().getUid()).child("phone").setValue("No Phone");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
                navigationView.getMenu().getItem(0).setChecked(true);
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View view) {
                navigationView.getMenu().getItem(0).setChecked(true);
                super.onDrawerOpened(view);
            }
       };

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.gromartlogoheader2);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //optional


        if (mLoginStatus){

            navigationView.getMenu().getItem(3).setVisible(true);

//            NavUsermail.setText(user.getDisplayName())

        }

        //NavigationView navigationView = (NavigationView) findViewById(R.id.NavView);


    }

    private void writeNewUser(String userId, String name, String email,String address, String phone) {
        User user = new User(name, email,address,phone);

        userRef.child(userId).setValue(user);
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

        MenuItem loginbtn = menu.findItem(R.id.login);
        MenuItem cartbtn = menu.findItem(R.id.cart);

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
            mDrawerLayout.closeDrawers();
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()
                                    ))
                            .build(),
                    RC_SIGN_IN);
            return true;

        }

        if (id==R.id.cart){
            mDrawerLayout.closeDrawers();
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
                        "Name",
                        "No Email","No Address",firebaseAuth.getCurrentUser().getPhoneNumber());


                // ...
            } else {
                Toast.makeText(this,"Sign In Failed", Toast.LENGTH_SHORT).show();
            }
        }


    }//Sign in pass/fail

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void setupViewPager(ViewPager viewPager){

        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabFragment1(), "GROCERY");
        adapter.addFragment(new TabFragment2(),"STATIONERY");
        viewPager.setAdapter(adapter);

    } //Create Tabs

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().build();

            firebaseAuth.getCurrentUser().updateProfile(profileUpdates);
        }
    }

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