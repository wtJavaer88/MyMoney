package com.wnc.mymoney.uihelper;

import android.app.Activity;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.wnc.mymoney.uihelper.listener.CtrlableHorGestureDetectorListener;
import com.wnc.mymoney.uihelper.listener.CtrlableVerGestureDetectorListener;

public class MyCtrlableGestureDetector extends SimpleOnGestureListener
{
	Activity context;
	CtrlableHorGestureDetectorListener horlistener;
	CtrlableVerGestureDetectorListener verlistener;
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
	public MyCtrlableGestureDetector(double scaleX, double scaleY, Activity context)
	{
		this(context);
		setScaleX(scaleX);
		setScaleY(scaleY);
	}

	public MyCtrlableGestureDetector(double scaleX, double scaleY, Activity context, CtrlableHorGestureDetectorListener listener, CtrlableVerGestureDetectorListener listener2)
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

	public MyCtrlableGestureDetector(Activity context)
	{
		this.context = context;
		if (context instanceof CtrlableHorGestureDetectorListener)
		{
			this.horlistener = (CtrlableHorGestureDetectorListener) context;
		}
		if (context instanceof CtrlableVerGestureDetectorListener)
		{
			this.verlistener = (CtrlableVerGestureDetectorListener) context;
		}
	}

	/**
	 * 返回false,表示其他组件的 点击方法暂时失效
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{
		if (this.context == null)
		{
			return false;
		}
		float deltaX = e2.getX() - e1.getX();
		float deltaY = e2.getY() - e1.getY();
		// 超过给定的比例才滑屏
		if (scaleX > 0 && Math.abs(deltaX) > scaleX * this.context.getWindowManager().getDefaultDisplay().getWidth())
		{
			if (this.horlistener != null)
			{
				if (deltaX > 0)
				{
					// System.out.println("left");
					this.horlistener.doLeft(new FlingPoint(e1.getX(), e1.getY()), new FlingPoint(e2.getX(), e2.getY()));
				}
				else if (deltaX < 0)
				{
					// System.out.println("right");
					this.horlistener.doRight(new FlingPoint(e1.getX(), e1.getY()), new FlingPoint(e2.getX(), e2.getY()));
				}
			}
		}

		// 超过给定的比例才滑屏
		if (scaleY > 0 && Math.abs(deltaY) > scaleY * this.context.getWindowManager().getDefaultDisplay().getHeight())
		{
			if (this.verlistener != null)
			{
				if (deltaY > 0)
				{
					this.verlistener.doUp(new FlingPoint(e1.getX(), e1.getY()), new FlingPoint(e2.getX(), e2.getY()));
				}
				else if (deltaY < 0)
				{
					this.verlistener.doDown(new FlingPoint(e1.getX(), e1.getY()), new FlingPoint(e2.getX(), e2.getY()));
				}
			}
		}

		return false;
	}
}