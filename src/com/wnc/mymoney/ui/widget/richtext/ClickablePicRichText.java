package com.wnc.mymoney.ui.widget.richtext;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;

import com.wnc.mymoney.util.BitmapHelper;
import com.wnc.mymoney.util.MyAppParams;

public class ClickablePicRichText implements RichText
{

    private String picUrl;
    private Activity activity;
    private int maxWidth = (int) (MyAppParams.getScreenWidth() * 0.9);

    public ClickablePicRichText(Activity activity, String text)
    {
        this.activity = activity;
        this.picUrl = text;
    }

    Bitmap bitmap;
    Drawable drawable;
    ImageSpan imgSpan;

    @Override
    public CharSequence getSequence()
    {
        Log.i("linkpic", "picUrl: " + this.picUrl);

        bitmap = BitmapHelper.getLocalBitmap(this.picUrl);
        if (bitmap == null)
        {
            Log.i("linkpic", "文件不存在" + this.picUrl);
            return "";
        }
        drawable = new BitmapDrawable(bitmap);
        Size size = getSuiteSize(bitmap);
        drawable.setBounds(0, 0, size.getWidth(), size.getHeight());
        imgSpan = new ImageSpan(drawable);
        SpannableString spanString = new SpannableString(" icon ");

        // 留出两个空格, 两侧都可以点击打开
        spanString.setSpan(new Clickable(activity, picUrl), 0, 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(imgSpan, 1, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new Clickable(activity, picUrl), 5, 6,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public void clearBitmap()
    {
        if (bitmap != null && !bitmap.isRecycled())
        {
            System.out.println("清空Bitmap");
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    private Size getSuiteSize(Bitmap b)
    {

        Size size = new Size(b.getWidth(), b.getHeight());
        System.out.println("before: " + size);
        if (size.getWidth() > maxWidth)
        {
            size.setHeight(size.getHeight() * maxWidth / size.getWidth());
            size.setWidth(maxWidth);
        }
        System.out.println("after: " + size);
        return size;
    }

}

class Size
{
    public int getWidth()
    {
        return this.width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    private int width;
    private int height;

    public Size(int w, int h)
    {
        this.width = w;
        this.height = h;
    }

    @Override
    public String toString()
    {
        return "Size: " + this.width + "," + this.height;
    }
}
