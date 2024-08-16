package com.example.gen_fit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.HashMap;

public class PlanActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    private TextView nameTv, emailTv, phoneTv, tabDietTV, tabExeciseTv, typeTv, HourTv, MinuteTv, SecondTv;

    private Button NotifyBtn;

    private ImageButton logoutBtn, editProfileBtn, homeBtn, planBtn;
    private ProgressDialog progressDialog;

    private ArrayList<ModelDiet> dietArrayList;

    private AdapterDiet adapterDiet;

    private ArrayList<ModelExercise> exerciseArrayList;

    private AdapterExercise adapterExercise;

    private RelativeLayout DietRl, exerciseRl;

    private RecyclerView dietRv, exerciseRv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        nameTv = findViewById(R.id.nameTv);
        emailTv= findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        logoutBtn = findViewById(R.id.logoutBtn);
        editProfileBtn=findViewById(R.id.editProfileBtn);
        homeBtn=findViewById(R.id.homeBtn);
        planBtn=findViewById(R.id.scheduleBtn);
        tabDietTV=findViewById(R.id.tabDietTv);
        tabExeciseTv=findViewById(R.id.tabExerciseTv);
        DietRl=findViewById(R.id.DietRl);
        exerciseRl=findViewById(R.id.exerciseRl);
        dietRv=findViewById(R.id.dietRv);
        exerciseRv=findViewById(R.id.exerciseRv);
        typeTv=findViewById(R.id.typeTv);
        HourTv=findViewById(R.id.HourTv);
        MinuteTv=findViewById(R.id.MinuteTv);
        SecondTv=findViewById(R.id.SecondTv);
        NotifyBtn=findViewById(R.id.NotifyBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        showExerciseUI();
        loadDiet();
        loadExercise();
        loadMyInfo();
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMeOffline();

            }
        });
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlanActivity.this, EditProfileActivity.class));
                finish();
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlanActivity.this, MainUserActivity.class));
                finish();
            }
        });
        planBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tabDietTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExerciseUI();
            }
        });
        tabExeciseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDietUI();
            }
        });
        typeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeDialog();
            }
        });
        HourTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hourDialog();
            }
        });
        MinuteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minuteDialog();
            }
        });
        SecondTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secondDialog();
            }
        });
        NotifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                String type = typeTv.getText().toString();
                String h = HourTv.getText().toString();
                String m = MinuteTv.getText().toString();
                String s = SecondTv.getText().toString();

                Integer hour = Integer.parseInt(h);
                Integer minute = Integer.parseInt(m);
                Integer second = Integer.parseInt(s);
                Integer HtS = hour * 360;
                Integer MtS = minute * 60;
                Integer fin = HtS+MtS+second*1000;
                if(type.equals("Diet")){
                    scheduleNotification(getNotification("Meal time"), fin);
                }
                else{
                    scheduleNotification(getNotification("Time for workout"), fin);
                }
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
                        Toast.makeText(PlanActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(PlanActivity.this, LoginActivity.class));
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

                            nameTv.setText(name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadDiet() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String goal = "" + ds.child("goal").getValue();
                            if (goal.equals("Bulk")) {
                                dietArrayList = new ArrayList<>();
//load orders of shop
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Diet");
                                ref.child("Bulk")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                //clear list before adding new data in it
                                                dietArrayList.clear();
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    ModelDiet modelDiet = ds.getValue(ModelDiet.class);
                                                    //add to list
                                                    dietArrayList.add(modelDiet);
                                                }
                                                adapterDiet = new AdapterDiet(PlanActivity.this, dietArrayList);
//set adapter to recyclerview
                                                dietRv.setAdapter(adapterDiet);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            } else if (goal.equals("Maintain Weight")) {
                                dietArrayList = new ArrayList<>();
//load orders of shop
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Diet");
                                ref.child("Maintenance")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                //clear list before adding new data in it
                                                dietArrayList.clear();
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    ModelDiet modelDiet = ds.getValue(ModelDiet.class);
                                                    //add to list
                                                    dietArrayList.add(modelDiet);
                                                }
                                                adapterDiet = new AdapterDiet(PlanActivity.this, dietArrayList);
//set adapter to recyclerview
                                                dietRv.setAdapter(adapterDiet);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                            else{
                                dietArrayList = new ArrayList<>();
//load orders of shop
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Diet");
                                ref.child("Fatloss")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                //clear list before adding new data in it
                                                dietArrayList.clear();
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    ModelDiet modelDiet = ds.getValue(ModelDiet.class);
                                                    //add to list
                                                    dietArrayList.add(modelDiet);
                                                }
                                                adapterDiet = new AdapterDiet(PlanActivity.this, dietArrayList);
//set adapter to recyclerview
                                                dietRv.setAdapter(adapterDiet);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



                

        private void loadExercise(){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String goal = "" + ds.child("goal").getValue();
                                if (goal.equals("Bulk")) {
                                    exerciseArrayList = new ArrayList<>();
//load orders of shop
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference (  "Excercise");
                                    ref.child("Bulk").orderByKey()
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                                                    //clear list before adding new data in it
                                                    exerciseArrayList.clear();
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        ModelExercise modelExercise = ds.getValue(ModelExercise.class);
                                                        //add to list
                                                        exerciseArrayList.add(modelExercise);
                                                    }
                                                    adapterExercise = new AdapterExercise( PlanActivity. this, exerciseArrayList);
//set adapter to recyclerview
                                                    exerciseRv.setAdapter(adapterExercise);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                } else if (goal.equals("Maintain Weight")) {
                                    exerciseArrayList = new ArrayList<>();
//load orders of shop
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference (  "Excercise");
                                    ref.child("Maintenance").orderByKey()
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                                                    //clear list before adding new data in it
                                                    exerciseArrayList.clear();
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        ModelExercise modelExercise = ds.getValue(ModelExercise.class);
                                                        //add to list
                                                        exerciseArrayList.add(modelExercise);
                                                    }
                                                    adapterExercise = new AdapterExercise( PlanActivity. this, exerciseArrayList);
//set adapter to recyclerview
                                                    exerciseRv.setAdapter(adapterExercise);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                                else{
                                    exerciseArrayList = new ArrayList<>();
//load orders of shop
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference (  "Excercise");
                                    ref.child("Fatloss").orderByKey()
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                                                    //clear list before adding new data in it
                                                    exerciseArrayList.clear();
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        ModelExercise modelExercise = ds.getValue(ModelExercise.class);
                                                        //add to list
                                                        exerciseArrayList.add(modelExercise);
                                                    }
                                                    adapterExercise = new AdapterExercise( PlanActivity. this, exerciseArrayList);
//set adapter to recyclerview
                                                    exerciseRv.setAdapter(adapterExercise);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

    }

    private void showExerciseUI() {
        exerciseRl.setVisibility(View.VISIBLE);
        DietRl.setVisibility (View. GONE);
        tabExeciseTv.setTextColor(getResources().getColor (R.color.black));
        tabExeciseTv.setBackgroundResource(R.drawable.shape_rect04);
        tabDietTV.setTextColor (getResources ().getColor(R.color.white));
        tabDietTV.setBackgroundColor(getResources ().getColor (android.R.color. transparent));
        }


    private void showDietUI() {
        DietRl.setVisibility(View.VISIBLE);
        exerciseRl.setVisibility (View. GONE);
        tabDietTV.setTextColor(getResources().getColor (R.color.black));
        tabDietTV.setBackgroundResource(R.drawable.shape_rect04);
        tabExeciseTv.setTextColor (getResources ().getColor(R.color.white));
        tabExeciseTv.setBackgroundColor(getResources ().getColor (android.R.color. transparent));
    }
    private void typeDialog() {
        //dialog
        AlertDialog.Builder builder = new AlertDialog. Builder(  this);
        builder.setTitle("Type")
                .setItems (constants.type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String category = constants.type[which];
                        //set picked category
                        typeTv.setText(category);
                    }

                })
                .show();
    }
    private void hourDialog() {
        //dialog
        AlertDialog.Builder builder = new AlertDialog. Builder(  this);
        builder.setTitle("HH")
                .setItems (constants.hour, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String category = constants.hour[which];
                        //set picked category
                        HourTv.setText(category);
                    }

                })
                .show();
    }

    private void minuteDialog() {
        //dialog
        AlertDialog.Builder builder = new AlertDialog. Builder(  this);
        builder.setTitle("mm")
                .setItems (constants.minutes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String category = constants.minutes[which];
                        //set picked category
                        MinuteTv.setText(category);
                    }

                })
                .show();
    }
    private void secondDialog() {
        //dialog
        AlertDialog.Builder builder = new AlertDialog. Builder(  this);
        builder.setTitle("mm")
                .setItems (constants.seconds, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String category = constants.seconds[which];
                        //set picked category
                        SecondTv.setText(category);
                    }

                })
                .show();
    }
    private void scheduleNotification (Notification notification , int delay) {

        Intent notificationIntent = new Intent( this, MyNotificationPublisher.class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent.getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock.elapsedRealtime () + delay ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;
    }
    private Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( "Scheduled Notification" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }
    }