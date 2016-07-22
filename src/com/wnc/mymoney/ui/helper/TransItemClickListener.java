package com.wnc.mymoney.ui.helper;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wnc.mymoney.R;
import com.wnc.mymoney.bean.Trade;
import com.wnc.mymoney.dao.TransactionsDao;
import com.wnc.mymoney.ui.AddOrEditTransActivity;
import com.wnc.mymoney.ui.NavTransactionActivity;
import com.wnc.mymoney.ui.SearchTransactionActivity;
import com.wnc.mymoney.ui.ShowCostDetailActivity;
import com.wnc.mymoney.ui.ViewTransActivity;
import com.wnc.mymoney.util.ClipBoardUtil;
import com.wnc.mymoney.util.ToastUtil;

public class TransItemClickListener implements OnItemClickListener,
        OnItemLongClickListener
{
    Activity activity;
    ListView listView;

    public TransItemClickListener(Activity activity, ListView listView)
    {
        this.activity = activity;
        this.listView = listView;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        ListView lv = (ListView) arg0;
        this.activity.startActivity(new Intent().setClass(this.activity,
                ViewTransActivity.class).putExtra("Trade",
                getSelectedTrade(arg0, arg2)));
    }

    private Trade getSelectedTrade(AdapterView arg0, int arg2)
    {
        ListView lv = (ListView) arg0;
        HashMap map = (HashMap) (lv).getItemAtPosition(arg2);
        return (Trade) map.get("Trade");
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> arg0, final View arg1,
            final int arg2, long arg3)
    {

        final String[] menuItems = new String[]
        { "修改", "删除", "复制" };
        new AlertDialog.Builder(this.activity)
                .setTitle("对记录进行操作")
                .setItems(menuItems, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        TransactionsDao.initDb(activity);
                        Trade selectedTrade = getSelectedTrade(arg0, arg2);
                        try
                        {
                            if (which == 2)
                            {
                                ClipBoardUtil.setClipBoardTextContent(activity,
                                        JSON.toJSONString(selectedTrade));

                                ToastUtil.showShortToast(activity, "复制成功!");
                            }
                            else if (which == 1)
                            {
                                deleteDialogOpen(selectedTrade);
                            }
                            else if (which == 0)
                            {
                                toTradeModify(selectedTrade);
                            }
                        }
                        catch (Exception ex)
                        {
                            ToastUtil.showShortToast(activity, ex.getMessage());
                        }
                        finally
                        {
                            TransactionsDao.closeDb();
                        }
                    }

                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // TODO Auto-generated method stub
                    }
                }).show();

        return true;
    }

    protected void deleteDialogOpen(final Trade selectedTrade)
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
        add_tag_dialg_content.setText("您确定要删除这笔流水吗？");
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
                delete(selectedTrade);
                dlg.dismiss();
            }
        });

    }

    protected boolean delete(Trade selectedTrade)
    {
        if (TransactionsDao.delete(activity, selectedTrade))
        {
            ToastUtil.showShortToast(activity, "成功删除!");
            // 执行刷新操作
            if (activity instanceof NavTransactionActivity)
            {
                ((NavTransactionActivity) activity).initData();
            }
            else if (activity instanceof SearchTransactionActivity)
            {
                ((SearchTransactionActivity) activity).createNewAdapter();
            }
            else if (activity instanceof ShowCostDetailActivity)
            {
                ((ShowCostDetailActivity) activity).initData();
            }
            Log.i("deleteTrade", JSON.toJSONString(selectedTrade));
            return true;
        }
        return false;
    }

    private void toTradeModify(Trade selectedTrade)
    {
        activity.startActivity(new Intent()
                .setClass(activity, AddOrEditTransActivity.class)
                .putExtra("isAdd", false).putExtra("Trade", selectedTrade));
    }
}
