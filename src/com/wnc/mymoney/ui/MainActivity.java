package com.wnc.mymoney.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.wnc.basic.BasicDateUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.backup.BackUpDataUtil;
import com.wnc.mymoney.dao.OnStartUpDataUtil;
import com.wnc.mymoney.dao.TransactionsDao;
import com.wnc.mymoney.service.LogService;
import com.wnc.mymoney.test.ExpandableListViewActivity;
import com.wnc.mymoney.ui.setting.SettingActivity;
import com.wnc.mymoney.uihelper.Setting;
import com.wnc.mymoney.util.SysInit;
import com.wnc.mymoney.util.app.AppRescouceReflect;
import com.wnc.mymoney.util.app.SharedPreferenceUtil;
import com.wnc.mymoney.util.common.TextFormatUtil;
import com.wnc.mymoney.util.enums.BackupTimeModel;
import com.wnc.mymoney.util.enums.NetChannel;
import com.wnc.mymoney.util.enums.TOTAL_RANGE;
import com.wnc.train.TrainTicketActivity;
import com.wnc.train.TrainUIMsgHelper;

public class MainActivity extends Activity
{

    private int LIMIT_PERMONTH = 0;
    private int LIMIT_PERWEEK = 0;
    LogService serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SysInit.init(this);
        SharedPreferenceUtil.init(this);
        Intent intent = new Intent(this, LogService.class);
        /** 进入Activity开始服务 */
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        initComponents();
        setViewsIfChange();
    }

    private void initComponents()
    {
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
                Intent localIntent = new Intent();
                localIntent.setClass(getApplicationContext(),
                        ExpandableListViewActivity.class);
                startActivity(localIntent);
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

    @Override
    public void onDestroy()
    {
        System.out.println("Destoryed Main!");
        // stopService(serviceIntent);
        this.unbindService(conn);

        backupData();

        TrainUIMsgHelper.stopVibrator();
        super.onDestroy();
    }

    private void backupData()
    {
        boolean isAuto = Boolean.valueOf(Setting.isBackupAuto());
        if (isAuto)
        {
            BackupTimeModel timeModel = Setting.getBackupTimeModel().equals(
                    "每次") ? BackupTimeModel.TIMELY : BackupTimeModel.DAILY;
            NetChannel way = Setting.getBackupWay().equals("邮箱") ? NetChannel.EMAIL
                    : NetChannel.SHARE;
            BackUpDataUtil.backup(this, timeModel, way);
        }
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
        if (BackUpDataUtil.canBackUpDb || Setting.budgetChanged
                || Setting.restored)
        {
            OnStartUpDataUtil.restart();// 重新开始计算统计数据
            setViewsIfChange();
        }
    }

}
