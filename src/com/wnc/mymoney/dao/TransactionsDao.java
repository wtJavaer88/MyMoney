package com.wnc.mymoney.dao;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.mymoney.backup.BackUpDataUtil;
import com.wnc.mymoney.bean.CostChartTotal;
import com.wnc.mymoney.bean.DayTranTotal;
import com.wnc.mymoney.bean.Trade;
import com.wnc.mymoney.util.common.TextFormatUtil;
import com.wnc.mymoney.util.enums.CostTypeUtil;
import com.wnc.string.PatternUtil;

public class TransactionsDao
{
	private static SQLiteDatabase db = null;

	public static void initDb(Context context)
	{
		db = context.openOrCreateDatabase("money.db", Context.MODE_PRIVATE, null);
	}

	public static boolean insert(Trade trade)
	{
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return false;
		}
		try
		{
			db.execSQL(
					"INSERT INTO transactions(ID,TYPE_ID,COSTLEVEL_ID,COSTDESC_ID,MEMBER,SHOP,PROJECT,COST,HASPICTURE,CREATE_TIME,CREATE_LONGTIME,MODIFY_TIME,MEMO,UUID) VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { trade.getType_id(), trade.getCostlevel_id(), trade.getCostdesc_id(), trade.getMember(), trade.getShop(), trade.getProject(), trade.getCost(), trade.getHaspicture(), trade.getCreatetime(), trade.getCreatelongtime(), trade.getModifytime(),
							trade.getMemo(), trade.getUuid() });
			trigger();
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
		return true;
	}

	private static void trigger()
	{
		BackUpDataUtil.canBackUpDb = true;
	}

	public static boolean update(Trade trade)
	{
		if (trade == null)
		{
			return false;
		}
		try
		{
			ContentValues cv = new ContentValues();
			cv.put("TYPE_ID", trade.getType_id());
			cv.put("COSTLEVEL_ID", trade.getCostlevel_id());
			cv.put("COSTDESC_ID", trade.getCostdesc_id());
			cv.put("MEMBER", trade.getMember());
			cv.put("SHOP", trade.getShop());
			cv.put("PROJECT", trade.getProject());
			cv.put("COST", trade.getCost());
			cv.put("HASPICTURE", trade.getHaspicture());
			cv.put("CREATE_TIME", trade.getCreatetime());
			cv.put("CREATE_LONGTIME", trade.getCreatelongtime());
			cv.put("MODIFY_TIME", trade.getModifytime());
			cv.put("MEMO", trade.getMemo());
			db.update("transactions", cv, "id = ?", new String[] { String.valueOf(trade.getId()) });
			trigger();
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
			throw new RuntimeException("修改Subject时异常," + ex.getMessage());
		}

		return true;
	}

	private static long getLongValue(Cursor c, String columnName)
	{
		return c.getLong(c.getColumnIndex(columnName));
	}

	private static String getStrValue(Cursor c, String columnName)
	{
		return c.getString(c.getColumnIndex(columnName));
	}

	public static List<Trade> getDayTrades(String dayStr)
	{
		Cursor c = getDayTradeCursor(dayStr);
		return getTradeList(c);
	}

	public static List<Trade> getDayTradesByCostLevel(String dayStr, int costLevel)
	{
		Cursor c = getDayTradesByCostLevelCursor(dayStr, costLevel);
		return getTradeList(c);
	}

	private static List<Trade> getTradeList(Cursor c)
	{
		List<Trade> list = new ArrayList<Trade>();

		while (c != null && c.moveToNext())
		{
			Trade bean = new Trade();
			bean.setId(Integer.parseInt(getStrValue(c, "ID")));
			bean.setType_id(Integer.parseInt(getStrValue(c, "TYPE_ID")));
			bean.setCostlevel_id(Integer.parseInt(getStrValue(c, "COSTLEVEL_ID")));
			bean.setCostdesc_id(Integer.parseInt(getStrValue(c, "COSTDESC_ID")));
			bean.setMember(getStrValue(c, "MEMBER"));
			bean.setShop(getStrValue(c, "SHOP"));
			bean.setProject(getStrValue(c, "PROJECT"));
			bean.setCost(Double.parseDouble(getStrValue(c, "COST")));
			bean.setHaspicture(Integer.parseInt(getStrValue(c, "HASPICTURE")));
			bean.setCreatetime(getStrValue(c, "CREATE_TIME"));
			bean.setCreatelongtime(getLongValue(c, "CREATE_LONGTIME"));
			bean.setModifytime(getStrValue(c, "MODIFY_TIME"));
			bean.setMemo(getStrValue(c, "MEMO"));
			bean.setUuid(getStrValue(c, "UUID"));
			list.add(bean);
		}
		c.close();
		return list;
	}

	public static void closeDb()
	{
		if (db != null)
		{
			db.close();
		}
	}

	/**
	 * 
	 * @param type
	 *            -1:支出 -2:收入
	 * @param month
	 *            月份:201604
	 * @return
	 */
	public static List<CostChartTotal> getCostChartTotalsInRange(int type, String startDay, String endDay)
	{
		List<CostChartTotal> list = new ArrayList<CostChartTotal>();
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return null;
		}
		Cursor c = null;
		if (type < 0)
		{
			c = db.rawQuery("select  costlevel_id TYPE,sum(cost) SUM from transactions where isdel=0 and type_id=? and substr(create_time,0,9) between ? and ? group by costlevel_id order by type asc", new String[] { "" + type, startDay, endDay });
		}
		else
		{
			c = db.rawQuery("select costdesc_id TYPE,sum(cost) SUM from transactions where isdel=0 and costlevel_id=? and substr(create_time,0,9) between ? and ? group by type  order by create_time,type asc", new String[] { "" + type, startDay, endDay });
		}
		CostChartTotal bean;
		while (c.moveToNext())
		{
			bean = new CostChartTotal();
			bean.setCost(BasicNumberUtil.getDouble(getStrValue(c, "SUM")));
			bean.setType(BasicNumberUtil.getNumber(getStrValue(c, "TYPE")));
			list.add(bean);
		}
		c.close();
		return list;
	}

	public static List<DayTranTotal> getAllTranTotalInRange(String startDay, String endDay)
	{
		List<DayTranTotal> list = new ArrayList<DayTranTotal>();
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return list;
		}
		Cursor c = db.rawQuery("select substr(create_time,0,9) DAY,type_id TYPE,sum(cost) SUM from transactions where isdel=0 group by type_id,substr(create_time,0,9) having day between ? and ? order by day,type asc", new String[] { startDay, endDay });
		String lastDay = "";
		DayTranTotal bean = null;
		while (c.moveToNext())
		{
			String day = getStrValue(c, "DAY");
			if (!day.equals(lastDay))
			{
				addBeanToList(bean, list);
				bean = new DayTranTotal();
			}
			bean.setSearchDate(day);
			lastDay = day;

			if (getStrValue(c, "TYPE").equals("-1"))
			{
				bean.setOutbound(Double.parseDouble(getStrValue(c, "SUM")));
			}
			else
			{
				bean.setInbound(Double.parseDouble(getStrValue(c, "SUM")));
			}
		}
		addBeanToList(bean, list);

		c.close();
		return list;
	}

	public static Trade getNextTrade(String curTime)
	{
		Trade trade = null;
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return trade;
		}
		Cursor c = db.rawQuery("select * from transactions where isdel=0 and CREATE_TIME > ? order by CREATE_LONGTIME asc", new String[] { curTime + "" });
		while (c != null && c.moveToNext())
		{
			trade = new Trade();
			trade.setId(Integer.parseInt(getStrValue(c, "ID")));
			trade.setType_id(Integer.parseInt(getStrValue(c, "TYPE_ID")));
			trade.setCostlevel_id(Integer.parseInt(getStrValue(c, "COSTLEVEL_ID")));
			trade.setCostdesc_id(Integer.parseInt(getStrValue(c, "COSTDESC_ID")));
			trade.setMember(getStrValue(c, "MEMBER"));
			trade.setShop(getStrValue(c, "SHOP"));
			trade.setProject(getStrValue(c, "PROJECT"));
			trade.setCost(Double.parseDouble(getStrValue(c, "COST")));
			trade.setHaspicture(Integer.parseInt(getStrValue(c, "HASPICTURE")));
			trade.setCreatetime(getStrValue(c, "CREATE_TIME"));
			trade.setCreatelongtime(getLongValue(c, "CREATE_LONGTIME"));
			trade.setModifytime(getStrValue(c, "MODIFY_TIME"));
			trade.setMemo(getStrValue(c, "MEMO"));
			trade.setUuid(getStrValue(c, "UUID"));
			break;
		}
		return trade;
	}

	public static Trade getPreTrade(String curTime)
	{
		Trade trade = null;
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return trade;
		}
		Cursor c = db.rawQuery("select * from transactions where isdel=0 and CREATE_TIME < ? order by CREATE_LONGTIME desc", new String[] { curTime + "" });
		while (c != null && c.moveToNext())
		{
			trade = new Trade();
			trade.setId(Integer.parseInt(getStrValue(c, "ID")));
			trade.setType_id(Integer.parseInt(getStrValue(c, "TYPE_ID")));
			trade.setCostlevel_id(Integer.parseInt(getStrValue(c, "COSTLEVEL_ID")));
			trade.setCostdesc_id(Integer.parseInt(getStrValue(c, "COSTDESC_ID")));
			trade.setMember(getStrValue(c, "MEMBER"));
			trade.setShop(getStrValue(c, "SHOP"));
			trade.setProject(getStrValue(c, "PROJECT"));
			trade.setCost(Double.parseDouble(getStrValue(c, "COST")));
			trade.setHaspicture(Integer.parseInt(getStrValue(c, "HASPICTURE")));
			trade.setCreatetime(getStrValue(c, "CREATE_TIME"));
			trade.setCreatelongtime(getLongValue(c, "CREATE_LONGTIME"));
			trade.setModifytime(getStrValue(c, "MODIFY_TIME"));
			trade.setMemo(getStrValue(c, "MEMO"));
			trade.setUuid(getStrValue(c, "UUID"));
			break;
		}
		return trade;
	}

	public static List<DayTranTotal> getAllTranTotalForChart(int costLevel, String startDay, String endDay)
	{
		List<DayTranTotal> list = new ArrayList<DayTranTotal>();
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return list;
		}
		Cursor c = null;
		if (CostTypeUtil.isLevelType(costLevel))
		{
			c = db.rawQuery("select substr(create_time,0,9) DAY,type_id TYPE,sum(cost) SUM from transactions where isdel=0 and costlevel_id=? group by type_id,substr(create_time,0,9) having day between ? and ? order by day,type asc", new String[] { "" + costLevel, startDay,
					endDay });
		}
		else
		{
			c = db.rawQuery("select substr(create_time,0,9) DAY,type_id TYPE,sum(cost) SUM from transactions where isdel=0 and costdesc_id=? group by type_id,substr(create_time,0,9) having day between ? and ? order by day,type asc", new String[] { "" + costLevel, startDay,
					endDay });
		}
		String lastDay = "";
		DayTranTotal bean = null;
		while (c.moveToNext())
		{
			String day = getStrValue(c, "DAY");
			if (!day.equals(lastDay))
			{
				addBeanToList(bean, list);
				bean = new DayTranTotal();
			}
			bean.setSearchDate(day);
			lastDay = day;

			if (getStrValue(c, "TYPE").equals("-1"))
			{
				bean.setOutbound(Double.parseDouble(getStrValue(c, "SUM")));
			}
			else
			{
				bean.setInbound(Double.parseDouble(getStrValue(c, "SUM")));
			}
		}
		addBeanToList(bean, list);

		c.close();
		return list;
	}

	private static void addBeanToList(DayTranTotal bean, List<DayTranTotal> list)
	{
		if (bean != null)
		{
			bean.setOtherParam();
			list.add(bean);
		}
	}

	public static List<Trade> getSearchTransactionWithDateRange(String haspic, String member, String categorypath, String keyword, String startDay, String endDay)
	{
		Cursor c = getSearchTradeCursorWithDateRange(haspic, member, categorypath, keyword, startDay, endDay);

		return getTradeList(c);
	}

	/**
	 * 将创建时间字符串转为时间比较
	 * 
	 * @param keyword
	 * @param startDay
	 * @param endDay
	 * @return
	 */
	private static Cursor getSearchTradeCursorWithDateRange(String haspic, String member, String categorypath, String keyword, String startDay, String endDay)
	{
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return null;
		}
		haspic = "%" + haspic + "%";
		member = "%" + member + "%";
		categorypath = "%" + categorypath + "%";
		startDay = TextFormatUtil.addSeparatorToDay(startDay);
		endDay = TextFormatUtil.addSeparatorToDay(endDay);
		if (!TextFormatUtil.isNumberRange(keyword))
		{
			keyword = "%" + keyword + "%";
			return db
					.rawQuery(
							"SELECT T.*,G.PATH,case HASPICTURE when 0 then '无图' else '有图' end PICTURE FROM TRANSACTIONS T  JOIN CATEGORY G ON T.COSTDESC_ID=G.ID"
									+ " WHERE PICTURE like ? AND MEMBER like ? AND PATH LIKE ?  AND (COST LIKE ? OR PROJECT LIKE ? OR SHOP LIKE ? OR MEMO LIKE ?)  AND isdel=0 AND (date(date(substr(create_time,0,5)|| '-'||substr(create_time,5,2)|| '-' || substr(create_time,7,2))) between date(?) and date(?))   ORDER BY CREATE_LONGTIME ASC",
							new String[] { haspic, member, categorypath, keyword, keyword, keyword, keyword, startDay, endDay });
		}
		else
		{
			String costMin = PatternUtil.getFirstPattern(keyword.replace(" ", ""), "\\d+\\.?\\d*");
			String costMax = PatternUtil.getLastPattern(keyword.replace(" ", ""), "\\d+\\.?\\d*");
			// System.out.println("金钱区间::  " + costMin + "  " + costMax);
			return db
					.rawQuery(
							"SELECT T.*,G.PATH,case HASPICTURE when 0 then '无图' else '有图' end PICTURE FROM TRANSACTIONS T  JOIN CATEGORY G ON T.COSTDESC_ID=G.ID"
									+ " WHERE PICTURE like ? AND MEMBER like ? AND PATH LIKE ?  AND (COST BETWEEN ? AND ?)  AND isdel=0 AND (date(date(substr(create_time,0,5)|| '-'||substr(create_time,5,2)|| '-' || substr(create_time,7,2))) between date(?) and date(?))   ORDER BY CREATE_LONGTIME ASC",
							new String[] { haspic, member, categorypath, costMin, costMax, startDay, endDay });
		}
	}

	private static Cursor getDayTradeCursor(String dayStr)
	{
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return null;
		}
		return db.rawQuery("SELECT * FROM TRANSACTIONS WHERE CREATE_TIME LIKE ? AND isdel=0  ORDER BY CREATE_LONGTIME ASC", new String[] { dayStr + "%" });
	}

	private static Cursor getDayTradesByCostLevelCursor(String dayStr, int costLevel)
	{
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return null;
		}
		Cursor c = null;
		if (CostTypeUtil.isLevelType(costLevel))
		{
			c = db.rawQuery("SELECT * FROM TRANSACTIONS WHERE CREATE_TIME LIKE ? AND isdel=0 AND costlevel_id=? ORDER BY CREATE_LONGTIME ASC", new String[] { dayStr + "%", "" + costLevel });
		}
		else
		{
			c = db.rawQuery("SELECT * FROM TRANSACTIONS WHERE CREATE_TIME LIKE ? AND isdel=0 AND costdesc_id=? ORDER BY CREATE_LONGTIME ASC", new String[] { dayStr + "%", "" + costLevel });
		}
		return c;
	}

	public static boolean delete(Activity activity, Trade trade)
	{
		initDb(activity);
		try
		{
			ContentValues cv = new ContentValues();
			cv.put("isdel", 1);
			db.update("transactions", cv, "id = ?", new String[] { String.valueOf(trade.getId()) });
			trigger();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw new RuntimeException("修改transactions时异常," + ex.getMessage());
		}
		closeDb();
		return true;
	}

	public static boolean checkTradeUUIDExist(String uuid)
	{
		if (db == null)
		{
			Log.e("dao", "Not opened Db !");
			return false;
		}
		Cursor c = db.rawQuery("SELECT * FROM TRANSACTIONS WHERE UUID = ? AND isdel=0", new String[] { uuid });
		while (c != null && c.moveToNext())
		{
			return true;
		}
		return false;
	}
}
