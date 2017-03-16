package edu.upenn.cis350.botanist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ben on 3/15/2017.
 */

public class ViewPlantActivity extends AppCompatActivity{
    private Plant plant;
    private File[] images;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_plant);


        //Get the layout
        LinearLayout viewPlantLayout = (LinearLayout) findViewById(R.id.view_plant_linlayout);

        //Get the plant object from context
        plant = (Plant) getIntent().getSerializableExtra("Plant");
        if (plant == null) {
            System.out.println("No plant was passed to this page. Sad!");
        }

        //Get jpg images from file
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File plantDir = new File(storageDir.getAbsolutePath() + "/" + plant.getName());
        images = findAllImages(plantDir); //MAY BE NULL!

        //Get text field objects
        TextView title = (TextView) findViewById(R.id.plant_name);
        TextView genus = (TextView) findViewById(R.id.plant_genus);

        title.setText(plant.getName());
        title.setTextSize(25);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        genus.setText(plant.getType());
        genus.setTextSize(15);
        genus.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

        //Create horizontal-scroll images from jpgs
        LinearLayout imageScroll = (LinearLayout) findViewById(R.id.image_spinner);
        for(int i = 0; i < images.length; i++) {
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(200, 200);
            ImageButton imgB = new ImageButton(this);
            imgB.setLayoutParams(imgParams);
            imgB.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imgB.setAdjustViewBounds(true);
            imgB.setPadding(15, 0, 15, 0);
            imgB.setId(i);
            Bitmap flowerPicture = BitmapFactory.decodeFile(images[i].getAbsolutePath());
            imgB.setImageBitmap(flowerPicture);

            //Set the button's onclick listener
            imgB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageScrollButtonPress(v.getId());
                }
            });

            imageScroll.addView(imgB);
        }


    }

    public void imageScrollButtonPress(int id) {
        //Any button press in this reigon will bring up a gallery of all photos.
        System.out.println("Pressed image number " + (id + 1));
    }

    private File[] findAllImages(File plantDir) {
        File[] jpg = plantDir.listFiles(new FilenameFilter() {
            public boolean accept(File plantDir, String filename) {
                System.out.println(filename.toLowerCase());
                return filename.toLowerCase().endsWith(".jpg");
            }
        });
        return jpg;
    }
}
