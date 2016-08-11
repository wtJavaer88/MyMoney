package com.wnc.srt;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.List;

import srt.DataHolder;
import srt.SRT_VIEW_TYPE;
import srt.SrtInfo;
import srt.SrtPlayService;
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
import com.wnc.mymoney.util.common.TextFormatUtil;
import com.wnc.srt.HeadSetUtil.OnHeadSetListener;

public class SrtActivity extends Activity implements OnClickListener,
        OnLongClickListener, HorGestureDetectorListener,
        UncaughtExceptionHandler
{
    // 组件设置成静态, 防止屏幕旋转的时候内存地址会变
    private static Button btnPlay;
    private static TextView movieTv;
    private static TextView chsTv;
    private static TextView engTv;
    private static TextView timelineTv;

    public final String srtFolder = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/srt/";

    private GestureDetector gestureDetector;
    AlertDialog alertDialog;

    int[] defaultTimePoint =
    { 0, 0, 0 };
    int[] defaultMoviePoint =
    { 0, -1 };// 初次使用请把右边序号设为-1,以便程序判断

    String[] settingItems = new String[]
    { "自动下一条", "播放声音", "打开复读", "音量调节" };
    static SrtPlayService srtPlayService;

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

        srtPlayService = new SrtPlayService(this);

        initView();
        initAlertDialog();
        initSettingDialog();
        if (srtPlayService.getAutoPlayThread() != null)
        {
            this.btnPlay.setText("停止");
        }
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
                                srtPlayService.favoriteCurrSrt();
                                break;
                            case 3:
                                srtPlayService.favoriteCurrSrt();
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
                                srtPlayService.switchReplayModel();
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btnSetting:
            setting();
            break;
        case R.id.btnChoose:
            showChooseMovieWheel();
            break;
        case R.id.btnSkip:
            if (hasSrtContent())
            {
                showSkipWheel();
            }
            break;
        case R.id.btnFirst:
            if (hasSrtContent())
            {
                getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_FIRST);
            }
            break;
        case R.id.btnLast:
            if (hasSrtContent())
            {
                getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_LAST);
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
        settingItems[2] = !srtPlayService.isReplayCtrl() ? "复读" : "不复读";
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

            int curIndex = srtPlayService.getCurIndex();
            WheelDialogShowUtil.showSrtDialog(this, leftArr, rightArr,
                    curIndex, curIndex, new AfterWheelChooseListener()
                    {
                        @Override
                        public void afterWheelChoose(Object... objs)
                        {
                            srtPlayService.setReplayIndex(
                                    Integer.valueOf(objs[0].toString()),
                                    Integer.valueOf(objs[1].toString()));
                            srtPlayService.setReplayCtrl(true);
                            DataHolder.setCurrentSrtIndex(srtPlayService
                                    .getBeginReplayIndex());
                        }
                    });
        }
    }

    private boolean hasSrtContent()
    {
        return srtPlayService.isSrtShowing();
    }

    /**
     * 显示该剧集图片
     */
    private void showThumbPic()
    {
        String filePath = srtPlayService.getThumbPicPath();
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

        srtPlayService.playSrt();
    }

    /**
     * 停止字幕播放
     */
    public void stopSrtPlay()
    {
        btnPlay.setText("播放");
        SrtVoiceHelper.stop();
        srtPlayService.stopSrt();
    }

    public Handler autoPlayHandler = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {

            if (srtPlayService.isReplayInvalid())
            {
                srtPlayService.stopReplayModel();
            }
            if (srtPlayService.isReplayCtrl())
            {
                // System.out.println("6:" + DataHolder.getCurrentSrtIndex() +
                // " "
                // + beginReplayIndex + " " + endReplayIndex);
                // 复读结束时,回到复读开始的地方继续复读
                if (srtPlayService.getCurIndex() == srtPlayService
                        .getEndReplayIndex())
                {
                    DataHolder.setCurrentSrtIndex(srtPlayService
                            .getBeginReplayIndex());
                    getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_CURRENT);
                }
                else
                {
                    // 复读模式下,也会自动播放下一条,但是临时性的
                    doRight();
                }
            }
            else if (srtPlayService.isAutoPlayModel())
            {
                // 在自动播放模式下,播放下一条
                doRight();
            }
            else
            {
                stopSrtPlay();
            }
        }
    };

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
        try
        {
            final String[] leftArr = srtPlayService.getDirs();
            System.out.println(Arrays.toString(leftArr));
            final String[][] rightArr = srtPlayService.getDirsFiles();
            System.out.println(Arrays.toString(rightArr));
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
                            String srtFilePath = srtPlayService
                                    .getSrtFileByArrIndex(defaultMoviePoint[0],
                                            defaultMoviePoint[1]);
                            initFileTv(srtFilePath);
                            srtPlayService.showNewSrtFile(srtFilePath);
                        }

                    });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void getSrtInfoAndPlay(SRT_VIEW_TYPE view_type)
    {

        if (alertDialog != null)
        {
            alertDialog.hide();
        }
        try
        {
            SrtInfo srt = srtPlayService.getSrtInfo(view_type);
            if (srt != null)
            {
                setContentAndPlay(srt);
            }
        }
        catch (Exception ex)
        {
            // btnPlay.setText("播放");
            stopSrtPlay();
            ToastUtil.showLongToast(this, ex.getMessage());
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
            chsTv.setText(srt.getEng() == null ? "NULL" : srt.getEng());
            engTv.setText(srt.getChs() == null ? "NULL" : srt.getChs());
        }
        else
        {
            // System.out.println("setContent:" + srt);
            chsTv.setText(srt.getChs() == null ? "NULL" : srt.getChs());
            engTv.setText(srt.getEng() == null ? "NULL" : srt.getEng());
        }
        if (srt.getFromTime() != null && srt.getToTime() != null)
        {
            timelineTv.setText(srt.getFromTime().toString() + " ---> "
                    + srt.getToTime().toString());

            defaultTimePoint[0] = srt.getFromTime().getHour();
            defaultTimePoint[1] = srt.getFromTime().getMinute();
            defaultTimePoint[2] = srt.getFromTime().getSecond();
        }
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
        getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_LEFT);
    }

    @Override
    public void doRight()
    {
        getSrtInfoAndPlay(SRT_VIEW_TYPE.VIEW_RIGHT);
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
        if (alertDialog != null)
        {
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
            srtPlayService.switchReplayModel();
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
        for (StackTraceElement o : ex.getStackTrace())
        {
            System.out.println(o.toString());
        }
        stopSrtPlay();
        ToastUtil.showShortToast(this, "播放出现异常");
    }

    public void reveiveMsg(Message msg)
    {
        autoPlayHandler.sendMessage(msg);
    }
}
