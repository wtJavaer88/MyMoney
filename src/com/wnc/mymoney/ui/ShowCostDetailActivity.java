package com.wnc.mymoney.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.wnc.mymoney.R;
import com.wnc.mymoney.bean.DayRangePoint;
import com.wnc.mymoney.bean.DayTranTotal;
import com.wnc.mymoney.dao.TransactionsDao;
import com.wnc.mymoney.uihelper.MyListViewAdapter;
import com.wnc.mymoney.util.enums.CostTypeUtil;

public class ShowCostDetailActivity extends DataViewActivity implements
        View.OnClickListener
{

    private ListView search_expense_lv;

    DayRangePoint dayRange;
    int costLevel = 1;
    String summary = "";

    ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.chart_transaction_activity);
        this.search_expense_lv = (ListView) findViewById(R.id.search_expense_lv);

        initIntentParameters();

        pieBtnCtrl();
        initSummary();
        initData();
    }

    private void initIntentParameters()
    {
        Intent localIntent = getIntent();

        this.dayRange = (DayRangePoint) localIntent
                .getSerializableExtra("dayrange");
        this.costLevel = localIntent.getIntExtra("costlevel", 1);
        this.summary = localIntent.getStringExtra("summary");
        System.out.println("dayRange:" + this.dayRange);
    }

    private void pieBtnCtrl()
    {
        if (CostTypeUtil.isDescType(costLevel))
        {
            findViewById(R.id.search_pie_btn).setVisibility(View.INVISIBLE);
        }
        else
        {
            findViewById(R.id.search_pie_btn).setOnClickListener(this);
        }
    }

    private void initSummary()
    {
        ((TextView) findViewById(R.id.summary_tv)).setText(summary);
    }

    /**
     * 实现数据的刷新,可做外部接口
     */
    public void initData()
    {
        TransactionsDao.initDb(this);
        this.mData.clear();
        List<DayTranTotal> totals = TransactionsDao.getAllTranTotalForChart(
                costLevel, this.dayRange.getStartDay(),
                this.dayRange.getEndDay());
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
        }
        MyListViewAdapter adapter = new MyListViewAdapter(this, this.mData,
                costLevel);
        this.search_expense_lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        TransactionsDao.closeDb();
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.search_pie_btn)
        {
            toPieChart();
        }
    }

    private void toPieChart()
    {
        startActivity(new Intent(this, MonthPieChartActivity.class).putExtra(
                "costlevel", costLevel).putExtra("dayrange", dayRange));
    }

    @Override
    public void delTrigger(ListView arg0, int arg2)
    {
        initData();
    }

    @Override
    public void modifyTrigger()
    {
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果经过修改
        if (requestCode == DataViewActivity.MODIFY_REQUEST_CODE)
        {
            modifyTrigger();
        }
    }
}