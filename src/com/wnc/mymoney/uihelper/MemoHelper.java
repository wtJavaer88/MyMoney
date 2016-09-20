package com.wnc.mymoney.uihelper;

import com.wnc.mymoney.bean.Trade;
import com.wnc.mymoney.util.common.FileTypeUtil;

public class MemoHelper
{
    /**
     * 将媒体资源统一改成数字提示,而不是文件名直接显示.
     * 
     * @param trade
     * @return
     */
    public static String concatMemo(Trade trade)
    {
        String ret = "";
        String other = "";
        final int Len = 5;
        String time = trade.getCreatetime().substring(9, 9 + Len);// 取时间的时和分
        int picCount = 0;
        int voiceCount = 0;
        int videoCount = 0;
        for (String res : trade.getMemo().split("[\\[\\]]"))
        {
            res = res.replace("[", "").replace("]", "");
            if (FileTypeUtil.isPicFile(res))
            {
                picCount++;
            }
            else if (FileTypeUtil.isVoiceFile(res))
            {
                voiceCount++;
            }
            else if (FileTypeUtil.isVideoFile(res))
            {
                videoCount++;
            }
            else
            {
                other += res + " ";
            }
        }
        // 如果只有一个图片,就不显示文字提示了,也就是默认
        if (picCount > 1)
        {
            ret += picCount + "图片 ";
        }
        if (voiceCount > 0)
        {
            ret += voiceCount + "录音 ";
        }
        if (videoCount > 0)
        {
            ret += videoCount + "录像 ";
        }
        if (ret.length() > 1)
        {
            ret = time + " " + "<" + ret.substring(0, ret.length() - 1) + "> "
                    + other;
        }
        else
        {
            ret = time + " " + other;
        }
        return ret;
    }
}
