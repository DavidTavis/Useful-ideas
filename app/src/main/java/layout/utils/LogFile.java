package layout.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by TechnoA on 04.05.2017.
 */

public class LogFile {

    private File file;

    public LogFile(Context context) {

        if(isExternalStorageWritable()){
            file = new File(getStorageDir(context, "LogWidget"), "LogWidget.txt");
        }

    }

    public void appendLog(String text){

        try
        {
            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
            buf.append(text);
            buf.newLine();
            buf.close();
            TraceUtils.LogInfo("WRITE LOG TO FILE " + text);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private File getStorageDir(Context context, String dir) {
        File file = new File(context.getExternalFilesDir(null), dir);
        TraceUtils.LogInfo(file.getAbsolutePath());
        return file;
    }

}
