package com.wnc.mymoney.bean;

import java.io.Serializable;

public class DayRangePoint implements Serializable
{
    private String startDay;
    private String endDay;

    public DayRangePoint(String s, String e)
    {
        if (s == null || e == null || s.length() != 8 || e.length() != 8)
        {
            throw new RuntimeException("起止日期参数不合法!");
        }
        setStartDay(s);
        setEndDay(e);
    }

    public String getStartDay()
    {
        return this.startDay;
    }

    public void setStartDay(String startDay)
    {
        this.startDay = startDay;
    }

    public String getEndDay()
    {
        return this.endDay;
    }

    public void setEndDay(String endDay)
    {
        this.endDay = endDay;
    }

    @Override
    public String toString()
    {
        return this.startDay + " - " + this.endDay;
    }
}
