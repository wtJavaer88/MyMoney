package com.wnc.mymoney.ui.helper;

import java.io.File;
import java.io.FileInputStream;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.View;

public class SrtVoiceHelper
{
    static MediaPlayer player = new MediaPlayer();
    static boolean isPlaying = false;

    public static void playAndHideBt(String voicePath, final View v)
            throws Exception
    {
        if (!isPlaying)
        {
            File file = new File(voicePath);
            FileInputStream fis = new FileInputStream(file);
            player.setDataSource(fis.getFD());
            player.prepare();
            player.setOnCompletionListener(new OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    isPlaying = false;
                    mp.reset();
                    if (v != null)
                    {
                        v.setVisibility(View.VISIBLE);
                    }
                }
            });
            player.start();
            isPlaying = true;
            if (v != null)
            {
                v.setVisibility(View.INVISIBLE);
            }
        }
    }

    // 提供给外部使用, 在播放前先判断一下是否可以
    public static boolean isPlaying()
    {
        return isPlaying;
    }
}
