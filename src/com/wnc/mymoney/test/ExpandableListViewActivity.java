package com.wnc.mymoney.test;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;

import com.wnc.mymoney.R;
import com.wnc.mymoney.ui.helper.MyExpandableListAdapter;
import com.wnc.mymoney.ui.widget.QExListView;

public class ExpandableListViewActivity extends Activity
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

        ExpandableListAdapter adapter = new MyExpandableListAdapter(this);
        FrameLayout layout = (FrameLayout) findViewById(R.id.list_layout);
        ExpandableListView expandListView = new QExListView(this);
        expandListView.setAdapter(adapter);
        layout.addView(expandListView);
    }
}
