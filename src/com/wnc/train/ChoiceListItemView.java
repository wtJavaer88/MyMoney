package com.wnc.train;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wnc.mymoney.R;

public class ChoiceListItemView extends LinearLayout implements Checkable
{

    private TextView nameTxt;
    private CheckBox selectBtn;

    public ChoiceListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.train_item, this, true);
        nameTxt = (TextView) v.findViewById(R.id.author);
        selectBtn = (CheckBox) v.findViewById(R.id.radio);
    }

    public void setName(String text)
    {
        nameTxt.setText(text);
    }

    @Override
    public boolean isChecked()
    {
        return selectBtn.isChecked();
    }

    @Override
    public void setChecked(boolean checked)
    {
        selectBtn.setChecked(checked);
    }

    @Override
    public void toggle()
    {
        selectBtn.toggle();
    }

}