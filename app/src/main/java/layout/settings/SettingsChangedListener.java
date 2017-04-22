package layout.settings;

import android.content.Context;

/**
 * Created by Angelo W on 20.04.2017.
 */

public interface SettingsChangedListener {

    void onSettingsChanged(String keyName, Context context);
}
