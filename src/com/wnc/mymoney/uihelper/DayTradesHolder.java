package com.wnc.mymoney.uihelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.wnc.mymoney.bean.Trade;
import com.wnc.mymoney.dao.TransactionsDao;

public class DayTradesHolder
{
	/**
	 * 缓存每日的交易记录
	 */
	static Map<String, List<Trade>> dayTradesMap = new HashMap<String, List<Trade>>();
	static Context activity;

	public static List<Trade> getDayTrades(String searchDate)
	{
		return getDayTrades(searchDate, 0);
	}

	public static List<Trade> getDayTrades(String searchDate, int costLevel)
	{
		List<Trade> tradeItems = null;
		TransactionsDao.initDb(activity);
		if (!dayTradesMap.containsKey(searchDate))
		{
			if (costLevel > 0)
				tradeItems = TransactionsDao.getDayTradesByCostLevel(
						searchDate, costLevel);
			else
			{
				tradeItems = TransactionsDao.getDayTrades(searchDate);
				addDayTrades(searchDate, tradeItems);
			}
		}
		else
		{
			tradeItems = dayTradesMap.get(searchDate);
		}
		TransactionsDao.closeDb();

		return tradeItems;
	}

	public static void addDayTrades(String searchDate, List<Trade> tradeItems)
	{
		dayTradesMap.put(searchDate, tradeItems);
	}

	public static void refreshDayTrades(String searchDate)
	{
		TransactionsDao.initDb(activity);
		if (dayTradesMap.containsKey(searchDate))
		{
			dayTradesMap.remove(searchDate);
		}
		addDayTrades(searchDate, TransactionsDao.getDayTrades(searchDate));
		TransactionsDao.closeDb();
	}

}
