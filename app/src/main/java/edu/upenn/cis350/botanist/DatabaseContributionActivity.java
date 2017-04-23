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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_contribution);
        TextView textview = (TextView) findViewById(R.id.new_plant);
        textview.setText(getIntent().getStringExtra("plant_type"));
        database = PlantDatabase.getInstance();
    }

    public void submit(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


}
