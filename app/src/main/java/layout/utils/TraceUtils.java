package layout.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Angelo W on 17.04.2017.
 */

public class TraceUtils {

    private static final String LOG_TAG = "MyLogWidget";

    public static void logInfo(String text) {

        Log.d(LOG_TAG, text);
//        appendLogToFile(text);

    }

    public static void logError(String text) {

        Log.e(LOG_TAG, text);

    }

    public static void toast(Context context, String info){
//        toast.makeText(context, info, toast.LENGTH_SHORT).show();
    }

    private static void appendLogToFile(String text){

        if(!isExternalStorageWritable()){
            return;
        }

        File logFile = new File("sdcard/log.file");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }





}
