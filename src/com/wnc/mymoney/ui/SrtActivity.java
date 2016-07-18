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
import com.wnc.mymoney.ui.helper.WheelDialogShowUtil;
import com.wnc.mymoney.util.ToastUtil;
import com.wnc.srt.DataHolder;
import com.wnc.srt.Picker;
import com.wnc.srt.PickerFactory;
import com.wnc.srt.SrtInfo;

public class SrtActivity extends Activity implements OnClickListener,
        HorGestureDetectorListener
{
    TextView chsTv;
    TextView engTv;
    TextView timelineTv;
    final String srtFolder = Environment.getExternalStorageDirectory()
            .getPath() + "/wnc/app/srt/";
    Map<Integer, String> srtFilePathes = new HashMap<Integer, String>();
    private GestureDetector gestureDetector;

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
        chsTv = (TextView) findViewById(R.id.chs_tv);
        engTv = (TextView) findViewById(R.id.eng_tv);
        timelineTv = (TextView) findViewById(R.id.timeline_tv);
        if (BasicStringUtil.isNotNullString(DataHolder.fileKey))
        {
            initFileTv(DataHolder.fileKey);
            getSrtAndSetContent(RIGHT);
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
                ((TextView) findViewById(R.id.file_tv)).setText(folder + " / "
                        + f.getName());
            }
        }
    }

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
                DataHolder.srtIndex = -1;
                DataHolder.setFileKey(srtFile);
                getSrtAndSetContent(RIGHT);
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

    private void showSkipWheel()
    {
        try
        {
            WheelDialogShowUtil.showTimeDialog(this,
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
                                setContent(DataHolder.getClosestSrt(h, m, s));
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
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void showChooseWheel()
    {
        File srtFolderFile = new File(srtFolder);

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
            leftArr[i] = folder.getName().substring(0, 10);
            File[] listFiles = folder.listFiles();
            String[] arr = new String[listFiles.length];
            int j = 0;
            for (File f2 : listFiles)
            {
                srtFilePathes.put(1000 * i + j, f2.getAbsolutePath());
                arr[j++] = f2.getName();
            }
            rightArr[i] = arr;
            i++;
        }
        try
        {
            WheelDialogShowUtil.showSelectDialog(this, "选择剧集", leftArr,
                    rightArr, 0, 0, new AfterWheelChooseListener()
                    {
                        @Override
                        public void afterWheelChoose(Object... objs)
                        {
                            int selectedLeftIndex = Integer.valueOf(objs[0]
                                    .toString());
                            int selectedRightIndex = Integer.valueOf(objs[1]
                                    .toString());
                            String srtFilePath = srtFilePathes.get(1000
                                    * selectedLeftIndex + selectedRightIndex);
                            // ((TextView) findViewById(R.id.file_tv))
                            // .setText(tvFolderFiles.get(
                            // selectedLeftIndex).getName()
                            // + " / "
                            // + new File(srtFilePath).getName());
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
        timelineTv.setText(srt.getFromTime().toString() + " ---> "
                + srt.getToTime().toString());
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
