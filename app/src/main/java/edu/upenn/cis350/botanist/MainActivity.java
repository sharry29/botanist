package edu.upenn.cis350.botanist;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    protected static File MY_PLANTS_FILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MY_PLANTS_FILE = confirmFlowersFilePresent();

        System.out.println("here");
        // TEST CODE!!!
        String FILENAME = "my_plants";
        FileInputStream f = null;
        try {
            f = openFileInput(FILENAME);
            int chr = f.read();
            String str = "";
            while (chr != -1) {
                str = str + Character.toString((char) chr);
                chr = f.read();
            }
            System.out.println(str);
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check needed in recent APK levels even if perms. in manifest
        verifyReadWritePermission(this);
        setContentView(R.layout.activity_main);

        // Set up the main menu list
        ListView mainMenuList = (ListView) findViewById(R.id.main_menu_list);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.main_menu_array, android.R.layout.simple_list_item_1);
        mainMenuList.setAdapter(adapter);
        mainMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) parent.getItemAtPosition(position);
                switch (itemValue) {
                    case "Monitor Plants":
                        Intent myPlantsIntent = new Intent(getApplicationContext(),
                                MyPlantsActivity.class);
                        startActivity(myPlantsIntent);
                        break;
                    case "Take a Photo":
                        Intent takePhotoIntent = new Intent(getApplicationContext(),
                                PhotoActivity.class);
                        startActivity(takePhotoIntent);
                        break;
                    case "Add New Plant":
                        Intent addPlantIntent = new Intent(getApplicationContext(),
                                AddPlantActivity.class);
                        startActivity(addPlantIntent);
                        break;
                    case "View Plant Types":
                        break;
                    case "Settings":
                        Intent userSettingsIntent = new Intent(getApplicationContext(),
                                UserSettingsActivity.class);
                        startActivity(userSettingsIntent);
                        break;
                }
            }
        });

        // If the user hasn't saved their settings before, go the settings activity
        SharedPreferences userSettings = getSharedPreferences(
                UserSettingsActivity.USER_SETTINGS_PREFS_NAME, Context.MODE_PRIVATE
        );
        if (!userSettings.getBoolean("settingsSaved", false)) {
            Intent userSettingsIntent = new Intent(getApplicationContext(),
                    UserSettingsActivity.class);
            startActivity(userSettingsIntent);
        }
    }

    protected File confirmFlowersFilePresent() {
        File storageDirectory = getFilesDir();
        File flowersFile = new File(storageDirectory.getAbsolutePath() + "/my_plants");
        try {
            flowersFile.createNewFile();
        } catch (IOException e) {
            System.err.println("Failed to confirm existence of my_flowers.");
            e.printStackTrace();
        }
        return flowersFile;
    }
    public static void verifyReadWritePermission(Activity activity) {
        int permissionCode = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCode == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity,
                                              STORAGE_PERMISSIONS,
                                              REQUEST_EXTERNAL_STORAGE);
        }
    }
}
