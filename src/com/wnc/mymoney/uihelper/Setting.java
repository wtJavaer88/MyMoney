package com.wnc.mymoney.uihelper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Setting
{
    static Activity context;
    static SharedPreferences sharedPreferences;
    final static String DEFAULT_MEMBER = "王";
    final static String DEFAULT_EMAIL = "1@qq.com";
    final static String DEFAULT_BUDGET = "500,2000";

    private final static String LAST_MEMBER = "M001";
    private final static String EMAIL = "M002";

    private final static String BACKUP_TIME_MODEL = "M0031";
    private final static String BACKUP_AUTO = "M0032";
    private final static String BACKUP_WAY = "M0033";

    private final static String BUDGET = "M004";

    private final static String AUTOPLAYVOICE = "M005";

    final public static void init(Activity context2)
    {
        context = context2;
        sharedPreferences = context.getSharedPreferences("share",
                context.MODE_PRIVATE);
    }

    public static void setBACKUP_TIME_MODEL(String backup_time_model)
    {
        changeValue(BACKUP_TIME_MODEL, backup_time_model);
    }

    public static void setBACKUP_AUTO(String backup_auto)
    {
        changeValue(BACKUP_AUTO, backup_auto);
    }

    public static void setBACKUP_WAY(String backup_way)
    {
        changeValue(BACKUP_WAY, backup_way);
    }

    public static void setMember(String member)
    {
        changeValue(LAST_MEMBER, member);
    }

    public static void setEmail(String email)
    {
        changeValue(EMAIL, email);
    }

    public static void setBudget(String budget)
    {
        changeValue(BUDGET, budget);
    }

    public static void setAutoPlayVoice(String autoplay)
    {
        changeValue(AUTOPLAYVOICE, autoplay);
    }

    public static String getLastMember()
    {
        return getShareDataByKey(LAST_MEMBER, DEFAULT_MEMBER);
    }

    public static String getEmail()
    {
        return getShareDataByKey(EMAIL, DEFAULT_EMAIL);
    }

    public static String getBudget()
    {
        return getShareDataByKey(BUDGET, DEFAULT_BUDGET);
    }

    public static String getBackupTimeModel()
    {
        return getShareDataByKey(BACKUP_TIME_MODEL, "每次");
    }

    public static String getBackupAuto()
    {
        return getShareDataByKey(BACKUP_AUTO, "false");
    }

    public static String getBackupWay()
    {
        return getShareDataByKey(BACKUP_WAY, "邮箱");
    }

    public static String getAutoPlayVoice()
    {
        return getShareDataByKey(AUTOPLAYVOICE, "false");
    }

    private static void changeValue(String key, String value)
    {
        if (value.equals(getShareDataByKey(key, "")))
        {
            return;
        }
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getShareDataByKey(String key, String defaultValue)
    {
        return sharedPreferences.getString(key, defaultValue);
    }

}
