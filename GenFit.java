package com.example.gen_fit;

import android.app.Application;

public class GenFit extends Application {
    public String getBMI() {
        return BMI;
    }

    public void setBMI(String BMI) {
        this.BMI = BMI;
    }

    private String BMI;

}
