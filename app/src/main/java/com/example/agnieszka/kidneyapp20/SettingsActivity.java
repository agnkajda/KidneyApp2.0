package com.example.agnieszka.kidneyapp20;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.agnieszka.kidneyapp20.data.KidneyContract;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    Button deleteAll;
    Context context;

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);
        //setContentView(R.layout.settings_layout);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
            // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_weight_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_height_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_phosphorus_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_potassium_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sodium_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_fluid_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_max_fluid_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_dialysis_key)));


        /*ListView v = getListView();
        v.addFooterView(new Button(this));
        deleteAll = (Button) setContentView.findViewById(R.id.delete_all_button);
        View.OnClickListener clickingToDelete = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = getApplicationContext();
                Uri uri = KidneyContract.ValuesEntry.CONTENT_URI;
                int rowsDeleted;
                rowsDeleted = context.getContentResolver().delete(uri, null, null);
                uri = KidneyContract.JournalEntry.CONTENT_URI;
                rowsDeleted = context.getContentResolver().delete(uri, null, null);
                Intent intent = new Intent (context, MainActivity.class);
                startActivity(intent);
            }

        };
        deleteAll.setOnClickListener(clickingToDelete);*/
        Preference button = findPreference(getString(R.string.myCoolButton));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                context = preference.getContext();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(preference.getContext());
                builder1.setMessage("Do you really want to erase the history?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                context = getApplicationContext();
                                Uri uri = KidneyContract.ValuesEntry.CONTENT_URI;
                                int rowsDeleted;
                                rowsDeleted = context.getContentResolver().delete(uri, null, null);
                                uri = KidneyContract.JournalEntry.CONTENT_URI;
                                rowsDeleted = context.getContentResolver().delete(uri, null, null);
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("fluid", "0");
                                editor.commit();
                                Intent intent = new Intent (context, MainActivity.class);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                        }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                return true;
            }
        });
    }


    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
}

