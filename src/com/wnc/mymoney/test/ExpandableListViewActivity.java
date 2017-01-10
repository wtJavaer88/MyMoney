package com.wnc.mymoney.test;

import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.wnc.mymoney.R;
import com.wnc.mymoney.ui.DataViewActivity;
import com.wnc.mymoney.uihelper.MyExpandableListAdapter;
import com.wnc.mymoney.util.enums.TOTAL_RANGE;
import com.wnc.mymoney.widget.QExListView;

public class ExpandableListViewActivity extends DataViewActivity
{
    /**
     * ----------定义数组--------------------------------------------------
     * -----------------
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expand_listview);

        ExpandableListAdapter adapter = new MyExpandableListAdapter(this,
                TOTAL_RANGE.CURRYEAR.create());
        FrameLayout layout = (FrameLayout) findViewById(R.id.list_layout);
        ExpandableListView expandListView = new QExListView(this);
        expandListView.setAdapter(adapter);
        layout.addView(expandListView);
    }

    @Override
    public void delTrigger(ListView arg0, int arg2)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void modifyTrigger()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh(int position)
    {
        // TODO Auto-generated method stub

    }
}
