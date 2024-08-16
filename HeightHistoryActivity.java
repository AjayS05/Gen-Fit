package com.example.gen_fit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HeightHistoryActivity extends AppCompatActivity {

    private String height, date;
    private ImageButton backBtn;



    private RecyclerView itemsRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelHeight> heightArrayList;
    private AdapterHeight adapterheight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height_history);

        backBtn =findViewById(R.id.backBtn);
        itemsRv = findViewById(R.id.itemsRv);
        firebaseAuth = FirebaseAuth.getInstance();
        LoadheightHistory();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HeightHistoryActivity.this, MainUserActivity.class));
                finish();
            }
        });
    }
    private void LoadheightHistory(){
        heightArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(  "Users");
        ref.child (firebaseAuth.getUid()).child ("HeightData")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        heightArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelHeight modelHeight = ds.getValue(ModelHeight.class);
                            //add to list
                            heightArrayList.add(modelHeight);
                        }

                        adapterheight = new AdapterHeight(HeightHistoryActivity.this, heightArrayList);

                        itemsRv.setAdapter(adapterheight);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}