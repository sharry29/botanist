package edu.upenn.cis350.botanist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Harry on 2/16/17.
 */

public class PhotoActivity extends AppCompatActivity {
    String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        //dispatchTakePictureIntent();
        //finish();

        //Create the list objects
//        LinearLayout plantList = (LinearLayout) findViewById(R.id.plant_list);
//        String FILENAME = "my_plants";
//        FileInputStream f = null;
//        try {
//            f = openFileInput(FILENAME);
//            int chr = f.read();
//            String str = "";
//            while (chr != -1) {
//                str = str + Character.toString((char) chr);
//                chr = f.read();
//            }
//            System.out.println(str);
//            f.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        for(int i = 0; i < numPlants; i++) {
//            //Create row items
//            LinearLayout listItem = new LinearLayout(this);
//            listItem.setOrientation(LinearLayout.HORIZONTAL);
//            //List picture
//            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(150, 150);
//            ImageView flowerImg = new ImageView(this);
//            flowerImg.setLayoutParams(imgParams);
//            flowerImg.setImageResource(R.drawable.flower);
//
//
//            //List Text
//            TextView name = new TextView(this);
//            name.setLines(2);
//            name.setText("PlantName" + (i+1) + "\nGenus" + (i+1) + "                                             ");
//
//            //Spacer view
//            View spacer = new View(this);
//            spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 0, LinearLayout.LayoutParams.MATCH_PARENT));
//
//
//            //Button
//            imgParams = new LinearLayout.LayoutParams(100, 100);
//            ImageButton imgB = new ImageButton(this);
//            imgB.setLayoutParams(imgParams);
//            imgB.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            imgB.setAdjustViewBounds(true);
//            imgB.setPadding(0,0,0,0);
//            imgB.setId(i);
//            imgB.setImageResource(R.drawable.right_arrow);
//
//            //Set the button's onclick listener
//            imgB.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    myPlantsButtonPress(v.getId());
//                }
//            });
//
//
//            //Compile this row
//            listItem.addView(flowerImg);
//            listItem.addView(name);
//            listItem.addView(spacer);
//            listItem.addView(imgB);
//            //Add this row to list
//            plantList.addView(listItem);
//        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
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
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
