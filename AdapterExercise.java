package com.example.gen_fit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Objects;

public class AdapterExercise extends RecyclerView.Adapter<AdapterExercise.HolderExercise> {
    private final Context context;

    private final ArrayList<ModelExercise>
            exerciseArrayList;

    public AdapterExercise(Context context, ArrayList<ModelExercise> exerciseArrayList){
        this.context = context;
        this.exerciseArrayList = exerciseArrayList;
    }
    @NonNull
    @Override
    public HolderExercise onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_exercise_day, parent, false);
        return new HolderExercise(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderExercise holder, int position) {
        ModelExercise modelExercise = exerciseArrayList.get(position);
        String One= modelExercise.getOne();
        String Two = modelExercise.getTwo();
        String Three= modelExercise.getThree();
        String Four = modelExercise.getFour();
        String Five = modelExercise.getFive();
        String Six = modelExercise.getSix();
        String Seven = modelExercise.getSeven();
        String Day = modelExercise.getDay();
        String Title= modelExercise.getTitle();


        holder.OneTv.setText(One);
        holder.TwoTv.setText(Two);
        holder.ThreeTv.setText(Three);
        holder.FourTv.setText(Four);
        holder.FiveTv.setText(Five);
        if(!Objects.equals(Six, " ") ) {
            holder.SixTv.setText(Six);
            holder.SixLabelTv.setText("6: ");
        }
        if(!Objects.equals(Seven, " ")){
            holder.SevenLabelTv.setText("7: ");
        holder.SevenTv.setText(Seven);}
        holder.DayTv.setText(Day);
        holder.TitleTv.setText(Title);
    }

    @Override
    public int getItemCount() {
        return exerciseArrayList.size();
    }

    public static class HolderExercise extends RecyclerView.ViewHolder {
        private final TextView OneTv, TwoTv, ThreeTv, FourTv, FiveTv, SixTv, SixLabelTv, SevenLabelTv, SevenTv, DayTv, TitleTv;

        public HolderExercise(@NonNull View itemView) {
            super(itemView);
            OneTv = itemView.findViewById(R.id.OneTv);
            TwoTv = itemView.findViewById(R.id.TwoTv);
            ThreeTv = itemView.findViewById(R.id.ThreeTv);
            FourTv = itemView.findViewById(R.id.FourTv);
            FiveTv = itemView.findViewById(R.id.FiveTv);
            SixTv = itemView.findViewById(R.id.SixTv);
            SevenTv = itemView.findViewById(R.id.SevenTv);
            DayTv = itemView.findViewById(R.id.DayTv);
            TitleTv = itemView.findViewById(R.id.TitleTv);
            SixLabelTv = itemView.findViewById(R.id.SixLabelTv);
            SevenLabelTv = itemView.findViewById(R.id.SevenLabelTv);

        }
    }
}

