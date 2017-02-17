package edu.upenn.cis350.botanist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class AddPlantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this,
                android.R.layout.simple_dropdown_item_1line, PLANTS);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.plant_list);
        textView.setAdapter(adapter);
    }

    private static final String[] PLANTS = new String[] {
            "Sunflower", "Rose", "Carnation", "Tulip", "Daffodil"
    };
}
