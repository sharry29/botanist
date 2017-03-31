package edu.upenn.cis350.botanist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Ben on 3/29/2017.
 */

public class ManagePicturesActivity extends AppCompatActivity {

    private Plant plant;
    private File[] pictures;
    private boolean[] selected;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_pictures);

        plant = (Plant) getIntent().getSerializableExtra("Plant");
        pictures = findAllImages(plant.getName());
        selected = new boolean[pictures.length]; //init to false!

        GridView gridview = (GridView) findViewById(R.id.current_pictures);
        gridview.setAdapter(new ImageAdapter(this, pictures));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(ManagePicturesActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();

            }
        });
        gridview.setPadding(0, 200, 0, 0);
        //Select and Deselect all
        Button selToggle = new Button(this);
        selToggle.setText("Select All");
        //Draw the images in the gridView
        for (int i = 0; i < pictures.length; i++) {
            ImageView b = (ImageView) gridview.getAdapter().getView(i, null, gridview);
        }

    }

    private File[] findAllImages(String plantName) {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File plantDir = new File(storageDir.getAbsolutePath() + "/" + plantName);
        File[] jpg = plantDir.listFiles(new FilenameFilter() {
            public boolean accept(File plantDir, String filename) {
                System.out.println(filename.toLowerCase());
                return filename.toLowerCase().endsWith(".jpg");
            }
        });
        return jpg;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.view_plant_options_menu, menu);
        menu.add("Delete All Pictures");
        menu.add("App Settings");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        if (item.getTitle().equals("App Settings")) {
            Intent settingsIntent = new Intent(getApplicationContext(), UserSettingsActivity.class);
            startActivity(settingsIntent);
        } else if (item.getTitle().equals("Delete All Pictures")) {
            //Delete the pictures..
        }
        return true;
    }

}
