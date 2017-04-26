package edu.upenn.cis350.botanist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
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

        //plantImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        ViewGroup.LayoutParams params = plantImage.getLayoutParams();

        plantAnimation = new AnimationDrawable();

        TreeMap<Date, File> orderedImages = new TreeMap<>();
        for (File img : images) {
            if (img != null) {
                String thisDateString =
                        img.getName().substring(plant.length(), plant.length() + 15);
                try {
                    Date thisDate = format.parse(thisDateString);
                    orderedImages.put(thisDate, img);
                } catch (ParseException e) {
                    System.err.println("Failed to generate GIF.");
                }
            }
        }
        numPhotos = orderedImages.size();
        while (!orderedImages.isEmpty()) {
            Map.Entry<Date, File> frameFile = orderedImages.pollFirstEntry();
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;
            Bitmap rotated = BitmapFactory.decodeFile(frameFile.getValue().toString(), options);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap fixed = Bitmap.createBitmap(rotated, 0, 0,
                    rotated.getWidth(), rotated.getHeight(), matrix, true);
            BitmapDrawable frame = new BitmapDrawable(this.getResources(), fixed);
            //Drawable frame = Drawable.createFromPath(frameFile.getValue().toString());
            //plantImage.setBackground(frame);

            plantAnimation.addFrame(frame, 4000 / numPhotos);
        }

        setContentView(plantImage);
        plantImage.setBackground(plantAnimation);

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