package com.wnc.mymoney.test;

import train.model.QueryModel;
import train.model.StationDao;
import train.parser.TicParser;
import train.util.HttpsConnUtil;
import android.app.Activity;
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
import com.wnc.mymoney.R;

public class TrainTicketActivity extends Activity implements OnClickListener
{
    private Button watch_Btn;
    private EditText startStation_Et;
    private EditText destStation_Et;
    private EditText date_Et;
    private EditText trains_Et;

    private TextView result_Tv;

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
        startStation_Et = (EditText) findViewById(R.id.start_et);
        destStation_Et = (EditText) findViewById(R.id.dest_et);
        date_Et = (EditText) findViewById(R.id.date_et);
        trains_Et = (EditText) findViewById(R.id.trains_et);
        result_Tv = (TextView) findViewById(R.id.result_tv);
        watch_Btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.watch_btn)
        {
            watch(0);
        }
    }

    private Handler handler_watch = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == 1)
            {
                parseResult(msg.obj.toString());
            }
        };
    };

    private void watch(final int count)
    {
        clearTicket();
        QueryModel queryModel = new QueryModel();
        final String url = queryModel.build(date_Et.getText().toString(),
                StationDao.getCityCode(TrainTicketActivity.this,
                        startStation_Et.getText().toString()), StationDao
                        .getCityCode(TrainTicketActivity.this, destStation_Et
                                .getText().toString()));
        new Thread(new Runnable()
        {
            Message msg = new Message();

            @Override
            public void run()
            {
                try
                {
                    if (count > 0)
                    {
                        Thread.sleep(20000);
                    }
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String result = HttpsConnUtil.requestHTTPSPage(
                        TrainTicketActivity.this, url);
                msg.what = 1;
                msg.obj = result;
                handler_watch.sendMessage(this.msg);
            }
        }).start();
    }

    public void parseResult(String result)
    {
        TicParser ticParser = new TicParser(result);
        ticParser.refresh();
        ticParser.parse();
        for (String train : trains_Et.getText().toString().split(","))
        {
            ticParser.addAssignTrain(train.trim().toUpperCase());
        }
        if (ticParser.checkAvaliable())
        {
            getTicket();
            vibratorPhone();
        }
        else
        {
            noTicket();
            watch(1);
        }
    }

    public void vibratorPhone()
    {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern =
        { 1000, 2000, 1000, 3000, 1000, 4000 }; // OFF/ON/OFF/ON......
        vibrator.vibrate(pattern, 1);
    }

    private void getTicket()
    {
        result_Tv.setText("有票!");
    }

    private void clearTicket()
    {
        result_Tv.setText("正在检查..." + BasicDateUtil.getCurrentDateTimeString());
    }

    private void noTicket()
    {
        result_Tv.setText("无票!");
    }
}