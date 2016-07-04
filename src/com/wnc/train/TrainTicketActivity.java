package com.wnc.train;

import train.dao.StationDao;
import train.model.QueryModel;
import train.parser.TicParser;
import train.util.HttpsConnUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.ui.helper.AfterWheelChooseListener;
import com.wnc.mymoney.ui.helper.WheelDialogShowUtil;
import com.wnc.mymoney.util.TextFormatUtil;
import com.wnc.mymoney.util.ToastUtil;

public class TrainTicketActivity extends Activity implements OnClickListener
{
    private Button watch_Btn;
    private Button chooseTrain_Btn;
    private EditText startStation_Et;
    private EditText destStation_Et;
    private EditText date_Et;
    private TextView trains_Tv;
    private EditText trains_fre_Et;
    private TextView result_Tv;

    final int watchDuration = 30;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_activity);
        initViews();
    }

    public void initViews()
    {
        watch_Btn = (Button) findViewById(R.id.watch_btn);
        chooseTrain_Btn = (Button) findViewById(R.id.trains_choose_bt);

        startStation_Et = (EditText) findViewById(R.id.start_et);
        destStation_Et = (EditText) findViewById(R.id.dest_et);
        date_Et = (EditText) findViewById(R.id.date_et);
        trains_fre_Et = (EditText) findViewById(R.id.trains_fre);
        trains_Tv = (TextView) findViewById(R.id.trains_tv);
        result_Tv = (TextView) findViewById(R.id.result_tv);
        watch_Btn.setOnClickListener(this);

        date_Et.setText(TextFormatUtil.addSeparatorToDay(BasicDateUtil
                .getCurrentDateString()));

        trains_fre_Et.setText("" + watchDuration);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.watch_btn)
        {
            watch(0);
        }
    }

    private Handler handler_watch = new WatchHandler(this);

    public void chooseDate(View v)
    {
        WheelDialogShowUtil.showCurrDateDialog(this,
                new AfterWheelChooseListener()
                {
                    @Override
                    public void afterWheelChoose(Object... objs)
                    {

                        String day = objs[0].toString().substring(0, 10);
                        if (!BasicDateUtil.isDateFormatTimeString(day,
                                "yyyy-MM-dd"))
                        {
                            ToastUtil.showLongToast(getApplicationContext(),
                                    "请检查是否有" + TextFormatUtil.addChnToDay(day)
                                            + "这一天!");
                        }
                        else
                        {
                            // System.out.println("日期:" + day);
                            date_Et.setText(day);
                        }

                    }

                });
    }

    public void clearData(View v)
    {
        this.trains_Tv.setText("");
        this.result_Tv.setText("");
    }

    public void chooseTrains(View v)
    {
        final String url = getUrl();
        if (BasicStringUtil.isNullString(url))
        {
            enableTrainBt(true);
            return;
        }
        enableTrainBt(false);

        Thread thread = new Thread(new Runnable()
        {
            Message msg = new Message();

            @Override
            public void run()
            {
                String result = "";
                // String result =
                // "\"validateMessagesShowId\":\"_validatorMessage\",\"status\":true,\"httpstatus\":200,\"data\":[{\"queryLeftNewDTO\":{\"train_no\":\"65000K165600\",\"station_train_code\":\"K1656\",\"start_station_telecode\":\"BJQ\",\"start_station_name\":\"深圳东\",\"end_station_telecode\":\"XFN\",\"end_station_name\":\"襄阳\",\"from_station_telecode\":\"HSN\",\"from_station_name\":\"黄石\",\"to_station_telecode\":\"HKN\",\"to_station_name\":\"汉口\",\"start_time\":\"05:36\",\"arrive_time\":\"07:55\",\"day_difference\":\"0\",\"train_class_name\":\"\",\"lishi\":\"02:19\",\"canWebBuy\":\"Y\",\"lishiValue\":\"139\",\"yp_info\":\"1001953130400995000910019500923006550085\",\"control_train_day\":\"20991231\",\"start_train_date\":\"20160704\",\"seat_feature\":\"W3431333\",\"yp_ex\":\"10401030\",\"train_seat_feature\":\"3\",\"seat_types\":\"1413\",\"location_code\":\"Q9\",\"from_station_no\":\"12\",\"to_station_no\":\"14\",\"control_day\":59,\"sale_time\":\"1800\",\"is_support_card\":\"0\",\"controlled_train_flag\":\"0\",\"controlled_train_message\":\"正常车次，不受控\",\"gg_num\":\"--\",\"gr_num\":\"--\",\"qt_num\":\"--\",\"rw_num\":\"9\",\"rz_num\":\"--\",\"tz_num\":\"--\",\"wz_num\":\"有\",\"yb_num\":\"--\",\"yw_num\":\"有\",\"yz_num\":\"有\",\"ze_num\":\"--\",\"zy_num\":\"--\",\"swz_num\":\"--\"},\"secretStr\":\"MjAxNi0wNy0wNSMwMCNLMTY1NiMwMjoxOSMwNTozNiM2NTAwMEsxNjU2MDAjSFNOI0hLTiMwNzo1NSPpu4Tnn7Mj5rGJ5Y%2BjIzEyIzE0IzEwMDE5NTMxMzA0MDA5OTUwMDA5MTAwMTk1MDA5MjMwMDY1NTAwODUjUTkjMTQ2NzYxNjY3Mjg1OSMxNDYyNjE1MjAwMDAwIzFENjUzNTJCNDM2NTgxNkM4MkEyQ0Y2NzgxRjEzQzY5NDA0NjNDRkM1MjdCRjdERDUzNkM2QTY1\",\"buttonTextInfo\":\"预订\"},{\"queryLeftNewDTO\":{\"train_no\":\"5500000Z2710\",\"station_train_code\":\"Z26\",\"start_station_telecode\":\"SNH\",\"start_station_name\":\"上海南\",\"end_station_telecode\":\"WCN\",\"end_station_name\":\"武昌\",\"from_station_telecode\":\"HSN\",\"from_station_name\":\"黄石\",\"to_station_telecode\":\"WCN\",\"to_station_name\":\"武昌\",\"start_time\":\"06:22\",\"arrive_time\":\"07:33\",\"day_difference\":\"0\",\"train_class_name\":\"\",\"lishi\":\"01:11\",\"canWebBuy\":\"Y\",\"lishiValue\":\"71\",\"yp_info\":\"40096500273006250186\",\"control_train_day\":\"20991231\",\"start_train_date\":\"20160704\",\"seat_feature\":\"3343\",\"yp_ex\":\"4030\",\"train_seat_feature\":\"3\",\"seat_types\":\"43\",\"location_code\":\"H3\",\"from_station_no\":\"03\",\"to_station_no\":\"05\",\"control_day\":59,\"sale_time\":\"1800\",\"is_support_card\":\"0\",\"controlled_train_flag\":\"0\",\"controlled_train_message\":\"正常车次，不受控\",\"gg_num\":\"--\",\"gr_num\":\"--\",\"qt_num\":\"--\",\"rw_num\":\"有\",\"rz_num\":\"--\",\"tz_num\":\"--\",\"wz_num\":\"--\",\"yb_num\":\"--\",\"yw_num\":\"有\",\"yz_num\":\"--\",\"ze_num\":\"--\",\"zy_num\":\"--\",\"swz_num\":\"--\"},\"secretStr\":\"MjAxNi0wNy0wNSMwMCNaMjYjMDE6MTEjMDY6MjIjNTUwMDAwMFoyNzEwI0hTTiNXQ04jMDc6MzMj6buE55%2BzI%2BatpuaYjCMwMyMwNSM0MDA5NjUwMDI3MzAwNjI1MDE4NiNIMyMxNDY3NjE2NjcyODU5IzE0NjI2MTUyMDAwMDAjQTZFNDc5RTczRTU0RUU3NzM1QkVFRUI0NDY3NjM3ODRFRTUxNTg0NjQ1MTZDMzZDOTNBRjhCMjE%3D\",\"buttonTextInfo\":\"预订\"},{\"queryLeftNewDTO\":{\"train_no\":\"5e00000Z3210\",\"station_train_code\":\"Z32\",\"start_station_telecode\":\"NGH\",\"start_station_name\":\"宁波\",\"end_station_telecode\":\"WCN\",\"end_station_name\":\"武昌\",\"from_station_telecode\":\"HSN\",\"from_station_name\":\"黄石\",\"to_station_telecode\":\"WCN\",\"to_station_name\":\"武昌\",\"start_time\":\"06:28\",\"arrive_time\":\"07:39\",\"day_difference\":\"0\",\"train_class_name\":\"\",\"lishi\":\"01:11\",\"canWebBuy\":\"Y\",\"lishiValue\":\"71\",\"yp_info\":\"1001653036400965002710016500043006250058\",\"control_train_day\":\"20991231\",\"start_train_date\":\"20160704\",\"seat_feature\":\"W3431333\",\"yp_ex\":\"10401030\",\"train_seat_feature\":\"3\",\"seat_types\":\"1413\",\"location_code\":\"H3\",\"from_station_no\":\"05\",\"to_station_no\":\"07\",\"control_day\":59,\"sale_time\":\"1800\",\"is_support_card\":\"0\",\"controlled_train_flag\":\"0\",\"controlled_train_message\":\"正常车次，不受控\",\"gg_num\":\"--\",\"gr_num\":\"--\",\"qt_num\":\"--\",\"rw_num\":\"有\",\"rz_num\":\"--\",\"tz_num\":\"--\",\"wz_num\":\"有\",\"yb_num\":\"--\",\"yw_num\":\"有\",\"yz_num\":\"4\",\"ze_num\":\"--\",\"zy_num\":\"--\",\"swz_num\":\"--\"},\"secretStr\":\"MjAxNi0wNy0wNSMwMCNaMzIjMDE6MTEjMDY6MjgjNWUwMDAwMFozMjEwI0hTTiNXQ04jMDc6Mzkj6buE55%2BzI%2BatpuaYjCMwNSMwNyMxMDAxNjUzMDM2NDAwOTY1MDAyNzEwMDE2NTAwMDQzMDA2MjUwMDU4I0gzIzE0Njc2MTY2NzI4NTkjMTQ2MjYxNTIwMDAwMCNDQjQxMUQ2MkU0MUM4QTFDOUQ2RTU5MUMyNEUxQ0QwM0NFND";
                try
                {
                    result = HttpsConnUtil.requestHTTPSPage(
                            TrainTicketActivity.this, url);
                    msg.what = 2;
                    msg.obj = result;
                    handler_watch.sendMessage(msg);
                }
                catch (Exception ex)
                {
                    msg.what = 0;
                    handler_watch.sendMessage(msg);
                }
            }

        });
        thread.setDaemon(true);
        thread.start();
    }

    private String getUrl()
    {
        String startCityCode = getCityCode(startStation_Et.getText().toString());
        String arriveCityCode = getCityCode(destStation_Et.getText().toString());

        if (!validStation(startCityCode, arriveCityCode))
        {
            return "";
        }
        QueryModel queryModel = new QueryModel();
        return queryModel.build(date_Et.getText().toString(), startCityCode,
                arriveCityCode);
    }

    private void watch(final int count)
    {
        if (!validTime())
        {
            return;
        }

        final String url = getUrl();
        if (BasicStringUtil.isNullString(url))
        {
            return;
        }
        clearTicket();
        Thread thread = new Thread(new Runnable()
        {
            Message msg = new Message();

            @Override
            public void run()
            {
                try
                {
                    if (count > 0)
                    {
                        Thread.sleep(1000 * Integer.parseInt(trains_fre_Et
                                .getText().toString()));
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                String result = "";
                try
                {
                    result = HttpsConnUtil.requestHTTPSPage(
                            TrainTicketActivity.this, url);
                    msg.what = 1;
                    msg.obj = result;
                    handler_watch.sendMessage(msg);
                }
                catch (Exception ex)
                {
                    msg.what = 0;
                    handler_watch.sendMessage(msg);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private String getCityCode(String cityName)
    {
        if (BasicStringUtil.isNullString(cityName))
        {
            return "";
        }
        return StationDao.getCityCode(TrainTicketActivity.this, cityName);
    }

    private boolean validStation(String startCityCode, String arriveCityCode)
    {
        if (BasicStringUtil.isNullString(startCityCode, arriveCityCode))
        {
            ToastUtil.showShortToast(TrainTicketActivity.this, "城市名不正确!");
            return false;
        }
        return true;
    }

    private boolean validTime()
    {
        String time = trains_fre_Et.getText().toString();
        if (BasicStringUtil.isNullString(time)
                || !BasicNumberUtil.isNumberString(time))
        {
            ToastUtil.showShortToast(this, "请输入一个数字!");
            return false;
        }
        if (BasicNumberUtil.getNumber(time) < 10)
        {
            ToastUtil.showShortToast(this, "监控时间不能小于10秒!");
            return false;
        }
        if (BasicNumberUtil.getNumber(time) > 3600)
        {
            ToastUtil.showShortToast(this, "监控时间不能大于1个小时!");
            return false;
        }
        return true;
    }

    public void parseResult(String result)
    {
        if (!checkResult(result))
        {
            return;
        }

        TicParser ticParser = new TicParser(result);
        ticParser.refresh();
        ticParser.parse();

        for (String train : trains_Tv.getText().toString().split(","))
        {
            ticParser.addAssignTrain(train.trim().toUpperCase());
        }
        if (ticParser.getTicketInfos().size() > 0)
        {
            if (ticParser.checkAvaliable())
            {
                getTicket();
                vibratorPhone();
            }
            else
            {
                noTicket("指定班次已经没有任何余票!");
                ToastUtil.showShortToast(this, "指定班次已经没有任何余票!");
                watch(1);
            }
        }
        else
        {
            noTicket("没查到指定的班次!");
            ToastUtil.showShortToast(this, "没查到指定的班次!");
            watch(1);
        }

    }

    public void enableTrainBt(boolean b)
    {
        chooseTrain_Btn.setEnabled(b);
    }

    public boolean checkResult(String result)
    {
        if (BasicStringUtil.isNullString(result))
        {
            noTicket("获取数据失败!");
            ToastUtil.showShortToast(this, "获取数据失败!");
            return false;
        }
        else if (result.contains("不在预售日期"))
        {
            noTicket("你选择的日期不在预售日期内!");
            ToastUtil.showLongToast(this, "你选择的日期不在预售日期内!");
            return false;
        }
        return true;
    }

    /**
     * 找到票后, 手机振动
     */
    public void vibratorPhone()
    {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern =
        { 1000, 10000, 3000, 20000, 5000, 30000 }; // OFF/ON/OFF/ON......
        vibrator.vibrate(pattern, -1);// 禁止循环
    }

    private void getTicket()
    {
        result_Tv.setText("有票!");
    }

    private void clearTicket()
    {
        result_Tv.setText("正在检查..." + BasicDateUtil.getCurrentDateTimeString());
    }

    void noTicket(String errMsg)
    {
        result_Tv.setText(errMsg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        enableTrainBt(true);
        if (requestCode == 100 && data != null)
        {
            String codes = data.getStringExtra("selTrainCodes");
            // System.out.println("back:  " + codes);
            this.trains_Tv.setText(codes);
        }
    }
}