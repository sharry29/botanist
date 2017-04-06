package edu.upenn.cis350.botanist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ben on 3/15/2017.
 */

public class ViewPlantActivity extends AppCompatActivity{
    private Plant plant;
    private File[] images;
    private PlantDatabase database;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = PlantDatabase.getInstance();
        setContentView(R.layout.activity_view_plant);

        //POPUP MENU TESTING
        //PopupMenu menu = new PopupMenu(getApplicationContext(), this);



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
        title.setPadding(25,0, 0, 0);
        genus.setText(plant.getType());
        genus.setTextSize(15);
        genus.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        genus.setPadding(25, 0, 0, 0);


        //Add a Description box. Obviously this is just filler text, will replace with plant info
        //when we find a plant API (or download a plant dictionary)
        TextView description = (TextView) findViewById(R.id.plant_description);
        PlantModel p = database.getPlantByName(plant.getType());
        if (p == null) {
            description.setText("Sorry, no information about this plant is available at the moment.");
        } else {
            description.setText("Light needs: " + p.getLight() + "\nYou can find more information about this plant at " + p.getUrl());
        }
            description.setPadding(25, 0, 0, 0);
        //viewPlantLayout.addView(description);

        //Create horizontal-scroll images from jpgs
        LinearLayout imageScroll = (LinearLayout) findViewById(R.id.image_spinner);
        if (images != null) {
            for (int i = 0; i < images.length; i++) {
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

        Button takePicture = new Button(this);
        takePicture.setText("Take A New Picture");
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent takePicture = new Intent(getApplicationContext(), PhotoActivity.class);
                dispatchTakePictureIntent(plant.getName());
            }
        });
        viewPlantLayout.addView(takePicture);

        Button viewGIF = new Button(this);
        viewGIF.setText("View GIF of Plant Growth");
        viewGIF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GifIntent = new Intent(getApplicationContext(), GifActivity.class);
                GifIntent.putExtra("images", images);
                GifIntent.putExtra("Plant", plant.getName());
                startActivity(GifIntent);
            }
        });
        viewPlantLayout.addView(viewGIF);

        //Add a Take a Picture bo

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


    /*All following code stolen from PhotoActivity (Modified slightly)*/
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
                startActivityForResult(takePictureIntent, PhotoActivity.REQUEST_IMAGE_CAPTURE);

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.print(data == null);
        // Check which request we're responding to
        if (requestCode == PhotoActivity.REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast t = Toast.makeText(getApplicationContext(),
                        "Picture saved.",
                        Toast.LENGTH_SHORT);
                t.show();
                refreshPlantList(false);
            }
        }
        //73 is literally just a random number that I chose. It represents
        //a return from ManagePicturesActivity
        else if (requestCode == 73) {
            if (resultCode == RESULT_OK) {
                //go back to managepics
                Toast t = Toast.makeText(getApplicationContext(),
                        "Picture deleted.",
                        Toast.LENGTH_SHORT);
                t.show();
                refreshPlantList(true);
            } else if (resultCode == RESULT_CANCELED) {
                //do not go back to managepics
                Toast t = Toast.makeText(getApplicationContext(),
                        "Pictures deleted.",
                        Toast.LENGTH_SHORT);
                t.show();
                refreshPlantList(false);
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
        //mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //To be called from this activity or ManagePicturesActivity
    //Purpose is so that new pictures/deleted pictures update realtime instead of on page reload
    public void refreshPlantList(boolean returnToManage) {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File plantDir = new File(storageDir.getAbsolutePath() + "/" + plant.getName());
        images = findAllImages(plantDir);

        if (returnToManage) {
            //Intent backToManagePictures = new Intent(getApplicationContext(), ManagePicturesActivity.class);
            Intent managePicsIntent = new Intent(getApplicationContext(), ManagePicturesActivity.class);
            managePicsIntent.putExtra("Plant", plant);
            startActivityForResult(managePicsIntent, 73);
            managePicsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(managePicsIntent);
        } else {
            Intent thisIntent = this.getIntent();
            thisIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(thisIntent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_plant_options_menu, menu);
        menu.add("Manage Plant Pictures");
        menu.add("App Settings");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selectio

        if (item.getTitle().equals("App Settings")) {
            Intent settingsIntent = new Intent(getApplicationContext(), UserSettingsActivity.class);
            startActivity(settingsIntent);
        } else if (item.getTitle().equals("Manage Plant Pictures")) {
            Intent managePicsIntent = new Intent(getApplicationContext(), ManagePicturesActivity.class);
            managePicsIntent.putExtra("Plant", plant);
            startActivityForResult(managePicsIntent, 73);
        }
        return true;
    }
}
