package com.example.tony.myapplication;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class PostActivity extends AppCompatActivity {

    UploadTask uploadTask;

    //Submit Form Items
    private Button post_image;
    private Button post_add;
    private EditText post_name;
    private EditText post_weight;
    private EditText post_features;
    private EditText post_disclaimer;
    private EditText post_ingredients;
    private EditText post_price;
    private EditText post_packaging;

    ImageView SelectImage;

    Uri FilePathUri;

    StorageReference storageReference;
    DatabaseReference databaseReference;

    int Image_Request_Code = 7;

    private ProgressBar mProgress;
    private ProgressDialog progressDialog;

    private static final int GALLERY_REQUEST = 1;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    String Storage_Path = "Product_Images";
    String Database_Path = "Products";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Intent myIntent = getIntent();

        //mStorage = FirebaseStorage.getInstance().getReference();

        storageReference = FirebaseStorage.getInstance().getReference(Storage_Path).child(myIntent.getStringExtra("category"));
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path).child(myIntent.getStringExtra("category"));

        //mDatabase = FirebaseDatabase.getInstance().getReference().child("products");


        post_image = (Button) findViewById(R.id.post_image);
        post_add = (Button) findViewById(R.id.productPost);
        post_name = (EditText) findViewById(R.id.productName);
        post_weight = (EditText) findViewById(R.id.productWeight);
        post_features = (EditText) findViewById(R.id.productFeatures);
        post_disclaimer = (EditText) findViewById(R.id.productDisclaimer);
        post_ingredients = (EditText) findViewById(R.id.productIngredients);
        post_price = (EditText) findViewById(R.id.productPrice);
        post_packaging = (EditText) findViewById(R.id.productPackaging);


        //mProgress = new ProgressBar(this);
        progressDialog = new ProgressDialog(this);

        SelectImage = (ImageView)findViewById(R.id.imageView);


        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

            }
        });

        post_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImageFileToFirebaseStorage();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                // Setting up bitmap selected image into ImageView.
                SelectImage.setImageBitmap(bitmap);

                // After selecting image change choose button above text.
                post_image.setText("Image Selected");

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(30);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    public void UploadImageFileToFirebaseStorage() {

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            // Setting progressDialog Title.
            progressDialog.setTitle("Image is Uploading...");

            // Showing progressDialog.
            progressDialog.show();

            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            // Getting image name from EditText and store into string variable.
                            String productName = post_name.getText().toString().trim();
                            String productWeight = post_weight.getText().toString().trim();
                            String productFeatures = post_features.getText().toString().trim();
                            String productDisclaimer = post_disclaimer.getText().toString().trim();
                            String productIngredients = post_ingredients.getText().toString().trim();
                            String productPrice = post_price.getText().toString().trim();
                            String productPackaging = post_packaging.getText().toString().trim();

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            @SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(productName, taskSnapshot.getDownloadUrl().toString(),productIngredients,productFeatures,productDisclaimer,productPackaging,productWeight,productPrice);

                            // Getting image upload ID.
                            String ImageUploadId = databaseReference.push().getKey();

                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);

                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");

                        }
                    });
        }
        else {

            Toast.makeText(getApplicationContext(), "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }

}