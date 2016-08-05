package com.wnc.mymoney.richtext;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;

public class ClickableVoiceRichText implements RichText
{
    private String voicePath;
    private Activity activity;

    public ClickableVoiceRichText(Activity activity, String text)
    {
        this.activity = activity;
        this.voicePath = text;
    }

    @Override
    public CharSequence getSequence()
    {
        String tip = "  录音文件  ";
        SpannableString spanableInfo = new SpannableString(tip);
        spanableInfo.setSpan(new Clickable(activity, voicePath), 0,
                tip.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanableInfo;
    }

}
