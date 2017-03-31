package edu.upenn.cis350.botanist;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Harry on 3/23/17.
 */

public class GifActivity extends AppCompatActivity{
    AnimationDrawable plantAnimation;
    int numPhotos;
    boolean gifRunning;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String plant = getIntent().getStringExtra("Plant");
        File[] images = (File[]) getIntent().getSerializableExtra("images");

        ImageView plantImage = new ImageView(this);
        plantAnimation = new AnimationDrawable();

        TreeMap<Date, File> orderedImages = new TreeMap<>();
        for (File img : images) {
            String thisDateString =
                    img.getName().substring(plant.length(), plant.length() + 15);
            try {
                Date thisDate = format.parse(thisDateString);
                orderedImages.put(thisDate, img);
            } catch (ParseException e) {
                System.err.println("Failed to generate GIF.");
            }
        }
        numPhotos = orderedImages.size();
        while (!orderedImages.isEmpty()) {
            Map.Entry<Date, File> frameFile = orderedImages.pollFirstEntry();
            Drawable frame = Drawable.createFromPath(frameFile.getValue().toString());
            plantImage.setBackground(frame);
            plantAnimation.addFrame(frame, 4000 / numPhotos);
        }
        plantImage.setBackground(plantAnimation);
        setContentView(plantImage);

        Toast t = Toast.makeText(getApplicationContext(),
                "Tap to start!",
                Toast.LENGTH_SHORT);
        t.show();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!gifRunning) {
                plantAnimation.start();
                gifRunning = !gifRunning;
                return true;
            }
            else {
                plantAnimation.stop();
                gifRunning = !gifRunning;
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

}