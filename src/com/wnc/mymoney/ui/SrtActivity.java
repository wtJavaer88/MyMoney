package com.wnc.mymoney.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.ui.helper.AfterWheelChooseListener;
import com.wnc.mymoney.ui.helper.HorGestureDetectorListener;
import com.wnc.mymoney.ui.helper.MyHorizontalGestureDetector;
import com.wnc.mymoney.ui.helper.PlayCompleteEvent;
import com.wnc.mymoney.ui.helper.Setting;
import com.wnc.mymoney.ui.helper.SrtVoiceHelper;
import com.wnc.mymoney.ui.helper.WheelDialogShowUtil;
import com.wnc.mymoney.util.TextFormatUtil;
import com.wnc.mymoney.util.ToastUtil;
import com.wnc.srt.DataHolder;
import com.wnc.srt.Picker;
import com.wnc.srt.PickerFactory;
import com.wnc.srt.SrtInfo;
import com.wnc.srt.SrtTextHelper;
import com.wnc.srt.TimeHelper;

public class SrtActivity extends Activity implements OnClickListener,
        HorGestureDetectorListener
{
    TextView chsTv;
    TextView engTv;
    TextView timelineTv;
    final String srtFolder = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/srt/";
    final int DELTA_UNIQUE = 1000;
    Map<Integer, String> srtFilePathes = new HashMap<Integer, String>();
    private GestureDetector gestureDetector;
    int[] defaultTimePoint =
    { 0, 0, 0 };
    int[] defaultMoviePoint =
    { 0, 0 };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_srt);
        initView();

        this.gestureDetector = new GestureDetector(this,
                new MyHorizontalGestureDetector(0.2, this));
    }

    private void initView()
    {
        btnPlay = (Button) findViewById(R.id.btnPlay);
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
                String name = TextFormatUtil.getFileNameNoExtend(f.getName());
                ((TextView) findViewById(R.id.file_tv)).setText(folder + " / "
                        + name);
            }
        }
    }

    final int CURRENT = 0;
    final int LEFT = 1;
    final int RIGHT = 2;

    private void parseSrt(String srtFile)
    {
        Date date1 = new Date();
        if (BasicFileUtil.isExistFile(srtFile))
        {
            if (!DataHolder.map.containsKey(srtFile))
            {
                Picker picker = PickerFactory.getPicker(srtFile);
                System.out.println("pickerTime:"
                        + (new Date().getTime() - date1.getTime()));
                List<SrtInfo> srtInfos = picker.getSrtInfos();
                DataHolder.appendData(srtFile, srtInfos);
                System.out.println("parseTime:"
                        + (new Date().getTime() - date1.getTime()));
                getSrtAndSetContent(RIGHT);
                System.out.println("overTime:"
                        + (new Date().getTime() - date1.getTime()));
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
            showChooseMovieWheel();
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

    public void playVoice(final View v)
    {
        if (btnPlay.getText().equals("停止"))
        {
            btnPlay.setText("播放");
            playBytype(1);
        }
        else if (btnPlay.getText().equals("播放"))
        {
            playBytype(0);
            btnPlay.setText("停止");
        }

    }

    Button btnPlay;
    boolean loop = true;

    /**
     * type等于1时停止,等于0时表示翻页继续播放
     * 
     * @param type
     */
    public void playBytype(final int type)
    {
        // System.out.println("type " + type);
        String voicePath = SrtTextHelper.getSrtVoiceLocation(
                DataHolder.getFileKey(), DataHolder.getCurrent());
        if (voicePath != null && BasicFileUtil.isExistFile(voicePath))
        {
            try
            {
                SrtVoiceHelper.play(voicePath, new PlayCompleteEvent()
                {
                    @Override
                    public void onComplete()
                    {
                        if (isLoopModel())
                        {
                            doRight();
                        }
                    }
                }, type);
            }
            catch (Exception e)
            {
                loop = false;
                btnPlay.setText("播放");
                e.printStackTrace();
                ToastUtil.showShortToast(this, "播放出现异常" + e.getMessage());
            }
        }
        else
        {
            if (t != null)
            {
                t.interrupt();
            }
            // btnPlay.setText("播放");
            Log.e("srt", "音频文件[" + voicePath + "]不存在!");
            if (isLoopModel())
            {
                t = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        long time = TimeHelper.getTime(DataHolder.getCurrent()
                                .getToTime())
                                - TimeHelper.getTime(DataHolder.getCurrent()
                                        .getFromTime());
                        try
                        {
                            Thread.sleep(time);
                            Message msg = new Message();
                            msg.what = type;
                            handler.sendMessage(msg);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }

                    }
                });
                t.start();
            }
        }
    }

    Thread t;
    public Handler handler = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {
            if (msg.what == 0)
            {
                doRight();
            }
        };
    };

    private boolean isLoopModel()
    {
        return loop && Boolean.valueOf(Setting.getAutoPlayVoice());
    }

    private void showSkipWheel()
    {
        try
        {
            WheelDialogShowUtil.showTimeDialog(this, defaultTimePoint,
                    new AfterWheelChooseListener()
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
                                setContentAndPlay(DataHolder.getClosestSrt(h,
                                        m, s));
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                ToastUtil.showShortToast(SrtActivity.this,
                                        e.getMessage());
                            }
                        }

                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showChooseMovieWheel()
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
                int length = 0;
                for (File f2 : listFiles)
                {
                    if (f2.isFile()
                            && (f2.getName().endsWith("ass") || f2.getName()
                                    .endsWith("srt")))
                    {
                        length++;
                    }
                }
                String[] arr = new String[length];
                int j = 0;
                for (File f2 : listFiles)
                {
                    if (f2.isFile()
                            && (f2.getName().endsWith("ass") || f2.getName()
                                    .endsWith("srt")))
                    {
                        srtFilePathes.put(DELTA_UNIQUE * i + j,
                                f2.getAbsolutePath());
                        // 文件名最大只取8位
                        arr[j++] = BasicStringUtil.subString(TextFormatUtil
                                .getFileNameNoExtend(f2.getName()), 0, 8);
                    }
                }
                rightArr[i] = arr;
                i++;
            }
            WheelDialogShowUtil.showSelectDialog(this, "选择剧集", leftArr,
                    rightArr, defaultMoviePoint[0], defaultMoviePoint[1],
                    new AfterWheelChooseListener()
                    {
                        @Override
                        public void afterWheelChoose(Object... objs)
                        {
                            defaultMoviePoint[0] = Integer.valueOf(objs[0]
                                    .toString());
                            defaultMoviePoint[1] = Integer.valueOf(objs[1]
                                    .toString());
                            String srtFilePath = srtFilePathes.get(DELTA_UNIQUE
                                    * defaultMoviePoint[0]
                                    + defaultMoviePoint[1]);
                            initFileTv(srtFilePath);
                            parseSrt(srtFilePath);
                        }

                    });
        }
        catch (Exception e)
        {
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
                setContentAndPlay(srt);
            }
        }
        catch (Exception ex)
        {
            ToastUtil.showShortToast(this, "错误:" + ex.getMessage());
        }
    }

    private void setContentAndPlay(SrtInfo srt)
    {
        chsTv.setText(srt.getChs());
        engTv.setText(srt.getEng());
        timelineTv.setText(srt.getFromTime().toString() + " ---> "
                + srt.getToTime().toString());

        btnPlay.setText("停止");
        loop = true;
        playBytype(0);
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
