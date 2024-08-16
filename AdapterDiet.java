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

public class AdapterDiet extends RecyclerView.Adapter<AdapterDiet.HolderDiet> {
    private Context context;

    private ArrayList<ModelDiet>
            dietArrayList;

    public AdapterDiet(Context context, ArrayList<ModelDiet> dietArrayList){
        this.context = context;
        this.dietArrayList = dietArrayList;
    }
    @NonNull
    @Override
    public HolderDiet onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_diet_day, parent, false);
        return new HolderDiet(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderDiet holder, int position) {
        ModelDiet modelDiet = dietArrayList.get(position);
        String Breakfast= modelDiet.getBreakfast();
        String MorningSnack = modelDiet.getSnack1();
        String Lunch= modelDiet.getLunch();
        String EveningSnack = modelDiet.getSnack2();
        String Dinner= modelDiet.getDinner();
        String Title = modelDiet.getTitle();

        holder.BreakfastTv.setText(Breakfast);
        holder.MorningSnackTv.setText(MorningSnack);
        holder.LunchTv.setText(Lunch);
        holder.EveningSnackTv.setText(EveningSnack);
        holder.DinnerTv.setText(Dinner);
        holder.DayTv.setText(Title);
    }

    @Override
    public int getItemCount() {
        return dietArrayList.size();
    }

    public class HolderDiet extends RecyclerView.ViewHolder {
        private TextView BreakfastTv, MorningSnackTv, LunchTv, EveningSnackTv, DinnerTv, DayTv;

        public HolderDiet(@NonNull View itemView) {
            super(itemView);
            BreakfastTv = itemView.findViewById(R.id.BreakfastTv);
            MorningSnackTv = itemView.findViewById(R.id.MorningSnackTv);
            LunchTv = itemView.findViewById(R.id.LunchTv);
            EveningSnackTv = itemView.findViewById(R.id.EveningSnackTv);
            DinnerTv = itemView.findViewById(R.id.DinnerTv);
            DayTv = itemView.findViewById(R.id.DayTv);
        }
    }
}

