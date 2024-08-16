package com.example.gen_fit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PublicKey;
import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private TextView nameTv, emailTv, phoneTv, BMITv, result;

    private ImageButton logoutBtn, editProfileBtn, homeBtn, planBtn;

    private Button weightHistory, heightHistory;
    private ProgressDialog progressDialog;


    private Double BMI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        nameTv = findViewById(R.id.nameTv);
        emailTv= findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        result = findViewById(R.id.Result);
        BMITv = findViewById(R.id.BMITv);
        logoutBtn = findViewById(R.id.logoutBtn);
        editProfileBtn=findViewById(R.id.editProfileBtn);
        homeBtn=findViewById(R.id.homeBtn);
        planBtn=findViewById(R.id.scheduleBtn);
        weightHistory = findViewById(R.id.weightHistory);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        checkUser();



        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMeOffline();

            }
        });
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this, EditProfileActivity.class));
                finish();
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        planBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this, PlanActivity.class));
                finish();
            }
        });
        weightHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this, WeightHistoryActivity.class));
                finish();
            }
        });

    }
    private void makeMeOffline(){
        progressDialog.setMessage("Logging Out...");
        HashMap<String, Object> hashMap = new HashMap();
        hashMap.put("online", "false");
        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        //update successfully
                        firebaseAuth.signOut();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed updating
                        progressDialog.dismiss();
                        Toast.makeText(MainUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
            finish();
        } else {
            loadMyInfo();
        }
    }
    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = "" + ds.child("name").getValue();
                            String email = "" + ds.child("email").getValue();
                            String phone = "" + ds.child("phone").getValue();

                            String accountType = "" + ds.child("accountType").getValue();
                            String height = "" + ds.child("Height").getValue();
                            Log.d("Height", height);
                            Double height1 = Double.parseDouble(height);
                            String weight = "" + ds.child("weight").getValue();

                            Double weight1 = Double.parseDouble(weight);
                            BMI = weight1 / ((height1 / 100) * (height1 / 100));


                            String bm = BMI.toString();
                            bm = "BMI is: "+bm.substring(0,4);
                            ((GenFit) getApplication()).setBMI(bm);
                            if(BMI<18){
                                String text = "You are Underweight";
                                result.setText(text);
                                result.setTextColor(Color.RED);
                            } else if (BMI>18&&BMI<25) {
                                String text = "You are fit";
                                result.setText(text);
                                result.setTextColor(Color.GREEN);
                            }
                            else if (BMI>25&&BMI<30) {
                                String text = "You are Overweight";
                                result.setText(text);
                                result.setTextColor(Color.YELLOW);
                            }
                            else if(BMI>30){
                                String text = "You are Obese";
                                result.setText(text);
                                result.setTextColor(Color.RED);
                            }
                            BMITv.setText(bm);
                            nameTv.setText(name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);
                            SpeedoMeterView Speed = (SpeedoMeterView) findViewById(R.id.speedometer);


                            Speed.setLabelConverter(new SpeedoMeterView.LabelConverter() {
                                @Override
                                public String getLabelFor(double progress, double maxProgress) {
                                    return String.valueOf((int) Math.round(progress));
                                }
                            });
                            Speed.setMaxSpeed(40);
                            Speed.setMajorTickStep(5);
                            Speed.setMinorTicks(0);


                            Speed.addColoredRange(0, 15, Color.RED);
                            Speed.addColoredRange(15, 18, Color.YELLOW);
                            Speed.addColoredRange(18, 25, Color.GREEN);
                            Speed.addColoredRange(25, 30, Color.YELLOW);
                            Speed.addColoredRange(30,40, Color.RED);
                            Speed.setSpeed(Math.round(BMI), 2000, 500);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}