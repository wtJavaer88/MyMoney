package com.wnc.mymoney.uihelper;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;

import com.wnc.basic.BasicFileUtil;

public class MyAppParams
{
	private String packageName;
	private Resources resources;
	private String appPath;
	public static final String workPath = Environment.getExternalStorageDirectory().getPath() + "/wnc/app/money/";
	public static final String setting_path = workPath + "setting.json";
	private String localLogPath;
	private String backupDbPath;
	private String tmpPicPath;
	private String tmpVoicePath;
	private String tmpVideoPath;
	private String zipPath;

	private static AssetManager assertMgr;
	private static int screenWidth;
	private static int screenHeight;

	private static MyAppParams singletonMyAppParams = new MyAppParams();

	private MyAppParams()
	{
		this.localLogPath = workPath + "log/";
		this.backupDbPath = workPath + "backupdb/";
		this.tmpPicPath = workPath + "tempimg/";

		this.tmpVoicePath = workPath + "tempamr/";
		this.tmpVideoPath = workPath + "tempMP4/";
		this.zipPath = workPath + "zip/";

		BasicFileUtil.makeDirectory(this.localLogPath);
		BasicFileUtil.makeDirectory(this.backupDbPath);

		BasicFileUtil.makeDirectory(this.tmpPicPath);
		BasicFileUtil.makeDirectory(this.tmpVoicePath);
		BasicFileUtil.makeDirectory(this.tmpVideoPath);

		BasicFileUtil.makeDirectory(this.zipPath);
	}

	public static MyAppParams getInstance()
	{
		return singletonMyAppParams;
	}

	public String getZipPath()
	{
		return this.zipPath;
	}

	public String getBackupDbPath()
	{
		return this.backupDbPath;
	}

	public static int getScreenWidth()
	{
		return screenWidth;
	}

	public static void setScreenWidth(int screenWidth)
	{
		MyAppParams.screenWidth = screenWidth;
	}

	public static int getScreenHeight()
	{
		return screenHeight;
	}

	public static void setScreenHeight(int screenHeight)
	{
		MyAppParams.screenHeight = screenHeight;
	}

	public void setPackageName(String name)
	{
		if (name == null)
		{
			return;
		}
		if (this.packageName == null)
		{
			this.packageName = name;
		}
	}

	public String getPackageName()
	{
		return this.packageName;
	}

	public void setAppPath(String path)
	{
		if (path == null)
		{
			return;
		}
		if (this.appPath == null)
		{
			this.appPath = path;
		}
	}

	public void setResources(Resources res)
	{
		if (res == null)
		{
			return;
		}
		if (this.resources == null)
		{
			this.resources = res;
		}
	}

	public Resources getResources()
	{
		return this.resources;
	}

	public String getLocalLogPath()
	{
		return this.localLogPath;
	}

	public String getTmpPicPath()
	{
		return this.tmpPicPath;
	}

	public String getTmpVoicePath()
	{
		return this.tmpVoicePath;
	}

	public String getAppPath()
	{
		return this.appPath;
	}

	public String getTmpVideoPath()
	{
		return this.tmpVideoPath;
	}

	public static AssetManager getAssertMgr()
	{
		return assertMgr;
	}

	public static void setAssertMgr(AssetManager assertMgr)
	{
		MyAppParams.assertMgr = assertMgr;
	}
}
