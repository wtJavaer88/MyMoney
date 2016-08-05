package com.wnc.mymoney.util.app;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ShareUtil
{
    public static void shareFile(Context activity, String file)
    {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(file)));

        share.setType("*/*");// 此处可发送多种文件
        activity.startActivity(Intent.createChooser(share, "Share"));
    }

}
