package com.wnc.mymoney.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wnc.mymoney.R;
import com.wnc.mymoney.dao.MemberDao;
import com.wnc.mymoney.util.ToastUtil;
import com.wnc.mymoney.widget.draglv.DragListView;
import com.wnc.mymoney.widget.draglv.EasyDragListAdapter;

public class DragListActivity extends Activity implements OnClickListener
{
    private EasyDragListAdapter adapter = null;
    List<String> data = new ArrayList<String>();
    Button saveBt;
    DragListView dragListView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag_list_main);
        saveBt = (Button) findViewById(R.id.titlebar_right_btn);
        saveBt.setText("保存");
        saveBt.setOnClickListener(this);
        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.add_btn).setOnClickListener(this);
        ((TextView) findViewById(R.id.title_tv)).setText("成员管理");
        initData();
        try
        {

            dragListView.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id)
                {
                    System.out.println("Click .." + position);
                }
            });
            dragListView
                    .setOnItemLongClickListener(new OnItemLongClickListener()
                    {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent,
                                View view, final int position, long id)
                        {
                            System.out.println("long..");

                            final String[] menuItems = new String[]
                            { "修改", "删除" };
                            new AlertDialog.Builder(DragListActivity.this)
                                    .setTitle("操作记录")
                                    .setItems(
                                            menuItems,
                                            new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which)
                                                {
                                                    if (which == 1)
                                                    {
                                                        deleteMember(position);
                                                    }
                                                    else if (which == 0)
                                                    {
                                                    }
                                                }

                                            })
                                    .setNegativeButton(
                                            "取消",
                                            new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which)
                                                {
                                                }
                                            }).show();

                            return true;
                        }
                    });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void initData()
    {
        MemberDao.initDb(this);
        data = MemberDao.getAllMembers();
        MemberDao.closeDb();

        dragListView = (DragListView) findViewById(R.id.other_drag_list);
        adapter = new EasyDragListAdapter(this, data);
        dragListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.titlebar_right_btn)
        {
            resortMembers();
        }
        else if (v.getId() == R.id.add_btn)
        {
            newDialogOpen();
        }
        else if (v.getId() == R.id.back_btn)
        {
            finish();
        }
    }

    protected void newDialogOpen()
    {

        final Dialog dlg = new Dialog(this, R.style.dialog);
        dlg.show();
        dlg.getWindow().setGravity(Gravity.CENTER);
        dlg.getWindow().setLayout((int) (1000 * 0.8),
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        dlg.getWindow().setContentView(R.layout.setting_add_tags_dialg);
        TextView add_tag_dialg_title = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_title);
        final EditText add_tag_dialg_content = (EditText) dlg
                .findViewById(R.id.add_tag_dialg_content);
        TextView add_tag_dialg_no = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_no);
        TextView add_tag_dialg_ok = (TextView) dlg
                .findViewById(R.id.add_tag_dialg_ok);

        add_tag_dialg_title.setText("添加成员");
        add_tag_dialg_content.setHint("输入成员名或简称");

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
                System.out.println(add_tag_dialg_content.getText());
                addNewMember(add_tag_dialg_content.getText().toString());
                dlg.dismiss();
            }
        });

    }

    public void addNewMember(String name)
    {
        MemberDao.initDb(this);
        try
        {
            if (MemberDao.insertMember(name))
            {
                ToastUtil.showShortToast(this, "成功添加新成员!");
                initData();
            }
        }
        catch (Exception ex)
        {
            ToastUtil.showShortToast(this, ex.getMessage());
        }
        MemberDao.closeDb();
    }

    private void deleteMember(int position)
    {
        String name = adapter.getArrayTitles().get(position);
        MemberDao.initDb(this);
        try
        {
            if (MemberDao.deleteByName(name))
            {
                ToastUtil.showShortToast(this, "删除成员[" + name + "]成功!");
                initData();
            }
        }
        catch (Exception ex)
        {
            ToastUtil.showShortToast(this, ex.getMessage());
        }
        MemberDao.closeDb();
    }

    private void resortMembers()
    {
        MemberDao.initDb(this);
        System.out.println(adapter.getArrayTitles());
        if (MemberDao.updateMembersOrder(adapter.getArrayTitles()))
        {
            ToastUtil.showShortToast(this, "重新排序成功!");
        }
        MemberDao.closeDb();
    }
}