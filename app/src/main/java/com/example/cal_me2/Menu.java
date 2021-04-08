package com.example.cal_me2;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;



import java.util.ArrayList;
import java.util.Map;

public class Menu extends Activity {
    Database db;
    Map<String, Object> map;
    ArrayList<Map<String, Object>> data;
    SimpleAdapter sAdapter;
    long selectedElementId=-1;
    ListView listView;
    ImageButton AHis, AEat, AKcal, ACalcu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmenu);

        AEat = findViewById(R.id.AEat);
        AKcal = findViewById(R.id.AKcal);
        ACalcu = findViewById(R.id.ACalcu);

        AEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivity(intent);
            }
        });
        AKcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        ACalcu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Calculator.class);
                startActivity(intent);
            }
        });


        db = Database.getDataBase(this); // open DB
        data= Database.cursorToArrayList(db.getDishes());

        // collation columns forming
        String[] from = new String[] {Database.DISH_COLUMN_NAME, Database.DISH_COLUMN_CALORIES_PER_100_GM }; // columns names
        int[] to = new int[] { R.id.db_item_name, R.id.db_item_right_text}; // places to write (View id)

        sAdapter = new SimpleAdapter(this, data ,  R.layout.history, from, to);
        listView= findViewById(R.id.listViewAdd);
        listView.setAdapter(sAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedElementId = id;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        menu.add(0, 1, 0, getString(R.string.order_by_alphabet));
        menu.add(0, 2, 1, getString(R.string.order_by_caloricity));
        menu.add(0, 3, 2, getString(R.string.order_by_id));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case 1: Database.dishSortId=1;
                break;
            case 2: Database.dishSortId=2;
                break;
            case 3: Database.dishSortId=0;
                break;
        }
        data.clear();
        data.addAll(Database.cursorToArrayList(db.getDishes()));
        sAdapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }

    public void onDeleteDish(View view)
    {
        if(selectedElementId<0)
            Toast.makeText(this, getString(R.string.pick_dish), Toast.LENGTH_SHORT).show();
        else
        {
            String dishName=(String)data.get((int)selectedElementId).get(Database.DISH_COLUMN_NAME);
            db.deleteDish(dishName);
            data.remove((int)selectedElementId);
            selectedElementId=-1;
            sAdapter.notifyDataSetChanged();
        }
    }

    public void onEditDish(View view)
    {
        if (selectedElementId<0) {
            Toast.makeText(this, getString(R.string.pick_dish), Toast.LENGTH_SHORT).show();
            return;
        }
        //fill with new data
        Menu_detail dish = new Menu_detail();
        map=data.get((int)selectedElementId);
        dish.setName(map.get(Database.DISH_COLUMN_NAME).toString());
        dish.setCalories(Integer.parseInt(map.get(Database.DISH_COLUMN_CALORIES_PER_100_GM).toString()));
        Menu_edit.dish=dish;
        onCreateDish(view);
    }

    public void onCreateDish(View view)
    {
        Intent intent = new Intent(this, Menu_edit.class);
        startActivity(intent);
        selectedElementId=-1;
    }

    @Override
    protected void onRestart() {
        data.clear();
        data.addAll(Database.cursorToArrayList(db.getDishes()));
        sAdapter.notifyDataSetChanged();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
