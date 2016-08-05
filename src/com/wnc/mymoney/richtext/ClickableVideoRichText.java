package com.wnc.mymoney.richtext;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;

public class ClickableVideoRichText implements RichText
{
    private String videoPath;
    private Activity activity;

    public ClickableVideoRichText(Activity activity, String text)
    {
        this.activity = activity;
        this.videoPath = text;
    }

    @Override
    public CharSequence getSequence()
    {
        String tip = "  录像文件  ";
        SpannableString spanableInfo = new SpannableString(tip);
        spanableInfo.setSpan(new Clickable(activity, videoPath), 0,
                tip.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanableInfo;
    }

}
