package com.wnc.srt;

import java.io.File;

import com.wnc.basic.BasicFileUtil;

public class SrtTextHelper
{
    public static String getClearText(String text)
    {
        return text.replaceAll("\\{.*?\\}", "").replaceAll("<.*?>", "");
    }

    /**
     * 根据当前字幕文件获取字幕同名的文件夹
     * 
     * @param srtFilePath
     * @return
     */
    public static String getSrtVoiceFolder(String srtFilePath)
    {
        return BasicFileUtil.getFileParent(srtFilePath) + File.separator
                + getFileNameNoExtend(srtFilePath);
    }

    public static String getSrtVoiceLocation(String srtFilePath, SrtInfo srtInfo)
    {
        return getSrtVoiceFolder(srtFilePath) + File.separator
                + srtInfo.getFromTime().toString().replace(":", "") + ".mp3";
    }

    public static String getFileNameNoExtend(String filePath)
    {
        int dotPos = filePath.lastIndexOf(".");
        int separatorPos = filePath.lastIndexOf(File.separator);
        if (dotPos != -1)
        {
            if (separatorPos != -1)
            {
                return filePath.substring(separatorPos + 1, dotPos);
            }
            else
            {
                return filePath.substring(0, dotPos);
            }
        }
        else
        {
            if (separatorPos != -1)
            {
                return filePath.substring(separatorPos + 1);
            }

        }
        return filePath;
    }
}
