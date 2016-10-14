package com.wnc.mymoney.ui;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.wnc.basic.BasicDateUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.backup.BackUpDataUtil;
import com.wnc.mymoney.backup.BackupFilesHolder;
import com.wnc.mymoney.dao.OnStartUpDataUtil;
import com.wnc.mymoney.dao.TransactionsDao;
import com.wnc.mymoney.service.LogService;
import com.wnc.mymoney.ui.setting.SettingActivity;
import com.wnc.mymoney.uihelper.Setting;
import com.wnc.mymoney.util.SysInit;
import com.wnc.mymoney.util.app.AppRescouceReflect;
import com.wnc.mymoney.util.common.TextFormatUtil;
import com.wnc.mymoney.util.enums.BackupTimeModel;
import com.wnc.mymoney.util.enums.NetChannel;
import com.wnc.mymoney.util.enums.TOTAL_RANGE;
import com.wnc.train.TrainTicketActivity;
import com.wnc.train.TrainUIMsgHelper;

public class MainActivity extends Activity implements UncaughtExceptionHandler
{

    private int LIMIT_PERMONTH = 0;
    private int LIMIT_PERWEEK = 0;
    LogService serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(this);

        SysInit.init(this);
        Intent intent = new Intent(this, LogService.class);
        /** 进入Activity开始服务 */
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        initComponents();
        setViewsIfChange();

    }

    private void initComponents()
    {
        dialog = new Dialog(this, R.style.CustomDialogStyle);
        // 底部菜单

        String[] menus = new String[]
        { "nav_yeartrans_tv", "nav_account_tv", "nav_report_tv",
                "nav_budget_tv", "nav_setting_tv", "add_expense_quickly_btn" };

        MyMenuClickListener menuClickListener = new MyMenuClickListener();
        for (String menuName : menus)
        {
            createAndListenViews(menuName, menuClickListener);
        }

        MyLayoutClickListener layoutClickListener = new MyLayoutClickListener();
        String[] layouts = new String[]
        { "month_expense_ly", "today_row_rl", "week_row_rl", "month_row_rl" };
        for (String layoutName : layouts)
        {
            createAndListenViews(layoutName, layoutClickListener);
        }
    }

    /**
     * 设置首页统计数据
     * 
     * @param b
     */
    private void setViewsIfChange()
    {
        TransactionsDao.initDb(this);
        ((TextView) findViewById(R.id.income_amount_tv)).setText(""
                + TextFormatUtil.getFormatMoneyStr(OnStartUpDataUtil
                        .getCurrMonthInBound()));
        ((TextView) findViewById(R.id.expense_amount_tv)).setText(""
                + TextFormatUtil.getFormatMoneyStr(OnStartUpDataUtil
                        .getCurrMonthOutBound()));

        LIMIT_PERWEEK = Setting.getBudget();
        LIMIT_PERMONTH = LIMIT_PERWEEK * 4;
        ((TextView) findViewById(R.id.week_balance_amount_tv)).setText(""
                + TextFormatUtil.getFormatMoneyStr(LIMIT_PERWEEK
                        - OnStartUpDataUtil.getCurrWeekOutBound()));
        ((TextView) findViewById(R.id.month_balance_amount_tv)).setText(""
                + TextFormatUtil.getFormatMoneyStr(LIMIT_PERMONTH
                        - OnStartUpDataUtil.getCurrMonthOutBound()));

        ((TextView) findViewById(R.id.today_expense_amount_tv)).setText(""
                + TextFormatUtil.getFormatMoneyStr(OnStartUpDataUtil
                        .getCurrDayOutBound()));
        ((TextView) findViewById(R.id.today_income_amount_tv)).setText(""
                + TextFormatUtil.getFormatMoneyStr(OnStartUpDataUtil
                        .getCurrDayInBound()));
        ((TextView) findViewById(R.id.week_expense_amount_tv)).setText(""
                + TextFormatUtil.getFormatMoneyStr(OnStartUpDataUtil
                        .getCurrWeekOutBound()));
        ((TextView) findViewById(R.id.week_income_amount_tv)).setText(""
                + TextFormatUtil.getFormatMoneyStr(OnStartUpDataUtil
                        .getCurrWeekInBound()));
        ((TextView) findViewById(R.id.month_expense_amount_tv)).setText(""
                + TextFormatUtil.getFormatMoneyStr(OnStartUpDataUtil
                        .getCurrMonthOutBound()));
        ((TextView) findViewById(R.id.month_income_amount_tv)).setText(""
                + TextFormatUtil.getFormatMoneyStr(OnStartUpDataUtil
                        .getCurrMonthInBound()));

        ((TextView) findViewById(R.id.month_tv)).setText(""
                + BasicDateUtil.getCurrentMonth());
        ((TextView) findViewById(R.id.year_tv)).setText("/"
                + BasicDateUtil.getCurrentYearString());
        TransactionsDao.closeDb();

    }

    public class MyLayoutClickListener implements OnClickListener
    {

        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
            case R.id.main_top_month_report_rl:

                break;
            case R.id.today_row_rl:
                intoTransTotalActivity(TOTAL_RANGE.CURRDAY);
                break;
            case R.id.week_row_rl:
                intoTransTotalActivity(TOTAL_RANGE.CURRWEEK);
                break;
            case R.id.month_row_rl:
                intoTransTotalActivity(TOTAL_RANGE.CURRMONTH);
                break;
            default:
                break;
            }
        }

    }

    public class MyMenuClickListener implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
            case R.id.nav_yeartrans_tv:
                intoTransTotalActivity(TOTAL_RANGE.CURRYEAR);
                break;
            case R.id.nav_account_tv:
                // Intent localIntent = new Intent();
                // localIntent.setClass(getApplicationContext(),
                // VideoActivity.class);
                // startActivity(localIntent);
                break;
            case R.id.nav_report_tv:
                toPieChart();
                break;
            case R.id.nav_budget_tv:
                intoTrainActivity();
                break;
            case R.id.nav_setting_tv:
                intoSettingActivity();
                break;
            case R.id.add_expense_quickly_btn:
                createNewExpand();
                break;
            default:
                break;
            }
        }
    }

    private void intoTrainActivity()
    {
        Intent localIntent = new Intent();
        localIntent.setClass(this, TrainTicketActivity.class);
        startActivity(localIntent);
    }

    private void intoTransTotalActivity(TOTAL_RANGE type)
    {
        Intent localIntent = new Intent();
        localIntent.setClass(this, NavTransactionActivity.class);
        localIntent.putExtra("mode", type.name());
        startActivity(localIntent);
    }

    public void intoSettingActivity()
    {
        try
        {
            Intent localIntent = new Intent();
            localIntent.setClass(this, SettingActivity.class);
            startActivity(localIntent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void toPieChart()
    {
        Intent localIntent = new Intent();
        localIntent.setClass(this, MonthPieChartActivity.class);
        startActivity(localIntent);
    }

    private void createNewExpand()
    {
        Intent localIntent = new Intent(this, AddOrEditTransActivity.class);
        localIntent.putExtra("state", 1);
        localIntent.putExtra("transType", 0);
        localIntent.putExtra("needOpenTransTemplate", false);
        startActivity(localIntent);
    }

    private void createAndListenViews(String resName,
            OnClickListener clickListener)
    {
        findViewById(AppRescouceReflect.getAppControlID(resName))
                .setOnClickListener(clickListener);
    }

    final int MESSAGE_BACKUP_SUCCESS = 1;
    final int MESSAGE_BACKUP_FAIL = 2;
    final int MESSAGE_BACKUP_TIMEOUT = 3;
    Handler backupHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case MESSAGE_BACKUP_SUCCESS:
                System.out.println("备份成功!!");
                setBackupMsg("备份成功!!");
                break;
            case MESSAGE_BACKUP_FAIL:
                System.out.println("备份失败!!");
                setBackupMsg("备份失败!!");
                break;
            case MESSAGE_BACKUP_TIMEOUT:
                System.out.println("备份超时!!");
                setBackupMsg("备份超时!!");
                break;
            default:
                break;
            }
            watchingBackup = false;
            BackUpDataUtil.canBackUpDb = false;
            BackupFilesHolder.clearBackupFiles();
        }
    };

    void setBackupMsg(String msg)
    {
        ((TextView) dialog.findViewById(R.id.backupinfo)).setText(msg);
    }

    Dialog dialog;

    public void showDialog()
    {
        dialog.setContentView(R.layout.common_wdailog);
        dialog.setCanceledOnTouchOutside(true);
        setBackupMsg("正在备份");
        dialog.show();
    }

    volatile boolean watchingBackup = true;

    @Override
    public void onBackPressed()
    {
        System.out.println("onBackPressed Main!");
        // stopService(serviceIntent);

        TrainUIMsgHelper.stopVibrator();
        // 自动备份逻辑
        boolean isAuto = Boolean.valueOf(Setting.isBackupAuto());
        final boolean flag = isAuto && BackUpDataUtil.canBackUpDb;
        if (flag)
        {
            showDialog();
            final Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {

                    if (backupData())
                    {
                        backupHandler.sendEmptyMessage(MESSAGE_BACKUP_SUCCESS);
                    }
                    else
                    {
                        backupHandler.sendEmptyMessage(MESSAGE_BACKUP_FAIL);
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
            final Thread thread2 = new Thread(new Runnable()
            {
                int sum_Time = 0;

                @Override
                public void run()
                {
                    try
                    {
                        int step = 5;
                        // 最多600秒就算超时,然后退出程序
                        while (sum_Time < 600 && watchingBackup)
                        {
                            Thread.sleep(step * 1000);
                            sum_Time += step;
                        }
                        if (watchingBackup)
                        {
                            backupHandler
                                    .sendEmptyMessage(MESSAGE_BACKUP_TIMEOUT);
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            thread2.setDaemon(true);
            thread2.start();
        }
        else
        {
            this.unbindService(conn);
            dialog.dismiss();
            finish();
        }
    }

    private boolean backupData()
    {
        BackupTimeModel timeModel = Setting.getBackupTimeModel().equals("每次") ? BackupTimeModel.TIMELY
                : BackupTimeModel.DAILY;
        NetChannel way = Setting.getBackupWay().equals("邮箱") ? NetChannel.EMAIL
                : NetChannel.SHARE;
        return BackUpDataUtil.backup(this, timeModel, way);
    }

    private ServiceConnection conn = new ServiceConnection()
    {
        /** 获取服务对象时的操作 */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            // TODO Auto-generated method stub
            serviceIntent = ((LogService.ServiceBinder) service).getService();

        }

        /** 无法获取到服务对象时的操作 */
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            // TODO Auto-generated method stub
            serviceIntent.stopSelf();
            serviceIntent = null;
        }

    };

    @Override
    protected void onResume()
    {
        super.onResume();
        if (Setting.datachanged || Setting.budgetChanged || Setting.restored)
        {
            Setting.datachanged = false;
            Setting.restored = false;
            Setting.budgetChanged = false;
            OnStartUpDataUtil.restart();// 重新开始计算统计数据
            setViewsIfChange();
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        Log.i("AAA", "uncaughtException   " + ex);
        for (StackTraceElement o : ex.getStackTrace())
        {
            System.out.println(o.toString());
        }
    }
}
