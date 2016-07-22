package com.wnc.mymoney.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.backup.BackUpDataUtil;
import com.wnc.mymoney.ui.helper.MyAppParams;
import com.wnc.mymoney.ui.helper.Setting;
import com.wnc.mymoney.ui.widget.MyToggle;
import com.wnc.mymoney.ui.widget.MyToggle.OnToggleStateListener;
import com.wnc.mymoney.util.MoveDbUtil;
import com.wnc.mymoney.util.ToastUtil;

public class SettingActivity extends Activity implements OnClickListener
{
    // 自定义开关对象
    private MyToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        setContentView(R.layout.setting_custom_set_activity);
        initView();
    }

    private void initView()
    {
        findViewById(R.id.members_set_ly).setOnClickListener(this);
        findViewById(R.id.email_set_ly).setOnClickListener(this);
        findViewById(R.id.cacheclear_set_ly).setOnClickListener(this);
        findViewById(R.id.dataclear_set_ly).setOnClickListener(this);
        findViewById(R.id.datarecover_set_ly).setOnClickListener(this);

        ((TextView) findViewById(R.id.email_tv)).setText(Setting.getEmail());

        toggle = (MyToggle) findViewById(R.id.toggle);
        toggle.setImageRes(R.drawable.btn_switch, R.drawable.btn_switch,
                R.drawable.btn_slip);
        toggle.setToggleState(true);
        toggle.setOnToggleStateListener(new OnToggleStateListener()
        {
            @Override
            public void onToggleState(boolean state)
            {
                if (state)
                {
                    ToastUtil.showShortToast(getApplicationContext(), "开关开启");
                }
                else
                {
                    ToastUtil.showShortToast(getApplicationContext(), "开关关闭");
                }
            }
        });
    }

    public void intoSettingActivity()
    {
        try
        {
            Intent localIntent = new Intent();
            localIntent.setClass(this, DragListActivity.class);
            startActivity(localIntent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
        case R.id.members_set_ly:
            intoSettingActivity();
            break;
        case R.id.email_set_ly:
            emailDialogOpen();
            break;
        case R.id.cacheclear_set_ly:
            if (BackUpDataUtil.clearAllTmpZips())
            {
                ToastUtil.showShortToast(getApplicationContext(), "清除缓存成功");
            }
            else
            {
                ToastUtil.showShortToast(getApplicationContext(), "清除缓存失败!");
            }
            break;
        case R.id.dataclear_set_ly:
            // 先备份源Db文件
            if (BackUpDataUtil.canBackUpDb)
            {
                BackUpDataUtil.backupDatabase(this);
            }
            if (MoveDbUtil.moveAssertDb("empty_money.db", "money.db", this))
            {
                ToastUtil.showShortToast(getApplicationContext(), "清空数据成功!");
            }
            else
            {
                ToastUtil.showShortToast(getApplicationContext(), "清空数据失败!");
            }
            break;
        case R.id.datarecover_set_ly:
            // 先备份源Db文件
            if (BackUpDataUtil.canBackUpDb)
            {
                BackUpDataUtil.backupDatabase(this);
            }
            if (MoveDbUtil.moveSdCardDb(MyAppParams.getInstance()
                    .getBackupDbPath() + "2016-07-18 16:40:30_money.db",
                    this.getDatabasePath("money.db")))
            {
                ToastUtil.showShortToast(getApplicationContext(), "还原数据成功!");
            }
            else
            {
                ToastUtil.showShortToast(getApplicationContext(), "还原数据失败!");
            }
            break;
        }
    }

    protected void emailDialogOpen()
    {
        final Dialog dlg = new Dialog(this, R.style.dialog);
        dlg.show();
        dlg.getWindow().setGravity(Gravity.CENTER);
        dlg.getWindow().setLayout((int) (MyAppParams.getScreenWidth() * 0.8),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        dlg.getWindow().setContentView(R.layout.setting_add_tags_dialg);
        TextView add_tag_dialg_title = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_title);
        final EditText add_tag_dialg_content = (EditText) dlg
                .findViewById(R.id.add_tag_dialg_content);
        TextView add_tag_dialg_no = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_no);
        TextView add_tag_dialg_ok = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_ok);

        add_tag_dialg_title.setText("设置邮箱");
        add_tag_dialg_content.setHint("输入邮箱");
        add_tag_dialg_content.setText(Setting.getEmail());

        add_tag_dialg_no.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dlg.dismiss();
            }
        });

        add_tag_dialg_ok.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String content = add_tag_dialg_content.getText().toString();
                if (BasicStringUtil.isNotNullString(content)
                        && BasicStringUtil.isEmailString(content))
                {
                    Setting.setEmail(content);
                    ToastUtil
                            .showShortToast(getApplicationContext(), "设置邮箱成功!");
                    dlg.dismiss();
                }
                else
                {
                    ToastUtil.showShortToast(getApplicationContext(),
                            "请输入一个邮箱!");
                }
            }
        });
    }
}
