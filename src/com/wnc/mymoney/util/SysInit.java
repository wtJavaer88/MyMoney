package com.wnc.mymoney.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.wnc.mymoney.backup.BackUpDataUtil;
import com.wnc.mymoney.dao.CategoryDao;
import com.wnc.mymoney.dao.MemberDao;
import com.wnc.mymoney.uihelper.MyAppParams;
import com.wnc.mymoney.uihelper.Setting;
import com.wnc.mymoney.util.app.MoveDbUtil;
import com.wnc.mymoney.util.app.SharedPreferenceUtil;

public class SysInit
{
    static Activity context;

    public static void init(Activity context2)
    {
        context = context2;

        BackUpDataUtil.clearTmpZips();
        // 初始化节日休假以及调休信息
        FestivalUtil.init(context);
        // 初始化Shared操作
        SharedPreferenceUtil.init(context);

        MyAppParams.getInstance().setPackageName(context.getPackageName());
        MyAppParams.getInstance().setResources(context.getResources());
        MyAppParams.getInstance().setAppPath(context.getFilesDir().getParent());
        MyAppParams.getInstance().setScreenWidth(
                context.getWindowManager().getDefaultDisplay().getWidth());
        MyAppParams.getInstance().setScreenHeight(
                context.getWindowManager().getDefaultDisplay().getHeight());
        MyAppParams.getInstance().setAssertMgr(context.getAssets());

        if (isFirstRun())
        {
            Setting.setAllBySavedJsonData();
            createDbAndFullData();
        }
        initGlobalMap();
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    // MailUtil.sendMail();
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 全局数据初始化
     */
    private static void initGlobalMap()
    {
        CategoryDao.initDb(context);
        CategoryDao.initBasicData();
        CategoryDao.closeDb();

        MemberDao.initDb(context);
        MemberDao.getAllMembers();
        MemberDao.closeDb();
    }

    private static void createDbAndFullData()
    {
        MoveDbUtil.moveEmptyMoneyDb(context);
    }

    private static boolean isFirstRun()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "share", context.MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isMoneyFirstRun",
                true);
        Editor editor = sharedPreferences.edit();
        if (isFirstRun)
        {
            Log.d("Sysinit", "第一次运行");
            editor.putBoolean("isMoneyFirstRun", false);
            editor.commit();
            return true;
        }
        else
        {
            Log.d("Sysinit", "不是第一次运行");
        }
        return false;
    }

}
