package com.wnc.srt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHolder
{
    public static String fileKey = "";
    public static int srtIndex = -1;// 正常的浏览从0开始

    public static Map<String, List<SrtInfo>> map = new HashMap<String, List<SrtInfo>>();
    public static Map<String, Integer> indexMap = new HashMap<String, Integer>();

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

    public static SrtInfo getFirst()
    {
        srtIndex = 0;
        return getSrtByIndex();
    }

    public static SrtInfo getCurrent()
    {
        return getSrtByIndex();
    }

    public static SrtInfo getLast()
    {
        checkExist();
        List<SrtInfo> list = map.get(fileKey);
        srtIndex = list.size() - 1;
        return getSrtByIndex();
    }

    private static SrtInfo getSrtByIndex()
    {
        checkExist();
        indexMap.put(fileKey, srtIndex);
        List<SrtInfo> list = map.get(fileKey);
        if (srtIndex == -1)
        {
            srtIndex = 0;
            indexMap.put(fileKey, srtIndex);
            throw new RuntimeException("下标越界!");
        }
        if (srtIndex == list.size())
        {
            srtIndex = list.size() - 1;
            indexMap.put(fileKey, srtIndex);
            throw new RuntimeException("下标越界!");
        }
        return list.get(srtIndex);
    }

    public static void switchFile(String file)
    {
        fileKey = file;
        srtIndex = indexMap.get(fileKey) == null ? -1 : indexMap.get(fileKey);
    }

    private static void checkExist()
    {
        if (!map.containsKey(fileKey))
        {
            throw new RuntimeException("找不到该文件的字幕!");
        }
    }

    public static SrtInfo getClosestSrt(int hour, int minute, int second)
    {
        checkExist();
        long l = TimeHelper.getTime(hour, minute, second, 0);
        List<SrtInfo> list = map.get(fileKey);
        SrtInfo srtInfo = null;
        for (int i = 0; i < list.size(); i++)
        {
            SrtInfo info = list.get(i);
            if (formatSeconds(info.getFromTime()) >= l
                    || formatSeconds(info.getToTime()) >= l)
            {
                srtInfo = info;
                srtIndex = i;
                break;
            }
        }
        // 返回最后一个
        if (srtInfo == null)
        {
            srtInfo = list.get(list.size() - 1);
            srtIndex = list.size() - 1;
        }
        return srtInfo;
    }

    private static long formatSeconds(TimeInfo timeInfo)
    {
        return TimeHelper.getTime(timeInfo.getHour(), timeInfo.getMinute(),
                timeInfo.getSecond(), timeInfo.getMillSecond());
    }

    public static void appendData(String srtFile, List<SrtInfo> srtInfos)
    {
        map.put(srtFile, srtInfos);
        switchFile(srtFile);
    }

    public static void setFileKey(String srtFile)
    {
        fileKey = srtFile;
    }
}
