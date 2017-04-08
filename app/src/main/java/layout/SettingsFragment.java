package layout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.example.david.mywidgetnewattempt.R;

/**
 * Created by TechnoA on 01.04.2017.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String LOG_TAG = "MyLogWidget";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ListPreference listPreference = (ListPreference) findPreference("listPref");
        String defaultValue = listPreference.getValue();

        if(defaultValue==null){
            listPreference.setValueIndex(0);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (key.equals("listPref"))
        {
            Log.d(LOG_TAG,"SettingsFragment listPref listener");
            Util.scheduleUpdate(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        Log.d(LOG_TAG,"SettingsFragment onResume");

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
        Log.d(LOG_TAG,"SettingsFragment onPause");
    }
}
