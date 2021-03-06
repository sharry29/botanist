package edu.upenn.cis350.botanist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class DatabaseContributionActivity extends AppCompatActivity {

    private PlantDatabase database;

    /**
     * Start the database contribution activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_contribution);
        TextView textview = (TextView) findViewById(R.id.new_plant);
        textview.setText(getIntent().getStringExtra("plant_type"));
        database = PlantDatabase.getInstance();
    }

    /**
     * When the user presses the submit button, we use the information in the form to create a new plant
     * and add it to the database.
     * @param view
     */
    public void submit(View view) {
        String name = String.valueOf(((TextView) findViewById(R.id.new_plant)).getText());
        String website = String.valueOf(((TextView) findViewById(R.id.website)).getText());
        String light = String.valueOf(((TextView) findViewById(R.id.light)).getText());
        database.addPlant(name, website, light);
        Intent i = new Intent(getApplicationContext(), ViewPlantActivity.class);
        i.putExtra("Plant", new Plant(getIntent().getStringExtra("plant_name"), getIntent().getStringExtra("plant_type")));
        startActivity(i);
    }


}
