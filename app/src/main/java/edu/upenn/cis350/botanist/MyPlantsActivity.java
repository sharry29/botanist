package edu.upenn.cis350.botanist;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

        //Temporary until we read the length of data structure
        //numPlants = 25;
        numPlants = plantList.size();

        //Create the list objects
        LinearLayout fullList = (LinearLayout) findViewById(R.id.plant_list);

        for(int i = 0; i < numPlants; i++) {
            //Create row items
            RelativeLayout listItem = new RelativeLayout(this);
            //listItem.setLayoutParams(RelativeLayout.LayoutParams.)
            //listItem.setOrientation(LinearLayout.HORIZONTAL);

            //List picture -- Eventually should display an image that the user takes for the plant
            RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(150, 150);
            imgParams.addRule(RelativeLayout.ALIGN_LEFT);
            ImageView flowerImg = new ImageView(this);
            int id = View.generateViewId();
            flowerImg.setId(id);
            flowerImg.setLayoutParams(imgParams);
            flowerImg.setImageResource(R.drawable.flower);


            //List Text
            TextView name = new TextView(this);
            //name.setGravity(Gravity.LEFT);
             RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(700, 100);
            textParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            textParams.addRule(RelativeLayout.RIGHT_OF, id);
            //textParams.setPaddingRelative(600, 0, 0, 0);
            //Left padding = width of the image
            name.setPaddingRelative(150, 0, 0, 0);


            name.setText(plantList.get(i).getName() + "\n" + plantList.get(i).getType() + "                             ");
            //name.setLayoutParams(textParams);
            name.setLines(2);


            //Spacer view
            //View spacer = new View(this);
            //ViewGroup.LayoutParams space = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
            //space.height = ViewGroup.LayoutParams.MATCH_PARENT + 3;
            //spacer.setBackgroundColor(Color.BLACK);



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
            //listItem.addView(spacer);
            listItem.addView(imgB);
            //Add this row to list
            fullList.addView(listItem);
        }
    }

    public void myPlantsButtonPress(int i) {
        System.out.println("You clicked button number " + (i + 1) + "!");
        System.out.println("Name: " + plantList.get(i).getName() + " Type: " + plantList.get(i).getType());
    }



    private void readPlantsFromFile() {
        try {
            System.out.println("Reading plants...");
            String FILENAME = "my_plants";
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

/*
I need to be able to call a sorted list of plants by Name, Age, Height, Genus, etc. Ideally array of some kind
 */