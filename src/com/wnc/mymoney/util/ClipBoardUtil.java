package com.wnc.mymoney.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.wnc.mymoney.bean.Trade;

public class ClipBoardUtil
{
    @SuppressWarnings("deprecation")
    public static String getClipBoardContent(Context context)
    {
        ClipboardManager clipboardManager = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager.hasPrimaryClip())
        {

            return clipboardManager.getPrimaryClip().getItemAt(0).getText()
                    .toString();
        }
        System.out.println("nullssss");
        return "";
    }

    public static void setTradeContent(Context context, String text)
    {
        @SuppressWarnings("deprecation")
        ClipboardManager clipboard = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData textCd = ClipData.newPlainText("key", text);
        clipboard.setPrimaryClip(textCd);
        System.out.println("text: " + text);
        Trade trade = JSONArray.parseObject(text, Trade.class);
        System.out.println(trade);
    }

    public static void setNormalContent(Context context, String text)
    {
        @SuppressWarnings("deprecation")
        ClipboardManager clipboard = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData textCd = ClipData.newPlainText("key", text);
        clipboard.setPrimaryClip(textCd);
        System.out.println("text: " + text);
    }
}
