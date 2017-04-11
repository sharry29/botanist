package edu.upenn.cis350.botanist;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    protected static File MY_PLANTS_FILE;
    int numPlants;
    private ArrayList<Plant> plantList = new ArrayList<Plant>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PlantDatabase pd = PlantDatabase.getInstance();
        MY_PLANTS_FILE = confirmFlowersFilePresent();
        readPlantsFromFile();
        // Check needed in recent APK levels even if perms. in manifest
        verifyReadWritePermission(this);
        setContentView(R.layout.activity_my_plants);
        android.support.v7.app.ActionBar bar = this.getSupportActionBar();
        bar.setTitle("Botanist");


        // If the user hasn't saved their settings before, go the settings activity
        SharedPreferences userSettings = getSharedPreferences(
                UserSettingsActivity.USER_SETTINGS_PREFS_NAME, Context.MODE_PRIVATE
        );
        if (!userSettings.getBoolean("settingsSaved", false)) {
            Intent userSettingsIntent = new Intent(getApplicationContext(),
                    UserSettingsActivity.class);
            startActivity(userSettingsIntent);
        }

        numPlants = plantList.size();

        //Create the list objects
        LinearLayout fullList = (LinearLayout) findViewById(R.id.plant_list);

        for(int i = 0; i < numPlants; i++) {
            //Create row items
            RelativeLayout listItem = new RelativeLayout(this);


            //List item picture - Defaults to colorful flower if there is no user img
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File plantDir = new File(storageDir.getAbsolutePath() + "/" + plantList.get(i).getName());
            File latestPic = null;
            try {
                latestPic = findLatestImage(i, plantDir);
            } catch(Exception e) {
                System.out.println("Oh no! Parse Exception.");
                e.printStackTrace();
            }


            //Draw image
            RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(300, 300);
            imgParams.addRule(RelativeLayout.ALIGN_LEFT);
            ImageView flowerImg = new ImageView(this);
            int id = View.generateViewId();
            flowerImg.setId(id);
            flowerImg.setLayoutParams(imgParams);

            if (latestPic == null) {
                //Display default picture
                flowerImg.setImageResource(R.drawable.flower);
            } else {
                //Uri uri = Uri.fromFile(latestPic);
                //flowerImg.setImageURI(uri);
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap flowerPicture = BitmapFactory.decodeFile(latestPic.getAbsolutePath(), options);
                Bitmap fixed = Bitmap.createBitmap(flowerPicture, 0, 0,
                        flowerPicture.getWidth(), flowerPicture.getHeight(), matrix, true);
                flowerImg.setImageBitmap(fixed);
            }


            //List Text
            TextView name = new TextView(this);
            RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(500, 100);
            textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            textParams.addRule(RelativeLayout.RIGHT_OF, id);

            name.setPaddingRelative(350, 0, 0, 0);


            name.setText(plantList.get(i).getName() + "\n" + plantList.get(i).getType());
            //name.setLayoutParams(textParams);
            name.setLines(2);


            //Button
            imgParams = new RelativeLayout.LayoutParams(200, 200);
            imgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            imgParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            ImageButton imgB = new ImageButton(this);
            imgB.setLayoutParams(imgParams);
            imgB.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imgB.setAdjustViewBounds(true);
            imgB.setPadding(0,0,0,0);
            imgB.setId(i);
            imgB.setImageResource(R.drawable.right_arrow);

            //Set the button's onclick listener
            imgB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myPlantsButtonPress(v.getId());
                }
            });


            //Compile this row
            listItem.addView(flowerImg);
            listItem.addView(name);
            listItem.addView(imgB);
            listItem.setPadding(0, 10, 0, 10);

            //Add this row to list
            fullList.addView(listItem);
        }
    }


    private File findLatestImage(int i, File plantDir) throws ParseException {
        File[] jpg = plantDir.listFiles(new FilenameFilter() {
            public boolean accept(File plantDir, String filename)
            { System.out.println(filename.toLowerCase()); return filename.toLowerCase().endsWith(".jpg"); }
        } );


        //If there are any user jpgs for this plant, display the most recent one
        File latestPic = null;
        Date latestDate = new Date(0);
        TreeMap<Date, File> photoDates = new TreeMap<Date, File>();
        if (jpg != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
            //Check the whole list of .jpg files from the directory
            for (File img : jpg) {
                //Roundabout way to find the DATETIME substring from filename (open to changes here)
                String thisDateString =
                        img.getName().substring
                                (plantList.get(i).getName().length(),
                                        plantList.get(i).getName().length() + 15);
//                    //DEBUGGING
//                    System.out.println("DATE STRING: " + thisDateString);
                Date thisDate = format.parse(thisDateString);
                photoDates.put(thisDate, img);
//                    //DEBUGGING
//                    System.out.println("LATEST DATE    :   " + latestDate);
            }
            return photoDates.get(photoDates.lastKey());
        } else {
            return null;
        }
    }

    public void myPlantsButtonPress(int i) {
        System.out.println("You clicked button number " + (i + 1) + "!");
        System.out.println("Name: " + plantList.get(i).getName() + " Type: " + plantList.get(i).getType());
        Intent viewPlantIntent = new Intent(getApplicationContext(), ViewPlantActivity.class);
        viewPlantIntent.putExtra("Plant", plantList.get(i));
        startActivity(viewPlantIntent);
    }



    private void readPlantsFromFile() {
        try {
            System.out.println("Reading plants...");
            String FILENAME = "my_plants";
            PackageManager m = getPackageManager();
            String s = getPackageName();
            PackageInfo p = m.getPackageInfo(s, 0);
            System.out.println(p.applicationInfo.dataDir);
            FileInputStream f = openFileInput(FILENAME);
            InputStreamReader isReader = new InputStreamReader(f);
            BufferedReader in = new BufferedReader(isReader);

            String plantInfo;
            while ((plantInfo = in.readLine()) != null) {
                plantList.add(new Plant(plantInfo));
            }
            System.out.println("Found " + plantList.size());
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("Plant file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException in readPlantsFromFile() in MyPlantsActivity");
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Responsible for undoing the last line drawn
            case R.id.action_add_plant:
                Intent addPlantIntent = new Intent(getApplicationContext(),
                        AddPlantActivity.class);
                startActivity(addPlantIntent);
                return true;

            // Removes all lines drawn
            case R.id.action_settings:
                Intent userSettingsIntent = new Intent(getApplicationContext(),
                        UserSettingsActivity.class);
                startActivity(userSettingsIntent);
                return true;

            default:
                return false;
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
