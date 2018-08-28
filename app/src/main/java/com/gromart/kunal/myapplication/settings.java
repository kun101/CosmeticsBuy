package com.example.tony.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * Created by Tony on 14/02/2018.
 */

public class settings extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private RecyclerView mproductlist;

    private DatabaseReference mDatabase;

    private TextView name;
    private TextView address;
    private TextView phone;
    private TextView email;

    final String[] key = new String[1];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        firebaseAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid());

        setTitle("Settings");

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().build();

        firebaseAuth.getCurrentUser().updateProfile(profileUpdates);

        final AlertDialog.Builder quantityDialog = new AlertDialog.Builder(settings.this); //Set up Alert Dialog for sign out
        final View customLayout2 = settings.this.getLayoutInflater().inflate(R.layout.settings_name, null);

        quantityDialog.setView(customLayout2);
        quantityDialog.setMessage("Enter your info below")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        EditText name = customLayout2.findViewById(R.id.settings_name);

                        String sname = String.valueOf(name.getText());

                        mDatabase.child("username").setValue(sname);

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(sname).build();
                        firebaseAuth.getCurrentUser().updateProfile(profileUpdates);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        final AlertDialog alert2 = quantityDialog.create();
        alert2.setTitle("Edit Details");

        final AlertDialog.Builder addDialog = new AlertDialog.Builder(settings.this); //Set up Alert Dialog for sign out
        final View addLayout = settings.this.getLayoutInflater().inflate(R.layout.settings_add, null);

        final EditText checkout_address1 = (EditText) addLayout.findViewById(R.id.settings_add);

        quantityDialog.setView(addLayout);
        quantityDialog.setMessage("Enter your info below")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        EditText name = addLayout.findViewById(R.id.settings_add);

                        String sname = String.valueOf(name.getText());

                        mDatabase.child("address").setValue(sname);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        final AlertDialog alert3 = quantityDialog.create();
        alert3.setTitle("Edit Details");

        final AlertDialog.Builder phoneDialog = new AlertDialog.Builder(settings.this); //Set up Alert Dialog for sign out
        final View phoneLayout = settings.this.getLayoutInflater().inflate(R.layout.settings_phone, null);

        final EditText checkout_phone1 = (EditText) phoneLayout.findViewById(R.id.settings_phonetext);

        quantityDialog.setView(phoneLayout);
        quantityDialog.setMessage("Enter your info below")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        EditText name = phoneLayout.findViewById(R.id.settings_phonetext);

                        String sname = String.valueOf(name.getText());

                        mDatabase.child("phone").setValue(sname);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        final AlertDialog alert4 = quantityDialog.create();
        alert4.setTitle("Edit Details");

        alert3.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                if (checkout_address1.getText().length() < 15)
                    ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                else if(checkout_address1.getText().length() >= 15)
                    ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

            }
        });

        alert4.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                if (checkout_phone1.getText().length() != 10)
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                else if(checkout_phone1.getText().length() == 10)
                    ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        });


        name = findViewById(R.id.settings_name);
        address = findViewById(R.id.settings_address2);

        TextView textView = (TextView) findViewById(R.id.textView3);
        CardView cardView1 = (CardView)findViewById(R.id.card1);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert2.show();
            }
        });

        TextView textView2 = (TextView) findViewById(R.id.settings_address);
        CardView cardView2 = (CardView)findViewById(R.id.card2);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert3.show();

                checkout_address1.addTextChangedListener(new TextWatcher() {
                    private void handleText() {
                        // Grab the button
                        final Button okButton = alert3.getButton(AlertDialog.BUTTON_POSITIVE);

                        if (checkout_address1.getText().length() > 15){
                            okButton.setEnabled(true);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        handleText();
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

        TextView textView3 = (TextView) findViewById(R.id.settings_phone);
        final TextView textViewphone = (TextView) findViewById(R.id.settings_phone2);
        CardView cardView3 = (CardView)findViewById(R.id.card3);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("username").getValue().toString());
                address.setText(dataSnapshot.child("address").getValue().toString());
                textViewphone.setText(dataSnapshot.child("phone").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
}