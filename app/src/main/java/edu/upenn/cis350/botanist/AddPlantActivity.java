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
    private PlantDatabase database;


    /**
     * Start the AddPlantActivity. Provide the list of plants to the AutoCompleteView.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = PlantDatabase.getInstance();
        setContentView(R.layout.activity_add_plant);
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,
                android.R.layout.simple_dropdown_item_1line, database.getPlantNames());
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.plant_list);
        textView.setAdapter(adapter);

    }


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
        if (!database.plantExists(plantType)) {
            addToDatabase(plantType, plantName);
        } else {
            Intent i = new Intent(getApplicationContext(), ViewPlantActivity.class);
            i.putExtra("Plant", new Plant(plantName, plantType));
            startActivity(i);
        }
    }

    private void addToDatabase(final String plantType, final String plantName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This plant does not exist in our database. Would you like to add it now?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getApplicationContext(), ViewPlantActivity.class);
                        i.putExtra("Plant", new Plant(plantName, plantType));
                        startActivity(i);
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getApplicationContext(), DatabaseContributionActivity.class);
                        i.putExtra("plant_type", plantType);
                        i.putExtra("plant_name", plantName);
                        startActivity(i);
                    }
                });
        AlertDialog databaseDialog = builder.create();
        databaseDialog.show();
    }



}

