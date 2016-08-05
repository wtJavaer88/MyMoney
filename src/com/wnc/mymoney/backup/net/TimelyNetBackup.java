package com.wnc.mymoney.backup.net;

import java.io.File;
import java.util.Collection;

import android.app.Activity;

import com.wnc.mymoney.backup.BackupFilesHolder;
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
    protected Collection<File> getBackupList()
    {
        return BackupFilesHolder.getBackupFiles();
    }

}
