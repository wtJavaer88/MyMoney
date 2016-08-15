package com.wnc.train;

import train.parser.TicParser;
import train.util.TrainsHelper;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.util.app.ToastUtil;

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
            System.out.println("msg1.obj.toString():  " + msg.obj.toString());
            String result = msg.obj.toString();
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
            parseResult(ticParser, abortSearch);
        }
        else if (msg.what == 22)
        {
            showTainList((String[]) msg.obj);
        }
        else if (msg.what == 2)
        {
            // 查列车信息, 然后跳到列表activity
            String result = msg.obj.toString();
            System.out.println("msg2.obj.toString():  " + msg.obj.toString());
            if (!checkResult(result))
            {
                return;
            }

            TicParser ticParser = new TicParser(result);
            ticParser.refresh();
            ticParser.parse();

            if (ticParser.getTicketInfos() != null)
            {
                String[] trains = TrainsHelper.getTrainsArr(ticParser);
                if (trains != null)
                {
                    holderTrains(trains);
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

    private void holderTrains(String[] trains)
    {
        CityTrainsHolder.putTrainsBetweenCities(
                trainActivity.getTwoCityCodeWithDate(), trains);
    }

    private void showTainList(String[] trains)
    {
        Intent localIntent = new Intent();
        localIntent.putExtra(RadioButtonListActivity.EXTRA_TRAINS, trains);
        localIntent.putExtra(RadioButtonListActivity.EXTRA_CITIES,
                trainActivity.getTwoCityNameWithDate());
        localIntent.setClass(trainActivity, RadioButtonListActivity.class);
        trainActivity.startActivityForResult(localIntent, 100);
    };

    private void clearErrMsg()
    {
        TextView result_Tv = trainActivity.getResult_Tv();
        if (!result_Tv.getText().toString().contains("正在检查"))
        {
            result_Tv.setText("");
        }
    }

    private void parseResult(TicParser ticParser, boolean abortSearch)
    {
        // 第一次搜索的时候, 如果之前没有保存列表, 此时就应该保存一份,以免非要点击列表按钮触发
        if (!CityTrainsHolder.containsCityInfo(trainActivity
                .getTwoCityCodeWithDate()))
        {
            String[] trains = TrainsHelper.getTrainsArr(ticParser);
            if (trains != null)
            {
                holderTrains(trains);
            }
        }

        System.out.println("15 getSelTrains:" + trainActivity.getSelTrains());
        for (String train : trainActivity.getSelTrains())
        {
            ticParser.addAssignTrain(train.trim().toUpperCase());
        }
        if (ticParser.getTicketInfos().size() > 0)
        {
            System.out.println("15:" + ticParser.getTicketInfos());
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
