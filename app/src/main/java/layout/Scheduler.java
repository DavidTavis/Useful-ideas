package layout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import layout.utils.TraceUtils;

/**
 * Created by TechnoA on 08.04.2017.
 */

public class Scheduler {

    public static void scheduleUpdate(Context context,String interval) {

        TraceUtils.LogInfo("Scheduler scheduleUpdate interval = " + interval);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long intervalMillis = Integer.parseInt(interval)*60*1000;

        PendingIntent pi = getAlarmIntent(context);
        am.cancel(pi);
        am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), intervalMillis, pi);
    }

    private static PendingIntent getAlarmIntent(Context context) {

        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction(NewAppWidget.UPDATE_ALL_WIDGETS);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }

    public static void clearUpdate(Context context) {

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getAlarmIntent(context));
    }

}
