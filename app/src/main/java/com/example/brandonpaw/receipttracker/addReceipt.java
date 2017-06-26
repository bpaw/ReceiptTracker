package com.example.brandonpaw.receipttracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class addReceipt extends AppCompatActivity  implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    // UI components for the input EditTexts
    EditText inputReceipt;
    EditText inputTip;
    EditText inputTax;
    EditText inputTotal;
    EditText inputFolders;

    // Button to mark adding receipts
    Button add;
    Button cam;

    String [] suggestions;

    ImageView mImageView;

    // Firebase Authentication object used to get the user to write a new post to
    private FirebaseAuth firebaseAuth;
    private FirebaseUser fireyUser;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the Firebase member(s)
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize the UI input components in this file
        inputReceipt = (EditText) findViewById(R.id.add_receipt);
        inputTip = (EditText) findViewById(R.id.add_tip);
        inputTax = (EditText) findViewById(R.id.add_tax);
        inputTotal = (EditText) findViewById(R.id.add_total);
        //inputFolders = (EditText) findViewById(R.id.add_folders);
        add = (Button) findViewById(R.id.button_add);
        cam = (Button) findViewById(R.id.button_camera);

        add.setOnClickListener(this);
        cam.setOnClickListener(this);

        // Initialize the suggestions field
        suggestions = new String[]{"June 2017", "Shopping", "Food", "Clothes", "School"};

        // Set the suggestions for our MultiAutoCompleteTeView for the folders information
        MultiAutoCompleteTextView folderSuggestions = (MultiAutoCompleteTextView) findViewById(R.id.add_folders);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.suggestion, suggestions);
        folderSuggestions.setAdapter(adapter);
        folderSuggestions.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        mImageView = (ImageView) findViewById(R.id.testerImageView);
    }

    public boolean requiredFieldsFilled() {

        String receiptVal = inputReceipt.getText().toString().trim();
        String totalVal = inputTotal.getText().toString().trim();

        return (!TextUtils.isEmpty(receiptVal) && !TextUtils.isEmpty(totalVal));
    }

    public String[] getValues() {

        String[] values = new String[5];

        values[0] = inputReceipt.getText().toString().trim();
        values[1] = inputTip.getText().toString().trim();
        values[2] = inputTax.getText().toString().trim();
        values[3] = inputTotal.getText().toString().trim();
        values[4] = inputFolders.getText().toString().trim();

        return values;
    }

    public void uploadReceipt() {

        // Error check the fields
        if (!requiredFieldsFilled()) {
            Toast.makeText(this, "Fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the user input to construct a Receipt object
        String[] input = getValues();

        // Construct a Receipt object and write it to the Firebase Databse
        int tip = Integer.parseInt(input[1]);
        int tax = Integer.parseInt(input[2]);
        int total = Integer.parseInt(input[3]);
        ArrayList<String> folders = new ArrayList<>();
        folders.add("June 2017");
        Receipt receipt = new Receipt("J Crew", 0, 0, 12, folders);

        fireyUser = firebaseAuth.getCurrentUser();
        databaseReference.child("Receipts").child(fireyUser.getUid()).setValue(receipt);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch(id) {
            case R.id.button_add:
                Toast.makeText(getApplicationContext(), "Uploading receipt...", Toast.LENGTH_SHORT).show();
                uploadReceipt();
                break;
            case R.id.button_camera:
                dispatchTakePictureIntent();
                break;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();

            fireyUser = firebaseAuth.getCurrentUser();
            StorageReference userRef = storageRef.child("Users").child(fireyUser.getUid());
            UploadTask uploadTask = userRef.putBytes(imageBytes);
            // ADD success and failure listeners for ^
        }
    }
}
