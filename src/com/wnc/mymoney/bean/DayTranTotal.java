package com.wnc.mymoney.bean;

import android.util.Log;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicStringUtil;

public class DayTranTotal
{
    private String day;
    private String week;
    private double inbound;
    private double outbound;
    private double balance;
    private String searchDate;

    public String getDay()
    {
        return this.day;
    }

    public void setDay(String day)
    {
        this.day = day;
    }

    public String getWeek()
    {
        return this.week;
    }

    public void setWeek(String week)
    {
        this.week = week;
    }

    public double getInbound()
    {
        return this.inbound;
    }

    public void setInbound(double inbound)
    {
        this.inbound = inbound;
    }

    public double getOutbound()
    {
        return this.outbound;
    }

    public void setOutbound(double outbound)
    {
        this.outbound = outbound;
    }

    public double getBalance()
    {
        return this.balance;
    }

    public void setBalance(double balance)
    {
        this.balance = balance;
    }

    public String getSearchDate()
    {
        return this.searchDate;
    }

    public void setSearchDate(String searchDate)
    {
        this.searchDate = searchDate;
    }

    @Override
    public String toString()
    {
        return "日期:" + this.searchDate + " 支出:" + this.outbound + " 收入:"
                + this.inbound;
    }

    private final String[] weeks =
    { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };

    public void setOtherParam()
    {
        if (BasicStringUtil.isNotNullString(this.searchDate)
                && this.searchDate.length() == 8)
        {
            this.day = this.searchDate.substring(6);
            this.balance = this.inbound - this.outbound;
            this.week = this.weeks[BasicDateUtil
                    .getWeekDayFromDateString(this.searchDate) % 7];
        }
        else
        {
            Log.i("err", "日期不合法!__ " + this.searchDate);
        }
    }
}
