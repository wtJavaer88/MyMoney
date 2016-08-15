package com.wnc.mymoney.uihelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.wnc.mymoney.R;
import com.wnc.mymoney.bean.Trade;
import com.wnc.mymoney.dao.TransactionsDao;
import com.wnc.mymoney.ui.DataViewActivity;
import com.wnc.mymoney.util.enums.CostTypeUtil;
import com.wnc.mymoney.vholder.TranListViewHolder;

public class MyListViewAdapter extends BaseAdapter
{

	private LayoutInflater mInflater;
	private DataViewActivity activity;
	List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	public Map<Integer, TranListViewHolder> listHolders = new HashMap<Integer, TranListViewHolder>();
	private int costLevel = 0;

	public MyListViewAdapter(DataViewActivity activity, List<Map<String, Object>> mData)
	{
		this.activity = activity;
		this.mData = mData;
		this.mInflater = LayoutInflater.from(activity);
	}

	/**
	 * 为统计图分消费类型所提供的接口
	 * 
	 * @param activity
	 * @param mData
	 * @param costLevel
	 */
	public MyListViewAdapter(DataViewActivity activity, List<Map<String, Object>> mData, int costLevel)
	{
		this(activity, mData);
		this.costLevel = costLevel;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		TranListViewHolder holder = null;
		if (convertView == null)
		{
			convertView = this.mInflater.inflate(R.layout.nav_year_trans_row, null);
			holder = getNewViewHolder(convertView);
			setHolderListView(holder, convertView);
			convertView.setTag(holder);
		}
		else
		{
			holder = (TranListViewHolder) convertView.getTag();
		}
		listHolders.put(position, holder);

		setHolderData(holder, position);

		return convertView;
	}

	private TranListViewHolder getNewViewHolder(View convertView)
	{
		TranListViewHolder holder = new TranListViewHolder();
		holder.trans_day_of_month_tv = (TextView) convertView.findViewById(R.id.trans_day_of_month_tv);
		holder.trans_day_of_week_tv = (TextView) convertView.findViewById(R.id.trans_day_of_week_tv);
		holder.day_of_income_tv = (TextView) convertView.findViewById(R.id.day_of_income_tv);
		holder.day_of_payout_tv = (TextView) convertView.findViewById(R.id.day_of_payout_tv);
		holder.day_of_balance_tv = (TextView) convertView.findViewById(R.id.day_of_balance_tv);

		return holder;
	}

	private void setHolderListView(TranListViewHolder holder, View convertView)
	{
		LinearLayout trans_rows_ly = (LinearLayout) convertView.findViewById(R.id.trans_rows_ly);
		ListView addedLV = new ListView(this.activity);
		trans_rows_ly.addView(addedLV);
		holder.listview = addedLV;
	}

	int curPosition = -1;

	public void setHolderData(TranListViewHolder holder, int position)
	{
		curPosition = position;
		holder.trans_day_of_month_tv.setText((String) this.mData.get(position).get("day"));
		holder.trans_day_of_week_tv.setText((String) this.mData.get(position).get("week"));
		holder.day_of_income_tv.setText("" + this.mData.get(position).get("in"));
		holder.day_of_payout_tv.setText("" + this.mData.get(position).get("out"));
		holder.day_of_balance_tv.setText("" + this.mData.get(position).get("balance"));

		simpleLvSet(holder.listview, this.mData.get(position).get("searchDate").toString());
	}

	private void simpleLvSet(ListView listview, String searchDate)
	{
		List<Map<String, Object>> mapData = getMapData(searchDate);
		final SimpleAdapter adapter = new SimpleAdapter(this.activity, mapData, R.layout.nav_year_trans_lv_item, new String[] { "icon", "name", "photo", "memo", "cost" }, new int[] { R.id.item_icon_iv, R.id.item_name_tv, R.id.photo_flag_iv, R.id.memo_tv, R.id.cost_tv });
		listview.setAdapter(adapter);
		TransItemClickListener listener = new TransItemClickListener(this.activity, listview);
		listview.setOnItemClickListener(listener);
		listview.setOnItemLongClickListener(listener);
		// 动态设置listview的高度
		if (adapter.getCount() > 0)
		{

			ViewGroup.LayoutParams params = listview.getLayoutParams();
			View listItem = adapter.getView(0, null, listview);
			listItem.measure(0, 0);
			params.height = listItem.getMeasuredHeight() * adapter.getCount() + listview.getDividerHeight() * (adapter.getCount() - 1);
			listview.setLayoutParams(params);
		}
		System.out.println("mapData.size():" + mapData.size());
		if (mapData.size() == 0 && mData.size() > curPosition)
		{
			activity.refresh(curPosition);
		}

	}

	private List<Map<String, Object>> getMapData(String searchDate)
	{
		TransactionsDao.initDb(this.activity);
		List<Trade> tradeItems;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (costLevel > 0)
		{
			tradeItems = TransactionsDao.getDayTradesByCostLevel(searchDate, costLevel);
		}
		else
		{
			tradeItems = TransactionsDao.getDayTrades(searchDate);
		}

		for (Trade trade : tradeItems)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("Trade", trade);
			map.put("id", trade.getId());// 获取交易ID
			map.put("icon", CostTypeUtil.getIcon(trade.getCostdesc_id()));
			map.put("name", CostTypeUtil.getCostTypeName(trade.getCostdesc_id()));
			map.put("photo", CostTypeUtil.getFlagIcon(trade.getHaspicture()));
			map.put("memo", trade.getMemo());
			map.put("cost", trade.getCost());
			map.put("searchDate", searchDate);
			list.add(map);
		}
		TransactionsDao.closeDb();
		return list;
	}

	@Override
	public int getCount()
	{
		return this.mData.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}
}