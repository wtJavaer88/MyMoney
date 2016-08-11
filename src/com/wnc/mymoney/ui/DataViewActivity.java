package com.wnc.mymoney.ui;

import android.widget.ListView;

public abstract class DataViewActivity extends BaseActivity
{
    public static final int MODIFY_REQUEST_CODE = 1001;

    public abstract void delTrigger(ListView arg0, int arg2);

    public abstract void modifyTrigger();
}
