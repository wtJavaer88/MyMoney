package com.wnc.mymoney.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.wnc.mymoney.backup.BackUpDataUtil;
import com.wnc.mymoney.dao.CategoryDao;
import com.wnc.mymoney.dao.MemberDao;
import com.wnc.mymoney.uihelper.MyAppParams;
import com.wnc.mymoney.util.app.MoveDbUtil;

public class SysInit
{
    static Activity context;

    public static void init(Activity context2)
    {
        context = context2;
        BackUpDataUtil.clearTmpZips();
        // 初始化节日休假以及调休信息
        FestivalUtil.init(context);

        MyAppParams.getInstance().setPackageName(context.getPackageName());
        MyAppParams.getInstance().setResources(context.getResources());
        MyAppParams.getInstance().setAppPath(context.getFilesDir().getParent());
        MyAppParams.getInstance().setScreenWidth(
                context.getWindowManager().getDefaultDisplay().getWidth());
        MyAppParams.getInstance().setScreenHeight(
                context.getWindowManager().getDefaultDisplay().getHeight());

        if (isFirstRun())
        {
            createDbAndFullData();
        }
        initGlobalMap();
    }

    private static void initGlobalMap()
    {
        CategoryDao.initDb(context);
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
