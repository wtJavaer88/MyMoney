package com.wnc.mymoney.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wnc.basic.BasicDateUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.bean.Trade;
import com.wnc.mymoney.dao.TransactionsDao;
import com.wnc.mymoney.richtext.ClickablePicRichText;
import com.wnc.mymoney.richtext.ClickableVideoRichText;
import com.wnc.mymoney.richtext.ClickableVoiceRichText;
import com.wnc.mymoney.uihelper.HorGestureDetectorListener;
import com.wnc.mymoney.uihelper.MyAppParams;
import com.wnc.mymoney.uihelper.MyHorizontalGestureDetector;
import com.wnc.mymoney.util.app.ToastUtil;
import com.wnc.mymoney.util.common.FileTypeUtil;
import com.wnc.mymoney.util.common.TextFormatUtil;
import com.wnc.mymoney.util.enums.CostTypeUtil;
import com.wnc.mymoney.widget.ComboBox;

public class ViewTransActivity extends BaseActivity implements
        HorGestureDetectorListener
{
    private static final String EQU_MODEL_NAME = "自转";
    private static final String IN_MODEL_NAME = "收入";
    private static final String OUT_MODEL_NAME = "支出";
    private static final int OUT_MODEL = -1;
    private static final int IN_MODEL = -2;
    private static final int EQU_MODEL = -3;

    private Trade viewedTrade;

    private ComboBox m_combobox;
    TextView categoryTV;
    Button tradeTimeBT;
    Button tradeMemberBT;
    Button tranSaveBT;
    Button tranCostBT;
    TextView descriptionTV;

    private Map<String, Integer> typeMap = new HashMap<String, Integer>();
    private boolean enter = false;
    List<ClickablePicRichText> picTexts = new ArrayList<ClickablePicRichText>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_trans_activity);
        initData();
        this.gestureDetector = new GestureDetector(this,
                new MyHorizontalGestureDetector(0.2, this));
        enter = true;
        initViews();
    }

    private void initViews()
    {
        initCombobox();
        initBts();
        initTv();
        setViewsInCurrModel();
    }

    private void initData()
    {
        viewedTrade = (Trade) getIntent().getSerializableExtra("Trade");
        if (viewedTrade == null)
        {
            ToastUtil.showLongToast(this, "要查看的交易记录为空!");
            finish();
        }
        Log.i("debug-uuid", "view:" + viewedTrade.getUuid());
    }

    private void initCombobox()
    {
        List<String> list_types = new ArrayList<String>();
        this.typeMap.put(OUT_MODEL_NAME, OUT_MODEL);
        list_types.add(OUT_MODEL_NAME);
        this.typeMap.put(IN_MODEL_NAME, IN_MODEL);
        list_types.add(IN_MODEL_NAME);
        this.typeMap.put(EQU_MODEL_NAME, EQU_MODEL);
        list_types.add(EQU_MODEL_NAME);

        this.m_combobox = (ComboBox) findViewById(R.id.id_combobox);
        this.m_combobox.setData(list_types);
        this.m_combobox.setEnabled(false);
    }

    private void initBts()
    {
        this.tradeTimeBT = (Button) findViewById(R.id.trade_time_row_btn);

        this.tradeMemberBT = (Button) findViewById(R.id.member_row_btn);

        this.tranCostBT = (Button) findViewById(R.id.cost_btn);

        findViewById(R.id.back_btn).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                finish();
            }
        });

    }

    private void initTv()
    {
        this.descriptionTV = (TextView) findViewById(R.id.memo_tv);
        this.categoryTV = (TextView) findViewById(R.id.category_name_tv);
    }

    private void setViewsInCurrModel()
    {
        this.tradeTimeBT.setText(BasicDateUtil.getDateTimeString(TextFormatUtil
                .getFormatedDate(viewedTrade.getCreatetime())));
        reSetCategoryTV(CostTypeUtil.getCostTypeName(viewedTrade
                .getCostlevel_id())
                + "-->"
                + CostTypeUtil.getCostTypeName(viewedTrade.getCostdesc_id()));
        this.tradeMemberBT.setText(viewedTrade.getMember());
        this.tranCostBT.setText(viewedTrade.getCost() + "");
        setDescriptionRichText(viewedTrade.getMemo());

        switch (viewedTrade.getType_id())
        {
        case OUT_MODEL:
            m_combobox.setText(OUT_MODEL_NAME);
            break;
        case IN_MODEL:
            m_combobox.setText(IN_MODEL_NAME);
            break;
        case EQU_MODEL:
            m_combobox.setText(EQU_MODEL_NAME);
            break;
        }
    }

    private void reSetCategoryTV(String category)
    {
        this.categoryTV.setText(category.replace(" ", ""));
    }

    private void setDescriptionRichText(final String memo)
    {

        if (enter)
        {
            this.descriptionTV.setMovementMethod(LinkMovementMethod
                    .getInstance());
            this.descriptionTV.setGravity(Gravity.LEFT | Gravity.TOP);
            this.descriptionTV.setClickable(true);
        }

        String[] segments = TextFormatUtil.getSegmentsInMemo(memo);
        for (String segment : segments)
        {
            asynAppend(segment);
        }
        descriptionTV.append("\n");
    }

    private void asynAppend(final String segment)
    {
        new Thread(new Runnable()
        {
            Message msg = new Message();

            @Override
            public void run()
            {

                if (segment.trim().length() == 0)
                {
                    msg.what = 0;
                    return;
                }
                String path = "";
                boolean isVoiceFile = FileTypeUtil.isVoiceFile(segment);
                boolean isPicFile = FileTypeUtil.isPicFile(segment);
                boolean isVideoFile = FileTypeUtil.isVideoFile(segment);
                if (isVoiceFile)
                {
                    path = MyAppParams.getInstance().getTmpVoicePath()
                            + segment.trim();
                }
                else if(isPicFile)
                {
                    path = MyAppParams.getInstance().getTmpPicPath()
                            + segment.trim();
                }
                else if(isVideoFile)
                {
                    path = MyAppParams.getInstance().getTmpVideoPath()
                            + segment.trim();
                }
                File file = new File(path);
                if (file != null && file.isFile() && file.exists())
                {
                    if (isVoiceFile)
                    {
                        ClickableVoiceRichText clickableVoiceRichText = new ClickableVoiceRichText(
                                ViewTransActivity.this, path);
                        msg.obj = clickableVoiceRichText.getSequence();
                    }
                    else if(isPicFile)
                    {
                        ClickablePicRichText clickablePicRichText = new ClickablePicRichText(
                                ViewTransActivity.this, path);
                        msg.obj = clickablePicRichText.getSequence();
                        picTexts.add(clickablePicRichText);
                    }
                    else if(isVideoFile)
                    {
                    	ClickableVideoRichText clickableVideoRichText = new ClickableVideoRichText(
                                ViewTransActivity.this, path);
                        msg.obj = clickableVideoRichText.getSequence();
                    }
                }
                else
                {
                    msg.obj = segment;
                }

                msg.what = 1;
                handler.sendMessage(msg);
            }
        }).start();

    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == 1)
            {
            	CharSequence result = msg.obj == null?"":(CharSequence)msg.obj;
                descriptionTV.append(result);
                descriptionTV.append(" ");
            }
        };
    };

    private GestureDetector gestureDetector;

    @Override
    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
    {
        if (!this.gestureDetector.onTouchEvent(paramMotionEvent))
        {
            return super.dispatchTouchEvent(paramMotionEvent);
        }
        return true;
    }

    @Override
    public void doLeft()
    {
        getNearTrade("L");
    }

    @Override
    public void doRight()
    {
        getNearTrade("R");
    }

    public void getNearTrade(String flag)
    {
        gc();
        enter = false;
        TransactionsDao.initDb(this);
        Trade nearTrade = null;
        if (flag.equals("L"))
        {
            nearTrade = TransactionsDao
                    .getPreTrade(viewedTrade.getCreatetime());
        }
        else if (flag.equals("R"))
        {
            nearTrade = TransactionsDao.getNextTrade(viewedTrade
                    .getCreatetime());
        }
        reSetViews(nearTrade);
        TransactionsDao.closeDb();
    }

    private void reSetViews(Trade nearTrade)
    {
        if (nearTrade != null)
        {
            descriptionTV.setText("");
            picTexts.clear();
            this.viewedTrade = nearTrade;
            setViewsInCurrModel();
        }
    }

    @Override
    public void onDestroy()
    {
        gc();
        super.onDestroy();
    }

    private void gc()
    {
        for (ClickablePicRichText clickablePicRichText : picTexts)
        {
            clickablePicRichText.clearBitmap();
        }
    }
}
