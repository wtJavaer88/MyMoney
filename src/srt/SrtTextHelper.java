package srt;

import java.io.File;

import com.wnc.basic.BasicFileUtil;
import com.wnc.mymoney.util.common.TextFormatUtil;

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
                + TextFormatUtil.getFileNameNoExtend(srtFilePath);
    }

    public static String getSrtVoiceLocation(String srtFilePath, SrtInfo srtInfo)
    {
        return getSrtVoiceFolder(srtFilePath) + File.separator
                + srtInfo.getFromTime().toString().replace(":", "") + ".mp3";
    }

}
