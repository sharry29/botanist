package edu.upenn.cis350.botanist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class AddPlantActivity extends AppCompatActivity {
    String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    //final FirebaseDatabase database = FirebaseDatabase.getInstance();


    /**
     * Start the AddPlantActivity. Provide the list of plants to the AutoCompleteView.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,
                android.R.layout.simple_dropdown_item_1line, getIntent().getExtras().getStringArray("plant list"));
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.plant_list);
        textView.setAdapter(adapter);
    }


    /**
     * List of plants used for the autocomplete textView. This is just meant to check the functionality
     * until we find a way to add and store more plants.
     */
    private static final String[] PLANTS = new String[] {
            "Sunflower", "Rose", "Carnation", "Tulip", "Daffodil"
    };

    /**
     * Checks to see if our collection of plants includes the plant the user wants to add. If so,
     * redirect the user to the Wikipedia page for the plant if they click the description button.
     * If we do not have this plant on record, let the user know.
     * @param view
     */
    public void checkDescription(View view) {
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.plant_list);
        String plant = String.valueOf(textView.getText());
        if (plant.equals("Sunflower")) {
            Uri uri = Uri.parse("https://en.wikipedia.org/wiki/Helianthus");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "A description is not available for this plant!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * Add the user's new plant to internal storage. Plants are stored in the format "Plant_Name:Plant_Type".
     * Each plant is on its own individual line in the file.
     * @param view
     */
    public void addNewPlant(View view) {
        TextView plantText = (TextView) findViewById(R.id.plant_name);
        String plantName = String.valueOf(plantText.getText());
        TextView typeText = (TextView) findViewById(R.id.plant_list);
        String plantType = String.valueOf(typeText.getText());
        String plantInfo = plantName + ":" + plantType + "\n";


        String FILENAME = "my_plants";
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_APPEND);
            fos.write(plantInfo.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        promptForPicture(plantName);
    }

    private void promptForPicture(final String plantName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to take a picture of this plant?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        takePicture(plantName);
                    }
                });
        AlertDialog pictureDialog = builder.create();
        pictureDialog.show();
    }

    private void takePicture(String folderName) {
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
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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

