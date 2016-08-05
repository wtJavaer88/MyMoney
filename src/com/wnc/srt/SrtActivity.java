package com.wnc.srt;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import srt.DataHolder;
import srt.Picker;
import srt.PickerFactory;
import srt.SrtInfo;
import srt.SrtTextHelper;
import srt.TimeHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.ui.richtext.ClickFileIntentFactory;
import com.wnc.mymoney.uihelper.AfterWheelChooseListener;
import com.wnc.mymoney.uihelper.HorGestureDetectorListener;
import com.wnc.mymoney.uihelper.MyHorizontalGestureDetector;
import com.wnc.mymoney.uihelper.Setting;
import com.wnc.mymoney.uihelper.WheelDialogShowUtil;
import com.wnc.mymoney.util.ClipBoardUtil;
import com.wnc.mymoney.util.FileSortUtil;
import com.wnc.mymoney.util.TextFormatUtil;
import com.wnc.mymoney.util.ToastUtil;
import com.wnc.srt.HeadSetUtil.OnHeadSetListener;

public class SrtActivity extends Activity implements OnClickListener,
        OnLongClickListener, HorGestureDetectorListener,
        UncaughtExceptionHandler
{
    TextView movieTv;
    TextView chsTv;
    TextView engTv;
    TextView timelineTv;
    final String srtFolder = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/srt/";
    final String thumbPicFolder = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/图片/";
    final String favoriteTxt = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/srt/favorite.txt";
    final int DELTA_UNIQUE = 1000;// 文件夹和所属文件的Map的Key规则
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
        // 设置横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 引入线控监听
        HeadSetUtil.getInstance().setOnHeadSetListener(headSetListener);
        HeadSetUtil.getInstance().open(this);
        // 设置未捕获异常的处理方法
        Thread.setDefaultUncaughtExceptionHandler(this);

        initView();
        initAlertDialog();
        this.gestureDetector = new GestureDetector(this,
                new MyHorizontalGestureDetector(0.2, this));

    }

    Builder alertDialogBuilder;

    private void initAlertDialog()
    {
        final String[] menuItems = new String[]
        { "复制英文", "复制中英文", "收藏", "分享" };
        alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("对字幕进行操作")
                .setItems(menuItems, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            switch (which)
                            {
                            case 0:
                                ClipBoardUtil.setNormalContent(
                                        SrtActivity.this, engTv.getText()
                                                .toString());
                                ToastUtil.showLongToast(
                                        getApplicationContext(), "复制成功!");
                                break;
                            case 1:
                                ClipBoardUtil.setNormalContent(
                                        SrtActivity.this, getEngChs());
                                ToastUtil.showLongToast(
                                        getApplicationContext(), "复制成功!");
                                break;
                            case 2:
                                favoriteSrt();
                                break;
                            case 3:
                                favoriteSrt();
                                shareSrt();
                                break;
                            default:
                                break;
                            }
                        }
                        catch (Exception e)
                        {
                            ToastUtil.showLongToast(getApplicationContext(),
                                    "操作失败!");
                            e.printStackTrace();
                        }
                    }

                    private void favoriteSrt()
                    {
                        if (BasicFileUtil.writeFileString(favoriteTxt,
                                getFavoriteContent(), "UTF-8", true))
                        {
                            ToastUtil.showLongToast(getApplicationContext(),
                                    "收藏成功!");
                        }
                        else
                        {
                            ToastUtil.showLongToast(getApplicationContext(),
                                    "收藏失败!");
                        }
                    }

                    private String getEngChs()
                    {
                        return chsTv.getText().toString() + "<>"
                                + engTv.getText().toString();
                    }

                    private void shareSrt()
                    {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Share Srt");
                        intent.putExtra(Intent.EXTRA_TEXT, getEngChs());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent, getTitle()));
                    }

                    private String getFavoriteContent()
                    {

                        return BasicDateUtil.getCurrentDateTimeString() + " \""
                                + getCurFile().replace(srtFolder, "") + "\" "
                                + DataHolder.getCurrent() + "\r\n";
                    }

                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
    }

    private String getCurFile()
    {
        return DataHolder.getFileKey();
    }

    private void initView()
    {

        btnPlay = (Button) findViewById(R.id.btnPlay);
        movieTv = (TextView) findViewById(R.id.file_tv);
        chsTv = (TextView) findViewById(R.id.chs_tv);
        engTv = (TextView) findViewById(R.id.eng_tv);
        timelineTv = (TextView) findViewById(R.id.timeline_tv);
        if (BasicStringUtil.isNotNullString(DataHolder.getFileKey()))
        {
            initFileTv(DataHolder.getFileKey());
            getSrtAndSetContent(VIEW_CURRENT);
        }
        engTv.setOnLongClickListener(this);

        movieTv.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        findViewById(R.id.btnFirst).setOnClickListener(this);
        findViewById(R.id.btnLast).setOnClickListener(this);
        findViewById(R.id.btnSkip).setOnClickListener(this);
        findViewById(R.id.btnChoose).setOnClickListener(this);
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

                movieTv.setText(folder + " / " + name);
            }
        }
    }

    final int VIEW_CURRENT = 0;
    final int VIEW_LEFT = 1;
    final int VIEW_RIGHT = 2;

    final int countsPerPage = 100;
    // String curFile = "";
    Picker picker;

    private void showNewSrt(String srtFile)
    {
        if (BasicFileUtil.isExistFile(srtFile))
        {
            stopAutoPlay();
            DataHolder.switchFile(srtFile);
            if (!DataHolder.map.containsKey(srtFile))
            {
                picker = PickerFactory.getPicker(srtFile);
                DataHolder.appendData(srtFile,
                        picker.getSrtInfos(0, countsPerPage));
                // 新进程去跑分页数据
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getDataByPage();
                    }
                }).start();
                getSrtAndSetContent(VIEW_RIGHT);
            }
            else
            {
                getSrtAndSetContent(VIEW_CURRENT);
            }
        }
        else
        {
            Log.e("srt", "not found " + srtFile);
        }
    }

    private void getDataByPage()
    {
        // 这儿的两个变量必须用局部的,防止切换字幕
        int curPage = 1;
        String curFile = getCurFile();
        System.out.println(curFile);
        while (DataHolder.completeMap.containsKey(curFile)
                && !DataHolder.completeMap.get(curFile))
        {
            DataHolder.appendData(
                    curFile,
                    picker.getSrtInfos(countsPerPage * curPage, countsPerPage
                            * (curPage + 1)));
            curPage++;
        }
        if (DataHolder.map.containsKey(curFile))
        {
            System.out.println("字幕结果数:" + DataHolder.map.get(curFile).size());
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
        case R.id.btnPlay:
            if (notEmptyStatu())
            {
                playVoice();
            }
            else
            {
                ToastUtil.showShortToast(getApplicationContext(), "没有选择任何剧集");
            }
            break;
        case R.id.file_tv:
            if (notEmptyStatu())
            {
                showThumbPic();
            }
            else
            {
                ToastUtil.showShortToast(getApplicationContext(), "没有选择任何剧集");
            }
            break;
        }
    }

    private boolean notEmptyStatu()
    {
        if (BasicFileUtil.isExistFile(getCurFile()))
        {
            return true;
        }
        return false;
    }

    /**
     * 显示该剧集图片
     */
    private void showThumbPic()
    {
        System.out.println(getCurFile().replace(srtFolder, ""));
        String filePath = thumbPicFolder + getCurFile().replace(srtFolder, "");
        int i = filePath.lastIndexOf(".");
        filePath = filePath.substring(0, i);
        filePath = filePath + File.separator
                + TextFormatUtil.getFileNameNoExtend(getCurFile()) + "_p1.jpg";
        System.out.println(filePath);
        System.out.println(BasicFileUtil.isExistFile(filePath));
        Intent intent = ClickFileIntentFactory.getIntentByFile(filePath);
        startActivity(intent);
    }

    public void playVoice()
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
    boolean replay = false;// 复读模式
    boolean autoplayCtrl = true;// 如果播放过程出异常,就不能单靠系统设置的值控制自动播放下一个了, 需要引入自己的变量

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
                        if (replay)
                        {
                            getSrtAndSetContent(VIEW_CURRENT);
                        }
                        else if (isAutoPlayModel())
                        {
                            doRight();
                        }
                        else
                        {
                            btnPlay.setText("播放");
                        }
                    }
                }, type);
            }
            catch (Exception e)
            {
                autoplayCtrl = false;
                btnPlay.setText("播放");
                e.printStackTrace();
                ToastUtil.showShortToast(this, "播放出现异常" + e.getMessage());
            }
        }
        else
        {
            stopAutoPlay();
            // btnPlay.setText("播放");
            // Log.e("srt", "音频文件[" + voicePath + "]不存在!");
            if (isAutoPlayModel())
            {
                autoPlayThread = new Thread(new Runnable()
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
                            autoPlayHandler.sendMessage(msg);
                        }
                        catch (InterruptedException e)
                        {
                            // e.printStackTrace();
                        }

                    }
                });
                autoPlayThread.start();
            }
        }
    }

    Thread autoPlayThread;
    public Handler autoPlayHandler = new Handler()
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

    private boolean isAutoPlayModel()
    {
        return autoplayCtrl && Boolean.valueOf(Setting.getAutoPlayVoice());
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

            for (File f : FileSortUtil.getSortFiles(srtFolderFile.listFiles()))
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
                List<File> fileList = FileSortUtil.getSortFiles(listFiles);
                int length = 0;
                for (File f2 : fileList)
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
                for (File f2 : fileList)
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
                            showNewSrt(srtFilePath);
                        }

                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 停止原来的自动播放
     */
    private void stopAutoPlay()
    {
        if (autoPlayThread != null)
        {
            autoPlayThread.interrupt();
        }
    }

    private void getSrtAndSetContent(int btId)
    {
        if (alertDialog != null)
        {
            alertDialog.hide();
        }
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
            case VIEW_LEFT:
                srt = DataHolder.getPre();
                break;
            case VIEW_RIGHT:
                srt = DataHolder.getNext();
                break;
            case VIEW_CURRENT:
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
            btnPlay.setText("播放");
            ToastUtil.showShortToast(this, "错误:" + ex.getMessage());
        }
    }

    private void setContentAndPlay(SrtInfo srt)
    {
        // 对于字幕里英文与中文颠倒的,用这种方法
        if (TextFormatUtil.containsChinese(srt.getEng()))
        {
            chsTv.setText(srt.getEng());
            engTv.setText(srt.getChs());
        }
        else
        {
            chsTv.setText(srt.getChs());
            engTv.setText(srt.getEng());
        }
        timelineTv.setText(srt.getFromTime().toString() + " ---> "
                + srt.getToTime().toString());

        defaultTimePoint[0] = srt.getFromTime().getHour();
        defaultTimePoint[1] = srt.getFromTime().getMinute();
        defaultTimePoint[2] = srt.getFromTime().getSecond();

        btnPlay.setText("停止");
        autoplayCtrl = true;
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
        getSrtAndSetContent(VIEW_LEFT);
    }

    @Override
    public void doRight()
    {
        // getDataByPage();
        getSrtAndSetContent(VIEW_RIGHT);
    }

    /**
     * 控制是否复读,只在自动播放模式下有用
     */
    private void switchReplayModel()
    {
        if (isAutoPlayModel())
        {
            this.replay = replay ? false : true;
        }
    }

    AlertDialog alertDialog;

    @Override
    public boolean onLongClick(View v)
    {
        alertDialog = alertDialogBuilder.show();
        return true;
    }

    @Override
    public void onDestroy()
    {
        autoplayCtrl = false;
        if (alertDialog != null)
        {
            System.out.println();
            alertDialog.dismiss();
        }
        super.onDestroy();
    }

    OnHeadSetListener headSetListener = new OnHeadSetListener()
    {
        @Override
        public void onDoubleClick()
        {
            ToastUtil.showShortToast(getApplicationContext(), "双击");
            switchReplayModel();
        }

        @Override
        public void onClick()
        {
            playVoice();
        }

        @Override
        public void onThreeClick()
        {
            ToastUtil.showShortToast(getApplicationContext(), "三击");
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
        case KeyEvent.KEYCODE_BACK:
            return super.onKeyDown(keyCode, event);
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            doRight();
            // System.out.println("down vol");
            return true;

        case KeyEvent.KEYCODE_VOLUME_UP:
            doLeft();
            // System.out.println("up vol");
            return true;
        case KeyEvent.KEYCODE_VOLUME_MUTE:
            System.out.println("mute vol");
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            return true;
        case KeyEvent.KEYCODE_VOLUME_UP:
            return true;
        case KeyEvent.KEYCODE_VOLUME_MUTE:
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        Log.i("AAA", "uncaughtException   " + ex);
        ex.getStackTrace();
        for (StackTraceElement o : ex.getStackTrace())
        {
            System.out.println(o.toString());
        }
    }
}
