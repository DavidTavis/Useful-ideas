package layout.PavelSh;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

import layout.GlobalClass;

/**
 * Created by Angelo W on 19.04.2017.
 */

public class Utils {

    public static GlobalClass getGlobal(Context context) {

        return (GlobalClass)context;
    }

    public static void playSound(Context context, Uri alert) {

        MediaPlayer mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

}
