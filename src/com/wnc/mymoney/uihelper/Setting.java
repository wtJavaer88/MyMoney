package com.wnc.mymoney.uihelper;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.mymoney.util.app.SharedPreferenceUtil;

public class Setting
{

	// 以下3个来判断主界面统计数据是否需要关心
	public static boolean budgetChanged = false;
	public static boolean restored = false;
	public static boolean datachanged = false;
	final static String DEFAULT_MEMBER = "王";
	final static String DEFAULT_EMAIL = "12345678@qq.com";
	final static String DEFAULT_BUDGET = "500";

	private final static String LAST_MEMBER = "M001";
	private final static String EMAIL = "M002";

	private final static String BACKUP_TIME_MODEL = "M0031";// 每次还是每天备份
	private final static String BACKUP_AUTO = "M0032";// 是否自动
	private final static String BACKUP_WAY = "M0033";// 邮箱还是分享文件
	// 每周预算
	private final static String WEEK_BUDGET = "M004";

	public static void saveJson()
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(LAST_MEMBER, getLastMember());
		map.put(EMAIL, getEmail());
		map.put(BACKUP_TIME_MODEL, getBackupTimeModel());
		map.put(BACKUP_AUTO, isBackupAuto() + "");
		map.put(BACKUP_WAY, getBackupWay());
		map.put(WEEK_BUDGET, getBudget() + "");
		BasicFileUtil.writeFileString(MyAppParams.setting_path, JSON.toJSONString(map), null, false);
	}

	public static void setAllBySavedJsonData()
	{
		try
		{
			String readFileString = BasicFileUtil.readFileString(MyAppParams.setting_path, "UTF-8");
			System.out.println("本地设置数据:" + readFileString);
			if (BasicStringUtil.isNotNullString(readFileString))
			{
				JSONObject obj = JSONObject.parseObject(readFileString);
				setBACKUP_AUTO(obj.getString(BACKUP_AUTO));
				setBACKUP_TIME_MODEL(obj.getString(BACKUP_TIME_MODEL));
				setBACKUP_WAY(obj.getString(BACKUP_WAY));
				setEmail(obj.getString(EMAIL));
				setMember(obj.getString(LAST_MEMBER));
				setBudget(obj.getString(WEEK_BUDGET));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setBACKUP_TIME_MODEL(String backup_time_model)
	{
		SharedPreferenceUtil.changeValue(BACKUP_TIME_MODEL, backup_time_model);
		Setting.saveJson();
	}

	public static void setBACKUP_AUTO(String backup_auto)
	{
		SharedPreferenceUtil.changeValue(BACKUP_AUTO, backup_auto);
		Setting.saveJson();
	}

	public static void setBACKUP_WAY(String backup_way)
	{
		SharedPreferenceUtil.changeValue(BACKUP_WAY, backup_way);
		Setting.saveJson();
	}

	public static void setMember(String member)
	{
		SharedPreferenceUtil.changeValue(LAST_MEMBER, member);
		Setting.saveJson();
	}

	public static void setEmail(String email)
	{
		SharedPreferenceUtil.changeValue(EMAIL, email);
		Setting.saveJson();
	}

	public static void setBudget(String budget)
	{
		budgetChanged = true;
		Setting.saveJson();
		SharedPreferenceUtil.changeValue(WEEK_BUDGET, budget);
	}

	public static String getLastMember()
	{
		return SharedPreferenceUtil.getShareDataByKey(LAST_MEMBER, DEFAULT_MEMBER);
	}

	public static String getEmail()
	{
		return SharedPreferenceUtil.getShareDataByKey(EMAIL, DEFAULT_EMAIL);
	}

	public static int getBudget()
	{
		return Integer.parseInt(SharedPreferenceUtil.getShareDataByKey(WEEK_BUDGET, DEFAULT_BUDGET));
	}

	public static String getBackupTimeModel()
	{
		return SharedPreferenceUtil.getShareDataByKey(BACKUP_TIME_MODEL, "每次");
	}

	public static boolean isBackupAuto()
	{
		return Boolean.valueOf(SharedPreferenceUtil.getShareDataByKey(BACKUP_AUTO, "false"));
	}

	public static String getBackupWay()
	{
		return SharedPreferenceUtil.getShareDataByKey(BACKUP_WAY, "邮箱");
	}

}
