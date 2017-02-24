package edu.upenn.cis350.botanist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static android.R.attr.id;
import static android.os.Build.ID;

/**
 * Created by Harry on 2/16/17.
 */

public class PhotoActivity extends AppCompatActivity {
    String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String myPlants = "my_plants";
    static final Set<String> plants = new TreeSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_take_photo);

        getPlantNames();
        populateListWithFlowers();

        Button returnButton = null;
        //Button returnButton = (Button) findViewById(R.id.button5);
        returnButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getPlantNames() {
        FileInputStream f = null;
        try {
            f = openFileInput(myPlants);
            int chr = f.read();
            StringBuilder str = new StringBuilder("");
            while (chr != -1) {
                if (chr == '\n') {
                    plants.add(str.toString());
                    str = new StringBuilder("");
                } else {
                    str.append((char) chr);
                }
                chr = f.read();
            }
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateListWithFlowers() {
        LinearLayout plantList = (LinearLayout) findViewById(R.id.plant_list);
        for(String plant : plants) {
            //Create row items
            LinearLayout listItem = new LinearLayout(this);
            listItem.setOrientation(LinearLayout.HORIZONTAL);
            //List picture
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(300, 300);
            ImageView flowerImg = new ImageView(this);
            flowerImg.setLayoutParams(imgParams);
            flowerImg.setImageResource(R.drawable.flower);


            //List Text
            TextView name = new TextView(this);
            StringBuilder text = new StringBuilder();
            int splitIndex = plant.lastIndexOf(':');
            final String userName = plant.substring(0, splitIndex);
            text.append(userName);
            text.append("\nGenus: ");
            text.append(plant.substring(splitIndex + 1));
            name.setLines(2);
            name.setText(text);

            //Spacer view
            View spacer = new View(this);
            spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1));



            //Button
            imgParams = new LinearLayout.LayoutParams(200, 200);
            ImageButton imgB = new ImageButton(this);
            imgB.setLayoutParams(imgParams);
            imgB.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imgB.setAdjustViewBounds(true);
            imgB.setPadding(0,0,0,0);
            imgB.setId(plant.hashCode());
            imgB.setImageResource(R.drawable.right_arrow);

            //Set the button's onclick listener
            imgB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(userName);
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

    private void dispatchTakePictureIntent(String folderName) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(folderName);
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println(ex);
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "edu.upenn.cis350.botanist",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("flower", folderName);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.print(data == null);
        // Check which request we're responding to
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast t = Toast.makeText(getApplicationContext(),
                                         "Picture saved.",
                                         Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }


    private File createImageFile(String folderName) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = folderName + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File plantDir = new File(storageDir.getAbsolutePath() + "/" + folderName);
        plantDir.mkdir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                plantDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
