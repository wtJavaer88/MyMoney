package com.wnc.srt;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import srt.DataHolder;
import srt.SrtInfo;
import srt.SrtTextHelper;
import srt.TimeHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
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
import com.wnc.mymoney.richtext.ClickFileIntentFactory;
import com.wnc.mymoney.uihelper.AfterWheelChooseListener;
import com.wnc.mymoney.uihelper.HorGestureDetectorListener;
import com.wnc.mymoney.uihelper.MyHorizontalGestureDetector;
import com.wnc.mymoney.util.app.ClipBoardUtil;
import com.wnc.mymoney.util.app.ToastUtil;
import com.wnc.mymoney.util.app.WheelDialogShowUtil;
import com.wnc.mymoney.util.common.MyFileUtil;
import com.wnc.mymoney.util.common.TextFormatUtil;
import com.wnc.srt.HeadSetUtil.OnHeadSetListener;

public class SrtActivity extends Activity implements OnClickListener,
        OnLongClickListener, HorGestureDetectorListener,
        UncaughtExceptionHandler
{
    private Button btnPlay;
    private TextView movieTv;
    private TextView chsTv;
    private TextView engTv;
    private TextView timelineTv;

    final String srtFolder = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/srt/";
    final String thumbPicFolder = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/图片/";
    final String favoriteTxt = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/srt/favorite.txt";
    final int DELTA_UNIQUE = 1000;// 文件夹和所属文件的Map的Key规则

    private Map<Integer, String> srtFilePathes = new HashMap<Integer, String>();
    private GestureDetector gestureDetector;
    AlertDialog alertDialog;

    int[] defaultTimePoint =
    { 0, 0, 0 };
    int[] defaultMoviePoint =
    { 0, -1 };// 初次使用请把右边序号设为-1,以便程序判断

    final int VIEW_CURRENT = 0;
    final int VIEW_LEFT = 1;
    final int VIEW_RIGHT = 2;

    static boolean replayCtrl = false;// 复读模式
    static boolean autoPlayNextCtrl = true;// 如果播放过程出异常,就不能单靠系统设置的值控制自动播放下一个了,
    static int beginReplayIndex = -1;
    static int endReplayIndex = -1;
    static Thread autoPlayThread;
    static String[] settingItems = new String[]
    { "自动下一条", "播放声音", "打开复读", "音量调节" };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_srt);
        // 设置横屏显示
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 引入线控监听
        HeadSetUtil.getInstance().setOnHeadSetListener(headSetListener);
        HeadSetUtil.getInstance().open(this);
        // 设置未捕获异常UncaughtExceptionHandler的处理方法
        Thread.setDefaultUncaughtExceptionHandler(this);

        initView();
        initAlertDialog();
        initSettingDialog();
        // 因为是横屏,所以设置的滑屏比例低一些
        this.gestureDetector = new GestureDetector(this,
                new MyHorizontalGestureDetector(0.1, this));

    }

    Builder alertDialogBuilder;
    Builder settingDialogBuilder;

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

    private void initSettingDialog()
    {
        settingDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("设置")
                .setItems(settingItems, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            switch (which)
                            {
                            case 0:
                                SrtSetting.setAutoPlayNext(SrtSetting
                                        .isAutoPlayNext() ? false : true);
                                break;
                            case 1:
                                SrtSetting.setPlayVoice(SrtSetting
                                        .isPlayVoice() ? false : true);
                                break;
                            case 2:
                                switchReplayModel();
                                break;
                            case 3:
                                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                mAudioManager.adjustStreamVolume(
                                        AudioManager.STREAM_MUSIC,
                                        AudioManager.ADJUST_RAISE,
                                        AudioManager.FX_FOCUS_NAVIGATION_UP);
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

    private int getCurIndex()
    {
        return DataHolder.getCurrentSrtIndex();
    }

    private void initView()
    {

        btnPlay = (Button) findViewById(R.id.btnPlay);
        movieTv = (TextView) findViewById(R.id.file_tv);
        chsTv = (TextView) findViewById(R.id.chs_tv);
        engTv = (TextView) findViewById(R.id.eng_tv);
        timelineTv = (TextView) findViewById(R.id.timeline_tv);
        engTv.setOnLongClickListener(this);
        timelineTv.setOnClickListener(this);
        movieTv.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        findViewById(R.id.btnFirst).setOnClickListener(this);
        findViewById(R.id.btnLast).setOnClickListener(this);
        findViewById(R.id.btnSkip).setOnClickListener(this);
        findViewById(R.id.btnChoose).setOnClickListener(this);
        findViewById(R.id.btnSetting).setOnClickListener(this);

        try
        {
            if (BasicStringUtil.isNotNullString(DataHolder.getFileKey()))
            {
                initFileTv(DataHolder.getFileKey());
                setContent(DataHolder.getCurrent());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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

    private void showNewSrtFile(String srtFile)
    {
        if (BasicFileUtil.isExistFile(srtFile))
        {
            stopSrtPlay();
            DataHolder.switchFile(srtFile);
            if (!DataHolder.map.containsKey(srtFile))
            {
                PickerHelper.dataEntity(getCurFile());
                getSrtInfoAndPlay(VIEW_RIGHT);
            }
            else
            {
                getSrtInfoAndPlay(VIEW_CURRENT);
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
        case R.id.btnSetting:
            setting();
            break;
        case R.id.btnChoose:
            stopReplayModel();
            showChooseMovieWheel();
            break;
        case R.id.btnSkip:
            if (hasSrtContent())
            {
                stopReplayModel();
                showSkipWheel();
            }
            break;
        case R.id.btnFirst:
            if (hasSrtContent())
            {
                stopReplayModel();
                getSrtInfoAndPlay(R.id.btnFirst);
            }
            break;
        case R.id.btnLast:
            if (hasSrtContent())
            {
                stopReplayModel();
                getSrtInfoAndPlay(R.id.btnLast);
            }
            break;
        case R.id.btnPlay:
            if (hasSrtContent())
            {
                clickPlayBtn();
            }
            break;
        case R.id.file_tv:
            if (hasSrtContent())
            {
                stopSrtPlay();
                showThumbPic();
            }
            break;
        case R.id.timeline_tv:
            if (hasSrtContent())
            {
                stopSrtPlay();// 停止播放
                showSrtInfoWheel();
            }
            break;
        }
    }

    private void setting()
    {
        settingItems[0] = !SrtSetting.isAutoPlayNext() ? "自动下一条" : "只播放一条";
        settingItems[1] = !SrtSetting.isPlayVoice() ? "播放声音" : "不播放声音";
        settingItems[2] = !replayCtrl ? "复读" : "不复读";
        alertDialog = settingDialogBuilder.show();
    }

    private void showSrtInfoWheel()
    {
        List<SrtInfo> currentSrtInfos = DataHolder.getCurrentSrtInfos();
        if (currentSrtInfos != null && !currentSrtInfos.isEmpty())
        {
            int size = currentSrtInfos.size();
            String leftArr[] = new String[size];
            String rightArr[] = new String[size];
            for (int i = 0; i < size; i++)
            {
                SrtInfo srtInfo = currentSrtInfos.get(i);
                leftArr[i] = srtInfo.getFromTime().toString();
                rightArr[i] = srtInfo.getToTime().toString();
            }

            int curIndex = getCurIndex();
            WheelDialogShowUtil.showSrtDialog(this, leftArr, rightArr,
                    curIndex, curIndex, new AfterWheelChooseListener()
                    {
                        @Override
                        public void afterWheelChoose(Object... objs)
                        {
                            setReplayIndex(Integer.valueOf(objs[0].toString()),
                                    Integer.valueOf(objs[1].toString()));
                            replayCtrl = true;
                            DataHolder.setCurrentSrtIndex(beginReplayIndex);
                        }
                    });
        }
    }

    private boolean hasSrtContent()
    {
        if (BasicFileUtil.isExistFile(getCurFile()))
        {
            return true;
        }
        ToastUtil.showShortToast(getApplicationContext(), "没有选择任何剧集");
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
                + TextFormatUtil.getFileNameNoExtend(getCurFile()) + "_p1.pic";
        Intent intent = ClickFileIntentFactory.getIntentByFile(filePath);
        startActivity(intent);
    }

    public void clickPlayBtn()
    {
        if (btnPlay.getText().equals("停止"))
        {
            stopSrtPlay();
        }
        else if (btnPlay.getText().equals("播放"))
        {
            beginSrtPlay();
        }
    }

    private void beginSrtPlay()
    {
        btnPlay.setText("停止");
        autoPlayNextCtrl = true;
        playSrt();
    }

    /**
     * 停止字幕播放
     */
    private void stopSrtPlay()
    {
        btnPlay.setText("播放");
        SrtVoiceHelper.stop();
        stopSrtPlayThread();
        autoPlayNextCtrl = false;
    }

    private void playSrt()
    {
        // 每次播放,先设置自动播放控制为true
        autoPlayNextCtrl = true;
        // 停止原有的播放线程,播放新字幕
        stopSrtPlayThread();
        autoPlayThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Message msg = new Message();
                long time = TimeHelper.getTime(DataHolder.getCurrent()
                        .getToTime())
                        - TimeHelper.getTime(DataHolder.getCurrent()
                                .getFromTime());
                try
                {
                    if (SrtSetting.isPlayVoice())
                    {
                        String voicePath = SrtTextHelper.getSrtVoiceLocation();
                        if (BasicFileUtil.isExistFile(voicePath))
                        {
                            SrtVoiceHelper.play(voicePath);
                        }
                    }
                    Thread.sleep(time);
                    autoPlayHandler.sendMessage(msg);
                }
                catch (InterruptedException e)
                {
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
        autoPlayThread.start();
    }

    public Handler autoPlayHandler = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {
            if (replayCtrl)
            {
                System.out.println("6:" + DataHolder.getCurrentSrtIndex() + " "
                        + beginReplayIndex + " " + endReplayIndex);
                if (getCurIndex() == endReplayIndex)
                {
                    DataHolder.setCurrentSrtIndex(beginReplayIndex);
                    getSrtInfoAndPlay(VIEW_CURRENT);
                }
                else
                {
                    getSrtInfoAndPlay(VIEW_RIGHT);
                }
            }
            else if (isAutoPlayModel())
            {
                // 只有在自动播放模式下,才播放下一条
                doRight();
            }
            else
            {
                stopSrtPlay();
            }
        };
    };

    private boolean isAutoPlayModel()
    {
        return autoPlayNextCtrl && SrtSetting.isAutoPlayNext();
    }

    private void showSkipWheel()
    {
        try
        {
            WheelDialogShowUtil.showTimeSelectDialog(this, defaultTimePoint,
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

            for (File f : MyFileUtil.getSortFiles(srtFolderFile.listFiles()))
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
                List<File> fileList = MyFileUtil.getSortFiles(listFiles);
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
            WheelDialogShowUtil.showRelativeDialog(this, "选择剧集", leftArr,
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
                            showNewSrtFile(srtFilePath);
                        }

                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 停止原来的字幕自动播放
     */
    private void stopSrtPlayThread()
    {
        if (autoPlayThread != null)
        {
            autoPlayThread.interrupt();
        }
    }

    private void getSrtInfoAndPlay(int btId)
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
            // btnPlay.setText("播放");
            stopSrtPlay();
            ToastUtil.showShortToast(this, "错误:" + ex.getMessage());
        }
    }

    private void setContentAndPlay(SrtInfo srt)
    {
        setContent(srt);
        beginSrtPlay();
    }

    private void setContent(SrtInfo srt)
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
        getSrtInfoAndPlay(VIEW_LEFT);
    }

    @Override
    public void doRight()
    {
        getSrtInfoAndPlay(VIEW_RIGHT);
    }

    /**
     * 控制切换是否复读,快捷设置仅复读本句
     */
    private void switchReplayModel()
    {
        this.replayCtrl = replayCtrl ? false : true;
        if (replayCtrl)
        {
            setReplayIndex(getCurIndex(), getCurIndex());
        }
        ToastUtil.showShortToast(getApplicationContext(), replayCtrl ? "复读"
                : "不复读");
    }

    private void setReplayIndex(int bIndex, int eIndex)
    {
        if (bIndex > eIndex)
        {
            ToastUtil.showLongToast(this, "结束时间不能小于开始时间!");
        }
        else
        {
            beginReplayIndex = bIndex;
            endReplayIndex = eIndex;
        }
    }

    private void stopReplayModel()
    {
        this.replayCtrl = false;
        setReplayIndex(-1, -1);
    }

    @Override
    public boolean onLongClick(View v)
    {
        stopSrtPlay();
        alertDialog = alertDialogBuilder.show();
        return true;
    }

    @Override
    public void onDestroy()
    {
        autoPlayNextCtrl = false;
        if (alertDialog != null)
        {
            System.out.println();
            alertDialog.dismiss();
        }
        HeadSetUtil.getInstance().close(this);
        super.onDestroy();
    }

    OnHeadSetListener headSetListener = new OnHeadSetListener()
    {
        @Override
        public void onDoubleClick()
        {
            switchReplayModel();
        }

        @Override
        public void onClick()
        {
            clickPlayBtn();
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
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return true;

        case KeyEvent.KEYCODE_VOLUME_UP:
            doLeft();
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
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
        autoPlayNextCtrl = false;
        btnPlay.setText("播放");
        ToastUtil.showShortToast(this, "播放出现异常");
        for (StackTraceElement o : ex.getStackTrace())
        {
            System.out.println(o.toString());
        }
    }
}
