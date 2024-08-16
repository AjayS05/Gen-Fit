package com.example.gen_fit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegisterUserActivity extends AppCompatActivity  {

    private ImageButton backBtn, gpsBtn;
    private ImageView profileIv;
    private EditText nameEt, phoneEt, weightEt, heightEt,
            emailEt, passwordEt, cpasswordEt;
    private TextView goalTv;
    private Button registerBtn;
    private TextView registerSellerTv;

    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    private Uri image_uri;
    private LocationManager locationManager;

    private double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
//init ui views
        backBtn = findViewById(R.id.backBtn);


        nameEt = findViewById(R.id.nameET);
        phoneEt = findViewById(R.id.phoneET);
        weightEt = findViewById(R.id.WeightET);
        heightEt = findViewById(R.id.HeightET);

        goalTv = findViewById(R.id.GoalTv);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordET);
        cpasswordEt = findViewById(R.id.cpasswordET);
        registerBtn = findViewById(R.id.RegisterBtn);




        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog( this);
        progressDialog. setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        goalTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick category
                goalDialog();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });

    }
    private String fullName, phoneNumber, Weight, Height,  goal, email, password, confirmPassword;
    private void inputData() {
        //input data
        fullName = nameEt.getText().toString().trim();

        phoneEt.getText().toString().trim();
        phoneNumber = phoneEt.getText().toString().trim();

        Weight = weightEt.getText().toString().trim();
        Height = heightEt.getText().toString().trim();

        goal = goalTv.getText().toString().trim();
        email = emailEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();
        confirmPassword = cpasswordEt.getText().toString().trim();
        //validate data
        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Enter Name...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Enter Phone Number...", Toast.LENGTH_SHORT).show();
            return;
        }


        if
        (!Patterns.EMAIL_ADDRESS.matcher (email).matches()) {
            Toast.makeText(  this, "Invalid email pattern...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password. length() <6){
            Toast.makeText( this,  "Password must be atleast 6 characters long...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(  this,  "Password doesn't match...", Toast.LENGTH_SHORT).show();
            return;
        }
        createAccount();
    }
    private void createAccount() {
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        //create account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess (AuthResult authResult) {
                        //account created
                        saverFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure (@NonNull Exception e) {
                        //failed creating account
                        progressDialog.dismiss();
                        Toast.makeText(RegisterUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void saverFirebaseData() {
        progressDialog.setMessage("Saving Account Info...");
        String timestamp=""+System.currentTimeMillis();
        if (image_uri==null){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid","" + firebaseAuth.getUid());
            hashMap.put("email","" + email);
            hashMap.put("name",""+fullName);

            hashMap.put("phone", ""+phoneNumber);

            hashMap.put("weight", ""+Weight);
            hashMap.put("Height", "" + Height);

            hashMap.put("goal",""+goal);


            hashMap.put("timestamp", ""+ timestamp);
            hashMap.put("accountType", "User");
            hashMap.put("online", "true");

            //save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess (Void avoid) {
                            //db updated
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterUserActivity.this, MainUserActivity.class));
                            finish();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure (@NonNull Exception e) {
                            //failed updating db
                            progressDialog.dismiss();
                            startActivity (new Intent( RegisterUserActivity. this, MainUserActivity.class));
                            finish();
                        }
                    });
                setHeightHistory();
                setWeightHistory();
        }
        else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("uid","" + firebaseAuth.getUid());
                    hashMap.put("email","" + email);
                    hashMap.put("name",""+fullName);

                    hashMap.put("phone", ""+phoneNumber);

                    hashMap.put("weight", ""+Weight);
                    hashMap.put("Height", "" + Height);

                    hashMap.put("goal",""+goal);


                    hashMap.put("timestamp", ""+ timestamp);
                    hashMap.put("accountType", "User");
                    hashMap.put("online", "true");


                    //save to db
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "Users");
                    ref.child(firebaseAuth.getUid()).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess (Void avoid) {
                                    //db updated
                                    progressDialog.dismiss();
                                    startActivity(new Intent(RegisterUserActivity.this, MainUserActivity.class));
                                    finish();
                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure (@NonNull Exception e) {
                                    //failed updating db
                                    progressDialog.dismiss();
                                    startActivity (new Intent( RegisterUserActivity. this, MainUserActivity.class));
                                    finish();
                                }
                            });
                }
        setHeightHistory();
        setWeightHistory();
        }
    private void goalDialog() {
        //dialog
        AlertDialog.Builder builder = new AlertDialog. Builder(  this);
        builder.setTitle("Set your goal")
                .setItems (constants.goals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String category = constants.goals[which];
                        //set picked category
                        goalTv.setText(category);
                    }

                })
                .show();
    }
    private void setWeightHistory(){
        Date currentTime = Calendar.getInstance().getTime();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("weight",""+Weight);
        hashMap.put("date",currentTime.toString());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(  "Users");
        reference.child(firebaseAuth.getUid()).child("WeightData").child(currentTime.toString()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }
    private void setHeightHistory(){
        Date currentTime = Calendar.getInstance().getTime();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Height",""+Height);
        hashMap.put("date",currentTime.toString());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(  "Users");
        reference.child(firebaseAuth.getUid()).child("HeightData").child(currentTime.toString()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });
    }

}