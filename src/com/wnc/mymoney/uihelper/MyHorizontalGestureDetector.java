package com.wnc.mymoney.uihelper;

import android.app.Activity;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class MyHorizontalGestureDetector extends SimpleOnGestureListener
{
    Activity context;
    HorGestureDetectorListener listener;
    double scale = 0.5f;

    public MyHorizontalGestureDetector(Activity context,
            HorGestureDetectorListener listener)
    {
        this.context = context;
        this.listener = listener;
    }

    public MyHorizontalGestureDetector(double scale, Activity context)
    {
        this(context);
        setScale(scale);
    }

    public void setScale(double scale)
    {
        if (scale > 0 && scale < 1)
        {
            this.scale = scale;
        }
    }

    public MyHorizontalGestureDetector(Activity context)
    {
        if (context instanceof HorGestureDetectorListener)
        {
            this.context = context;
            this.listener = (HorGestureDetectorListener) context;
        }
    }

    /**
     * 返回false,表示其他组件的 点击方法暂时失效
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY)
    {
        if (this.context == null)
        {
            return false;
        }
        float x = e2.getX() - e1.getX();
        // 超过三分之一才滑屏
        if (Math.abs(x) > scale
                * this.context.getWindowManager().getDefaultDisplay()
                        .getWidth())
        {
            if (x > 0)
            {
                // System.out.println("left");
                if (this.listener != null)
                {
                    this.listener.doLeft();
                }
            }
            else if (x < 0)
            {
                // System.out.println("right");
                if (this.listener != null)
                {
                    this.listener.doRight();
                }
            }
        }
        return false;
    }
}