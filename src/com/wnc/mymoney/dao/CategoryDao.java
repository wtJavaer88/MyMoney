package com.wnc.mymoney.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wnc.mymoney.bean.Category;
import com.wnc.mymoney.bean.MyWheelBean;

public class CategoryDao
{
    private static SQLiteDatabase db = null;
    public static Map<Integer, String> iconMap = new HashMap<Integer, String>();
    // 默认的收入支出类型
    public static int defaultOutTradeType = Integer.MIN_VALUE;
    public static int defaultOutDescTradeType = Integer.MIN_VALUE;
    public static int defaultInTradeType = Integer.MIN_VALUE;
    public static int defaultInDescTradeType = Integer.MIN_VALUE;

    public static void initDb(Context context)
    {
        if (db == null)
        {
            db = context.openOrCreateDatabase("money.db", Context.MODE_PRIVATE,
                    null);
            if (allList != null && allList.isEmpty())
            {
                initTypes();
            }
        }
    }

    private static void initIcons()
    {
        for (Category category : allList)
        {
            iconMap.put(category.getId(), category.getIcon());
        }
    }

    private static void initTypes()
    {
        getTypesFromdb();
        if (allList != null && allList.size() > 0)
        {
            splitTypes();
            initIcons();
        }
    }

    private static void splitTypes()
    {
        for (Category category : allList)
        {
            if (category.getDepth() == 1)
            {
                if (category.getPath().startsWith("/-1/"))
                {
                    // get an outtype
                    outLevels.add(category);
                    outDescTypes.put(category, getsonList(category));
                    if (defaultOutTradeType == Integer.MIN_VALUE)
                    {
                        defaultOutTradeType = category.getId();
                    }
                }
                else if (category.getPath().startsWith("/-2/"))
                {
                    // get an intype
                    inLevels.add(category);
                    inDescTypes.put(category, getsonList(category));
                    if (defaultInTradeType == Integer.MIN_VALUE)
                    {
                        defaultInTradeType = category.getId();
                    }
                }
            }
        }
    }

    private static List<MyWheelBean> getsonList(Category category)
    {
        List<MyWheelBean> tmpList = new ArrayList<MyWheelBean>();

        for (Category category_son : allList)
        {
            if (category_son.getParent_id().equals(category.getId() + ""))
            {
                tmpList.add(category_son);
                if (defaultOutDescTradeType == Integer.MIN_VALUE
                        && category_son.getPath().startsWith("/-1/"))
                {
                    defaultOutDescTradeType = category_son.getId();
                }
                if (defaultInDescTradeType == Integer.MIN_VALUE
                        && category_son.getPath().startsWith("/-2/"))
                {
                    defaultInDescTradeType = category_son.getId();
                }
            }
        }
        return tmpList;
    }

    private static void getTypesFromdb()
    {

        if (db == null)
        {
            Log.e("dao", "Not opened Db !");
            return;
        }
        Cursor c = db.rawQuery(
                "SELECT * FROM category WHERE DEPTH>0 ORDER BY ORDERED ASC",
                new String[]
                {});
        while (c.moveToNext())
        {
            Category bean = new Category();
            bean.setId(Integer.parseInt(getStrValue(c, "ID")));
            bean.setName(getStrValue(c, "NAME"));
            bean.setDepth(Integer.parseInt(getStrValue(c, "DEPTH")));
            bean.setIcon(getStrValue(c, "ICON"));
            bean.setOrdered(getStrValue(c, "ORDERED"));
            bean.setPath(getStrValue(c, "PATH"));
            bean.setParent_id(getStrValue(c, "PARENT_ID"));
            allList.add(bean);
        }
        c.close();

    }

    public static void closeDb()
    {
        if (db != null)
        {
            db.close();
            db = null;
        }
    }

    private static List<Category> allList = new ArrayList<Category>();
    private static List<MyWheelBean> outLevels = new ArrayList<MyWheelBean>();
    private static Map<MyWheelBean, List<MyWheelBean>> outDescTypes = new HashMap<MyWheelBean, List<MyWheelBean>>();
    private static List<MyWheelBean> inLevels = new ArrayList<MyWheelBean>();
    private static Map<MyWheelBean, List<MyWheelBean>> inDescTypes = new HashMap<MyWheelBean, List<MyWheelBean>>();

    public static List<Category> getAllList()
    {
        return allList;
    }

    public static List<MyWheelBean> getAllLevels(int filter)
    {
        if (filter == -1 && outLevels != null)
        {
            return outLevels;
        }
        if (filter == -2 && inLevels != null)
        {
            return inLevels;
        }

        return null;
    }

    public static Map<MyWheelBean, List<MyWheelBean>> getAllDescTypes(int filter)
    {
        if (filter == -1 && outDescTypes != null)
        {
            return outDescTypes;
        }
        if (filter == -2 && inDescTypes != null)
        {
            return inDescTypes;
        }

        return null;
    }

    private static String getStrValue(Cursor c, String columnName)
    {
        return c.getString(c.getColumnIndex(columnName));
    }

}
