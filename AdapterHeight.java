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

public class AdapterHeight extends RecyclerView.Adapter<AdapterHeight.Holderheight> {
    private Context context;

    private ArrayList<ModelHeight>
            heightArrayList;

    public AdapterHeight(Context context, ArrayList<ModelHeight> heightArrayList){
        this.context = context;
        this.heightArrayList = heightArrayList;
    }
    @NonNull
    @Override
    public Holderheight onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_height_history, parent, false);
        return new Holderheight(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holderheight holder, int position) {
        ModelHeight modelheight = heightArrayList.get(position);
        String height= modelheight.getHeight();
        String date = modelheight.getDate();

        holder.heightTv.setText("height: "+ height);
        holder.dateTv.setText("Date: "+ date);
    }

    @Override
    public int getItemCount() {
        return heightArrayList.size();
    }

    public class Holderheight extends RecyclerView.ViewHolder {
        private TextView heightTv, dateTv;

        public Holderheight(@NonNull View itemView) {
            super(itemView);
            heightTv = itemView.findViewById(R.id.heightTv);
            dateTv =itemView.findViewById(R.id.dateTv);
        }
    }
}
