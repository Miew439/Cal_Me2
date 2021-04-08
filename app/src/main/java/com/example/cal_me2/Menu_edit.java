package com.example.cal_me2;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class Menu_edit extends Activity {
    static Menu_detail dish;
    Database db;
    private String firstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editmenu);

        db = Database.getDataBase(this);
        if (dish == null) {
            dish = new Menu_detail();
        }
        firstName = dish.getName();
        ((EditText) findViewById(R.id.editMName)).setText(dish.getName());
        ((EditText) findViewById(R.id.editKcal)).setText(((Integer) dish.getCaloriesPer100Gm()).toString());
        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dish.setName(((EditText) findViewById(R.id.editMName)).getText().toString());
                dish.setCalories(Integer.parseInt(((EditText) findViewById(R.id.editKcal)).getText().toString()));
                if (!firstName.equals(dish.getName()))
                    db.deleteDish(firstName);
                if (db.getDish(dish.getName()) == null)
                    db.addDish(dish);
                else
                    db.updateDish(dish);
                dish = null;
                finish();
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dish = null;
                finish();
            }
        });

    }
}
