package com.example.cal_me2;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

public class Calculator extends Activity {
    Calculator_detail profile;
    TextView optimalCalorieNumber;
    ImageButton CEat, CKcal, CCalcu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate);
        profile = Calculator_detail.getProfile(this);

        ((EditText) findViewById(R.id.editAge)).setText(profile.getAge() + "");
        ((EditText) findViewById(R.id.editHeight)).setText(profile.getHeight() + "");
        ((EditText) findViewById(R.id.editWeight)).setText(profile.getWeight() + "");
        ((RadioButton) findViewById(R.id.radioFemale)).setChecked(!profile.getGender());
        ((EditText) findViewById(R.id.editNeed)).setText(profile.getAimCalorie() + "");
        optimalCalorieNumber = ((TextView) findViewById(R.id.Kcal));
        optimalCalorieNumber.setText(profile.calculateCalories() + " " + "แคลอรี่");


        CEat = findViewById(R.id.CEat);
        CKcal = findViewById(R.id.CKcal);
        CCalcu = findViewById(R.id.CCalcu);

        CEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
            }
        });
        CKcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        CCalcu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Calculator.class);
                startActivity(intent);
            }
        });


    }

    public void onGenderRadioButtonClick(View view) {
        switch (view.getId()) {
            case R.id.radioMale:
                profile.setGender(this, true);
                break;
            case R.id.radioFemale:
                profile.setGender(this, false);
                break;
        }
        optimalCalorieNumber.setText(profile.calculateCalories() + " " + "แคลอรี่");
        optimalCalorieNumber.requestLayout();
    }

    public void onHeightChangeButtonClick(View view) {
        profile.setHeight(this, Integer.parseInt(((EditText) view).getText().toString()));
        optimalCalorieNumber.setText(profile.calculateCalories() + " "  + "แคลอรี่");
        optimalCalorieNumber.requestLayout();
    }

    public void onWeightChangeButtonClick(View view) {
        profile.setWeight(this, Integer.parseInt(((EditText) view).getText().toString()));
        optimalCalorieNumber.setText(profile.calculateCalories() + " " + "แคลอรี่");
        optimalCalorieNumber.requestLayout();
    }

    public void onAgeChangeButtonClick(View view) {
        profile.setAge(this, Integer.parseInt(((EditText) view).getText().toString()));
        optimalCalorieNumber.setText(profile.calculateCalories() + " " + "แคลอรี่");
        optimalCalorieNumber.requestLayout();
    }

    public void onAimCalorieChangeButtonClick(View view) {
        profile.setAimCalorie(this, Integer.parseInt(((EditText) view).getText().toString()));
        optimalCalorieNumber.setText(profile.calculateCalories() + " " + "แคลอรี่");
        optimalCalorieNumber.requestLayout();
    }

    @Override
    public void onDestroy() {
        profile.setHeight(this, Integer.parseInt(((EditText) findViewById(R.id.editHeight)).getText().toString()));
        profile.setWeight(this, Integer.parseInt(((EditText) findViewById(R.id.editWeight)).getText().toString()));
        profile.setAge(this, Integer.parseInt(((EditText) findViewById(R.id.editAge)).getText().toString()));
        profile.setAimCalorie(this, Integer.parseInt(((EditText) findViewById(R.id.editNeed)).getText().toString()));
        profile.saveData(this);
        super.onDestroy();
    }
}
