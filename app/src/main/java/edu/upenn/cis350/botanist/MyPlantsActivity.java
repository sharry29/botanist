package edu.upenn.cis350.botanist;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Ben on 2/16/2017.
 */

public class MyPlantsActivity extends AppCompatActivity {

    int numPlants;
    private ArrayList<Plant> plantList = new ArrayList<Plant>();

    protected void onCreate(Bundle savedInstanceState) {
        readPlantsFromFile();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plants);


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
            RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(150, 150);
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
                Bitmap flowerPicture = BitmapFactory.decodeFile(latestPic.getAbsolutePath());
                flowerImg.setImageBitmap(flowerPicture);
            }


            //List Text
            TextView name = new TextView(this);
             RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(700, 100);
            textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            textParams.addRule(RelativeLayout.RIGHT_OF, id);

            name.setPaddingRelative(200, 0, 0, 0);


            name.setText(plantList.get(i).getName() + "\n" + plantList.get(i).getType() + "                             ");
            //name.setLayoutParams(textParams);
            name.setLines(2);


            //Button
            imgParams = new RelativeLayout.LayoutParams(100, 100);
            imgParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
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

}

/*
I need to be able to call a sorted list of plants by Name, Age, Height, Genus, etc. Ideally array of some kind.
This is just so the user can sort the plants however they want.
 */