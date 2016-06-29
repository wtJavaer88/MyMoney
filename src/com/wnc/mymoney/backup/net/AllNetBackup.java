package com.wnc.mymoney.backup.net;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.wnc.mymoney.backup.NetChannel;
import com.wnc.mymoney.util.MyAppParams;

public class AllNetBackup extends AbstractNetBackup
{

    public AllNetBackup(NetChannel channel, Activity activity)
    {
        setTip("所有");
        setChannel(channel);
    }

    @Override
    protected List<File> getBackupList()
    {
        List<File> list = new ArrayList<File>()
        {
            {
                add(new File(MyAppParams.getInstance().getBackupDbPath()));
                add(new File(MyAppParams.getInstance().getLocalLogPath()));
                add(new File(MyAppParams.getInstance().getTmpPicPath()));
            }
        };
        return list;
    }

}
