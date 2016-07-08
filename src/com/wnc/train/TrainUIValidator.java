package com.wnc.train;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;

public class TrainUIValidator
{
    public static boolean validStation(String startCityCode,
            String arriveCityCode)
    {
        if (BasicStringUtil.isNullString(startCityCode, arriveCityCode))
        {
            TrainUIMsgHelper.illegalParamAndReset("城市名不正确!");
            return false;
        }
        return true;
    }

    public static boolean validTime(String time)
    {

        if (BasicStringUtil.isNullString(time)
                || !BasicNumberUtil.isNumberString(time))
        {
            TrainUIMsgHelper.illegalParamAndReset("请输入一个数字!");
            return false;
        }
        if (BasicNumberUtil.getNumber(time) < 10)
        {
            TrainUIMsgHelper.illegalParamAndReset("监控时间不能小于10秒!");
            return false;
        }
        if (BasicNumberUtil.getNumber(time) > 3600)
        {
            TrainUIMsgHelper.illegalParamAndReset("监控时间不能大于1个小时!");
            return false;
        }
        return true;
    }
}
