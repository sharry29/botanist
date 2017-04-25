package edu.upenn.cis350.botanist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DaysWateredActivity extends AppCompatActivity {

    private String plantName;
    private String fileName;

    private DateFormat formatter;
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

        plantName = getIntent().getStringExtra("Plant Name");
        fileName = (plantName == null || plantName.isEmpty()) ? null : (plantName.toLowerCase().replaceAll(" ", "_") + "_days_watered");

        calendar = Calendar.getInstance();
        boolean wateredToday = setUpWateredList();

        if (fileName == null || wateredToday) {
            disableButton();
        }
    }

    private boolean setUpWateredList() {
        List<String> daysWatered = null;

        BufferedReader br = null;
        try {
            if (fileName != null) {
                FileInputStream fis = openFileInput(fileName);
                InputStreamReader isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);

                daysWatered = new ArrayList<>();

                String nextLine = br.readLine();
                while (nextLine != null) {
                    daysWatered.add(nextLine);
                    nextLine = br.readLine();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        String[] days = new String[daysWatered.size()];
        days = daysWatered.toArray(days);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, days);
        wateredList.setAdapter(adapter);

        long today = System.currentTimeMillis();
        calendar.setTimeInMillis(today);
        return daysWatered.contains(formatter.format(calendar.getTime()));
    }

    private void disableButton() {
        waterButton.setText("Plant already watered");
        waterButton.setAlpha(.5f);
        waterButton.setClickable(false);
    }

    public void markWatered(View view) {
        long today = System.currentTimeMillis();
        calendar.setTimeInMillis(today);
        String todayString = formatter.format(calendar.getTime()) + "\n";

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, Context.MODE_APPEND);
            fos.write(todayString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        setUpWateredList();
        disableButton();
    }
}
