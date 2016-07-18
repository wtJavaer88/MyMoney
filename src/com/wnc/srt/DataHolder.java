package com.wnc.srt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHolder
{
    public static String fileKey = "";
    public static int srtIndex = -1;// 正常的浏览从0开始

    public static Map<String, List<SrtInfo>> map = new HashMap<String, List<SrtInfo>>();

    public static SrtInfo getNext()
    {
        srtIndex++;
        return getSrtByIndex();
    }

    public static SrtInfo getPre()
    {
        srtIndex--;
        return getSrtByIndex();
    }

    private static SrtInfo getSrtByIndex()
    {
        if (!map.containsKey(fileKey))
        {
            throw new RuntimeException("找不到该文件的字幕!");
        }
        List<SrtInfo> list = map.get(fileKey);
        if (srtIndex == -1)
        {
            srtIndex = 0;
            throw new RuntimeException("下标越界!");
        }
        if (srtIndex == list.size())
        {
            srtIndex = list.size() - 1;
            throw new RuntimeException("下标越界!");
        }
        return list.get(srtIndex);
    }

    public static SrtInfo getClosestSrt(int hour, int minute, int second)
    {
        if (map.containsKey(fileKey))
        {
            long l = hour * 3600 + minute * 60 + second;
            List<SrtInfo> list = map.get(fileKey);
            for (SrtInfo info : list)
            {
                if (formatSeconds(info.getFromTime()) >= l
                        || formatSeconds(info.getToTime()) >= l)
                {
                    setSrtIndex(info);
                    return info;
                }
            }
            // 返回最后一个
            SrtInfo srtInfo = list.get(list.size() - 1);
            setSrtIndex(srtInfo);
            return srtInfo;
        }
        throw new RuntimeException("找不到该文件的字幕!");
    }

    private static void setSrtIndex(SrtInfo srtInfo)
    {
        srtIndex = srtInfo.getSrtIndex() - 1;
    }

    private static long formatSeconds(TimeInfo fromTime)
    {
        return fromTime.getHour() * 3600 + fromTime.getMinute() * 60
                + fromTime.getSecond();
    }

    public static void appendData(String srtFile, List<SrtInfo> srtInfos)
    {
        map.put(srtFile, srtInfos);
        fileKey = srtFile;
        srtIndex = -1;
    }
}
