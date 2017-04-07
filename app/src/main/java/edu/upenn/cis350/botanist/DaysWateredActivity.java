package edu.upenn.cis350.botanist;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class DaysWateredActivity extends AppCompatActivity {
    public static final String DAYS_WATERED_PREFS_NAME = "DaysWateredPrefs";
    private DateFormat formatter;
    private SharedPreferences daysWatered;
    private Button waterButton;
    private ListView wateredList;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_watered);
        formatter = new SimpleDateFormat("EEE, MMM d, yyyy");

        waterButton = (Button) findViewById(R.id.water_button);
        wateredList = (ListView) findViewById(R.id.watered_list);

        daysWatered = getSharedPreferences(DAYS_WATERED_PREFS_NAME, Context.MODE_PRIVATE);
        populateSampleData();
        Map<String, ?> allDays = daysWatered.getAll();
        setUpWateredList(allDays);


        long today = System.currentTimeMillis();
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(today);

        String todayString = formatter.format(calendar.getTime());

        if (allDays.containsKey(todayString)) {
            disableButton();
        }
    }

    public void populateSampleData() {
        SharedPreferences.Editor editor = daysWatered.edit();
        editor.clear();
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String[] dates = {"2017/04/06"};
        try {
            for (String date : dates) {
                Date d = format.parse(date);
                long milliseconds = d.getTime();
                editor.putLong(formatter.format(milliseconds), milliseconds);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            editor.apply();
        }
    }

    public void setUpWateredList(Map<String, ?> allDays) {
        String[] days = new String[allDays.size()];
        days = allDays.keySet().toArray(days);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, days);
        wateredList.setAdapter(adapter);
    }

    public void disableButton() {
        waterButton.setText("Plant already watered");
        waterButton.setAlpha(.5f);
        waterButton.setClickable(false);
    }

    public void markWatered(View view) {
        long today = System.currentTimeMillis();
        calendar.setTimeInMillis(today);
        String todayString = formatter.format(calendar.getTime());

        SharedPreferences.Editor editor = daysWatered.edit();
        editor.putLong(todayString, today);
        editor.apply();

        Map<String, ?> allDays = daysWatered.getAll();
        setUpWateredList(allDays);
        disableButton();
    }
}
