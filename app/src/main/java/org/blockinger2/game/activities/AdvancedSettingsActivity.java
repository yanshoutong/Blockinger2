package org.blockinger2.game.activities;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import org.blockinger2.game.R;

public class AdvancedSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.advanced_preferences);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Preference pref = findPreference("pref_rng");
        if (PreferenceManager.getDefaultSharedPreferences(this).getString("pref_rng", "").equals("sevenbag")) {
            pref.setSummary(getResources().getStringArray(R.array.randomizer_preference_array)[0]);//"7-Bag-Randomization");
        } else {
            pref.setSummary(getResources().getStringArray(R.array.randomizer_preference_array)[1]);
        }

        pref = findPreference("pref_fpslimittext");
        pref.setSummary(PreferenceManager.getDefaultSharedPreferences(this).getString("pref_fpslimittext", ""));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (key.equals("pref_rng")) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            if (sharedPreferences.getString(key, "").equals("sevenbag")) {
                connectionPref.setSummary(getResources().getStringArray(R.array.randomizer_preference_array)[0]);//"7-Bag-Randomization");
            } else {
                connectionPref.setSummary(getResources().getStringArray(R.array.randomizer_preference_array)[1]);
            }
        }
        if (key.equals("pref_fpslimittext")) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        finish();
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
