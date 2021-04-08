package com.example.cal_me2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MainActivity extends Activity {
    Calculator_detail profile;
    Day viewDay; // viewed day
    Database db;

    ImageButton DEat, DKcal, DCalcu;
    SimpleAdapter dishesAdapter;
    ArrayList<Map<String, Object>> dishesData;
    SimpleAdapter exercisesAdapter;
    ArrayList<Map<String, Object>> exercisesData;
    private static final int DATE_DIALOG = 1;
    private static final int VIEW_DISHES_DIALOG = 2;
    private static final int VIEW_EXERCISES_DIALOG = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.setDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });
        findViewById(R.id.leftArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewDay(viewDay.getPreviousDay(db).getDate());
            }
        });
        findViewById(R.id.rightArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewDay(viewDay.getNextDay(db).getDate());
            }
        });
        findViewById(R.id.diary_dish_add_button).setOnClickListener(new ToWindowOnClick(this, MenuAdd.class) {
            @Override
            public void onClick(View v) {
                MenuAdd.date = viewDay.getDate();
                super.onClick(v);
            }
        });

        DEat = findViewById(R.id.DEat);
        DKcal = findViewById(R.id.DKcal);
        DCalcu = findViewById(R.id.DCalcu);

        DEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
            }
        });
        DKcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        DCalcu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Calculator.class);
                startActivity(intent);
            }
        });

        profile = Calculator_detail.getProfile(this);
        db = Database.getDataBase(this);
        setViewDay(new MyDate());

        dishesData = Database.cursorToArrayList(db.getAllDayDishes(viewDay.getDate()));

        String[] from = new String[]{Database.DISH_COLUMN_NAME, Database.DAYS_DISH_COLUMN_WEIGHT};
        int[] to = new int[]{R.id.db_item_name, R.id.db_item_right_text};

        dishesAdapter = new SimpleAdapter(this, dishesData, R.layout.history, from, to);



    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb;
        switch (id) {
            case DATE_DIALOG:
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(viewDay.getDate());
                return new DatePickerDialog(this, myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            case VIEW_DISHES_DIALOG:
                adb = new AlertDialog.Builder(this);
                adb.setTitle(getString(R.string.eaten_dishes));
                adb.setAdapter(dishesAdapter, null);
                return adb.create();
            case VIEW_EXERCISES_DIALOG:
                adb = new AlertDialog.Builder(this);
                adb.setTitle(getString(R.string.exercises_per_day));
                adb.setAdapter(exercisesAdapter, null);
                return adb.create();
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DATE_DIALOG:
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(viewDay.getDate());
                ((DatePickerDialog) dialog).updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                break;
            case VIEW_DISHES_DIALOG:
                break;
            case VIEW_EXERCISES_DIALOG:
                break;
        }
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String newDate = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
            try {
                changeViewDay(new MyDate(Day.format.parse(newDate)));
            } catch (ParseException e) {
                Toast.makeText(MainActivity.this, getString(R.string.invalid_date), Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(MainActivity.this, getString(R.string.invalid_year), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void onViewDayDishes(View v) {
        showDialog(VIEW_DISHES_DIALOG);
    }

    public void onViewDayExercises(View v) {
        showDialog(VIEW_EXERCISES_DIALOG);
    }

    private void changeViewDay(MyDate date) {
        saveViewDay();
        setViewDay(date);
    }

    private void saveViewDay()
    {
        viewDay.setRecord(((EditText) findViewById(R.id.record)).getText().toString());
        for (Map<String, Object> dish : dishesData)
            viewDay.addDish(db.getDish((String) dish.get(Database.DISH_COLUMN_NAME)), Integer.parseInt((String) dish.get(Database.DAYS_DISH_COLUMN_WEIGHT)));
        db.saveDay(viewDay);
    }

    private void setViewDay(MyDate date) {
        setViewDay(Day.getDayByDate(db, date));
    }

    private void setViewDay(Day day) {
        viewDay = day;
        if (dishesData != null) {
            dishesData.clear();
            dishesData.addAll(Database.cursorToArrayList(db.getAllDayDishes(viewDay.getDate())));
            dishesAdapter.notifyDataSetChanged();
        }
        ((TextView) findViewById(R.id.diary_weekday)).setText(Day.getDayOfWeekByDate(day.getDate()));
        ((EditText) findViewById(R.id.setDate)).setText(Day.format.format(day.getDate()));
        ((EditText) findViewById(R.id.record)).setText(day.getRecord());
        updateCaloriesRow();
    }

    protected void onRestart() {
        dishesData.clear();
        dishesData.addAll(Database.cursorToArrayList(db.getAllDayDishes(viewDay.getDate())));
        dishesAdapter.notifyDataSetChanged();
        updateCaloriesRow();
        super.onRestart();
    }

    private void updateCaloriesRow() {
        Integer receivedCalories = viewDay.getReceivedCalories();
        ((TextView) findViewById(R.id.kcalGot)).setText(receivedCalories.toString()+" Kcal");
        ((TextView) findViewById(R.id.kcalNeed)).setText(((Integer) (profile.getAimCalorie() - receivedCalories)).toString()+" Kcal");
        findViewById(R.id.kcalGot).requestLayout();
        findViewById(R.id.kcalNeed).requestLayout();
    }

    @Override
    protected void onDestroy() {
        saveViewDay();
        db.close();
        super.onDestroy();
    }
}
