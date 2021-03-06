package com.wnc.mymoney.util.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.wnc.mymoney.R;
import com.wnc.mymoney.uihelper.MyAppParams;
import com.wnc.mymoney.uihelper.PositiveEvent;

public class ConfirmUtil
{
    public static void confirmDelete(Activity activity, String alertMsg,
            final PositiveEvent event)
    {
        final AlertDialog dlg = new AlertDialog.Builder(activity).create();
        dlg.show();
        dlg.getWindow().setGravity(Gravity.CENTER);
        dlg.getWindow().setLayout((int) (MyAppParams.getScreenWidth() * 0.8),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        dlg.getWindow().setContentView(R.layout.setting_add_tags_dialg);
        TextView add_tag_dialg_title = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_title);
        TextView add_tag_dialg_content = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_content);
        TextView add_tag_dialg_no = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_no);
        TextView add_tag_dialg_ok = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_ok);
        add_tag_dialg_title.setText("删除确认");
        add_tag_dialg_content.setText(alertMsg);
        add_tag_dialg_no.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dlg.dismiss();
            }
        });
        add_tag_dialg_ok.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                event.onPositive();
                dlg.dismiss();
            }
        });
    }
}
