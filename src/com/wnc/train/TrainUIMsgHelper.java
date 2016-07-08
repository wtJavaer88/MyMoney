package com.wnc.train;

import android.os.Message;
import android.os.Vibrator;

import com.wnc.basic.BasicDateUtil;
import com.wnc.mymoney.util.ToastUtil;

public class TrainUIMsgHelper
{
    public static TrainTicketActivity trainTicketActivity;
    public static Vibrator vibrator;

    public static void stopVibrator()
    {
        if (vibrator != null)
        {
            vibrator.cancel();
        }
    }

    public static void getTicket()
    {
        trainTicketActivity.getResult_Tv().setText("有票!");
        vibratorPhone();
    }

    public static void runMsg()
    {
        trainTicketActivity.getResult_Tv().setText(
                "正在检查..." + BasicDateUtil.getCurrentDateTimeString());
    }

    public static void noTicket(String errMsg)
    {
        trainTicketActivity.getResult_Tv().setText(errMsg);
        Thread thread = new Thread(new Runnable()
        {
            Message msg = new Message();

            @Override
            public void run()
            {
                try
                {

                    Thread.sleep(trainTicketActivity.ERR_SHOW_TIME);
                    msg.what = 3;
                    trainTicketActivity.getHandler_watch().sendMessage(msg);
                }
                catch (Exception ex)
                {
                    msg.what = 4;
                    trainTicketActivity.getHandler_watch().sendMessage(msg);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static void illegalParamAndReset(String msg)
    {
        trainTicketActivity.reset();
        TrainUIMsgHelper.noTicket(msg);
        ToastUtil.showShortToast(trainTicketActivity, msg);
    }

    public static void enableAllBt()
    {
        enableWatchBt(true);
        enableDateBt(true);
        enableTrainBt(true);
    }

    /**
     * 找到票后, 手机振动
     */
    private static void vibratorPhone()
    {

        long[] pattern =
        { 1000, 10000, 3000, 20000, 5000, 30000 }; // OFF/ON/OFF/ON......
        vibrator.vibrate(pattern, -1);// 禁止循环
    }

    public static void enableTrainBt(boolean b)
    {
        trainTicketActivity.getChooseTrain_Btn().setEnabled(b);
    }

    public static void enableDateBt(boolean b)
    {
        trainTicketActivity.getChooseDate_Btn().setEnabled(b);
    }

    public static void enableWatchBt(boolean b)
    {
        trainTicketActivity.getWatch_Btn().setEnabled(b);
    }

    public static void disableAllBt()
    {
        enableTrainBt(false);
        enableWatchBt(false);
        enableDateBt(false);
    }
}
