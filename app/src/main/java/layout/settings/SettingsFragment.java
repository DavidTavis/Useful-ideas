package layout.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.example.david.mywidgetnewattempt.R;

import layout.utils.TraceUtils;
import layout.utils.Utils;

/**
 * Created by TechnoA on 01.04.2017.
 */

public class SettingsFragment extends PreferenceFragment {
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = getActivity();
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ListPreference listPreference = (ListPreference) findPreference("interval");
        String defaultValue = listPreference.getValue();
        if(defaultValue==null){
            listPreference.setValueIndex(0);
        }
    }

    @Override
    public void onResume() {

        TraceUtils.LogInfo("SettingsFragment onResume");
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(Utils.getGlobal(context).getSettings());
    }


    @Override
    public void onPause() {
        TraceUtils.LogInfo("SettingsFragment onPause");
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(Utils.getGlobal(context).getSettings());
        super.onPause();
    }
}
