package com.wnc.mymoney.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wnc.mymoney.R;

public class ListViewEmptyTips extends LinearLayout
{
    private TextView a;
    private TextView b;

    public ListViewEmptyTips(Context paramContext)
    {
        this(paramContext, null);
    }

    public ListViewEmptyTips(Context paramContext,
            AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
        a(paramContext);
    }

    private void a(Context paramContext)
    {
        setOrientation(1);
        setGravity(1);
        b(paramContext);
        c(paramContext);
        d(paramContext);
    }

    private void b(Context paramContext)
    {
        View localView = new View(paramContext);
        localView.setBackgroundResource(R.drawable.common_lv_empty_tips);
        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                400, 400);
        localLayoutParams.gravity = 1;
        addView(localView, localLayoutParams);
    }

    private void c(Context paramContext)
    {
        Resources localResources = paramContext.getResources();
        this.a = new TextView(paramContext);
        this.a.setText("没有记录");
        this.a.setTypeface(Typeface.DEFAULT_BOLD);
        this.a.setTextColor(Color.parseColor("#816342"));
        this.a.setTextSize(50);
        this.a.setShadowLayer(1.0F, 0.0F, 1.0F, -2063597569);
        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                -2, -2);
        localLayoutParams.gravity = 1;
        localLayoutParams.topMargin = 140;
        addView(this.a, localLayoutParams);
    }

    private void d(Context paramContext)
    {
        Resources localResources = paramContext.getResources();
        this.b = new TextView(paramContext);
        this.b.setText("您可以进入 \"记账界面\" 填写");
        this.b.setTextColor(Color.parseColor("#615B54"));
        this.b.setTextSize(20);
        this.b.setShadowLayer(1.0F, 0.0F, 1.0F, -2063597569);
        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                -2, -2);
        localLayoutParams.gravity = 1;
        localLayoutParams.topMargin = 20;
        addView(this.b, localLayoutParams);
    }

    public TextView a()
    {
        return this.b;
    }
}