package com.wnc.mymoney.backup;

import android.app.Activity;

import com.wnc.mymoney.backup.net.AbstractNetBackup;
import com.wnc.mymoney.backup.net.AllNetBackup;
import com.wnc.mymoney.backup.net.NullNetBackup;
import com.wnc.mymoney.backup.net.TimelyNetBackup;
import com.wnc.mymoney.util.ToastUtil;

public class NetBackupFactory
{
    public static AbstractNetBackup getNetBackup(BackupTimeModel model,
            NetChannel channel, Activity activity)
    {
        switch (model)
        {
        case TIMELY:
            return new TimelyNetBackup(channel, activity);
        case ALL:
            return new AllNetBackup(channel, activity);
        }
        ToastUtil.showLongToast(activity, "BackupTimeModel参数不合法! " + model);
        return new NullNetBackup();
    }
}
