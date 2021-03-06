package com.wnc.mymoney.richtext;

import android.content.Intent;

import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.util.app.MyIntentUtil;
import com.wnc.mymoney.util.common.FileTypeUtil;

public class ClickFileIntentFactory
{
    public static Intent getIntentByFile(String filePath)
    {
        if (BasicStringUtil.isNullString(filePath))
        {
            throw new IllegalArgumentException("参数不能为空!");
        }

        if (FileTypeUtil.isTextFile(filePath))
        {
            return MyIntentUtil.getLocalTextFileIntent(filePath);
        }
        else if (FileTypeUtil.isPicFile(filePath))
        {
            return MyIntentUtil.getImageFileIntent(filePath);
        }
        else if (filePath.endsWith(".doc") || filePath.endsWith(".docx"))
        {
            return MyIntentUtil.getWordFileIntent(filePath);
        }
        else if (filePath.endsWith(".xls") || filePath.endsWith(".xlsx"))
        {
            return MyIntentUtil.getExcelFileIntent(filePath);
        }
        else if (filePath.endsWith(".pdf"))
        {
            return MyIntentUtil.getPdfFileIntent(filePath);
        }
        else if (FileTypeUtil.isVoiceFile(filePath))
        {
            return MyIntentUtil.getAudioFileIntent(filePath);
        }
        else if (FileTypeUtil.isVideoFile(filePath))
        {
            return MyIntentUtil.getVideoFileIntent(filePath);
        }

        throw new IllegalArgumentException(filePath + "该文件类型找不到对应的Intent");
    }

}
