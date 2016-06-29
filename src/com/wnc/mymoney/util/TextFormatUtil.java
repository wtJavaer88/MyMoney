package com.wnc.mymoney.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;

public class TextFormatUtil
{
    /**
     * 获取格式化的只有一位小数的数字
     * 
     * @param num
     * @return
     */
    public static String getFormatMoneyStr(double num)
    {
        return BasicNumberUtil.getDoubleFormat(num, 1);
    }

    /**
     * 获取当前的月份,省略开头的0
     * 
     * @param yearMonthStr
     * @return
     */
    public static String getMonthStr(String yearMonthStr)
    {
        if (BasicStringUtil.isNullString(yearMonthStr))
        {
            return "1";
        }
        return yearMonthStr.substring(4, 6).replaceFirst("0", "");
    }

    /**
     * 从日期格式中移除-符号,以便插入数据库
     * 
     * @param dateStr
     * @return
     */
    public static String getDateStrForDb(String dateStr)
    {
        return dateStr.replace("-", "");
    }

    /**
     * 在日期中加上分隔符-
     * 
     * @param day
     * @return
     */
    public static String addSeparatorToDay(String day)
    {
        return day.substring(0, 4) + "-" + day.substring(4, 6) + "-"
                + day.substring(6, 8);
    }

    /**
     * 在时间中加上分隔符冒号:
     * 
     * @param time
     * @return
     */
    public static String addSeparatorToTime(String time)
    {
        return time.substring(0, 2) + ":" + time.substring(2, 4) + ":"
                + time.substring(4, 6);
    }

    /**
     * 在日期中加上年月日
     * 
     * @param day
     * @return
     */
    public static String addChnToDay(String day)
    {
        return day.replaceFirst("-", "年").replaceFirst("-", "月") + "日";
    }

    /**
     * 在日期中加上年月日
     * 
     * @param day
     * @return
     */
    public static String addChnToDayNoSeperate(String day)
    {
        StringBuilder accum = new StringBuilder();
        accum.append(day.substring(0, 4));
        accum.append("年");
        accum.append(day.substring(4, 6));
        accum.append("月");
        accum.append(day.substring(6, 8));
        accum.append("日");
        return accum.toString();
    }

    /**
     * 根据指定字符串获取日期
     * 
     * @param timeStr
     * @return
     */
    public static Date getFormatedDate(String timeStr)
    {
        Date date = null;
        String format = "yyyy-MM-dd HH:mm:ss";
        if (!timeStr.contains(":"))
        {
            format = "yyyy-MM-dd";
        }
        if (!timeStr.contains("-"))
        {
            format = format.replace("-", "");
        }

        try
        {
            date = new SimpleDateFormat(format).parse(timeStr);
        }
        catch (ParseException e)
        {
            Log.i("err", "日期转换异常:" + e.getMessage());
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取某一项消费的数据格式
     * 
     * @param percent
     * @param cost
     * @param name
     * @return
     */
    public static String getPercentWithName(float percent, double cost,
            String name)
    {
        String percentStr = TextFormatUtil.getFormatMoneyStr(percent * 100);
        String costStr = TextFormatUtil.getFormatMoneyStr(cost);
        StringBuilder accumStr = new StringBuilder();
        accumStr.append(name + ":\n");
        accumStr.append(percentStr + "%  ");
        accumStr.append("¥" + costStr);
        return accumStr.toString();
    }

    public static String[] getSegmentsInMemo(String memo)
    {
        String[] list = new String[]
        {};
        if (memo != null && BasicStringUtil.isNotNullString(memo.trim()))
        {
            list = memo.split("[\\[\\]]");
        }
        return list;
    }

}