package com.wnc.mymoney.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wnc.basic.BasicDateUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.bean.DayRangePoint;
import com.wnc.mymoney.bean.DayTranTotal;
import com.wnc.mymoney.dao.TransactionsDao;
import com.wnc.mymoney.ui.helper.HorGestureDetectorListener;
import com.wnc.mymoney.ui.helper.MyExpandableListAdapter;
import com.wnc.mymoney.ui.helper.MyHorizontalGestureDetector;
import com.wnc.mymoney.ui.helper.MyListViewAdapter;
import com.wnc.mymoney.ui.widget.QExListView;
import com.wnc.mymoney.util.TOTAL_RANGE;
import com.wnc.mymoney.util.TextFormatUtil;

/**
 * 通过接受参数, 查找指定区间内的消费量
 * <p>
 * 本年: 20160101 - 20161231
 * <p>
 * 本月: 20160401 - 20160431
 * </p>
 * 本周: 20160425 - 20160427
 * <p>
 * 本日: 20160427 - 20160427
 * <p>
 * 近一周:20160425 - 20160427
 * <p>
 * 近一月:20160328 - 20160427
 * <p>
 * 近一年:20150428 - 20160427
 * 
 * @author cpr216
 * 
 */
public class NavTransactionActivity extends BaseActivity implements
        View.OnClickListener, HorGestureDetectorListener
{
    private GestureDetector gestureDetector;
    private TextView titleTv;
    private TextView dateRangeTv;

    private Button preBt;
    private Button nextBt;
    private ListView expanseLV;
    private EditText searchHint;
    private TextView inboundTV;
    private TextView outboundTV;
    private TextView balanceTV;
    private View defaultTipView;

    TOTAL_RANGE TOTAL_RANGE_TYPE;
    DayRangePoint dayRange;

    View expandHeader1;
    View expandHeader2;
    View expandHeader3;

    ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.nav_trans_activity);

        initDateRange();

        this.gestureDetector = new GestureDetector(this,
                new MyHorizontalGestureDetector(0.33, this));

        initViews();
        initData();
        initTitle();
    }

    private void initDateRange()
    {
        Intent localIntent = getIntent();
        String mode = localIntent.getStringExtra("mode");

        try
        {
            this.TOTAL_RANGE_TYPE = TOTAL_RANGE.valueOf(mode);
        }
        catch (Exception ex)
        {
            // 默认转到当前日
            this.TOTAL_RANGE_TYPE = TOTAL_RANGE.CURRDAY;
        }
        this.dayRange = this.TOTAL_RANGE_TYPE.create();
    }

    private void initViews()
    {
        this.titleTv = ((TextView) findViewById(R.id.title_tv));
        this.dateRangeTv = ((TextView) findViewById(R.id.nav_trans_header_date_range_tv));
        findViewById(R.id.titlebar_right_btn).setVisibility(View.INVISIBLE);
        findViewById(R.id.listview_loading_tv).setVisibility(View.INVISIBLE);

        this.preBt = ((Button) findViewById(R.id.pre_btn));
        this.nextBt = ((Button) findViewById(R.id.next_btn));
        this.preBt.setOnClickListener(this);
        this.nextBt.setOnClickListener(this);
        findViewById(R.id.back_btn).setOnClickListener(this);

        this.defaultTipView = findViewById(R.id.lv_empty_lvet);

        initListView();
    }

    /**
     * 同时也是提供给外部的一个接口
     */
    public void initData()
    {
        TransactionsDao.initDb(this);
        this.mData.clear();
        List<DayTranTotal> totals = TransactionsDao.getAllTranTotalInRange(
                this.dayRange.getStartDay(), this.dayRange.getEndDay());
        double balanceSum = 0;
        double inboundSum = 0;
        double outboundSum = 0;
        for (DayTranTotal total : totals)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("searchDate", total.getSearchDate());
            map.put("day", total.getDay());
            map.put("week", total.getWeek());
            map.put("in", total.getInbound());
            map.put("out", total.getOutbound());
            map.put("balance", total.getBalance());
            this.mData.add(map);

            inboundSum += total.getInbound();
            outboundSum += total.getOutbound();
            balanceSum += total.getBalance();
        }

        if (this.mData.size() > 0)
        {
            this.defaultTipView.setVisibility(View.GONE);
            this.expanseLV.setVisibility(View.VISIBLE);
            if (this.TOTAL_RANGE_TYPE == TOTAL_RANGE.CURRYEAR)
            {
                ((ExpandableListView) this.expanseLV)
                        .setAdapter(new MyExpandableListAdapter(this));
            }
            else
            {
                expanseLV.setAdapter(new MyListViewAdapter(this, this.mData));
            }
        }
        else
        {
            this.defaultTipView.setVisibility(View.VISIBLE);
            this.expanseLV.setVisibility(View.GONE);
        }
        this.balanceTV.setText(TextFormatUtil.getFormatMoneyStr(balanceSum));
        this.inboundTV.setText(TextFormatUtil.getFormatMoneyStr(inboundSum));
        this.outboundTV.setText(TextFormatUtil.getFormatMoneyStr(outboundSum));
        TransactionsDao.closeDb();
    }

    private void initTitle()
    {
        // title_tv
        // nav_trans_header_date_range_tv

        String bday = this.dayRange.getStartDay();
        String eday = this.dayRange.getEndDay();
        String curday = BasicDateUtil.getCurrentDateString();
        if (eday.compareTo(curday) > 0)
        {
            eday = curday;
        }

        this.titleTv.setGravity(81);
        dateRangeTv.setVisibility(View.VISIBLE);
        if (this.TOTAL_RANGE_TYPE == TOTAL_RANGE.CURRDAY)
        {
            this.titleTv.setText(this.TOTAL_RANGE_TYPE.getSearchHint());
            this.dateRangeTv.setText(TextFormatUtil.addChnToDayNoSeperate(bday)
                    .replace("年0", "年"));
        }
        else
        {
            if (this.TOTAL_RANGE_TYPE == TOTAL_RANGE.CURRMONTH)
            {
                dateRangeTv.setVisibility(View.INVISIBLE);
                this.titleTv.setText(TextFormatUtil.addChnToDayNoSeperate(bday)
                        .substring(0, 8).replace("年0", "年"));
            }
            else if (this.TOTAL_RANGE_TYPE == TOTAL_RANGE.CURRYEAR)
            {
                dateRangeTv.setVisibility(View.INVISIBLE);
                this.titleTv.setText(TextFormatUtil.addChnToDayNoSeperate(bday)
                        .substring(0, 5));
            }
            else if (this.TOTAL_RANGE_TYPE == TOTAL_RANGE.CURRWEEK)
            {
                this.titleTv.setText(this.TOTAL_RANGE_TYPE.getSearchHint());
                this.dateRangeTv.setText(bday + " - " + eday);
            }
        }
    }

    private void initListView()
    {
        FrameLayout listLayout = ((FrameLayout) findViewById(R.id.list_layout));
        if (this.expanseLV != null)
        {
            listLayout.removeView(this.expanseLV);
        }
        if (this.TOTAL_RANGE_TYPE == TOTAL_RANGE.CURRYEAR)
        {
            // 分组的ListView
            QExListView lv = new QExListView(this);
            lv.setGroupIndicator(null);// 取消箭头
            expanseLV = lv;
        }
        else
        {
            this.expanseLV = new ListView(this);
        }
        listLayout.addView(this.expanseLV);
        // this.expanseLV.setAdapter(null);

        LayoutInflater localLayoutInflater = getLayoutInflater();
        this.expandHeader1 = localLayoutInflater.inflate(
                R.layout.nav_trans_header_search_bar, null);

        this.expandHeader1.findViewById(R.id.search_rl)
                .setOnClickListener(this);
        this.searchHint = (EditText) this.expandHeader1
                .findViewById(R.id.search_et);
        this.searchHint.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View arg0, MotionEvent event)
            {
                int touchEvent = event.getAction();
                switch (touchEvent)
                {
                case MotionEvent.ACTION_DOWN:
                    Intent localIntent = new Intent();
                    localIntent.setClass(getApplicationContext(),
                            SearchTransactionActivity.class);
                    localIntent.putExtra("dayrange",
                            NavTransactionActivity.this.dayRange).putExtra(
                            "keyword", "");
                    ;
                    startActivity(localIntent);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                default:
                    break;
                }
                return true;
            }
        });
        this.searchHint.setHint("搜索" + this.TOTAL_RANGE_TYPE.getSearchHint()
                + "流水");

        this.expanseLV.addHeaderView(this.expandHeader1);

        this.expandHeader2 = localLayoutInflater.inflate(
                R.layout.nav_year_trans_header_conspectus, null);
        this.balanceTV = ((TextView) this.expandHeader2
                .findViewById(R.id.nav_year_trans_conspectus_balance_tv));
        this.inboundTV = ((TextView) this.expandHeader2
                .findViewById(R.id.nav_year_trans_conspectus_inbound_tv));
        this.outboundTV = ((TextView) this.expandHeader2
                .findViewById(R.id.nav_year_trans_conspectus_outbound_tv));

        this.expanseLV.addHeaderView(this.expandHeader2);

        this.expandHeader3 = localLayoutInflater.inflate(
                R.layout.nav_year_trans_header_shadow, null);
        this.expanseLV.addHeaderView(this.expandHeader3);
        this.expanseLV.setHeaderDividersEnabled(false);

    }

    @Override
    public void onClick(View v)
    {
        if (v == this.preBt)
        {
            doLeft();
        }
        else if (v == this.nextBt)
        {
            doRight();
        }
        else if (v.getId() == R.id.back_btn)
        {
            finish();
        }
    }

    @Override
    public void doLeft()
    {
        this.dayRange = this.TOTAL_RANGE_TYPE.pre(this.dayRange.getStartDay());
        reSetData();
    }

    @Override
    public void doRight()
    {
        DayRangePoint tmp = this.TOTAL_RANGE_TYPE.next(this.dayRange
                .getEndDay());
        if (tmp.getStartDay().compareTo(BasicDateUtil.getCurrentDateString()) > 0)
        {
            return;
        }
        this.dayRange = tmp;
        reSetData();
    }

    private void reSetData()
    {
        initListView();// 这个重新加载header,直接删除有bug
        initData();
        initTitle();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
    {
        if (!this.gestureDetector.onTouchEvent(paramMotionEvent))
        {
            return super.dispatchTouchEvent(paramMotionEvent);
        }
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // initData();
        // if (expanseLV instanceof QExListView)
        // {
        // int index = ((QExListView) expanseLV).getCurrentGroup();
        // if (index > -1)
        // {
        // ((QExListView) expanseLV).expandGroup(index);
        // }
        // else
        // {
        // ((QExListView) expanseLV).expandGroup(0);
        // }
        //
        // }

    }
}