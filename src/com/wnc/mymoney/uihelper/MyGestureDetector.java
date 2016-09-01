package com.wnc.mymoney.uihelper;

import android.app.Activity;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class MyGestureDetector extends SimpleOnGestureListener
{
    Activity context;
    HorGestureDetectorListener horlistener;
    VerGestureDetectorListener verlistener;
    double scaleX = 0.5f;
    double scaleY = 0.5f;

    /**
     * 如果double参数为0则代表不执行接口的方法
     * <p>
     * 如果context参数实现了两个接口,就不用指定接口参数了
     * 
     * @param scaleX
     * @param scaleY
     * @param context
     */
    public MyGestureDetector(double scaleX, double scaleY, Activity context)
    {
        this(context);
        setScaleX(scaleX);
        setScaleY(scaleY);
    }

    boolean horCtrl = false;
    double horTop = 0;
    double horButtom = 0;

    /**
     * 将左右滑动的范围控制在一个定制的上下区间内
     * 
     * @param top
     * @param buttom
     * @return
     */
    public MyGestureDetector setEffectHorRange(double top, double buttom)
    {
        horCtrl = true;
        horTop = top;
        horButtom = buttom;
        return this;
    }

    public MyGestureDetector(double scaleX, double scaleY, Activity context,
            HorGestureDetectorListener listener,
            VerGestureDetectorListener listener2)
    {
        this(context);
        setScaleX(scaleX);
        setScaleY(scaleY);
        this.horlistener = listener;
        this.verlistener = listener2;
    }

    public void setScaleX(double scale)
    {
        if (scale > 0 && scale < 1)
        {
            this.scaleX = scale;
        }
    }

    public void setScaleY(double scale)
    {
        if (scale > 0 && scale < 1)
        {
            this.scaleY = scale;
        }
    }

    public MyGestureDetector(Activity context)
    {
        this.context = context;
        if (context instanceof HorGestureDetectorListener)
        {
            this.horlistener = (HorGestureDetectorListener) context;
        }
        if (context instanceof VerGestureDetectorListener)
        {
            this.verlistener = (VerGestureDetectorListener) context;
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
        float deltaX = e2.getX() - e1.getX();
        float deltaY = e2.getY() - e1.getY();

        postHorEvent(e2, deltaX);
        postVerEvent(deltaY);

        return false;
    }

    private void postVerEvent(float deltaY)
    {
        // 竖向 超过给定的比例才滑屏
        if (scaleY > 0
                && Math.abs(deltaY) > scaleY
                        * this.context.getWindowManager().getDefaultDisplay()
                                .getHeight())
        {
            if (this.verlistener != null)
            {
                if (deltaY > 0)
                {
                    this.verlistener.doUp();
                }
                else if (deltaY < 0)
                {
                    this.verlistener.doDown();
                }
            }
        }
    }

    private void postHorEvent(MotionEvent e2, float deltaX)
    {
        // 横向 超过给定的比例才滑屏
        if (scaleX > 0
                && Math.abs(deltaX) > scaleX
                        * this.context.getWindowManager().getDefaultDisplay()
                                .getWidth())
        {

            if (this.horlistener != null)
            {
                if (horCtrl)
                {
                    System.out.println(horTop + "  " + e2.getY() + "  "
                            + horButtom);
                    if (horTop < e2.getY() && e2.getY() < horButtom)
                    {
                        if (deltaX > 0)
                        {
                            this.horlistener.doLeft();
                        }
                        else if (deltaX < 0)
                        {
                            this.horlistener.doRight();
                        }
                    }
                }
                else
                {
                    if (deltaX > 0)
                    {
                        this.horlistener.doLeft();
                    }
                    else if (deltaX < 0)
                    {
                        this.horlistener.doRight();
                    }
                }
            }
        }
    }
}