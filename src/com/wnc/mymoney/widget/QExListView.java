package com.wnc.mymoney.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;

public class QExListView extends ExpandableListView implements OnScrollListener
{

    @Override
    public void setAdapter(ExpandableListAdapter adapter)
    {
        // TODO Auto-generated method stub
        super.setAdapter(adapter);
    }

    private TextView _groupTv;
    public int _groupIndex = -1;
    private ExpandableListAdapter _exAdapter;

    // Context context;

    /**
     * @param context
     */
    public QExListView(Context context)
    {
        super(context);
        super.setOnScrollListener(this);

    }

    /**
     * @param context
     * @param attrs
     */
    public QExListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        super.setOnScrollListener(this);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public QExListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        super.setOnScrollListener(this);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount)
    {

        if (this._exAdapter == null)
        {
            this._exAdapter = this.getExpandableListAdapter();
        }

        int ptp = view.pointToPosition(0, 0);
        if (ptp != AdapterView.INVALID_POSITION)
        {
            QExListView qExlist = (QExListView) view;
            long pos = qExlist.getExpandableListPosition(ptp);
            int groupPos = ExpandableListView.getPackedPositionGroup(pos);
            int childPos = ExpandableListView.getPackedPositionChild(pos);

            setGroup(groupPos);

            if (childPos < 0)
            {
                groupPos = -1;
            }

            // System.out.println("P:" + pos + " G:" + groupPos + " C:" +
            // childPos
            // + "      _groupIndex" + this._groupIndex);
            if (groupPos > -1)
            {
                int maxInGroup = getExpandableListAdapter().getChildrenCount(
                        groupPos);
                if (childPos == maxInGroup - 1)
                {
                    clearTipTv();
                }
                else
                {
                    if (this._groupTv != null)
                    {
                        this._groupTv.setText(getExpandableListAdapter()
                                .getGroup(groupPos).toString().substring(4)
                                .replaceFirst("0", "")
                                + "月");
                        this._groupTv.setVisibility(VISIBLE);
                    }
                }
            }

            if (groupPos < this._groupIndex)
            {

                this._groupIndex = groupPos;
                clearTipTv();
            }
            else if (groupPos > this._groupIndex)
            {
                final FrameLayout fl = (FrameLayout) getParent();
                this._groupIndex = groupPos;
                if (this._groupTv != null)
                {
                    fl.removeView(this._groupTv);
                }

                this._groupTv = new TextView(getContext());
                _groupTv.setTextColor(Color.RED);
                _groupTv.setTextSize(30);

                this._groupTv.setOnClickListener(new OnClickListener()
                {

                    private Handler _viewHandler = new Handler();

                    @Override
                    public void onClick(View v)
                    {
                        collapseGroup(QExListView.this._groupIndex);
                        this._viewHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                fl.removeView(QExListView.this._groupTv);
                                fl.addView(QExListView.this._groupTv,
                                        new LayoutParams(
                                                LayoutParams.FILL_PARENT,
                                                LayoutParams.WRAP_CONTENT));
                            }
                        });
                    }
                });

                fl.addView(this._groupTv, fl.getChildCount(), new LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            }
        }
    }

    /**
     * 添加一个方法清除提示的悬浮区
     */
    public void clearTipTv()
    {
        if (this._groupTv != null)
        {
            this._groupTv.setVisibility(GONE);// 这里设置Gone
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
    }

    int curGroup = -1;

    private void setGroup(int i)
    {
        curGroup = i;
    }

    public int getCurrentGroup()
    {
        return curGroup;
    }

}
