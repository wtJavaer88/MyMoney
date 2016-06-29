package com.wnc.mymoney.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wnc.basic.BasicDateUtil;
import com.wnc.mymoney.util.SysInit;

public class MemberDao
{
    private static SQLiteDatabase db = null;

    public static void initDb(Context context)
    {
        db = context.openOrCreateDatabase("money.db", Context.MODE_PRIVATE,
                null);
    }

    public static boolean insertMember(String name) throws RuntimeException
    {
        if (db == null)
        {
            Log.e("dao", "Not opened Db !");
            return false;
        }
        try
        {
            if (checkExist(name))
            {
                throw new RuntimeException("该成员已经存在!");
            }
            String currentTime = BasicDateUtil.getCurrentDateTimeString();
            int max = getCounts() + 1;

            db.execSQL(
                    "INSERT INTO member(ID,NAME,ITEM_ORDER,CREATE_TIME) VALUES (NULL,?, ?,?)",
                    new Object[]
                    { name.trim(), max, currentTime });
            list.add(name);
            trigger();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
        return true;
    }

    private static boolean checkExist(String name)
    {
        Cursor c = db.rawQuery(
                "SELECT * FROM member WHERE ISDEL=0 AND name = ? ",
                new String[]
                { name });
        if (c.moveToNext())
        {
            return true;
        }
        c.close();
        return false;
    }

    public static int getCounts()
    {
        if (list != null)
        {
            return list.size();
        }
        return 0;
    }

    public static boolean updateMembersOrder(List<String> list)
    {
        boolean ret = false;
        if (list == null)
        {
            return false;
        }
        db.beginTransaction();
        try
        {

            int order = 1;
            for (String name : list)
            {
                ContentValues cv = new ContentValues();
                cv.put("ITEM_ORDER", order++);
                db.update("MEMBER", cv, "name = ?", new String[]
                { name });
                trigger();
            }
            db.setTransactionSuccessful();// 设置成功
            ret = true;
        }
        catch (Exception ex)
        {
            ret = false;
        }
        finally
        {
            db.endTransaction();// 成功的时候会提交, 不然会回滚
        }

        return ret;
    }

    private static void trigger()
    {
        SysInit.canBackUpDb = true;
    }

    private static String getStrValue(Cursor c, String columnName)
    {
        return c.getString(c.getColumnIndex(columnName));
    }

    public static List<String> getAllMembersForSearch()
    {
        List<String> searchMembers = new ArrayList<String>();
        if (list != null)
        {
            searchMembers.clear();
            searchMembers.add("全部成员");
            for (String member : list)
            {
                searchMembers.add(member);
            }
        }
        else
        {
            searchMembers.add("全部成员");
        }

        return searchMembers;
    }

    static List<String> list = null;

    public static List<String> getAllMembers()
    {
        if (list != null)
        {
            return list;
        }
        if (db == null)
        {
            Log.e("dao", "Not opened Db !");
            return list;
        }
        Cursor c = db
                .rawQuery(
                        "SELECT name FROM member WHERE ISDEL = 0 ORDER BY ITEM_ORDER ASC",
                        null);
        list = new ArrayList<String>();
        while (c.moveToNext())
        {
            list.add(getStrValue(c, "NAME"));
        }
        c.close();
        return list;
    }

    public static boolean deleteByName(String name)
    {
        if (db == null)
        {
            Log.e("dao", "Not opened Db !");
            return false;
        }

        if (checkExistInTrasactions(name))
        {
            throw new RuntimeException("<" + name + ">该成员在消费表中已有引用!");
        }

        try
        {
            ContentValues cv = new ContentValues();
            cv.put("isdel", 1);
            if (db.update("member", cv, "name = ?", new String[]
            { String.valueOf(name) }) == 0)
            {
                return false;
            }
            list.remove(name);
            trigger();
        }
        catch (Exception ex)
        {
            throw new RuntimeException("修改成员表时异常," + ex.getMessage());
        }
        return true;
    }

    private static boolean checkExistInTrasactions(String name)
    {
        Cursor c = db.rawQuery(
                "SELECT * FROM transactions WHERE ISDEL=0 AND member = ?",
                new String[]
                { name });
        if (c.moveToNext())
        {
            return true;
        }
        c.close();
        return false;
    }

    public static void closeDb()
    {
        if (db != null)
        {
            db.close();
        }
    }
}
