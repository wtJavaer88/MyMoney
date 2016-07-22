package com.wnc.mymoney.ui.helper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Setting
{
    static Activity context;
    static SharedPreferences sharedPreferences;
    final static String DEFAULT_MEMBER = "王";

    private final static String LAST_MEMBER = "M001";
    private final static String EMAIL = "M002";
    private final static String BACKUP_TIME_MODEL = "M003";

    final public static void init(Activity context2)
    {
        context = context2;
        sharedPreferences = context.getSharedPreferences("share",
                context.MODE_PRIVATE);
    }

    public static void setMember(String member)
    {
        changeValue(LAST_MEMBER, member);
    }

    public static void setEmail(String email)
    {
        changeValue(EMAIL, email);
    }

    public static String getLastMember()
    {
        return getShareDataByKey(LAST_MEMBER, DEFAULT_MEMBER);
    }

    public static String getEmail()
    {
        return getShareDataByKey(EMAIL, "1@qq.com");
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
