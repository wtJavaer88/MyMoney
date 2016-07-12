package com.wnc.mymoney.ui.widget.richtext;

import android.app.Activity;
import android.content.Intent;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.wnc.mymoney.util.ToastUtil;

public class Clickable extends ClickableSpan implements OnClickListener
{
    private String filePath;
    private Activity activity;

    public Clickable(Activity activity, String uri)
    {
        this.activity = activity;
        this.filePath = uri;
    }

    @Override
    public void onClick(View v)
    {
        if (activity != null)
        {
            try
            {
                Log.i("linkfile click", filePath);
                // ToastUtil.showShortToast(activity,
                // "点击:" + new File(filePath).getName());
                Intent intent = ClickFileIntentFactory
                        .getIntentByFile(filePath);
                activity.startActivity(intent);
            }
            catch (Exception ex)
            {
                ToastUtil.showShortToast(activity, ex.getMessage());
                Log.e("openfile", "打开文件异常:" + ex.getMessage());
            }
        }
        else
        {
            Log.e("context", "没有设置上下文Context!");
        }
    }
}
