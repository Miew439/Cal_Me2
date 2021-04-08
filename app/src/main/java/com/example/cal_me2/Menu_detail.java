package com.example.cal_me2;


public class Menu_detail {
    private String name;
    private int caloriesPerPlate;

    public Menu_detail() {
        this("", 0);
    }

    public Menu_detail(String name, int caloriesPer100Gm) {
        this.caloriesPerPlate = caloriesPerPlate;
        this.name = name;
    }

    public int parseCalories(int weight) {
        return caloriesPerPlate * weight;
    }

    public String getName() {
        return name;
    }

    public int getCaloriesPer100Gm() {
        return caloriesPerPlate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(int caloriesPer100Gm) {
        this.caloriesPerPlate = caloriesPerPlate;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Menu_detail)
            return name.equals(((Menu_detail) o).name);
        else
            return false;
    }
}
