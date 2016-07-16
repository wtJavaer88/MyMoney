package com.wnc.mymoney.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.wnc.mymoney.R;
import com.wnc.mymoney.ui.helper.MovieRecorder;

public class MovieActivity extends Activity
{
    private static final String LOG_TAG = MovieActivity.class.getSimpleName();

    MovieRecorder mRecorder;

    Button btnRecoder;
    Button btnRetry;

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    private final int FREE_STATUS = 0;
    private final int RUNNING_STATUS = 1;
    private final int OVER_STATUS = 2;
    public static final String RECORDED_PATH = "moviepath";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 设置横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        setContentView(R.layout.activity_movie);

        btnRecoder = (Button) findViewById(R.id.btnRecoder);
        btnRecoder.setOnClickListener(mRecordingClick);
        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(mRecordingClick);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceHolderCallback); // holder加入回调接口
        // setType必须设置，要不出错.
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mRecorder = new MovieRecorder(surfaceView);

        setBtStatu(RUNNING_STATUS);
    }

    // 界面加载完成
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (hasFocus)
        {
            mRecorder.start();
        }
    }

    @Override
    protected void onDestroy()
    {
        if (mRecorder != null)
        {
            mRecorder.release();
        }
        super.onDestroy();
    };

    Callback surfaceHolderCallback = new Callback()
    {
        @Override
        public void surfaceDestroyed(SurfaceHolder arg0)
        {
            surfaceView = null;
            surfaceHolder = null;
            mRecorder = null;
        }

        @Override
        public void surfaceCreated(SurfaceHolder arg0)
        {
            // TODO Auto-generated method stub
            surfaceHolder = arg0;
        }

        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
                int arg3)
        {
            // TODO Auto-generated method stub
            surfaceHolder = arg0;
        }
    };

    OnClickListener mRecordingClick = new OnClickListener()
    {
        @Override
        public void onClick(View arg0)
        {
            if (arg0.getId() == R.id.btnRecoder)
            {
                if (mRecorder.isRecording())
                {
                    mRecorder.stopRecording();
                    setBtStatu(OVER_STATUS);
                }
            }
            else if (arg0.getId() == R.id.btnRetry)
            {
                mRecorder.stopRecording();
                mRecorder = new MovieRecorder(surfaceView);
                mRecorder.start();
            }
        }

    };

    private void setBtStatu(int status)
    {
        switch (status)
        {
        case FREE_STATUS:
            this.btnRecoder.setText("点击开始录制");
            break;
        case RUNNING_STATUS:
            this.btnRecoder.setText("正在录制,请点击结束");
            break;
        case OVER_STATUS:
            setResultAndFinish();
            // this.btnRecoder.setText("录制结束,点击可以重新录制");
            break;
        }
    }

    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        setResultAndFinish();
        return true;
    }

    private void setResultAndFinish()
    {
        Intent intent = new Intent();
        intent.putExtra(RECORDED_PATH, this.mRecorder.getRecordedFile());// 放入返回值
        setResult(0, intent);// 放入回传的值,并添加一个Code,方便区分返回的数据
        finish();
    }
}
