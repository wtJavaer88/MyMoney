package com.wnc.mymoney.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wnc.mymoney.bean.DayRangePoint;
import com.wnc.mymoney.bean.DayTranTotal;
import com.wnc.mymoney.bean.NullPriceTotalBean;
import com.wnc.mymoney.bean.PriceTotalBean;
import com.wnc.mymoney.util.TOTAL_RANGE;

public class OnStartUpDataUtil
{
    private static final DayRangePoint monthPoint = TOTAL_RANGE.CURRMONTH
            .create();
    private static final DayRangePoint weekPoint = TOTAL_RANGE.CURRWEEK
            .create();
    private static final DayRangePoint dayPoint = TOTAL_RANGE.CURRDAY.create();

    private static PriceTotalBean mPriceTotalBean;
    private static PriceTotalBean wPriceTotalBean;
    private static PriceTotalBean dPriceTotalBean;

    public static double getCurrMonthOutBound()
    {
        return getPriceTotalInrange(monthPoint).getOut();
    }

    public static double getCurrMonthInBound()
    {
        return getPriceTotalInrange(monthPoint).getIn();
    }

    public static double getCurrWeekOutBound()
    {
        return getPriceTotalInrange(weekPoint).getOut();
    }

    public static double getCurrWeekInBound()
    {
        return getPriceTotalInrange(weekPoint).getIn();
    }

    public static double getCurrDayOutBound()
    {

        return getPriceTotalInrange(dayPoint).getOut();
    }

    public static double getCurrDayInBound()
    {
        return getPriceTotalInrange(dayPoint).getIn();
    }

    public static PriceTotalBean getPriceTotalInrange(
            DayRangePoint dayRangePoint)
    {
        if (dayRangePoint == monthPoint)
        {
            if (mPriceTotalBean == null)
            {
                mPriceTotalBean = getTotalBean(dayRangePoint);
            }
            return mPriceTotalBean;
        }
        else if (dayRangePoint == weekPoint)
        {
            if (wPriceTotalBean == null)
            {
                wPriceTotalBean = getTotalBean(dayRangePoint);
            }
            return wPriceTotalBean;
        }

        else if (dayRangePoint == dayPoint)
        {
            if (dPriceTotalBean == null)
            {
                dPriceTotalBean = getTotalBean(dayRangePoint);
            }
            return dPriceTotalBean;
        }
        return new NullPriceTotalBean();
    }

    private static PriceTotalBean getTotalBean(DayRangePoint dayRangePoint)
    {
        List<DayTranTotal> totals = TransactionsDao.getAllTranTotalInRange(
                dayRangePoint.getStartDay(), dayRangePoint.getEndDay());
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

            inboundSum += total.getInbound();
            outboundSum += total.getOutbound();
            balanceSum += total.getBalance();
        }
        return new PriceTotalBean(inboundSum, outboundSum, balanceSum);
    }

    public static void restart()
    {
        mPriceTotalBean = null;
        wPriceTotalBean = null;
        dPriceTotalBean = null;
    }
}
