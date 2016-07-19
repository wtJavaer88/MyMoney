package com.wnc.srt;

public class TimeHelper
{
    public static long getTime(int h, int m, int s, int mill)
    {
        return 1000L * (3600 * h + 60 * m + s) + mill;
    }
}
