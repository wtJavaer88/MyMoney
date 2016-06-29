package com.wnc.mymoney.bean;

public class NullPriceTotalBean extends PriceTotalBean
{
    public NullPriceTotalBean()
    {
        super(0, 0, 0);
    }

    public NullPriceTotalBean(double i, double o, double b)
    {
        super(i, o, b);
    }

    @Override
    public double getIn()
    {
        return this.in;
    }

    @Override
    public double getOut()
    {
        return this.out;
    }

    @Override
    public double getBalance()
    {
        return this.balance;
    }
}