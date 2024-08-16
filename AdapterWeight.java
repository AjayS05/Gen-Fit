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

public class AdapterWeight extends RecyclerView.Adapter<AdapterWeight.HolderWeight> {
    private Context context;

    private ArrayList<ModelWeight>
        weightArrayList;

    public AdapterWeight(Context context, ArrayList<ModelWeight> weightArrayList){
        this.context = context;
        this.weightArrayList = weightArrayList;
    }
    @NonNull
    @Override
    public HolderWeight onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_weight_history, parent, false);
        return new HolderWeight(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderWeight holder, int position) {
        ModelWeight modelWeight = weightArrayList.get(position);
        String weight= modelWeight.getWeight();
        String date = modelWeight.getDate();

        holder.weightTv.setText("Weight: "+ weight);
        holder.dateTv.setText("Date: "+ date);
    }

    @Override
    public int getItemCount() {
        return weightArrayList.size();
    }

    public class HolderWeight extends RecyclerView.ViewHolder {
        private TextView weightTv, dateTv;

        public HolderWeight(@NonNull View itemView) {
            super(itemView);
            weightTv = itemView.findViewById(R.id.weightTv);
            dateTv =itemView.findViewById(R.id.dateTv);
        }
    }
}
