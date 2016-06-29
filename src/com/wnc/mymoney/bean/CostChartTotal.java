package com.wnc.mymoney.bean;

/**
 * 用于图表统计的
 * 
 * @author cpr216
 * 
 */
public class CostChartTotal
{
    private DayRangePoint dayRangePoint;
    private int type;// 消费类型
    private double cost;// 消费金额

    public DayRangePoint getDayRangePoint()
    {
        return dayRangePoint;
    }

    public void setDayRangePoint(DayRangePoint dayRangePoint)
    {
        this.dayRangePoint = dayRangePoint;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public double getCost()
    {
        return cost;
    }

    public void setCost(double cost)
    {
        this.cost = cost;
    }

}
