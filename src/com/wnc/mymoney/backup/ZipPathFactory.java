package com.wnc.mymoney.backup;

import com.wnc.mymoney.backup.net.AbstractNetBackup;
import com.wnc.mymoney.backup.net.AllNetBackup;
import com.wnc.mymoney.backup.net.TimelyNetBackup;
import com.wnc.mymoney.uihelper.MyAppParams;

public class ZipPathFactory
{
	public static String getZipPath(AbstractNetBackup anbu)
	{
		/**
		 * 修改文件名, 不能带空格
		 */
		String format = MyAppParams.getInstance().getZipPath() + "%s"
				+ System.currentTimeMillis() + ".zip";
		String key = "";
		if (anbu instanceof TimelyNetBackup)
		{
			key = "timely_";
		}
		else if (anbu instanceof AllNetBackup)
		{
			key = "all_";
		}
		return String.format(format, key);
	}

}
