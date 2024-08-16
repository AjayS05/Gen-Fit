package com.example.gen_fit;

public class ModelHeight {
    String Height, date;

    public ModelHeight(){}

    public ModelHeight(String Height, String date){
        this.Height = Height;
        this.date = date;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String Height) {
        this.Height = Height;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
