package com.wnc.mymoney.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.mymoney.bean.FestivalDay;
import com.wnc.string.PatternUtil;

public class FestivalUtil
{
    private static List<FestivalDay> list = new ArrayList<FestivalDay>();

    public static List<FestivalDay> getFestivalDays()
    {
        return list;
    }

    public static void init(Activity activity)
    {
        for (int year = 2016; year <= 2016; year++)
        {
            List<String> readFrom = AssertUtil.getContent(activity, year
                    + "-festival.txt", "GBK");
            for (String line : readFrom)
            {
                String[] arr = line.split("[    \t ]");
                // System.out.println("安排:" + arr[1] + " " + arr[3]);
                fangjia(year, arr);
                tiaoxiu(year, arr);
                System.out.println();
            }
        }
    }

    private static void tiaoxiu(int year, String[] arr)
    {
        List<String> patternStrings = PatternUtil.getPatternStrings(arr[2],
                "\\d+");
        int size = patternStrings.size();
        if (size > 0 && size % 2 == 0)
        {
            // System.out.println(arr[0] + "调休:");
            for (int i = 0; i < size; i += 2)
            {
                FestivalDay festivalDay = new FestivalDay();
                festivalDay.setFestival(arr[0]);
                festivalDay.setDate(year + align(patternStrings.get(i)) + ""
                        + align(patternStrings.get(i + 1)));
                festivalDay.setFree(false);
                festivalDay.setYear(year);
                // System.out.println(festivalDay.toString());
                list.add(festivalDay);
            }
            // System.out.println(arr[0] + "调休结束");
        }
    }

    private static void fangjia(int year, String[] arr)
    {
        // System.out.println(arr[0] + "开始放假:");
        String fesBeginDay = getFesBeginDay(year,
                PatternUtil.getFirstPattern(arr[1], ".*?~"));
        int count = BasicNumberUtil.getNumber(PatternUtil.getFirstPattern(
                arr[3], "\\d+"));
        int i = 0;
        // 防止死循环
        while (i < count && count < 365)
        {
            String tmp = BasicDateUtil
                    .getDateAfterDayDateString(fesBeginDay, i);
            FestivalDay festivalDay = new FestivalDay();
            festivalDay.setFestival(arr[0]);
            festivalDay.setDate(tmp);
            festivalDay.setFree(true);
            festivalDay.setYear(year);
            // System.out.println(festivalDay.toString());
            list.add(festivalDay);
            i++;
        }
        // System.out.println(arr[0] + "放假结束");
    }

    private static String getFesBeginDay(int year, String string)
    {
        String month = PatternUtil.getFirstPattern(string, "\\d+");
        String day = PatternUtil.getLastPattern(string, "\\d+");
        month = align(month);
        day = align(day);
        return year + month + day;
    }

    private static String align(String number)
    {
        return number.length() == 1 ? "0" + number : number;
    }

    public static String getFesStr(String spendDay)
    {
        String ret = "";
        for (FestivalDay festivalDay : list)
        {
            if (spendDay.equals(festivalDay.getDate()))
            {
                ret = festivalDay.isFree() ? (festivalDay.getFestival() + "休假")
                        : (festivalDay.getFestival() + "调休");
            }
        }
        return ret;
    }
}
