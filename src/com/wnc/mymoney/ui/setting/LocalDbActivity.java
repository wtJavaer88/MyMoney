package com.wnc.mymoney.ui.setting;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wnc.basic.BasicFileUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.backup.BackUpDataUtil;
import com.wnc.mymoney.uihelper.MyAppParams;
import com.wnc.mymoney.uihelper.PositiveEvent;
import com.wnc.mymoney.util.app.ConfirmUtil;
import com.wnc.mymoney.util.app.MoveDbUtil;
import com.wnc.mymoney.util.app.ShareUtil;
import com.wnc.mymoney.util.app.ToastUtil;
import com.wnc.mymoney.util.common.TextFormatUtil;

public class LocalDbActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localdb_list);

        final ListView listView = (ListView) findViewById(R.id.dblist);
        listView.setAdapter(getAdapter());

        listView.setOnItemLongClickListener(new OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    final int arg2, long arg3)
            {
                final Activity activity = LocalDbActivity.this;
                ListView lv = (ListView) arg0;
                final HashMap map = (HashMap) (lv).getItemAtPosition(arg2);
                // System.out.println(map);
                final String[] menuItems = new String[]
                { "还原", "删除", "分享" };
                new AlertDialog.Builder(LocalDbActivity.this)
                        .setTitle("对记录进行操作")
                        .setItems(menuItems,
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        final String dbpath = String
                                                .valueOf(map.get("path"));
                                        try
                                        {
                                            if (which == 0)
                                            {
                                                // 先备份源Db文件
                                                if (BackUpDataUtil.canBackUpDb)
                                                {
                                                    BackUpDataUtil
                                                            .backupDatabase(activity);
                                                }
                                                if (MoveDbUtil.moveSdCardDb(
                                                        dbpath,
                                                        activity.getDatabasePath("money.db")))
                                                {
                                                    ToastUtil
                                                            .showLongToast(
                                                                    getApplicationContext(),
                                                                    "还原数据成功!");
                                                    finish();
                                                }
                                                else
                                                {
                                                    ToastUtil
                                                            .showShortToast(
                                                                    getApplicationContext(),
                                                                    "还原数据失败!");
                                                }
                                            }
                                            else if (which == 1)
                                            {
                                                ConfirmUtil.confirmDelete(
                                                        activity, "确定要删除数据吗?",
                                                        new PositiveEvent()
                                                        {
                                                            @Override
                                                            public void onPositive()
                                                            {
                                                                boolean b = BasicFileUtil
                                                                        .deleteFile(dbpath);
                                                                if (b)
                                                                {
                                                                    ToastUtil
                                                                            .showLongToast(
                                                                                    getApplicationContext(),
                                                                                    "删除数据文件成功!");
                                                                    listView.setAdapter(getAdapter());
                                                                }
                                                                else
                                                                {
                                                                    ToastUtil
                                                                            .showLongToast(
                                                                                    getApplicationContext(),
                                                                                    "删除数据文件失败!");
                                                                }
                                                            }
                                                        });

                                            }
                                            else if (which == 2)
                                            {
                                                ShareUtil.shareFile(activity,
                                                        dbpath);
                                            }
                                        }
                                        catch (Exception ex)
                                        {
                                            ToastUtil.showShortToast(
                                                    LocalDbActivity.this,
                                                    ex.getMessage());
                                        }
                                        finally
                                        {
                                        }
                                    }

                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which)
                                    {
                                        // TODO Auto-generated method stub
                                    }
                                }).show();

                return true;
            }
        });
    }

    private ListAdapter getAdapter()
    {
        File[] files = new File(MyAppParams.getInstance().getBackupDbPath())
                .listFiles();
        for (int i = 0; i < files.length - 1; i++)
        {
            long max = files[i].lastModified();
            int pos = i;
            for (int j = i + 1; j < files.length; j++)
            {
                if (files[j].lastModified() > max)
                {
                    max = files[j].lastModified();
                    pos = j;
                }
            }
            if (max > files[i].lastModified())
            {
                File tmp = files[pos];
                files[pos] = files[i];
                files[i] = tmp;
            }
        }

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i = 0; i < files.length; i++)
        {
            Map map = new HashMap<String, String>();
            map.put("name",
                    TextFormatUtil.getFileNameNoExtend(files[i].getName()));
            map.put("path", files[i].getAbsolutePath());
            list.add(map);
        }

        return new SimpleAdapter(this, list, R.layout.localdb_item,
                new String[]
                { "name" }, new int[]
                { R.id.dbname });
    }
}