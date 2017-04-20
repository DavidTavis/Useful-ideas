package layout;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.example.david.mywidgetnewattempt.R;

import layout.PavelSh.TraceUtils;
import layout.PavelSh.Utils;

/**
 * Created by TechnoA on 01.04.2017.
 */

public class SettingsFragment extends PreferenceFragment {

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
    public void onResume() {

        TraceUtils.LogInfo("SettingsFragment onResume");
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(Utils.getGlobal(getContext()).getSettings());
    }

    @Override
    public void onPause() {
        TraceUtils.LogInfo("SettingsFragment onPause");
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(Utils.getGlobal(getContext()).getSettings());
        super.onPause();
    }
}
