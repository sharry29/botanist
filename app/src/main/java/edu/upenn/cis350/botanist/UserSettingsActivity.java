package edu.upenn.cis350.botanist;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class UserSettingsActivity extends AppCompatActivity {
    public static final String USER_SETTINGS_PREFS_NAME = "UserSettingsPrefs";

    SharedPreferences userSettings;
    private Spinner spinner;
    private EditText firstNameET;
    private EditText lastNameET;
    private EditText cityET;
    private EditText stateET;
    private EditText countryET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        userSettings = getSharedPreferences(USER_SETTINGS_PREFS_NAME, Context.MODE_PRIVATE);

        // Sets up the spinner with notification frequency preferences
        spinner = (Spinner) findViewById(R.id.notification_frequency_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notification_frequency_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Gets all of the Edit Text views
        firstNameET = (EditText) findViewById(R.id.first_name_edit_text);
        lastNameET = (EditText) findViewById(R.id.last_name_edit_text);
        cityET = (EditText) findViewById(R.id.city_edit_text);
        stateET = (EditText) findViewById(R.id.state_edit_text);
        countryET = (EditText) findViewById(R.id.country_edit_text);

        // Presets the values of the Edit Text views with data from shared preferences if they exist
        firstNameET.setText(userSettings.getString("firstName", ""));
        lastNameET.setText(userSettings.getString("lastName", ""));
        cityET.setText(userSettings.getString("city", ""));
        stateET.setText(userSettings.getString("state", ""));
        countryET.setText(userSettings.getString("country", ""));

        // Presets the notification frequency of the spinner
        String freqPref = userSettings.getString("notificationFrequency", null);
        if (freqPref != null && (freqPref.equals("Hourly") || freqPref.equals("Daily") ||
                freqPref.equals("Never"))) {
            spinner.setSelection(adapter.getPosition(freqPref));
        }
    }


    // Adds the values from the Edit Text views to the user settings shared preferences
    public void onButtonPress(View view) {
        SharedPreferences.Editor editor = userSettings.edit();

        editor.putString("firstName", firstNameET.getText().toString());
        editor.putString("lastName", lastNameET.getText().toString());
        editor.putString("city", cityET.getText().toString());
        editor.putString("state", stateET.getText().toString());
        editor.putString("country", countryET.getText().toString());
        editor.putString("notificationFrequency", spinner.getSelectedItem().toString());
        editor.apply();

        editor.putBoolean("notificationsScheduled", scheduleNotifications());
        editor.putBoolean("settingsSaved", true);

        Toast.makeText(getApplication(), "User Settings saved successfully.",
                Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean scheduleNotifications() {
        // Sets a repeating notification unless the user specified not to
        String notificationFrequency = spinner.getSelectedItem().toString();
        if (notificationFrequency.equals("Never")) return false;

        NotificationPublisher.setRepeatingAlarm(this);

        return true;
    }

}
