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
import com.wnc.basic.BasicNumberUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.bean.DayRangePoint;
import com.wnc.mymoney.bean.DayTranTotal;
import com.wnc.mymoney.bean.Trade;
import com.wnc.mymoney.dao.TransactionsDao;
import com.wnc.mymoney.uihelper.HorGestureDetectorListener;
import com.wnc.mymoney.uihelper.MyExpandableListAdapter;
import com.wnc.mymoney.uihelper.MyHorizontalGestureDetector;
import com.wnc.mymoney.uihelper.MyListViewAdapter;
import com.wnc.mymoney.util.common.TextFormatUtil;
import com.wnc.mymoney.util.enums.TOTAL_RANGE;
import com.wnc.mymoney.vholder.TranListViewHolder;
import com.wnc.mymoney.widget.QExListView;

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
public class NavTransactionActivity extends DataViewActivity implements View.OnClickListener, HorGestureDetectorListener
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
	public MyListViewAdapter adapter;

	@Override
	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		setContentView(R.layout.nav_trans_activity);

		initDateRange();

		this.gestureDetector = new GestureDetector(this, new MyHorizontalGestureDetector(0.33, this));

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
		List<DayTranTotal> totals = TransactionsDao.getAllTranTotalInRange(this.dayRange.getStartDay(), this.dayRange.getEndDay());
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
				((ExpandableListView) this.expanseLV).setAdapter(new MyExpandableListAdapter(this));
			}
			else
			{
				adapter = new MyListViewAdapter(this, this.mData);
				expanseLV.setAdapter(adapter);
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
			this.dateRangeTv.setText(TextFormatUtil.addChnToDayNoSeperate(bday).replace("年0", "年"));
		}
		else
		{
			if (this.TOTAL_RANGE_TYPE == TOTAL_RANGE.CURRMONTH)
			{
				dateRangeTv.setVisibility(View.INVISIBLE);
				this.titleTv.setText(TextFormatUtil.addChnToDayNoSeperate(bday).substring(0, 8).replace("年0", "年"));
			}
			else if (this.TOTAL_RANGE_TYPE == TOTAL_RANGE.CURRYEAR)
			{
				dateRangeTv.setVisibility(View.INVISIBLE);
				this.titleTv.setText(TextFormatUtil.addChnToDayNoSeperate(bday).substring(0, 5));
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
		this.expandHeader1 = localLayoutInflater.inflate(R.layout.nav_trans_header_search_bar, null);

		this.expandHeader1.findViewById(R.id.search_rl).setOnClickListener(this);
		this.searchHint = (EditText) this.expandHeader1.findViewById(R.id.search_et);
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
					localIntent.setClass(getApplicationContext(), SearchTransactionActivity.class);
					localIntent.putExtra("dayrange", NavTransactionActivity.this.dayRange).putExtra("keyword", "");
					startActivityForResult(localIntent, 1);
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
		this.searchHint.setHint("搜索" + this.TOTAL_RANGE_TYPE.getSearchHint() + "流水");

		this.expanseLV.addHeaderView(this.expandHeader1);

		this.expandHeader2 = localLayoutInflater.inflate(R.layout.nav_year_trans_header_conspectus, null);
		this.balanceTV = ((TextView) this.expandHeader2.findViewById(R.id.nav_year_trans_conspectus_balance_tv));
		this.inboundTV = ((TextView) this.expandHeader2.findViewById(R.id.nav_year_trans_conspectus_inbound_tv));
		this.outboundTV = ((TextView) this.expandHeader2.findViewById(R.id.nav_year_trans_conspectus_outbound_tv));

		this.expanseLV.addHeaderView(this.expandHeader2);

		this.expandHeader3 = localLayoutInflater.inflate(R.layout.nav_year_trans_header_shadow, null);
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
		DayRangePoint tmp = this.TOTAL_RANGE_TYPE.next(this.dayRange.getEndDay());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		System.out.println(requestCode + "  " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		// 如果经过修改
		if (requestCode == DataViewActivity.MODIFY_REQUEST_CODE)
		{
			modifyTrigger();
		}
		// 如果是从SearchTransactionActivity返回并经过数据修改
		if (resultCode == SearchTransactionActivity.SEARCH_MODIFY_RESULT)
		{
			// System.out.println("改了我的数据!");
			modifyTrigger();
		}
	}

	@Override
	public void modifyTrigger()
	{
		initData();
	}

	@Override
	public void delTrigger(ListView arg0, int arg2)
	{
		if (this.TOTAL_RANGE_TYPE != TOTAL_RANGE.CURRYEAR)
		{
			reComputeTotalData(arg0, arg2);
		}
		else
		{
			initData();
		}
	}

	private void reComputeTotalData(ListView arg0, int arg2)
	{
		for (Map.Entry<Integer, TranListViewHolder> item : adapter.listHolders.entrySet())
		{
			if (item.getValue().listview == arg0)
			{
				Map<String, Object> map = (HashMap) arg0.getItemAtPosition(arg2);
				Map<String, Object> totalDayMap = mData.get(item.getKey());
				double in = BasicNumberUtil.getDouble(this.inboundTV.getText().toString().trim());
				double out = BasicNumberUtil.getDouble(this.outboundTV.getText().toString().trim());
				Trade trade = (Trade) map.get("Trade");

				double totalIn = (Double) totalDayMap.get("in");
				double totalOut = (Double) totalDayMap.get("out");
				double cost = trade.getCost();
				if (trade.getType_id() == -1)
				{
					out -= cost;
					totalOut -= cost;
				}
				else
				{
					in -= cost;
					totalIn -= cost;
				}
				this.balanceTV.setText(TextFormatUtil.getFormatMoneyStr(in - out));
				this.inboundTV.setText(TextFormatUtil.getFormatMoneyStr(in));
				this.outboundTV.setText(TextFormatUtil.getFormatMoneyStr(out));

				totalDayMap.remove("in");
				totalDayMap.remove("out");
				totalDayMap.remove("balance");
				totalDayMap.put("in", totalIn);
				totalDayMap.put("out", totalOut);
				totalDayMap.put("balance", in - out);
				adapter.setHolderData(item.getValue(), item.getKey());
			}
		}
	}

	public void refresh(int position)
	{
		mData.remove(position);
		expanseLV.setAdapter(adapter);
	}
}