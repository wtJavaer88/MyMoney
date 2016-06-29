package com.wnc.mymoney.util;

import java.util.Date;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.bean.DayRangePoint;

public class DayRangePointUtil
{
    private static String currentDayStr;
    private static final String yearStr;
    private static final String monthStr;
    private static final String dayStr;
    static
    {
        currentDayStr = BasicDateUtil.getCurrentDateString();
        yearStr = (currentDayStr.substring(0, 4));
        monthStr = (currentDayStr.substring(4, 6));
        dayStr = (currentDayStr.substring(6, 8));
    }

    public static void main(String[] args)
    {
        System.out.println(getCurrentYearPoint());
        System.out.println(getCurrentMonthPoint());
        System.out.println(getCurrentWeekPoint());
        System.out.println(getCurrentDayPoint());
        System.out.println(getLastYearPoint());
        System.out.println(getLastMonthPoint());
        System.out.println(getLastWeekPoint());

        System.out.println("------分割线---");
        DayRangePoint p = getCurrentWeekPoint();
        System.out.println(getPreWeekPointFromCurr(p.getStartDay()));
        System.out.println(getNextWeekPointFromCurr(p.getEndDay()));

        p = getCurrentMonthPoint();
        System.out.println(getPreMonthPointFromCurr(p.getStartDay()));
        System.out.println(getNextMonthPointFromCurr(p.getEndDay()));

        p = getCurrentDayPoint();
        System.out.println(getPreDayPointFromCurr(p.getStartDay()));
        System.out.println(getNextDayPointFromCurr(p.getEndDay()));

    }

    public static DayRangePoint getCurrentYearPoint()
    {
        return new DayRangePoint(yearStr + "0101", yearStr + "1231");
    }

    public static DayRangePoint getPreYearPointFromCurr(String yearStartDay)
    {
        int year = Integer.parseInt((yearStartDay.substring(0, 4))) - 1;
        return new DayRangePoint(year + "0101", year + "1231");
    }

    public static DayRangePoint getNextYearPointFromCurr(String yearEndDay)
    {
        int year = Integer.parseInt((yearEndDay.substring(0, 4))) + 1;
        return new DayRangePoint(year + "0101", year + "1231");
    }

    public static DayRangePoint getCurrentMonthPoint()
    {
        return new DayRangePoint(yearStr + monthStr + "01", currentDayStr);
    }

    public static DayRangePoint getPreMonthPointFromCurr(String monthStartDay)
    {
        int year = Integer.parseInt((monthStartDay.substring(0, 4)));
        int month = Integer.parseInt((monthStartDay.substring(4, 6)));
        year = (month == 1) ? year - 1 : year;
        month = (month == 1) ? 12 : month - 1;
        return getMonthDayRange(year, month);
    }

    public static DayRangePoint getNextMonthPointFromCurr(String monthEndDay)
    {
        int year = Integer.parseInt((monthEndDay.substring(0, 4)));
        int month = Integer.parseInt((monthEndDay.substring(4, 6)));
        year = (month == 12) ? year + 1 : year;
        month = (month == 12) ? 1 : month + 1;
        return getMonthDayRange(year, month);
    }

    private static DayRangePoint getMonthDayRange(int year, int month)
    {
        String y = BasicStringUtil.fillLeftStringNotruncate(year + "", 4, "0");
        String m = BasicStringUtil.fillLeftStringNotruncate(month + "", 2, "0");
        int maxDayOfMonth = BasicDateUtil.getDateDayOfMonth(y + m + "01");
        return new DayRangePoint(y + m + "01", y + m + maxDayOfMonth);
    }

    public static DayRangePoint getPreWeekPointFromCurr(String weekStartDay)
    {
        return new DayRangePoint(BasicDateUtil.getDateBeforeDayDateString(
                weekStartDay, 7), BasicDateUtil.getDateBeforeDayDateString(
                weekStartDay, 1));
    }

    public static DayRangePoint getNextWeekPointFromCurr(String weekEndDay)
    {
        return new DayRangePoint(BasicDateUtil.getDateAfterDayDateString(
                weekEndDay, 1), BasicDateUtil.getDateAfterDayDateString(
                weekEndDay, 7));
    }

    public static DayRangePoint getCurrentWeekPoint()
    {
        int week = new Date().getDay();
        int skip = (week == 0) ? 6 : week - 1;
        String monday = BasicDateUtil.getDateBeforeDayDateString(currentDayStr,
                skip);
        return new DayRangePoint(monday, currentDayStr);
    }

    public static DayRangePoint getCurrentDayPoint()
    {
        return new DayRangePoint(currentDayStr, currentDayStr);
    }

    public static DayRangePoint getPreDayPointFromCurr(String dayStartDay)
    {
        String pre = BasicDateUtil.getDateBeforeDayDateString(dayStartDay, 1);
        return new DayRangePoint(pre, pre);
    }

    public static DayRangePoint getNextDayPointFromCurr(String dayEndDay)
    {
        String next = BasicDateUtil.getDateAfterDayDateString(dayEndDay, 1);
        return new DayRangePoint(next, next);
    }

    public static DayRangePoint getLastYearPoint()
    {
        return new DayRangePoint(BasicDateUtil.getDateBeforeDayDateString(
                currentDayStr, 365), currentDayStr);
    }

    public static DayRangePoint getLastMonthPoint()
    {
        return new DayRangePoint(BasicDateUtil.getDateBeforeDayDateString(
                currentDayStr, 30), currentDayStr);
    }

    public static DayRangePoint getLastWeekPoint()
    {
        return new DayRangePoint(BasicDateUtil.getDateBeforeDayDateString(
                currentDayStr, 6), currentDayStr);
    }
}
