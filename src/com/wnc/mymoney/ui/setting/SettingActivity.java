package com.wnc.mymoney.ui.setting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.R;
import com.wnc.mymoney.backup.BackUpDataUtil;
import com.wnc.mymoney.ui.DragListActivity;
import com.wnc.mymoney.uihelper.MyAppParams;
import com.wnc.mymoney.uihelper.PositiveEvent;
import com.wnc.mymoney.uihelper.Setting;
import com.wnc.mymoney.util.app.ConfirmUtil;
import com.wnc.mymoney.util.app.MoveDbUtil;
import com.wnc.mymoney.util.app.ToastUtil;
import com.wnc.mymoney.util.common.MyFileUtil;
import com.wnc.mymoney.widget.MyToggle;
import com.wnc.mymoney.widget.MyToggle.OnToggleStateListener;

public class SettingActivity extends Activity implements OnClickListener
{
	// 自定义开关对象
	private MyToggle toggle;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

		setContentView(R.layout.setting_custom_set_activity);
		initView();
	}

	private void initView()
	{
		findViewById(R.id.members_set_ly).setOnClickListener(this);
		findViewById(R.id.email_set_ly).setOnClickListener(this);
		findViewById(R.id.cacheclear_set_ly).setOnClickListener(this);
		findViewById(R.id.dataclear_set_ly).setOnClickListener(this);
		findViewById(R.id.datarecover_set_ly).setOnClickListener(this);
		findViewById(R.id.backstyle_set_ly).setOnClickListener(this);
		findViewById(R.id.budget_set_ly).setOnClickListener(this);
		emailTextSet();
		budgetTextSet();
		backupTextSet();
		toggle = (MyToggle) findViewById(R.id.toggle);
		toggle.setImageRes(R.drawable.btn_switch, R.drawable.btn_switch, R.drawable.btn_slip);
		toggle.setToggleState(Setting.isAutoPlayVoice());
		toggle.setOnToggleStateListener(new OnToggleStateListener()
		{
			@Override
			public void onToggleState(boolean state)
			{
				if (state)
				{
					ToastUtil.showShortToast(getApplicationContext(), "开关开启");
					Setting.setAutoPlayVoice("true");
				}
				else
				{
					ToastUtil.showShortToast(getApplicationContext(), "开关关闭");
					Setting.setAutoPlayVoice("false");
				}
			}
		});
	}

	public void intoActivity(Class clazz)
	{
		try
		{
			Intent localIntent = new Intent();
			localIntent.setClass(this, clazz);
			startActivity(localIntent);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.backstyle_set_ly:
			intoActivity(BackupSetActivity.class);
			break;
		case R.id.members_set_ly:
			intoActivity(DragListActivity.class);
			break;
		case R.id.email_set_ly:
			emailDialogOpen();
			break;
		case R.id.cacheclear_set_ly:
			long cacheSize = BasicFileUtil.getFolderSize(MyAppParams.getInstance().getZipPath());
			ConfirmUtil.confirmDelete(this, "缓存大小:" + MyFileUtil.convertFileSize(cacheSize) + ", 确定要清除吗?", new PositiveEvent()
			{
				@Override
				public void onPositive()
				{
					if (BackUpDataUtil.clearAllTmpZips())
					{
						ToastUtil.showShortToast(getApplicationContext(), "清除缓存成功");
					}
					else
					{
						ToastUtil.showShortToast(getApplicationContext(), "清除缓存失败!");
					}
				}
			});
			break;
		case R.id.dataclear_set_ly:
			ConfirmUtil.confirmDelete(this, "确定要清空数据吗?", new PositiveEvent()
			{
				@Override
				public void onPositive()
				{
					// 先备份源Db文件
					if (BackUpDataUtil.canBackUpDb)
					{
						BackUpDataUtil.backupDatabase(getApplicationContext());
					}
					if (MoveDbUtil.moveEmptyMoneyDb(getApplicationContext()))
					{
						ToastUtil.showShortToast(getApplicationContext(), "清空数据成功!");
					}
					else
					{
						ToastUtil.showShortToast(getApplicationContext(), "清空数据失败!");
					}
				}
			});
			break;

		case R.id.datarecover_set_ly:
			intoActivity(LocalDbActivity.class);
			break;
		case R.id.budget_set_ly:
			budgetDialogOpen();
			break;
		}
	}

	protected void emailDialogOpen()
	{
		final Dialog dlg = new Dialog(this, R.style.dialog);
		dlg.show();
		dlg.getWindow().setGravity(Gravity.CENTER);
		dlg.getWindow().setLayout((int) (MyAppParams.getScreenWidth() * 0.8), android.view.WindowManager.LayoutParams.WRAP_CONTENT);
		dlg.getWindow().setContentView(R.layout.setting_add_tags_dialg);
		TextView add_tag_dialg_title = (TextView) dlg.findViewById(R.id.add_tag_dialg_title);
		final EditText add_tag_dialg_content = (EditText) dlg.findViewById(R.id.add_tag_dialg_content);
		TextView add_tag_dialg_no = (TextView) dlg.findViewById(R.id.add_tag_dialg_no);
		TextView add_tag_dialg_ok = (TextView) dlg.findViewById(R.id.add_tag_dialg_ok);

		add_tag_dialg_title.setText("设置邮箱");
		add_tag_dialg_content.setHint("输入邮箱");
		add_tag_dialg_content.setText(Setting.getEmail());

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
				String content = add_tag_dialg_content.getText().toString();
				if (BasicStringUtil.isNotNullString(content) && BasicStringUtil.isEmailString(content))
				{
					Setting.setEmail(content);
					emailTextSet();
					ToastUtil.showShortToast(getApplicationContext(), "设置邮箱成功!");
					dlg.dismiss();
				}
				else
				{
					ToastUtil.showShortToast(getApplicationContext(), "请输入一个邮箱!");
				}
			}

		});
	}

	protected void budgetDialogOpen()
	{
		final Dialog dlg = new Dialog(this, R.style.dialog);
		dlg.show();
		dlg.getWindow().setGravity(Gravity.CENTER);
		dlg.getWindow().setLayout((int) (MyAppParams.getScreenWidth() * 0.8), android.view.WindowManager.LayoutParams.WRAP_CONTENT);
		dlg.getWindow().setContentView(R.layout.setting_add_tags_dialg);
		TextView add_tag_dialg_title = (TextView) dlg.findViewById(R.id.add_tag_dialg_title);
		final EditText add_tag_dialg_content = (EditText) dlg.findViewById(R.id.add_tag_dialg_content);
		TextView add_tag_dialg_no = (TextView) dlg.findViewById(R.id.add_tag_dialg_no);
		TextView add_tag_dialg_ok = (TextView) dlg.findViewById(R.id.add_tag_dialg_ok);

		add_tag_dialg_title.setText("设置一周预算");
		add_tag_dialg_content.setHint("设置一周预算");
		add_tag_dialg_content.setText("" + Setting.getBudget());

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
				String content = add_tag_dialg_content.getText().toString();
				if (BasicStringUtil.isNotNullString(content) && content.matches("\\d+"))
				{
					Setting.setBudget(content);
					budgetTextSet();
					ToastUtil.showShortToast(getApplicationContext(), "设置预算成功!");
					dlg.dismiss();
				}
				else
				{
					ToastUtil.showLongToast(getApplicationContext(), "请输入一个数字,如500!");
				}
			}

		});
	}

	private void emailTextSet()
	{
		((TextView) findViewById(R.id.email_tv)).setText(Setting.getEmail());
	}

	private void budgetTextSet()
	{
		((TextView) findViewById(R.id.budget_tv)).setText("" + Setting.getBudget());
	}

	private void backupTextSet()
	{
		if (Setting.isBackupAuto())
		{
			((TextView) findViewById(R.id.backstyle_tv)).setText(Setting.getBackupTimeModel() + Setting.getBackupWay() + "备份");
		}
		else
		{
			((TextView) findViewById(R.id.backstyle_tv)).setText("手动备份!");
		}
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		backupTextSet();
	}
}
