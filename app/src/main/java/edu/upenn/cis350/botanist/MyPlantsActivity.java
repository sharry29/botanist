package edu.upenn.cis350.botanist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Ben on 2/16/2017.
 */

public class MyPlantsActivity extends AppCompatActivity {

    int numPlants;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plants);

        numPlants = 25;

        //Create the list objects
        LinearLayout plantList = (LinearLayout) findViewById(R.id.plant_list);

        for(int i = 0; i < numPlants; i++) {
            //Create row items
            LinearLayout listItem = new LinearLayout(this);
            listItem.setOrientation(LinearLayout.HORIZONTAL);
            //List picture
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(150, 150);
            ImageView flowerImg = new ImageView(this);
            flowerImg.setLayoutParams(imgParams);
            flowerImg.setImageResource(R.drawable.flower);


            //List Text
            TextView name = new TextView(this);
            name.setLines(2);
            name.setText("PlantName" + (i+1) + "\nGenus" + (i+1) + "                                             ");

            //Spacer view
            View spacer = new View(this);
            spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 0, LinearLayout.LayoutParams.MATCH_PARENT));


            //Button
            imgParams = new LinearLayout.LayoutParams(100, 100);
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
            listItem.addView(spacer);
            listItem.addView(imgB);
            //Add this row to list
            plantList.addView(listItem);
        }
    }

    public void myPlantsButtonPress(int i) {
        System.out.println("The " + i + "th button was clicked!");
    }
}

/*
I need to be able to call a sorted list of plants by Name, Age, Height, Genus, etc. Ideally array of some kind
 */