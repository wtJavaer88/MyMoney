package com.wnc.mymoney.bean;

public class FestivalDay
{
    String date;
    String festival;
    boolean isFree;
    int year;

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getFestival()
    {
        return festival;
    }

    public void setFestival(String festival)
    {
        this.festival = festival;
    }

    public boolean isFree()
    {
        return isFree;
    }

    public void setFree(boolean isFree)
    {
        this.isFree = isFree;
    }

    @Override
    public String toString()
    {
        return "FestivalDay [date=" + date + ", festival=" + festival
                + ", isFree=" + isFree + "]";
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }
}
