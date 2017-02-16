package edu.upenn.cis350.botanist;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check needed in recent APK levels even if perms. in manifest
        verifyReadWritePermission(this);
        setContentView(R.layout.activity_main);
    }

    public static void verifyReadWritePermission(Activity activity) {
        int permissionCode = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCode == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity,
                                              STORAGE_PERMISSIONS,
                                              REQUEST_EXTERNAL_STORAGE);
        }
    }

    public void addPlant(View view) {
        Intent intent = new Intent(this, AddPlantActivity.class);
        startActivity(intent);
    }
}
