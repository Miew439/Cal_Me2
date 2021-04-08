package com.example.cal_me2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static final String DB_NAME = "CalorieCounterDB2";
    private static final int DB_VERSION = 1;

    private static final String DAYS_TABLE = "days";
    private static final String DISH_TABLE = "dishes";
    private static final String DAYS_DISHES_TABLE = "days_dishes";

    public static final String COLUMN_ID = "_id";
    public static final String DAYS_COLUMN_DATE = "date";
    public static final String DAYS_COLUMN_RECORD = "record";
    public static final String DISH_COLUMN_NAME = "dish";
    public static final String DISH_COLUMN_CALORIES_PER_100_GM = "calories";
    public static final String DAYS_DISH_COLUMN_WEIGHT = "weight";

    private static final String sampleDishesNames[] = {"กระเพาะปลา", "ก๋วยเตี๋ยวคั่วไก่", "กุ้งอบวุ้นเส้น", "แกงจืดวุ้นเส้น", "ข้าวต้มปลา", "ข้าวมันไก่", "คะน้าหมูกรอบ", "ต้มยำกุ้ง"};
    private static final int sampleDishesCalories[] = {150, 435, 300, 85, 325, 596, 420, 65};


    public static final int MAX_RECORD_WIDTH = 700;
    public static int dishSortId = 0;

    private static Context context;
    private static Database database;
    private static DBHelper dbHelper;
    private static SQLiteDatabase mDB;

    private Database(Context context) {
        Database.context = context;
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        mDB = dbHelper.getWritableDatabase();
    }

    public static Database getDataBase(Context context) {
        if (Database.context == null && database != null) {
            database.close();
        }
        if (database == null || Database.context == null) {
            database = new Database(context);
        }
        return database;
    }

    public void close() {
        if (dbHelper != null)
            dbHelper.close();
        database = null;
        context = null;
    }


    public Cursor getDishes() {
        switch (dishSortId) {
            case 1:
                return mDB.query(DISH_TABLE, null, null, null, null, null, DISH_COLUMN_NAME);
            case 2:
                return mDB.query(DISH_TABLE, null, null, null, null, null, DISH_COLUMN_CALORIES_PER_100_GM);
        }
        return mDB.query(DISH_TABLE, null, null, null, null, null, null);
    }

    public Menu_detail getDish(String name) {
        Cursor cursor = mDB.query(DISH_TABLE, null, DISH_COLUMN_NAME + "='" + name + "'", null, null, null, null);
        if (!cursor.moveToFirst())
            return null;
        else
            return new Menu_detail(cursor.getString(cursor.getColumnIndex(DISH_COLUMN_NAME)), cursor.getInt(cursor.getColumnIndex(DISH_COLUMN_CALORIES_PER_100_GM)));
    }

    public void addDish(String name, int calories) {
        ContentValues cv = new ContentValues();
        cv.put(DISH_COLUMN_NAME, name);
        cv.put(DISH_COLUMN_CALORIES_PER_100_GM, calories);
        mDB.insert(DISH_TABLE, null, cv);
    }

    public void addDish(Menu_detail dish) {
        addDish(dish.getName(), dish.getCaloriesPer100Gm());
    }

    public void updateDish(String name, int calories) {
        ContentValues cv = new ContentValues();
        cv.put(DISH_COLUMN_NAME, name);
        cv.put(DISH_COLUMN_CALORIES_PER_100_GM, calories);
        mDB.update(DISH_TABLE, cv, DISH_COLUMN_NAME + "='" + name + "'", null);
    }

    public void updateDish(Menu_detail dish) {
        updateDish(dish.getName(), dish.getCaloriesPer100Gm());
    }

    public void deleteDish(String name) {
        mDB.delete(DISH_TABLE, DISH_COLUMN_NAME + "='" + name + "'", null);
        mDB.delete(DAYS_DISHES_TABLE, DISH_COLUMN_NAME + "='" + name + "'", null);
    }




    public Cursor getDaysDishesData() {
        return mDB.query(DAYS_DISHES_TABLE, null, null, null, null, null, null);
    }

    public Cursor getAllDayDishes(MyDate date) {
        Cursor cursor = mDB.query(DAYS_DISHES_TABLE, null, DAYS_COLUMN_DATE + "=" + date.getTime(), null, null, null, null);
        if (cursor.moveToFirst())
            return cursor;
        else
            return null;
    }

    public Cursor getDayDish(MyDate date, String dishName) {
        Cursor cursor = mDB.query(DAYS_DISHES_TABLE, null, DAYS_COLUMN_DATE + " = " + date.getTime() + " AND " + DISH_COLUMN_NAME + " = '" + dishName + "'", null, null, null, null);
        if (cursor.moveToFirst())
            return cursor;
        else
            return null;
    }

    public void updateDayDish(MyDate date, String dishName, int weight) {
        ContentValues cv = new ContentValues();
        cv.put(DAYS_COLUMN_DATE, date.getTime());
        cv.put(DISH_COLUMN_NAME, dishName);
        cv.put(DAYS_DISH_COLUMN_WEIGHT, weight);
        mDB.update(DAYS_DISHES_TABLE, cv, DAYS_COLUMN_DATE + " = " + date.getTime() + " AND " + DISH_COLUMN_NAME + " = '" + dishName + "'", null);

    }

    public void addDayDish(MyDate date, String dishName, int weight) {
        if (weight == 0)
            return;
        Cursor cursor = getDayDish(date, dishName);
        if (cursor == null) {
            ContentValues cv = new ContentValues();
            cv.put(DAYS_COLUMN_DATE, date.getTime());
            cv.put(DISH_COLUMN_NAME, dishName);
            cv.put(DAYS_DISH_COLUMN_WEIGHT, weight);
            mDB.insert(DAYS_DISHES_TABLE, null, cv);
        } else {
            updateDayDish(date, dishName, weight);
        }
    }

    public void deleteDayDish(MyDate date, String dishName) {
        mDB.delete(DAYS_DISHES_TABLE, DAYS_COLUMN_DATE + " = " + date.getTime() + " AND " + DISH_COLUMN_NAME + " = '" + dishName + "'", null);
    }

    public void deleteDayDishes(MyDate date) {
        mDB.delete(DAYS_DISHES_TABLE, DAYS_COLUMN_DATE + " = " + date.getTime(), null);
    }

    public Cursor getDaysRecords() {
        return mDB.query(DAYS_TABLE, null, null, null, null, null, null);
    }

    public Cursor getDayRecord(MyDate date) {
        Cursor cursor = mDB.query(DAYS_TABLE, null, DAYS_COLUMN_DATE + "=" + date.getTime(), null, null, null, null);
        if (!cursor.moveToFirst())
            return null;
        else
            return cursor;
    }

    public void updateDayRecord(MyDate date, String record) {
        ContentValues cv = new ContentValues();
        cv.put(DAYS_COLUMN_DATE, date.getTime());
        cv.put(DAYS_COLUMN_RECORD, record);
        mDB.update(DAYS_TABLE, cv, DAYS_COLUMN_DATE + " = " + date.getTime(), null);
    }

    public void addDayRecord(MyDate date, String record) {
        ContentValues cv = new ContentValues();
        cv.put(DAYS_COLUMN_DATE, date.getTime());
        cv.put(DAYS_COLUMN_RECORD, record);
        mDB.insert(DAYS_TABLE, null, cv);
    }

    public void deleteDayRecord(MyDate date) {
        mDB.delete(DAYS_TABLE, DAYS_COLUMN_DATE + " = " + date.getTime(), null);
    }


    public Day getDay(MyDate date) {
        Day day = new Day(date);
        if (getDayRecord(date) == null)
            return day;
        int cc = getDayRecord(date).getColumnIndex(DAYS_COLUMN_RECORD);
        String rec = getDayRecord(date).getString(cc);
        day.setRecord(rec);
        Cursor cursor = getAllDayDishes(date);
        if (cursor != null) {
            do {
                day.addDish(getDish(cursor.getString(cursor.getColumnIndex(DISH_COLUMN_NAME))), cursor.getInt(cursor.getColumnIndex(DAYS_DISH_COLUMN_WEIGHT)));
            } while (cursor.moveToNext());
        }
        return day;
    }

    public void saveDay(Day day) {
        MyDate date = day.getDate();
        Cursor cursor;
        cursor = getDayRecord(date);
        if (cursor == null)
            addDayRecord(date, day.getRecord());
        else
            updateDayRecord(date, day.getRecord());
        cursor = getAllDayDishes(date);
        if (cursor == null)
            for (Map.Entry<Menu_detail, Integer> dish : day.getDishes().entrySet())
                addDayDish(date, dish.getKey().getName(), dish.getValue());
        else {
            deleteDayDishes(date);
            for (Map.Entry<Menu_detail, Integer> dish : day.getDishes().entrySet())
                addDayDish(date, dish.getKey().getName(), dish.getValue());
        }

    }


    private static class DBHelper extends SQLiteOpenHelper {
        DBHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, dbName, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + DISH_TABLE + " ( " + COLUMN_ID + " integer, " + DISH_COLUMN_NAME + " text primary key," + DISH_COLUMN_CALORIES_PER_100_GM + " integer not null" + ");"); // create table dishes
            db.execSQL("create table " + DAYS_DISHES_TABLE + " ( " + DAYS_COLUMN_DATE + " integer, " + DISH_COLUMN_NAME + " text, " + DAYS_DISH_COLUMN_WEIGHT + " integer default 100," + "FOREIGN KEY(" + DAYS_COLUMN_DATE + ") REFERENCES " + DAYS_TABLE + "(" + DAYS_COLUMN_DATE + "), FOREIGN KEY(" + DISH_COLUMN_NAME + ") REFERENCES " + DISH_TABLE + "(" + DISH_COLUMN_NAME + ") " + ");");//create table days_dishes
            db.execSQL("create table " + DAYS_TABLE + " ( " + DAYS_COLUMN_DATE + " integer primary key, " + DAYS_COLUMN_RECORD + " varchar(" + MAX_RECORD_WIDTH + ") default ''" + ");");//create table days
            defaultFillDishTable(db); // fill table of dishes
        }


        private void defaultFillDishTable(SQLiteDatabase db) {
            ContentValues cv = new ContentValues();
            for (int i = 0; i < sampleDishesNames.length; i++) {
                cv.put(DISH_COLUMN_NAME, sampleDishesNames[i]);
                cv.put(DISH_COLUMN_CALORIES_PER_100_GM, sampleDishesCalories[i]);
                db.insert(DISH_TABLE, null, cv);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public static ArrayList<Map<String, Object>> cursorToArrayList(Cursor cursor) {
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Map<String, Object> map = new HashMap<>();
                for (String colName : cursor.getColumnNames()) {
                    map.put(colName, cursor.getString(cursor.getColumnIndex(colName)));
                }
                data.add(map);
            } while (cursor.moveToNext());
        }
        return data;
    }

}
