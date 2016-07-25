package com.wnc.mymoney.ui.helper;

import java.io.File;
import java.io.FileInputStream;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class SrtVoiceHelper
{
    static MediaPlayer player;
    static boolean isPlaying = false;

    public static void play(String voicePath,
            final PlayCompleteEvent playCompleteEvent, int type)
            throws Exception
    {
        if (player != null && player.isPlaying())
        {
            player.stop();
            isPlaying = false;
            if (type == 1)
            {
                return;
            }
        }

        if (!isPlaying)
        {
            File file = new File(voicePath);
            FileInputStream fis = new FileInputStream(file);
            player = new MediaPlayer();
            player.setDataSource(fis.getFD());
            player.prepare();
            player.setOnCompletionListener(new OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    isPlaying = false;
                    player.reset();
                    player.release();
                    player = null;
                    playCompleteEvent.onComplete();
                }
            });
            player.start();
            isPlaying = true;
        }
    }
}
