package com.example.cal_me2;

import android.app.Activity;
import android.database.Cursor;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.Map;

public class MenuAdd extends Activity {
    private Database db;
    private Map<String, Object> map;
    private ArrayList<Map<String, Object>> data;
    private SimpleAdapter sAdapter;
    private long selectedElementId = -1;
    ListView listView;
    public static MyDate date;
    private static final int SET_WEIGHT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        db = Database.getDataBase(this);
        data = Database.cursorToArrayList(db.getDishes());

        String[] from = new String[]{Database.DISH_COLUMN_NAME, Database.DISH_COLUMN_CALORIES_PER_100_GM};
        int[] to = new int[]{R.id.db_item_name, R.id.db_item_right_text};

        sAdapter = new SimpleAdapter(this, data, R.layout.history, from, to);
        listView = findViewById(R.id.listMenu);
        listView.setAdapter(sAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) findViewById(R.id.pick_dish_picked_dish_name)).setText(((TextView) view.findViewById(R.id.db_item_name)).getText().toString());
                selectedElementId = id;
                onSetPickedDishWeight(view);
            }
        });


        findViewById(R.id.AbtnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedElementId < 0) {
                    finish();
                    return;
                }
                String pickedDishName = (String) data.get((int) selectedElementId).get(Database.DISH_COLUMN_NAME);
                int pickedWeight = Integer.parseInt(((EditText) findViewById(R.id.editAmount)).getText().toString());
                Cursor cursor = db.getDayDish(date, pickedDishName);
                if (cursor == null)
                    db.addDayDish(date, pickedDishName, pickedWeight);
                else
                    db.updateDayDish(date, pickedDishName, pickedWeight + cursor.getInt(cursor.getColumnIndex(Database.DAYS_DISH_COLUMN_WEIGHT)));
                finish();
            }
        });
        findViewById(R.id.AbtnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onSetPickedDishWeight(View view) {
        showDialog(SET_WEIGHT);
    }




    }

