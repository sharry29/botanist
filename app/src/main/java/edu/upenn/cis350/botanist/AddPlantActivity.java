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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
     * Checks to see if our databaseincludes the plant the user wants to add. If so,
     * redirect the user to the website associated with that plant.
     * If we do not have this plant on record or if the website is not valid, send a message to the user..
     * @param view
     */
    public void checkDescription(View view) {
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.plant_list);
        String plant = String.valueOf(textView.getText());
        if (database.plantExists(plant)) {
            try {
                Uri uri = Uri.parse(database.getPlantByName(plant).getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "There is either no website associated with " +
                        "this plant yet, or the website no longer exists. Sorry!",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "There is either no website associated " +
                    "with this plant yet, or the website no longer exists. Sorry!",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * Add the user's new plant to internal storage. Plants are stored in the format "Plant_Name:Plant_Type".
     * Each plant is on its own individual line in the file. If the plant does not exist in our database,
     * the user is given the option to add it. Otherwise, the user is redirected to their new plant's page.
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
            FileInputStream f = openFileInput(FILENAME);
            InputStreamReader isReader = new InputStreamReader(f);
            BufferedReader in = new BufferedReader(isReader);

            String info = "";
            while ((info = in.readLine()) != null) {
                String[] parts = info.split(":");
                if (parts[0].toLowerCase().equals(plantName.toLowerCase())) {
                    Toast.makeText(getApplicationContext(), "You already have a plant named " +
                            plantName + ". Please pick another name!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            f.close();
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

    /**
     * This dialogue box asks the user if they want to add the new plant type to our database. If they do,
     * they are redirected to the DatabaseContributionActivity, otherwise they are redirected to their plant's page.
     * @param plantType
     * @param plantName
     */
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

