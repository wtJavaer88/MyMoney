package com.wnc.mymoney.bean;

public class PriceTotalBean
{
    double in;
    double out;
    double balance;

    public PriceTotalBean()
    {

    }

    public PriceTotalBean(double i, double o, double b)
    {
        this.in = i;
        this.out = o;
        this.balance = b;
    }

    public double getIn()
    {
        return this.in;
    }

    public void setIn(double in)
    {
        this.in = in;
    }

    public double getOut()
    {
        return this.out;
    }

    public void setOut(double out)
    {
        this.out = out;
    }

    public double getBalance()
    {
        return this.balance;
    }

    public void setBalance(double balance)
    {
        this.balance = balance;
    }
}