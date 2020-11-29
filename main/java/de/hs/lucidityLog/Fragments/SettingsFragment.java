package de.hs.lucidityLog.Fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import de.hs.lucidityLog.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
