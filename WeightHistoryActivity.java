package com.example.gen_fit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class WeightHistoryActivity extends AppCompatActivity {

    private String weight, date;
    private ImageButton backBtn;



    private RecyclerView itemsRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelWeight> weightArrayList;
    private AdapterWeight adapterWeight;

    GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_history);

        backBtn =findViewById(R.id.backBtn);
        itemsRv = findViewById(R.id.itemsRv);
        firebaseAuth = FirebaseAuth.getInstance();
        graphView = findViewById(R.id.idGraphView);
        LoadWeightHistory();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WeightHistoryActivity.this, MainUserActivity.class));
                finish();
            }
        });
    }
    private void LoadWeightHistory(){
    weightArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(  "Users");
        ref.child (firebaseAuth.getUid()).child ("WeightData")
                .addValueEventListener(new ValueEventListener() {
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{});
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        weightArrayList.clear();
                        Double x = 1.0;
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String weight = ds.child("weight").getValue().toString();
                            Double weight1 = Double.parseDouble(weight);
                            String date = ds.child("date").getValue().toString();
                            SimpleDateFormat formatter1=new SimpleDateFormat("E MMM dd h:m:s z yyyy");
                            Date date1 = null;
                            try {
                                date1 = formatter1.parse(date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            SimpleDateFormat formatter2=new SimpleDateFormat("dd/mm/yy");
                            Date date2 = null;

                            series.appendData(new DataPoint(x,weight1),true, 30);
                            x = x+1;
                            ModelWeight modelWeight = ds.getValue(ModelWeight.class);

                            //add to list
                            weightArrayList.add(modelWeight);
                        }
                        graphView.setTitle("Weight History");
                        graphView.setTitleColor(R.color.teal_200);
                        graphView.setTitleTextSize(18);
                        graphView.getViewport().setXAxisBoundsManual(true);
                        graphView.getViewport().setYAxisBoundsManual(true);
                        graphView.getViewport().setMinY(30);
                        graphView.getViewport().setScrollable(true);
                        graphView.getViewport().setMaxY(120);
                        graphView.getViewport().setMinX(1);
                        graphView.getViewport().setMaxX(10);
                        graphView.getViewport().setMaxXAxisSize(0.2);
                        graphView.getViewport().setMaxYAxisSize(5);
                        graphView.getViewport().setDrawBorder(true);
                        graphView.getViewport().setBackgroundColor(Color.CYAN);
                        graphView.getViewport().setBorderColor(Color.RED);
                        //graphView.getViewport().setBackgroundColor(Color.BLUE);
                        graphView.addSeries(series);

                        adapterWeight = new AdapterWeight(WeightHistoryActivity.this, weightArrayList);

                        itemsRv.setAdapter(adapterWeight);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}