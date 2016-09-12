package com.wnc.train;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;

import train.dao.StationDao;
import train.model.QueryModel;
import train.util.HttpsConnUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.ui.BaseVerActivity;
import com.wnc.mymoney.uihelper.Setting;
import com.wnc.mymoney.util.app.ToastUtil;
import com.wnc.mymoney.util.app.WheelDialogShowUtil;
import com.wnc.mymoney.util.common.TextFormatUtil;
import com.wnc.mymoney.widget.ComboBox;
import com.wnc.mymoney.widget.ComboBox.ListViewItemClickListener;
import com.wnc.mymoney.widget.MyToggle;
import com.wnc.mymoney.widget.MyToggle.OnToggleStateListener;

public class TrainTicketActivity extends BaseVerActivity implements
        UncaughtExceptionHandler
{
    private Button watch_Btn;
    private Button chooseTrain_Btn;
    private Button chooseDate_Btn;
    private Button clear_Btn;
    // 自定义开关对象
    private MyToggle toggle;

    private EditText fromStation_Et;
    private EditText toStation_Et;
    private TextView date_Tv;
    private TextView trains_Tv;
    private EditText trains_fre_Et;
    private TextView result_Tv;

    private Handler handler_watch = new WatchHandler(this);
    private final int DEFAULT_WATCH_DURATION = 30;
    public final int ERR_SHOW_TIME = 2000;// 错误消息显示时间
    private boolean firstWatch = true;
    private boolean abortSearch = false;

    private String fromCityCode;
    private String toCityCode;
    private String url;// WatchHandler中的reWatch中还会用到

    private ComboBox t_combobox;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_activity);
        Thread.setDefaultUncaughtExceptionHandler(this);

        initViews();
        // 对ui助手进行初始化
        initTrainUIMsgHelper();
    }

    private void initTrainUIMsgHelper()
    {
        TrainUIMsgHelper.trainTicketActivity = this;
        TrainUIMsgHelper.vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void initViews()
    {
        watch_Btn = (Button) findViewById(R.id.watch_btn);
        chooseTrain_Btn = (Button) findViewById(R.id.trains_choose_bt);
        chooseDate_Btn = (Button) findViewById(R.id.date_choose_bt);
        clear_Btn = (Button) findViewById(R.id.clearTrans_btn);
        t_combobox = (ComboBox) findViewById(R.id.timeComb);

        fromStation_Et = (EditText) findViewById(R.id.from_et);
        toStation_Et = (EditText) findViewById(R.id.to_et);
        trains_fre_Et = (EditText) findViewById(R.id.trains_fre);
        date_Tv = (TextView) findViewById(R.id.date_tv);
        trains_Tv = (TextView) findViewById(R.id.trains_tv);
        result_Tv = (TextView) findViewById(R.id.result_tv);

        date_Tv.setText(TextFormatUtil.addSeparatorToDay(BasicDateUtil
                .getCurrentDateString()));
        trains_fre_Et.setText("" + DEFAULT_WATCH_DURATION);
        t_combobox.setData(Arrays.asList("点我选择", "10", "20", "30", "40", "50",
                "60"));
        t_combobox.setListViewOnClickListener(new ListViewItemClickListener()
        {
            @Override
            public void onItemClick(int position)
            {
                if (BasicNumberUtil.isNumberString(t_combobox.getText()
                        .toString()))
                {
                    trains_fre_Et.setText(t_combobox.getText());
                }
            }
        });

        toggle = (MyToggle) findViewById(R.id.togSite);
        toggle.setImageRes(R.drawable.btn_switch, R.drawable.btn_switch,
                R.drawable.btn_slip);
        toggle.setToggleState(false);
        toggle.setOnToggleStateListener(new OnToggleStateListener()
        {
            @Override
            public void onToggleState(boolean state)
            {
                if (state)
                {
                    ToastUtil.showShortToast(getApplicationContext(), "开关开启");
                    Setting.setBACKUP_AUTO("true");
                }
                else
                {
                    ToastUtil.showShortToast(getApplicationContext(), "开关关闭");
                    Setting.setBACKUP_AUTO("false");
                }
            }
        });
    }

    public void exchangeCity(View v)
    {
        String city1 = fromStation_Et.getText().toString();
        String city2 = toStation_Et.getText().toString();
        fromStation_Et.setText(city2);
        toStation_Et.setText(city1);
    }

    public void watchTickets(View v)
    {
        abortSearch = false;
        TrainUIMsgHelper.disableAllUI();
        if (!TrainUIValidator.validTime(trains_fre_Et.getText().toString()))
        {
            return;
        }

        url = getUrl();
        if (BasicStringUtil.isNullString(url))
        {
            return;
        }

        watch();
    }

    public void chooseDate(View v)
    {
        WheelDialogShowUtil
                .showCurrDateDialog(
                        this,
                        new com.wnc.mymoney.uihelper.listener.AfterWheelChooseListener()
                        {
                            @Override
                            public void afterWheelChoose(Object... objs)
                            {

                                String day = objs[0].toString()
                                        .substring(0, 10);
                                if (!BasicDateUtil.isDateFormatTimeString(day,
                                        "yyyy-MM-dd"))
                                {
                                    ToastUtil.showLongToast(
                                            getApplicationContext(),
                                            "请检查是否有"
                                                    + TextFormatUtil
                                                            .addChnToDay(day)
                                                    + "这一天!");
                                }
                                else
                                {
                                    date_Tv.setText(day);
                                }
                            }

                        });
    }

    public void clearTrains(View v)
    {
        this.trains_Tv.setText("");
        CityTrainsHolder.lastSelIds = null;
    }

    public void stopSearch(View v)
    {
        abortSearch = true;
        this.result_Tv.setText("请点击监控开始查询!");
        TrainUIMsgHelper.stopVibrator();
        reset();
    }

    public void chooseTrains(View v)
    {
        TrainUIMsgHelper.disableAllUI();

        final String t_url = getUrl();
        if (BasicStringUtil.isNullString(t_url))
        {
            reset();
            return;
        }

        Thread thread = new Thread(new Runnable()
        {
            Message msg = new Message();

            @Override
            public void run()
            {
                String result = "";
                // String result =
                // "\"validateMessagesShowId\":\"_validatorMessage\",\"status\":true,\"httpstatus\":200,\"data\":[{\"queryLeftNewDTO\":{\"train_no\":\"65000K165600\",\"station_train_code\":\"K1656\",\"start_station_telecode\":\"BJQ\",\"start_station_name\":\"深圳东\",\"end_station_telecode\":\"XFN\",\"end_station_name\":\"襄阳\",\"from_station_telecode\":\"HSN\",\"from_station_name\":\"黄石\",\"to_station_telecode\":\"HKN\",\"to_station_name\":\"汉口\",\"start_time\":\"05:36\",\"arrive_time\":\"07:55\",\"day_difference\":\"0\",\"train_class_name\":\"\",\"lishi\":\"02:19\",\"canWebBuy\":\"Y\",\"lishiValue\":\"139\",\"yp_info\":\"1001953130400995000910019500923006550085\",\"control_train_day\":\"20991231\",\"start_train_date\":\"20160704\",\"seat_feature\":\"W3431333\",\"yp_ex\":\"10401030\",\"train_seat_feature\":\"3\",\"seat_types\":\"1413\",\"location_code\":\"Q9\",\"from_station_no\":\"12\",\"to_station_no\":\"14\",\"control_day\":59,\"sale_time\":\"1800\",\"is_support_card\":\"0\",\"controlled_train_flag\":\"0\",\"controlled_train_message\":\"正常车次，不受控\",\"gg_num\":\"--\",\"gr_num\":\"--\",\"qt_num\":\"--\",\"rw_num\":\"9\",\"rz_num\":\"--\",\"tz_num\":\"--\",\"wz_num\":\"有\",\"yb_num\":\"--\",\"yw_num\":\"有\",\"yz_num\":\"有\",\"ze_num\":\"--\",\"zy_num\":\"--\",\"swz_num\":\"--\"},\"secretStr\":\"MjAxNi0wNy0wNSMwMCNLMTY1NiMwMjoxOSMwNTozNiM2NTAwMEsxNjU2MDAjSFNOI0hLTiMwNzo1NSPpu4Tnn7Mj5rGJ5Y%2BjIzEyIzE0IzEwMDE5NTMxMzA0MDA5OTUwMDA5MTAwMTk1MDA5MjMwMDY1NTAwODUjUTkjMTQ2NzYxNjY3Mjg1OSMxNDYyNjE1MjAwMDAwIzFENjUzNTJCNDM2NTgxNkM4MkEyQ0Y2NzgxRjEzQzY5NDA0NjNDRkM1MjdCRjdERDUzNkM2QTY1\",\"buttonTextInfo\":\"预订\"},{\"queryLeftNewDTO\":{\"train_no\":\"5500000Z2710\",\"station_train_code\":\"Z26\",\"start_station_telecode\":\"SNH\",\"start_station_name\":\"上海南\",\"end_station_telecode\":\"WCN\",\"end_station_name\":\"武昌\",\"from_station_telecode\":\"HSN\",\"from_station_name\":\"黄石\",\"to_station_telecode\":\"WCN\",\"to_station_name\":\"武昌\",\"start_time\":\"06:22\",\"arrive_time\":\"07:33\",\"day_difference\":\"0\",\"train_class_name\":\"\",\"lishi\":\"01:11\",\"canWebBuy\":\"Y\",\"lishiValue\":\"71\",\"yp_info\":\"40096500273006250186\",\"control_train_day\":\"20991231\",\"start_train_date\":\"20160704\",\"seat_feature\":\"3343\",\"yp_ex\":\"4030\",\"train_seat_feature\":\"3\",\"seat_types\":\"43\",\"location_code\":\"H3\",\"from_station_no\":\"03\",\"to_station_no\":\"05\",\"control_day\":59,\"sale_time\":\"1800\",\"is_support_card\":\"0\",\"controlled_train_flag\":\"0\",\"controlled_train_message\":\"正常车次，不受控\",\"gg_num\":\"--\",\"gr_num\":\"--\",\"qt_num\":\"--\",\"rw_num\":\"有\",\"rz_num\":\"--\",\"tz_num\":\"--\",\"wz_num\":\"--\",\"yb_num\":\"--\",\"yw_num\":\"有\",\"yz_num\":\"--\",\"ze_num\":\"--\",\"zy_num\":\"--\",\"swz_num\":\"--\"},\"secretStr\":\"MjAxNi0wNy0wNSMwMCNaMjYjMDE6MTEjMDY6MjIjNTUwMDAwMFoyNzEwI0hTTiNXQ04jMDc6MzMj6buE55%2BzI%2BatpuaYjCMwMyMwNSM0MDA5NjUwMDI3MzAwNjI1MDE4NiNIMyMxNDY3NjE2NjcyODU5IzE0NjI2MTUyMDAwMDAjQTZFNDc5RTczRTU0RUU3NzM1QkVFRUI0NDY3NjM3ODRFRTUxNTg0NjQ1MTZDMzZDOTNBRjhCMjE%3D\",\"buttonTextInfo\":\"预订\"},{\"queryLeftNewDTO\":{\"train_no\":\"5e00000Z3210\",\"station_train_code\":\"Z32\",\"start_station_telecode\":\"NGH\",\"start_station_name\":\"宁波\",\"end_station_telecode\":\"WCN\",\"end_station_name\":\"武昌\",\"from_station_telecode\":\"HSN\",\"from_station_name\":\"黄石\",\"to_station_telecode\":\"WCN\",\"to_station_name\":\"武昌\",\"start_time\":\"06:28\",\"arrive_time\":\"07:39\",\"day_difference\":\"0\",\"train_class_name\":\"\",\"lishi\":\"01:11\",\"canWebBuy\":\"Y\",\"lishiValue\":\"71\",\"yp_info\":\"1001653036400965002710016500043006250058\",\"control_train_day\":\"20991231\",\"start_train_date\":\"20160704\",\"seat_feature\":\"W3431333\",\"yp_ex\":\"10401030\",\"train_seat_feature\":\"3\",\"seat_types\":\"1413\",\"location_code\":\"H3\",\"from_station_no\":\"05\",\"to_station_no\":\"07\",\"control_day\":59,\"sale_time\":\"1800\",\"is_support_card\":\"0\",\"controlled_train_flag\":\"0\",\"controlled_train_message\":\"正常车次，不受控\",\"gg_num\":\"--\",\"gr_num\":\"--\",\"qt_num\":\"--\",\"rw_num\":\"有\",\"rz_num\":\"--\",\"tz_num\":\"--\",\"wz_num\":\"有\",\"yb_num\":\"--\",\"yw_num\":\"有\",\"yz_num\":\"4\",\"ze_num\":\"--\",\"zy_num\":\"--\",\"swz_num\":\"--\"},\"secretStr\":\"MjAxNi0wNy0wNSMwMCNaMzIjMDE6MTEjMDY6MjgjNWUwMDAwMFozMjEwI0hTTiNXQ04jMDc6Mzkj6buE55%2BzI%2BatpuaYjCMwNSMwNyMxMDAxNjUzMDM2NDAwOTY1MDAyNzEwMDE2NTAwMDQzMDA2MjUwMDU4I0gzIzE0Njc2MTY2NzI4NTkjMTQ2MjYxNTIwMDAwMCNDQjQxMUQ2MkU0MUM4QTFDOUQ2RTU5MUMyNEUxQ0QwM0NFND";

                if (CityTrainsHolder.containsCityInfo(getTwoCityCodeWithDate()))
                {
                    msg.what = 22;
                    msg.obj = CityTrainsHolder
                            .getTrains(getTwoCityCodeWithDate());
                    handler_watch.sendMessage(msg);

                }
                else
                {
                    try
                    {
                        result = HttpsConnUtil.requestHTTPSPage(
                                TrainTicketActivity.this, t_url);
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
            }

        });
        thread.setDaemon(true);
        thread.start();
    }

    private String getUrl()
    {
        fromCityCode = getCityCode(fromStation_Et.getText().toString());
        toCityCode = getCityCode(toStation_Et.getText().toString());
        if (!TrainUIValidator.validStation(fromCityCode, toCityCode))
        {
            return "";
        }
        return new QueryModel().build(date_Tv.getText().toString(),
                fromCityCode, toCityCode);
    }

    private String getCityCode(String cityName)
    {
        if (BasicStringUtil.isNullString(cityName))
        {
            return "";
        }
        return StationDao.getCityCode(TrainTicketActivity.this, cityName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        TrainUIMsgHelper.enableAllUI();
        if (requestCode == RadioButtonListActivity.RETURN_CODE && data != null)
        {
            String codes = data
                    .getStringExtra(RadioButtonListActivity.EXTRA_TRAIN_CODES);
            this.trains_Tv.setText(codes);
        }
    }

    public void watch()
    {
        TrainUIMsgHelper.runMsg();
        Thread thread = new Thread(new Runnable()
        {
            Message msg = new Message();

            @Override
            public void run()
            {
                try
                {
                    if (!firstWatch)
                    {
                        Thread.sleep(1000 * Integer.parseInt(trains_fre_Et
                                .getText().toString()));
                    }
                    else
                    {
                        firstWatch = false;
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                if (abortSearch)
                {
                    return;
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

    public void reset()
    {
        this.firstWatch = true;
        TrainUIMsgHelper.enableAllUI();
    }

    public String getTwoCityCodeWithDate()
    {
        return fromCityCode + "-" + toCityCode + this.date_Tv.getText();
    }

    public String getTwoCityNameWithDate()
    {
        return this.fromStation_Et.getText() + " - "
                + this.toStation_Et.getText() + "  " + this.date_Tv.getText();
    }

    public String[] getSelTrains()
    {
        return trains_Tv.getText().toString().split(",");
    }

    public TextView getResult_Tv()
    {
        return result_Tv;
    }

    public Button getChooseTrain_Btn()
    {
        return chooseTrain_Btn;
    }

    public Button getChooseDate_Btn()
    {
        return chooseDate_Btn;
    }

    public Button getWatch_Btn()
    {
        return watch_Btn;
    }

    public Button getClear_Btn()
    {
        return clear_Btn;
    }

    public EditText getTrains_fre_Et()
    {
        return this.trains_fre_Et;
    }

    public ComboBox getCombox()
    {
        return t_combobox;
    }

    public boolean isAbortSearch()
    {
        return abortSearch;
    }

    public Handler getHandler_watch()
    {
        return this.handler_watch;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        Log.i("AAA", "uncaughtException   " + ex);
        for (StackTraceElement o : ex.getStackTrace())
        {
            System.out.println(o.toString());
        }
    }

    public boolean getMustSite()
    {
        return this.toggle.getToggleState();
    }

}