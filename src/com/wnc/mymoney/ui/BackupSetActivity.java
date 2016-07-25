package com.wnc.mymoney.ui;

import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.wnc.mymoney.R;
import com.wnc.mymoney.ui.helper.Setting;
import com.wnc.mymoney.ui.widget.ComboBox;
import com.wnc.mymoney.ui.widget.ComboBox.ListViewItemClickListener;
import com.wnc.mymoney.ui.widget.MyToggle;
import com.wnc.mymoney.ui.widget.MyToggle.OnToggleStateListener;
import com.wnc.mymoney.util.ToastUtil;

public class BackupSetActivity extends Activity implements OnClickListener
{
    // 自定义开关对象
    private MyToggle toggle;
    private ComboBox backupWayComb;
    private ComboBox backupFreComb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        setContentView(R.layout.backup_activity);
        initView();
    }

    private void initView()
    {
        backupWayComb = (ComboBox) findViewById(R.id.backupWayComb);
        backupFreComb = (ComboBox) findViewById(R.id.backupFreComb);
        backupWayComb.setData(Arrays.asList("邮箱", "分享"));
        backupFreComb.setData(Arrays.asList("每次", "每天", "每周"));

        backupWayComb.setText(Setting.getBackupWay());
        backupFreComb.setText(Setting.getBackupTimeModel());

        toggle = (MyToggle) findViewById(R.id.autobackup_toggle);
        toggle.setImageRes(R.drawable.btn_switch, R.drawable.btn_switch,
                R.drawable.btn_slip);
        toggle.setToggleState(Boolean.valueOf(Setting.getBackupAuto()));
        toggle.setOnToggleStateListener(new OnToggleStateListener()
        {
            @Override
            public void onToggleState(boolean state)
            {
                if (state)
                {
                    ToastUtil.showShortToast(getApplicationContext(), "开关开启");
                    Setting.setBACKUP_AUTO("true");
                }
                else
                {
                    ToastUtil.showShortToast(getApplicationContext(), "开关关闭");
                    Setting.setBACKUP_AUTO("false");
                }
            }
        });

        backupWayComb
                .setListViewOnClickListener(new ListViewItemClickListener()
                {
                    @Override
                    public void onItemClick(int position)
                    {
                        Setting.setBACKUP_WAY(backupWayComb.getText()
                                .toString());
                    }
                });
        backupFreComb
                .setListViewOnClickListener(new ListViewItemClickListener()
                {
                    @Override
                    public void onItemClick(int position)
                    {
                        Setting.setBACKUP_TIME_MODEL(backupFreComb.getText()
                                .toString());
                    }
                });
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
        }
    }
}
