package edu.upenn.cis350.botanist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

        setContentView(R.layout.activity_view_plant);

        //POPUP MENU TESTING
        //PopupMenu menu = new PopupMenu(getApplicationContext(), this);


        database = PlantDatabase.getInstance();
        //Get the layout
        LinearLayout viewPlantLayout = (LinearLayout) findViewById(R.id.view_plant_linlayout);

        //Get the plant object from context
        plant = (Plant) getIntent().getSerializableExtra("Plant");

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
        if (database.plantExists(plant.getType())) {
            String light = database.getPlantByName(plant.getType()).getLight();
            if (light.contains("null") || light.equals("")) {
                genus.setText(plant.getType() + "\n~Light Needs: " + "unavailable.");
            } else {
                genus.setText(plant.getType() + "\n~Light Needs: " + light);
            }
        } else {
            genus.setText(plant.getType());
        }
        genus.setTextSize(15);
        genus.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        genus.setPadding(25, 0, 0, 0);

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

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                Bitmap flowerPicture = BitmapFactory.decodeFile(images[i].getAbsolutePath(), options);
                if (flowerPicture != null) {
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
        }
        if (!database.plantExists(plant.getType())) {
            Button addToDB = new Button(this);
            addToDB.setText("Add this plant to our database!");
            addToDB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), DatabaseContributionActivity.class);
                    i.putExtra("plant_type", plant.getType());
                    startActivity(i);
                }
            });
            viewPlantLayout.addView(addToDB);
        }
        if (database.plantExists(plant.getType())) {
            Button viewDescription = new Button(this);
            viewDescription.setText("View a Description of the " + plant.getType());
            viewDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkDescription(plant.getType());
                }
            });
            viewPlantLayout.addView(viewDescription);
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
                if (images != null) {
                    Intent GifIntent = new Intent(getApplicationContext(), GifActivity.class);
                    GifIntent.putExtra("images", images);
                    GifIntent.putExtra("Plant", plant.getName());
                    startActivity(GifIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "You need to take pictures before we can generate your GIF!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        viewPlantLayout.addView(viewGIF);

        Button viewDaysWatered = new Button(this);
        viewDaysWatered.setText("View Days Watered");
        viewDaysWatered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent daysWateredIntent = new Intent(getApplicationContext(),
                        DaysWateredActivity.class);
                daysWateredIntent.putExtra("Plant Name", plant.getName());
                startActivity(daysWateredIntent);
            }
        });
        viewPlantLayout.addView(viewDaysWatered);

        Button goBack = new Button(this);
        goBack.setBackgroundColor(Color.DKGRAY);
        goBack.setText("Go Back to My Plants");
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homepageIntent = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(homepageIntent);
            }
        });
        viewPlantLayout.addView(goBack);

    }

    public void imageScrollButtonPress(int id) {
        //Any button press in this reigon will bring up a gallery of all photos.
    }

    private File[] findAllImages(File plantDir) {
        File[] jpg = plantDir.listFiles(new FilenameFilter() {
            public boolean accept(File plantDir, String filename) {
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
        // Check which request we're responding to
        if (requestCode == PhotoActivity.REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                LinearLayout imageScroll = (LinearLayout) findViewById(R.id.image_spinner);
                imageScroll.removeAllViews();
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File plantDir = new File(storageDir.getAbsolutePath() + "/" + plant.getName());
                images = findAllImages(plantDir); //MAY BE NULL!
                for(int i = 0; i < images.length; i++) {
                    LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(200, 200);
                    ImageButton imgB = new ImageButton(this);
                    imgB.setLayoutParams(imgParams);
                    imgB.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imgB.setAdjustViewBounds(true);
                    imgB.setPadding(15, 0, 15, 0);
                    imgB.setId(i);

                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 10;
                    Bitmap flowerPicture = BitmapFactory.decodeFile(images[i].getAbsolutePath(), options);
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
            } else if (resultCode == RESULT_FIRST_USER) {
                //do not go back to managepics
                Toast t = Toast.makeText(getApplicationContext(),
                        "Pictures deleted.",
                        Toast.LENGTH_SHORT);
                t.show();
                refreshPlantList(false);
            } else if (resultCode == RESULT_CANCELED) {
                refreshPlantList(false); //They hit the back button
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
            //startActivity(managePicsIntent);
        } else {
            finish();
            Intent thisIntent = this.getIntent();
            thisIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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

    private void checkDescription(String plant) {
        if (database.plantExists(plant)) {
            try {
                Uri uri = Uri.parse(database.getPlantByName(plant).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "There is either no website associated with this plant yet, or the website no longer exists. Sorry!",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "There is either no website associated with this plant yet, or the website no longer exists. Sorry!",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onBackPressed() {
        // Override use of back button
    }
}
