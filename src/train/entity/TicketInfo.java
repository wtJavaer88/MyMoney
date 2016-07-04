package train.entity;

public class TicketInfo
{
    private String trainCode;
    private int yzCount;// 硬座票
    private int wzCount;// 无座票
    private int yiCount;// 一等票
    private int erCount;// 二等票
    private String startTime;// 开车时间
    private String arriveTime;// 到达时间

    public String getTrainCode()
    {
        return trainCode;
    }

    public void setTrainCode(String trainCode)
    {
        this.trainCode = trainCode;
    }

    public int getYzCount()
    {
        return yzCount;
    }

    public void setYzCount(int yzCount)
    {
        this.yzCount = yzCount;
    }

    public int getWzCount()
    {
        return wzCount;
    }

    public void setWzCount(int wzCount)
    {
        this.wzCount = wzCount;
    }

    public int getYiCount()
    {
        return yiCount;
    }

    public void setYiCount(int yiCount)
    {
        this.yiCount = yiCount;
    }

    public int getErCount()
    {
        return erCount;
    }

    public void setErCount(int erCount)
    {
        this.erCount = erCount;
    }

    @Override
    public String toString()
    {
        return "TicketInfo [trainCode=" + trainCode + ", yzCount=" + yzCount
                + ", wzCount=" + wzCount + ", yiCount=" + yiCount
                + ", erCount=" + erCount + "]";
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getArriveTime()
    {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime)
    {
        this.arriveTime = arriveTime;
    }
}
