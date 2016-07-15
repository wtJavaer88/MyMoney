package com.wnc.mymoney.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.wnc.mymoney.backup.BackUpDataUtil;
import com.wnc.mymoney.dao.CategoryDao;
import com.wnc.mymoney.dao.MemberDao;

public class SysInit
{
    static Activity context;
    /**
     * 只有数据库做了一点点修改, 就可以设为true
     */
    public static boolean canBackUpDb = false;
    public static String lastMember = "";

    public static void init(Activity context2)
    {
        context = context2;
        BackUpDataUtil.clearTmpZips();

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
        lastMember = getLastMemberFromShare();
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
        MoveDbUtil.moveDb("money.db", context);

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

    public static void changeMember(String member)
    {
        if (member.equals(lastMember))
        {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "share", context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString("lastMember", member);
        editor.commit();
        lastMember = member;
    }

    public static String getLastMember()
    {
        return lastMember;
    }

    public static String getLastMemberFromShare()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "share", context.MODE_PRIVATE);
        return sharedPreferences.getString("lastMember", "柏");
    }
}
