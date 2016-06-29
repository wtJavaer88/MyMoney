package com.wnc.mymoney.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ToastUtil
{

    public static void showShortToast(Context context, Object toastMsg)
    {
        if (valid(context, toastMsg))
        {
            Toast.makeText(context, toastMsg.toString(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public static void showLongToast(Context context, Object toastMsg)
    {
        if (valid(context, toastMsg))
        {
            Toast.makeText(context, toastMsg.toString(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private static boolean valid(Context context, Object toastMsg)
    {
        Log.i("TOAST", "info:" + toastMsg);
        if (context == null)
        {
            Toast.makeText(context, "请不要传NULL的context", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        else if (toastMsg == null)
        {
            Toast.makeText(context, "请不要传NULL的msg", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
