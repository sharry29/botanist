package edu.upenn.cis350.botanist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

/**
* Created by Ben on 3/29/2017.
*/

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private File[] pictures;

    public ImageAdapter(Context c, File[] pictures) {
        mContext = c;
        this.pictures = pictures;
    }

    public int getCount() {
        return pictures.length;
    }

    public File getItem(int position) {
        return pictures[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        Bitmap flowerPicture = BitmapFactory.decodeFile(pictures[position].getAbsolutePath());
        imageView.setImageBitmap(flowerPicture);
        return imageView;
    }

    // references to our images

}

