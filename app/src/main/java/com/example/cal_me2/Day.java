package com.example.cal_me2;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;


public class Day {
    public static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
    private static final String[] DAYS_OF_WEEK = {"Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static final String[] MONTH_OF_YEAR = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private final String dayOfWeek;
    private final MyDate date;
    private static Calendar calendar = Calendar.getInstance();
    private String record;
    private Map<Menu_detail, Integer> dishes;

    public Day(MyDate date) {
        this.date = date;
        record = "";
        dishes = new LinkedHashMap<>();
        dayOfWeek = getDayOfWeekByDate(date);
        calendar.setTime(date);
    }

    public Day(MyDate date, String record, Map<Menu_detail, Integer> dishes) {
        this.date = date;
        this.record = record;
        this.dishes = dishes;
        dayOfWeek = getDayOfWeekByDate(date);
        calendar.setTime(date);
    }

    public static Day getCurrentDay(Database db) {
        MyDate date = new MyDate();
        return getDayByDate(db, date);
    }

    public Day getNextDay(Database db) {
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        MyDate nextDayDate = new MyDate(calendar.getTime());
        calendar.setTime(date);
        return getDayByDate(db, nextDayDate);
    }

    public Day getPreviousDay(Database db) {
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        MyDate nextDayDate = new MyDate(calendar.getTime());
        calendar.setTime(date);
        return getDayByDate(db, nextDayDate);
    }

    public static Day getDayByDate(Database db, MyDate date) {
        calendar.setTime(date);
        if (calendar.get(Calendar.YEAR) < 2000 || calendar.get(Calendar.YEAR) >= 2100)
            throw new NullPointerException();
        return db.getDay(date);
    }

    public static String getMonthByNumber(int i) {
        if (i < 12)
            return MONTH_OF_YEAR[i];
        else
            return null;
    }

    public int getReceivedCalories() {
        int calories = 0;
        for (Map.Entry<Menu_detail, Integer> c : dishes.entrySet())
            calories += c.getKey().parseCalories(c.getValue());
        return calories;
    }


    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public MyDate getDate() {
        return date;
    }

    public Map<Menu_detail, Integer> getDishes() {
        return dishes;
    }


    public void addDish(Menu_detail dish, Integer weight) {
        if (!dishes.containsKey(dish)) {
            dishes.put(dish, weight);
        } else {
            dishes.put(dish, dishes.get(dish) + weight);
        }
    }


    public void saveChanges(Database db) {
        db.saveDay(this);
    }

    public static String getDayOfWeekByDate(MyDate date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return DAYS_OF_WEEK[calendar.get(Calendar.DAY_OF_WEEK)];
    }
}
