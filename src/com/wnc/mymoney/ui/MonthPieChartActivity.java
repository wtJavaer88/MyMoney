package com.wnc.mymoney.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.ulucu.chart.ChartProp;
import com.example.ulucu.chart.ChartPropChangeListener;
import com.example.ulucu.chart.MyButton;
import com.example.ulucu.chart.PieView;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.bean.CostChartTotal;
import com.wnc.mymoney.bean.DayRangePoint;
import com.wnc.mymoney.dao.TransactionsDao;
import com.wnc.mymoney.util.app.ToastUtil;
import com.wnc.mymoney.util.common.TextFormatUtil;
import com.wnc.mymoney.util.enums.CostTypeUtil;
import com.wnc.mymoney.util.enums.TOTAL_RANGE;

public class MonthPieChartActivity extends BaseActivity implements
        OnClickListener
{
    private PieView pieView;
    private MyButton myButton;
    private Button preBt;
    private Button nextBt;
    private TextView segmentInfoView;
    private TextView titleTv;

    private int CHART_COST_TYPE = -1;// 默认展示所有的支出类型
    TOTAL_RANGE curTotalRange = TOTAL_RANGE.CURRMONTH;// 这个支持数据的左右变动
    DayRangePoint day_range = this.curTotalRange.create();// 默认展示当月内数据

    int color[] = new int[]
    { Color.GREEN, Color.YELLOW, Color.BLUE, Color.RED, Color.MAGENTA,
            Color.WHITE };

    List<String> names = new ArrayList<String>();
    List<ChartProp> acps = new ArrayList<ChartProp>();
    List<CostChartTotal> totals = new ArrayList<CostChartTotal>();
    List<Float> percents = new ArrayList<Float>();

    public Handler handler = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {
            ChartProp chartProp = (ChartProp) msg.obj;
            MonthPieChartActivity.this.myButton
                    .setBackgroundPaintColor(chartProp.getColor());
            MonthPieChartActivity.this.segmentInfoView.setText(chartProp
                    .getName());
            MonthPieChartActivity.this.segmentInfoView.setTextColor(chartProp
                    .getColor());

        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testpie_activity);
        initIntentData();
        initViews();
        setTitle();
    }

    private void initIntentData()
    {
        Intent intent = getIntent();
        CHART_COST_TYPE = intent.getIntExtra("costlevel", CHART_COST_TYPE);
        DayRangePoint tmp = (DayRangePoint) intent
                .getSerializableExtra("dayrange");
        if (tmp != null)
        {
            day_range = tmp;
        }
    }

    /**
     * 
     * Description:初始化界面元素
     * 
     */

    public void initViews()
    {
        this.pieView = (PieView) this.findViewById(R.id.lotteryView);
        this.myButton = (MyButton) this.findViewById(R.id.MyBt);
        this.segmentInfoView = (TextView) this.findViewById(R.id.MyTV);
        this.preBt = (Button) this.findViewById(R.id.pre_date_range_btn);
        this.nextBt = (Button) this.findViewById(R.id.next_date_range_btn);
        titleTv = (TextView) this.findViewById(R.id.date_interval_str_tv);
        this.preBt.setOnClickListener(this);
        this.nextBt.setOnClickListener(this);
        this.segmentInfoView.setOnClickListener(this);

        initItems();
        pieViewOnWork();
    }

    private void pieViewOnWork()
    {
        Message msg = new Message();
        msg.obj = this.pieView.getCurrentChartProp();
        this.handler.sendMessage(msg);

        this.pieView.setChartPropChangeListener(new ChartPropChangeListener()
        {
            @Override
            public void getChartProp(ChartProp chartProp)
            {
                Message msg = new Message();
                msg.obj = chartProp;
                MonthPieChartActivity.this.handler.sendMessage(msg);
            }
        });

        this.pieView.start();
    }

    private void setTitle()
    {
        StringBuilder accumStr = new StringBuilder();
        accumStr.append(day_range.getStartDay().substring(0, 4));
        accumStr.append("年");
        accumStr.append(TextFormatUtil.getMonthStr(day_range.getStartDay()));
        accumStr.append("月");
        accumStr.append(" ");
        accumStr.append(CHART_COST_TYPE == -1 ? "全部开销"
                : (CHART_COST_TYPE == -2 ? "全部收入" : CostTypeUtil
                        .getCostTypeName(this.CHART_COST_TYPE)));

        titleTv.setText(accumStr.toString());
    }

    /**
     * 
     * Description:初始化转盘的颜色，文字
     * 
     */
    public void initItems()
    {
        TransactionsDao.initDb(this);

        resetDatas();

        totals = TransactionsDao.getCostChartTotalsInRange(CHART_COST_TYPE,
                this.day_range.getStartDay(), day_range.getEndDay());
        if (totals.size() > 0)
        {
            initPieView();
        }
        else
        {
            ToastUtil.showLongToast(this, "没有任何数据,将展示示例!");
            showExample();
        }
        TransactionsDao.closeDb();
    }

    private void initPieView()
    {
        float sum = getSumAndInitPieDatas();

        initChartProps(sum);
    }

    private void initChartProps(float sum)
    {
        this.pieView.setSumCost(BasicNumberUtil.getDoubleFormat(sum / 1d, 1));
        ArrayList<ChartProp> acps = this.pieView
                .createCharts(this.names.size());
        int size = acps.size();

        for (int i = 0; i < size; i++)
        {
            ChartProp chartProp = acps.get(i);
            chartProp.setIndex(i);
            chartProp.setId(totals.get(i).getType());
            chartProp.setColor(this.color[i % color.length]);
            chartProp.setPercent(percents.get(i));
            chartProp.setName(TextFormatUtil.getPercentWithName(
                    percents.get(i), percents.get(i) * sum, this.names.get(i)));
        }
        this.pieView.initPercents();
    }

    private float getSumAndInitPieDatas()
    {
        float sum = 0;
        for (CostChartTotal total : totals)
        {
            this.names.add(CostTypeUtil.getCostTypeName(total.getType()));
            sum += total.getCost();
        }

        if (sum > 0)
        {
            for (CostChartTotal total : totals)
            {
                percents.add((float) total.getCost() / sum);
            }
        }
        return sum;
    }

    private void showExample()
    {
        this.names.add("张三");
        this.names.add("李四");

        int sum = 1000;

        List<Float> percents = new ArrayList<Float>();
        if (sum > 0)
        {
            percents.add(0.4f);
            percents.add(0.6f);
        }
        this.pieView.setSumCost(sum + "");
        acps = this.pieView.createCharts(this.names.size());
        int size = acps.size();
        for (int i = 0; i < size; i++)
        {
            ChartProp chartProp = acps.get(i);
            chartProp.setColor(this.color[i]);
            chartProp.setPercent(percents.get(i));
            chartProp.setName(TextFormatUtil.getPercentWithName(
                    percents.get(i), percents.get(i) * sum, this.names.get(i)));
        }
        this.pieView.initPercents();
    }

    public void refresh()
    {
        pieView.reInit();
        pieView.rotateEnable();
        initViews();
        setTitle();
    }

    private void resetDatas()
    {
        this.names.clear();
        totals.clear();
        percents.clear();
    }

    @Override
    public void onClick(View v)
    {
        if (v == segmentInfoView && totals.size() > 0)
        {
            toCostDetail();
        }
        else if (v == nextBt)
        {
            nextDateRangePie();
        }
        else if (v == preBt)
        {
            preDateRangePie();
        }
    }

    private void toCostDetail()
    {
        startActivity(new Intent(this, ShowCostDetailActivity.class)
                .putExtra("costlevel", pieView.getCurrentChartProp().getId())
                .putExtra("dayrange", day_range)
                .putExtra("summary", getSummary()));
    }

    private void nextDateRangePie()
    {
        DayRangePoint tmp = curTotalRange.next(this.day_range.getEndDay());
        if (tmp.getStartDay().compareTo(BasicDateUtil.getCurrentDateString()) < 0)
        {
            day_range = tmp;
            refresh();
        }
    }

    private void preDateRangePie()
    {
        day_range = curTotalRange.pre(this.day_range.getStartDay());
        refresh();
    }

    private String getSummary()
    {
        String date = titleTv.getText().toString().split(" ")[0];
        String segmentInfo = segmentInfoView.getText().toString();
        String costName = segmentInfo.substring(0, segmentInfo.indexOf(":"));
        String cost = segmentInfo.substring(segmentInfo.indexOf("¥"));

        return date + " " + costName + " (" + cost + ")";
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        refresh();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (this.pieView != null)
        {
            this.pieView.rotateDisable();
        }
    }

}