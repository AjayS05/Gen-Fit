package com.example.gen_fit;

public class ModelDiet {

    String Title, Breakfast, Snack1, Lunch, Snack2, Dinner;

    public ModelDiet(){}

    public ModelDiet(String Title, String Breakfast, String Snack1, String Lunch, String Snack2, String Dinner){
        this.Title  = Title;
        this.Breakfast = Breakfast;
        this.Snack1 = Snack1;
        this.Lunch = Lunch;
        this.Snack2 = Snack2;
        this.Dinner = Dinner;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String day) {
        this.Title = Title;
    }

    public String getBreakfast() {
        return Breakfast;
    }

    public void setBreakfast(String Breakfast) {
        this.Breakfast = Breakfast;
    }

    public String getSnack1() {
        return Snack1;
    }

    public void setSnack1(String Snack1) {
        this.Snack1 = Snack1;
    }

    public String getLunch() {
        return Lunch;
    }

    public void setLunch(String Lunch) {
        this.Lunch = Lunch;
    }

    public String getSnack2() {
        return Snack2;
    }

    public void setSnack2(String Snack2) {
        this.Snack2 = Snack2;
    }

    public String getDinner() {
        return Dinner;
    }

    public void setDinner(String Dinner) {
        this.Dinner = Dinner;
    }
}
