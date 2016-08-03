package com.wnc.mymoney.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wnc.mymoney.R;
import com.wnc.mymoney.ui.helper.SimulatorCal;

public class CostKeyboardActivity extends Activity
{

    private LinearLayout layout;
    private SimulatorCal simulator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_currency_rate_input_panel);
        this.simulator = new SimulatorCal(getIntent().getStringExtra("cost"));
        showContent();
        this.layout = (LinearLayout) findViewById(R.id.digit_input_panel_ly);

        // 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        this.layout.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                // Toast.LENGTH_SHORT).show();
            }
        });

    }

    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // setResultAndFinish();
        finish();
        return true;
    }

    private void setResultAndFinish()
    {
        Intent intent = new Intent();
        intent.putExtra("cost", this.simulator.getResult());// 放入返回值
        setResult(0, intent);// 放入回传的值,并添加一个Code,方便区分返回的数据
        finish();
    }

    public void calculator(View v)
    {

        switch (v.getId())
        {
        case R.id.zero:
            this.simulator.simulate("0");
            break;
        case R.id.one:
            this.simulator.simulate("1");
            break;
        case R.id.two:
            this.simulator.simulate("2");
            break;
        case R.id.three:
            this.simulator.simulate("3");
            break;
        case R.id.four:
            this.simulator.simulate("4");
            break;
        case R.id.five:
            this.simulator.simulate("5");
            break;
        case R.id.six:
            this.simulator.simulate("6");
            break;
        case R.id.seven:
            this.simulator.simulate("7");
            break;
        case R.id.eight:
            this.simulator.simulate("8");
            break;
        case R.id.nine:
            this.simulator.simulate("9");
            break;
        case R.id.dot:
            this.simulator.simulate(".");
            break;
        case R.id.clear:
            this.simulator.simulate("c");
            break;
        case R.id.subtract:
            this.simulator.simulate("-");
            break;
        case R.id.add:
            this.simulator.simulate("+");
            break;
        case R.id.ok:
            this.simulator.simulate("=");
            setResultAndFinish();
            break;
        case R.id.equal:
            this.simulator.simulate("=");
            setResultAndFinish();
            break;
        }
        showContent();
    }

    private void showContent()
    {
        ((TextView) findViewById(R.id.content)).setText(this.simulator
                .getContent());
    }
}
