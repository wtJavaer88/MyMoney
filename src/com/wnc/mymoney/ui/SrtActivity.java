package com.wnc.mymoney.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.ui.helper.AfterWheelChooseListener;
import com.wnc.mymoney.ui.helper.HorGestureDetectorListener;
import com.wnc.mymoney.ui.helper.MyHorizontalGestureDetector;
import com.wnc.mymoney.ui.helper.SrtVoiceHelper;
import com.wnc.mymoney.ui.helper.WheelDialogShowUtil;
import com.wnc.mymoney.util.TextFormatUtil;
import com.wnc.mymoney.util.ToastUtil;
import com.wnc.srt.DataHolder;
import com.wnc.srt.Picker;
import com.wnc.srt.PickerFactory;
import com.wnc.srt.SrtInfo;
import com.wnc.srt.SrtTextHelper;

public class SrtActivity extends Activity implements OnClickListener, HorGestureDetectorListener
{
	TextView chsTv;
	TextView engTv;
	TextView timelineTv;
	final String srtFolder = Environment.getExternalStorageDirectory().getPath() + "/wnc/app/srt/";
	final int DELTA_UNIQUE = 1000;
	Map<Integer, String> srtFilePathes = new HashMap<Integer, String>();
	private GestureDetector gestureDetector;
	int[] defaultTimePoint = { 0, 0, 0 };
	int[] defaultMoviePoint = { 0, 0 };

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_srt);
		initView();

		this.gestureDetector = new GestureDetector(this, new MyHorizontalGestureDetector(0.2, this));
	}

	private void initView()
	{
		chsTv = (TextView) findViewById(R.id.chs_tv);
		engTv = (TextView) findViewById(R.id.eng_tv);
		timelineTv = (TextView) findViewById(R.id.timeline_tv);
		if (BasicStringUtil.isNotNullString(DataHolder.getFileKey()))
		{
			initFileTv(DataHolder.getFileKey());
			getSrtAndSetContent(CURRENT);
		}

		chsTv.setOnClickListener(this);
		engTv.setOnClickListener(this);
		findViewById(R.id.btnFirst).setOnClickListener(this);
		findViewById(R.id.btnLast).setOnClickListener(this);
		// findViewById(R.id.btnPre).setOnClickListener(this);
		// findViewById(R.id.btnNext).setOnClickListener(this);
		findViewById(R.id.btnSkip).setOnClickListener(this);
		findViewById(R.id.btnChoose).setOnClickListener(this);
		findViewById(R.id.btnSkip).setOnClickListener(this);

	}

	private void initFileTv(String srtFilePath)
	{
		if (BasicFileUtil.isExistFile(srtFilePath))
		{
			File f = new File(srtFilePath);
			String folder = f.getParent();
			int i = folder.lastIndexOf("/");
			if (i != -1)
			{
				folder = folder.substring(i + 1);
				((TextView) findViewById(R.id.file_tv)).setText(folder + " / " + f.getName());
			}
		}
	}

	final int CURRENT = 0;
	final int LEFT = 1;
	final int RIGHT = 2;

	private void parseSrt(String srtFile)
	{
		if (BasicFileUtil.isExistFile(srtFile))
		{
			if (!DataHolder.map.containsKey(srtFile))
			{
				Picker picker = PickerFactory.getPicker(srtFile);
				List<SrtInfo> srtInfos = picker.getSrtInfos();
				DataHolder.appendData(srtFile, srtInfos);
				getSrtAndSetContent(RIGHT);
			}
			else
			{
				DataHolder.switchFile(srtFile);
				getSrtAndSetContent(CURRENT);
			}
		}
		else
		{
			Log.e("srt", "not found " + srtFile);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btnChoose:
			showChooseWheel();
			break;
		case R.id.btnSkip:
			showSkipWheel();
			break;
		case R.id.btnFirst:
			getSrtAndSetContent(R.id.btnFirst);
			break;
		case R.id.btnLast:
			getSrtAndSetContent(R.id.btnLast);
			break;
		}
	}

	public void playVoice(View v)
	{
		String voicePath = SrtTextHelper.getSrtVoiceLocation(DataHolder.getFileKey(), DataHolder.getCurrent());
		if (voicePath != null && BasicFileUtil.isExistFile(voicePath))
		{
			try
			{
				if (!SrtVoiceHelper.isPlaying())
				{
					SrtVoiceHelper.playAndHideBt(voicePath, v);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				ToastUtil.showShortToast(this, "出现异常" + e.getMessage());
			}
		}
		else
		{
			System.out.println(voicePath);
			ToastUtil.showShortToast(this, "音频文件不存在!");
		}

	}

	private void showSkipWheel()
	{
		try
		{
			WheelDialogShowUtil.showTimeDialog(this, defaultTimePoint, new AfterWheelChooseListener()
			{
				@Override
				public void afterWheelChoose(Object... objs)
				{
					try
					{
						int h = Integer.parseInt(objs[0].toString());
						int m = Integer.parseInt(objs[1].toString());
						int s = Integer.parseInt(objs[2].toString());
						defaultTimePoint[0] = h;
						defaultTimePoint[1] = m;
						defaultTimePoint[2] = s;
						setContent(DataHolder.getClosestSrt(h, m, s));
					}
					catch (Exception e)
					{
						e.printStackTrace();
						ToastUtil.showShortToast(SrtActivity.this, e.getMessage());
					}
				}

			});
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private void showChooseWheel()
	{
		File srtFolderFile = new File(srtFolder);
		try
		{

			final List<File> tvFolderFiles = new ArrayList<File>();
			for (File f : srtFolderFile.listFiles())
			{
				if (f.isDirectory())
				{
					tvFolderFiles.add(f);
				}
			}
			int tvs = tvFolderFiles.size();
			final String[] leftArr = new String[tvs];
			final String[][] rightArr = new String[tvs][];
			int i = 0;
			for (File folder : tvFolderFiles)
			{
				// 文件夹最大只取10位
				leftArr[i] = BasicStringUtil.subString(folder.getName(), 0, 10);
				File[] listFiles = folder.listFiles();
				String[] arr = new String[listFiles.length];
				int j = 0;
				for (File f2 : listFiles)
				{
					srtFilePathes.put(DELTA_UNIQUE * i + j, f2.getAbsolutePath());
					// 文件名最大只取8位
					arr[j++] = BasicStringUtil.subString(TextFormatUtil.getFileNameNoExtend(f2.getName()), 0, 8);
				}
				rightArr[i] = arr;
				i++;
			}
			WheelDialogShowUtil.showSelectDialog(this, "选择剧集", leftArr, rightArr, defaultMoviePoint[0], defaultMoviePoint[1], new AfterWheelChooseListener()
			{
				@Override
				public void afterWheelChoose(Object... objs)
				{
					defaultMoviePoint[0] = Integer.valueOf(objs[0].toString());
					defaultMoviePoint[1] = Integer.valueOf(objs[1].toString());
					String srtFilePath = srtFilePathes.get(DELTA_UNIQUE * defaultMoviePoint[0] + defaultMoviePoint[1]);
					initFileTv(srtFilePath);
					parseSrt(srtFilePath);
				}

			});
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private void getSrtAndSetContent(int btId)
	{
		try
		{
			SrtInfo srt = null;
			switch (btId)
			{
			case R.id.btnFirst:
				srt = DataHolder.getFirst();
				break;
			case R.id.btnLast:
				srt = DataHolder.getLast();
				break;
			case LEFT:
				srt = DataHolder.getPre();
				break;
			case RIGHT:
				srt = DataHolder.getNext();
				break;
			case CURRENT:
				srt = DataHolder.getCurrent();
				break;
			}
			if (srt != null)
			{
				setContent(srt);
			}
		}
		catch (Exception ex)
		{
			ToastUtil.showShortToast(this, "错误:" + ex.getMessage());
		}
	}

	private void setContent(SrtInfo srt)
	{
		chsTv.setText(srt.getChs());
		engTv.setText(srt.getEng());
		timelineTv.setText(srt.getFromTime().toString() + " ---> " + srt.getToTime().toString());
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
	{
		if (!this.gestureDetector.onTouchEvent(paramMotionEvent))
		{
			return super.dispatchTouchEvent(paramMotionEvent);
		}
		return true;
	}

	@Override
	public void doLeft()
	{
		getSrtAndSetContent(LEFT);
	}

	@Override
	public void doRight()
	{
		getSrtAndSetContent(RIGHT);
	}
}
