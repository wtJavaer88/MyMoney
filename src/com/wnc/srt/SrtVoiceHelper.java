package com.wnc.srt;

import java.io.File;
import java.io.FileInputStream;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class SrtVoiceHelper
{
	static MediaPlayer player;
	static boolean isPlaying = false;

	public static void play(String voicePath, final PlayCompleteEvent playCompleteEvent, int type) throws Exception
	{
		try
		{
			if (player != null)
			{
				player.reset();
				player.release();
				player = null;
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
				player.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
				player.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
				{
					@Override
					public void onPrepared(MediaPlayer mediaPlayer)
					{
						mediaPlayer.start();
						isPlaying = true;
					}
				});
			}
		}
		catch (Exception e)
		{
			player = null;
			isPlaying = false;
			throw new RuntimeException(e);
		}
	}
}
