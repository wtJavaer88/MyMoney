package com.wnc.train;

import java.util.ArrayList;
import java.util.List;

import train.entity.TicketInfo;
import train.parser.TicParser;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.wnc.basic.BasicNumberUtil;
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
        }
        else if (msg.what == 1)
        {
            System.out.println("msg.obj.toString():  " + msg.obj.toString());
            trainActivity.parseResult(msg.obj.toString());
        }
        else if (msg.what == 22)
        {
            System.out.println("已有保存列车班次信息!");
            String[] trains = (String[]) msg.obj;
            Intent localIntent = new Intent();
            localIntent.putExtra("trains", trains);
            localIntent.setClass(trainActivity, RadioButtonListActivity.class);
            trainActivity.startActivityForResult(localIntent, 100);
        }
        else if (msg.what == 2)
        {
            String result = msg.obj.toString();
            if (!trainActivity.checkResult(result))
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

                Intent localIntent = new Intent();
                if (patternStrings.size() > 0)
                {
                    String[] trains = patternStrings
                            .toArray(new String[patternStrings.size()]);

                    trainActivity.putNewTrains(trains);
                    localIntent.putExtra("trains", trains);
                    localIntent.setClass(trainActivity,
                            RadioButtonListActivity.class);
                    // trainActivity.startActivity(localIntent);
                    trainActivity.startActivityForResult(localIntent, 100);
                }
                else
                {
                    ToastUtil.showShortToast(trainActivity, "查不到两市间的列车信息!");
                }
            }

        }
        else if (msg.what == 3)
        {
            trainActivity.clearErrMsg();
        }

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
}
