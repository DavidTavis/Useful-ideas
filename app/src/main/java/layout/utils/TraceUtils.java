package layout.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Angelo W on 17.04.2017.
 */

public class TraceUtils {

    private static final String LOG_TAG = "MyLogWidget";

    public static void LogInfo(String text) {

        Log.d(LOG_TAG, text);
    }

    public static void LogError(String text) {

        Log.e(LOG_TAG, text);
    }

    public static void Toast(Context context, String info){
//        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }
}
