package com.wnc.mymoney.util.enums;

import android.util.Log;

import com.wnc.mymoney.R;
import com.wnc.mymoney.bean.Category;
import com.wnc.mymoney.dao.CategoryDao;
import com.wnc.mymoney.uihelper.MyAppParams;

public class CostTypeUtil
{
    public static String getCostTypeName(int descId)
    {
        String ret = "其它开销";
        for (Category c : CategoryDao.getAllList())
        {
            if (c.getId() == descId)
            {
                return c.getName();
            }
        }
        return ret;
    }

    public static boolean isLevelType(int id)
    {
        for (Category c : CategoryDao.getAllList())
        {
            if (c.getId() == id)
            {
                if (c.getDepth() == 1)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDescType(int id)
    {
        if (id < 1)
        {
            return false;
        }
        return !isLevelType(id);
    }

    public static int getIcon(int descId)
    {
        int srcId = R.drawable.icon_qtzx_qtzc;
        try
        {
            srcId = getAppRrawbleID(CategoryDao.iconMap.get(descId));
        }
        catch (Exception ex)
        {
            Log.i("res", "找不到资源id图标 " + descId);
        }
        return srcId;
    }

    public static int getFlagIcon(int flag)
    {
        if (flag == 1)
        {
            return R.drawable.photo_flag;
        }
        return R.drawable.empty_photo_flag;
    }

    private static int getAppRrawbleID(String picname)
    {
        return MyAppParams
                .getInstance()
                .getResources()
                .getIdentifier(picname, "drawable",
                        MyAppParams.getInstance().getPackageName());
    }
}
