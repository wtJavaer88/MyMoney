package com.wnc.mymoney.uihelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.wnc.mymoney.R;
import com.wnc.mymoney.bean.DayRangePoint;
import com.wnc.mymoney.bean.DayTranTotal;
import com.wnc.mymoney.bean.PriceTotalBean;
import com.wnc.mymoney.bean.Trade;
import com.wnc.mymoney.dao.TransactionsDao;
import com.wnc.mymoney.ui.DataViewActivity;
import com.wnc.mymoney.uihelper.listener.TransItemClickListener;
import com.wnc.mymoney.util.common.TextFormatUtil;
import com.wnc.mymoney.util.enums.CostTypeUtil;
import com.wnc.mymoney.vholder.ExpandGroupHolder;
import com.wnc.mymoney.vholder.TranListViewHolder;

public class MyExpandableListAdapter extends BaseExpandableListAdapter
{

    private String[] armTypes;
    private PriceTotalBean[] monthBalance;
    private DayTranTotal[][] arms;
    DataViewActivity activity;
    DayRangePoint dayRange;

    public MyExpandableListAdapter(DataViewActivity activity,
            DayRangePoint dayRange)
    {
        this.activity = activity;
        this.dayRange = dayRange;
        initData();
    }

    public void initData()
    {
        TransactionsDao.initDb(this.activity);
        List<DayTranTotal> totals = TransactionsDao.getAllTranTotalInRange(
                dayRange.getStartDay(), dayRange.getEndDay());

        List<String> monthNameList = new ArrayList<String>();
        List<List<DayTranTotal>> monthDayAllTotalList = new ArrayList<List<DayTranTotal>>();
        List<DayTranTotal> monthDesclist = new ArrayList<DayTranTotal>();

        String lastMonth = totals.get(0).getSearchDate().substring(0, 6);
        monthNameList.add(lastMonth);

        for (DayTranTotal total : totals)
        {
            String month = total.getSearchDate().substring(0, 6);
            if (month.equals(lastMonth))
            {
                monthDesclist.add(total);// 往该月中加一个数据
            }
            else
            {
                monthNameList.add(month);
                monthDayAllTotalList.add(monthDesclist);// 一个月的数据累加完毕
                monthDesclist = new ArrayList<DayTranTotal>();
                monthDesclist.add(total);// 往该月中加第一个数据
            }
            lastMonth = month;
        }
        monthDayAllTotalList.add(monthDesclist);// 最后一个月的数据累加完毕

        int monthCounts = monthNameList.size();
        armTypes = monthNameList.toArray(new String[monthCounts]);
        arms = new DayTranTotal[monthCounts][];
        monthBalance = new PriceTotalBean[monthCounts];

        for (int i = 0; i < monthDayAllTotalList.size(); i++)
        {
            arms[i] = monthDayAllTotalList.get(i).toArray(
                    new DayTranTotal[monthDayAllTotalList.get(i).size()]);
            PriceTotalBean pricebean = new PriceTotalBean();
            for (DayTranTotal daytotal : monthDayAllTotalList.get(i))
            {
                pricebean.setIn(pricebean.getIn() + daytotal.getInbound());
                pricebean.setOut(pricebean.getOut() + daytotal.getOutbound());
                pricebean.setBalance(pricebean.getBalance()
                        + daytotal.getBalance());
            }
            monthBalance[i] = pricebean;
        }
        TransactionsDao.closeDb();
    }

    /* ===========组元素表示可折叠的列表项，子元素表示列表项展开后看到的多个子元素项============= */

    /**
     * ----------得到armTypes和arms中每一个元素的ID------------------------------
     * -------------
     */

    // 获取组在给定的位置编号，即armTypes中元素的ID
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    // 获取在给定的组的儿童的ID，就是arms中元素的ID
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    /**
     * ----------根据上面得到的ID的值，来得到armTypes和arms中元素的个数 ------------------------
     */

    // 获取的群体数量，得到armTypes里元素的个数
    @Override
    public int getGroupCount()
    {
        return armTypes.length;
    }

    // 取得指定组中的儿童人数，就是armTypes中每一个种族它军种的个数
    @Override
    public int getChildrenCount(int groupPosition)
    {
        return arms[groupPosition].length;
    }

    /**
     * ----------利用上面getGroupId得到ID，从而根据ID得到armTypes中的数据，并填到TextView中 -----
     */

    // 获取与给定的组相关的数据，得到数组armTypes中元素的数据
    @Override
    public Object getGroup(int groupPosition)
    {
        return armTypes[groupPosition];
    }

    // 获取一个视图显示给定组，存放armTypes
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent)
    {
        ExpandGroupHolder holder = null;

        if (convertView == null)
        {
            holder = new ExpandGroupHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.expandablelist_group_item, null);
            holder.monthTv = (TextView) convertView.findViewById(R.id.month_tv);
            holder.incomeTv = (TextView) convertView
                    .findViewById(R.id.income_amount_tv);
            holder.payoutTv = (TextView) convertView
                    .findViewById(R.id.payout_amount_tv);
            holder.balanceTv = (TextView) convertView
                    .findViewById(R.id.balance_amount_tv);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ExpandGroupHolder) convertView.getTag();
        }
        holder.monthTv.setText(TextFormatUtil
                .getMonthStr(armTypes[groupPosition]));
        holder.incomeTv.setText(TextFormatUtil
                .getFormatMoneyStr(monthBalance[groupPosition].getIn()));
        holder.payoutTv.setText(TextFormatUtil
                .getFormatMoneyStr(monthBalance[groupPosition].getOut()));
        holder.balanceTv.setText(TextFormatUtil
                .getFormatMoneyStr(monthBalance[groupPosition].getBalance()));
        return convertView;
    }

    /**
     * ----------利用上面getChildId得到ID，从而根据ID得到arms中的数据，并填到TextView中------ ---
     */

    // 获取与孩子在给定的组相关的数据,得到数组arms中元素的数据
    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return arms[groupPosition][childPosition];
    }

    // 获取一个视图显示在给定的组 的儿童的数据，就是存放arms
    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent)
    {
        TranListViewHolder holder = null;
        if (convertView == null)
        {

            holder = new TranListViewHolder();

            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.nav_year_trans_row, null);
            holder.trans_day_of_month_tv = (TextView) convertView
                    .findViewById(R.id.trans_day_of_month_tv);
            holder.trans_day_of_week_tv = (TextView) convertView
                    .findViewById(R.id.trans_day_of_week_tv);
            holder.day_of_income_tv = (TextView) convertView
                    .findViewById(R.id.day_of_income_tv);
            holder.day_of_payout_tv = (TextView) convertView
                    .findViewById(R.id.day_of_payout_tv);
            holder.day_of_balance_tv = (TextView) convertView
                    .findViewById(R.id.day_of_balance_tv);

            LinearLayout trans_rows_ly = (LinearLayout) convertView
                    .findViewById(R.id.trans_rows_ly);

            ListView addedLV = new ListView(activity);
            trans_rows_ly.addView(addedLV);

            holder.listview = addedLV;

            convertView.setTag(holder);

        }
        else
        {
            holder = (TranListViewHolder) convertView.getTag();
        }

        DayTranTotal total = arms[groupPosition][childPosition];

        holder.trans_day_of_month_tv.setText(total.getDay());
        holder.trans_day_of_week_tv.setText(total.getWeek());
        holder.day_of_income_tv.setText(total.getInbound() + "");
        holder.day_of_payout_tv.setText(total.getOutbound() + "");
        holder.day_of_balance_tv.setText(total.getBalance() + "");
        simpleLvSet(holder, total.getSearchDate());

        return convertView;
    }

    private void simpleLvSet(TranListViewHolder holder, String searchDate)
    {
        ListView listview = holder.listview;
        final List<Map<String, Object>> mapData = getMapData(searchDate);
        setLvHeadVisible(mapData.size() > 5, holder);
        final SimpleAdapter adapter = new SimpleAdapter(activity, mapData,
                R.layout.nav_year_trans_lv_item, new String[]
                { "icon", "name", "photo", "memo", "cost" }, new int[]
                { R.id.item_icon_iv, R.id.item_name_tv, R.id.photo_flag_iv,
                        R.id.memo_tv, R.id.cost_tv });
        listview.setAdapter(adapter);
        TransItemClickListener listener = new TransItemClickListener(activity,
                listview);
        listview.setOnItemClickListener(listener);
        listview.setOnItemLongClickListener(listener);
        // 动态设置listview的高度
        if (adapter.getCount() > 0)
        {
            ViewGroup.LayoutParams params = listview.getLayoutParams();
            View listItem = adapter.getView(0, null, listview);
            listItem.measure(0, 0);
            params.height = listItem.getMeasuredHeight() * adapter.getCount()
                    + listview.getDividerHeight() * (adapter.getCount() - 1);
            listview.setLayoutParams(params);
        }
    }

    private void setLvHeadVisible(boolean b, TranListViewHolder holder)
    {
        int v = b ? View.VISIBLE : View.INVISIBLE;
        holder.day_of_income_tv.setVisibility(v);
        holder.day_of_payout_tv.setVisibility(v);
        holder.day_of_balance_tv.setVisibility(v);
    }

    private List<Map<String, Object>> getMapData(String searchDate)
    {
        TransactionsDao.initDb(activity);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<Trade> tradeItems = TransactionsDao.getDayTrades(searchDate);

        for (Trade trade : tradeItems)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Trade", trade);
            map.put("id", trade.getId());// 获取交易ID
            map.put("icon", CostTypeUtil.getIcon(trade.getCostdesc_id()));
            map.put("name",
                    CostTypeUtil.getCostTypeName(trade.getCostdesc_id()));
            map.put("photo", CostTypeUtil.getFlagIcon(trade.getHaspicture()));
            map.put("memo", MemoHelper.concatMemo(trade));
            map.put("cost", trade.getCost());
            map.put("searchDate", searchDate);
            list.add(map);
        }
        TransactionsDao.closeDb();
        return list;
    }

    /**
     * -------------------其他设置------------------------------------------
     * -------------------------
     */

    // 孩子在指定的位置是可选的，即：arms中的元素是可点击的
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    // 表示孩子是否和组ID是跨基础数据的更改稳定
    @Override
    public boolean hasStableIds()
    {
        return true;
    }
}
