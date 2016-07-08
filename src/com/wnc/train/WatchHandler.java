package com.wnc.train;

import java.util.ArrayList;
import java.util.List;

import train.entity.TicketInfo;
import train.parser.TicParser;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.util.ToastUtil;

public class WatchHandler extends Handler
{
    TrainTicketActivity trainActivity;

    public WatchHandler(TrainTicketActivity trainActivity)
    {
        this.trainActivity = trainActivity;
    }

    @Override
    public void handleMessage(Message msg)
    {
        if (msg.what == 0)
        {
            ToastUtil.showShortToast(trainActivity, "无法从网络获取数据!");
            trainActivity.reset();
        }
        else if (msg.what == 1)
        {
            // System.out.println("msg.obj.toString():  " + msg.obj.toString());
            parseResult(msg.obj.toString());
        }
        else if (msg.what == 22)
        {
            showTainList((String[]) msg.obj);
        }
        else if (msg.what == 2)
        {
            // 查列车信息, 然后跳到列表activity
            String result = msg.obj.toString();
            if (!checkResult(result))
            {
                return;
            }

            TicParser ticParser = new TicParser(result);
            ticParser.refresh();
            ticParser.parse();
            List<String> patternStrings = new ArrayList<String>();
            if (ticParser.getTicketInfos() != null)
            {
                for (TicketInfo ticketInfo : ticParser.getTicketInfos())
                {
                    int costTime = getCostMinutes(ticketInfo);
                    patternStrings.add(ticketInfo.getTrainCode() + "  ["
                            + ticketInfo.getStartTime() + " -- "
                            + ticketInfo.getArriveTime() + "] " + costTime
                            + "分钟");

                }

                if (patternStrings.size() > 0)
                {
                    String[] trains = patternStrings
                            .toArray(new String[patternStrings.size()]);

                    trainActivity.putNewTrains(trains);
                    showTainList(trains);
                }
                else
                {
                    ToastUtil.showShortToast(trainActivity, "查不到两市间的列车信息!");
                    trainActivity.reset();
                }
            }

        }
        else if (msg.what == 3)
        {
            clearErrMsg();
        }

    }

    private void showTainList(String[] trains)
    {
        Intent localIntent = new Intent();
        localIntent.putExtra("trains", trains);
        localIntent.setClass(trainActivity, RadioButtonListActivity.class);
        trainActivity.startActivityForResult(localIntent, 100);
    };

    private int getCostMinutes(TicketInfo ticketInfo)
    {
        String startTime = ticketInfo.getStartTime().replace(":", "");
        String arriveTime = ticketInfo.getArriveTime().replace(":", "");
        int h = 0;
        int m = 0;
        try
        {
            if (BasicNumberUtil.isNumberString(startTime)
                    && BasicNumberUtil.isNumberString(startTime))
            {

                int start = Integer.parseInt(startTime);
                int arrive = Integer.parseInt(arriveTime);
                if (start > arrive)
                {
                    arrive = arrive + 2400;
                }
                h = (arrive / 100 - start / 100);
                m = (arrive % 100 - start % 100);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
        return h * 60 + m;
    }

    private void clearErrMsg()
    {
        TextView result_Tv = trainActivity.getResult_Tv();
        if (!result_Tv.getText().toString().contains("正在检查"))
        {
            result_Tv.setText("");
        }
    }

    private void parseResult(String result)
    {
        boolean abortSearch = trainActivity.isAbortSearch();
        if (abortSearch)
        {
            return;
        }
        if (!checkResult(result))
        {
            return;
        }

        TicParser ticParser = new TicParser(result);
        ticParser.refresh();
        ticParser.parse();
        for (String train : trainActivity.getSelTrains())
        {
            ticParser.addAssignTrain(train.trim().toUpperCase());
        }
        if (ticParser.getTicketInfos().size() > 0)
        {
            if (ticParser.checkAvaliable())
            {
                if (!abortSearch)
                {
                    TrainUIMsgHelper.getTicket();
                }
            }
            else
            {
                if (!abortSearch)
                {
                    reWatch("指定班次已经没有任何余票!");
                }
            }
        }
        else
        {
            if (!abortSearch)
            {
                reWatch("没查到指定的班次!");
            }
        }

    }

    private void reWatch(String msg)
    {
        TrainUIMsgHelper.noTicket(msg);
        ToastUtil.showShortToast(trainActivity, msg);
        trainActivity.watch();
    }

    public boolean checkResult(String result)
    {
        boolean b = true;
        if (BasicStringUtil.isNullString(result))
        {
            TrainUIMsgHelper.illegalParamAndReset("获取数据失败!");
            b = false;
        }
        else if (result.contains("不在预售日期"))
        {
            TrainUIMsgHelper.illegalParamAndReset("你选择的日期不在预售日期内!");
            b = false;
        }
        if (!b)
        {
            trainActivity.reset();
        }
        return b;
    }
}
