package com.wnc.srt;

public class TimeInfo
{
    private int hour;
    private int minute;
    private int second;
    private int millSecond;

    public int getHour()
    {
        return hour;
    }

    public void setHour(int hour)
    {
        this.hour = hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public void setMinute(int minute)
    {
        this.minute = minute;
    }

    public int getSecond()
    {
        return second;
    }

    public void setSecond(int second)
    {
        this.second = second;
    }

    public int getMillSecond()
    {
        return millSecond;
    }

    public void setMillSecond(int millSecond)
    {
        this.millSecond = millSecond;
    }

    @Override
    public String toString()
    {
        return "TimeInfo [hour=" + hour + ", minute=" + minute + ", second="
                + second + ", millSecond=" + millSecond + "]";
    }
}
