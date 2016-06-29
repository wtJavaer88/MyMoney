package com.wnc.mymoney.backup.net;

import java.io.File;
import java.util.List;

import android.app.Activity;

import com.wnc.mymoney.backup.BackUpDataUtil;
import com.wnc.mymoney.backup.NetChannel;

public class TimelyNetBackup extends AbstractNetBackup
{

    public TimelyNetBackup(NetChannel channel, Activity activity)
    {
        setTip("实时");
        setChannel(channel);
        setActivity(activity);
    }

    @Override
    protected List<File> getBackupList()
    {
        return BackUpDataUtil.getBackupFiles();
    }

}
