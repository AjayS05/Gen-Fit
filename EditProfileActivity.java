package com.example.gen_fit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private ImageButton backBtn;

    private EditText nameEt, phoneEt, weightEt, heightEt,
            emailEt, passwordEt, cpasswordEt;
    private TextView goalTv;
    private Button updateBtn;



    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
//init ui views
        backBtn = findViewById(R.id.backBtn);


        nameEt = findViewById(R.id.nameET);
        phoneEt = findViewById(R.id.phoneET);
        weightEt = findViewById(R.id.WeightET);
        heightEt = findViewById(R.id.HeightET);

        goalTv = findViewById(R.id.GoalTv);
        emailEt = findViewById(R.id.emailEt);

        updateBtn = findViewById(R.id.UpdateBtn);




        firebaseAuth = FirebaseAuth.getInstance();
        loadUserDetails();
        progressDialog = new ProgressDialog( this);
        progressDialog. setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this, MainUserActivity.class));
                finish();
            }
        });
        goalTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick category
                goalDialog();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });

    }
    private void loadUserDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference( "Users");
        reference.child (firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                        //get data
                        String email = "" + dataSnapshot.child("email").getValue().toString();
                        String name = "" + dataSnapshot.child("name").getValue().toString();
                        String phone= "" + dataSnapshot.child("phone").getValue().toString();
                        String weight = "" + dataSnapshot.child("weight").getValue().toString();
                        String height = "" + dataSnapshot.child("Height").getValue().toString();
                        String goal = "" + dataSnapshot.child("goal").getValue().toString();


                        if (goal.equals("Fat Loss")) {
                            goalTv.setText("Fat Loss");

                        } else if (goal.equals("Maintain Weight")) {
                            goalTv.setText("Maintain Weight");
                        }
                        else {
                            goalTv.setText("Bulk");
                        }

                            emailEt.setText(email);
                            nameEt.setText(name);
                            phoneEt.setText(phone);
                            weightEt.setText(weight);
                            heightEt.setText(height);

                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
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

        updateProfile();
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
    private void updateProfile() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference( "Users");
        reference.child (firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                        //get data

                        String oldweight = "" + dataSnapshot.child("weight").getValue().toString();
                        String oldheight = "" + dataSnapshot.child("Height").getValue().toString();


                        weightEt.setText(oldweight);
                        heightEt.setText(oldheight);
                        if (!oldheight.equals(Height)){
                            setHeightHistory();
                        }
                        if (!oldweight.equals(Weight)){
                            setWeightHistory();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        //show progress
        progressDialog.setMessage("Updating product...");
        progressDialog.show();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("email",""+email);
            hashMap.put("name", ""+fullName);
            hashMap.put("phone","" + phoneNumber);
            hashMap.put("weight", ""+Weight);
            hashMap.put("goal", "" + goal);
            hashMap.put("Height", ""+Height);

            //update
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference(  "Users");
            reference.child(firebaseAuth.getUid())
                    .updateChildren (hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess (Void avoid) {
                            //update success
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new
                                                  OnFailureListener() {
                                                      @Override
                                                      public void onFailure (@NonNull Exception e) {
                                                          //update failed
                                                          progressDialog.dismiss();
                                                          Toast.makeText(  EditProfileActivity. this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                      }
                                                  });

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


