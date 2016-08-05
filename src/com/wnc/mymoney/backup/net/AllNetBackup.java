package com.wnc.mymoney.backup.net;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;

import com.wnc.mymoney.uihelper.MyAppParams;
import com.wnc.mymoney.util.enums.NetChannel;

public class AllNetBackup extends AbstractNetBackup
{

    public AllNetBackup(NetChannel channel, Activity activity)
    {
        setTip("所有");
        setChannel(channel);
    }

    @Override
    protected Collection<File> getBackupList()
    {
        List<File> list = new ArrayList<File>()
        {
            {
                add(new File(MyAppParams.getInstance().getBackupDbPath()));
                add(new File(MyAppParams.getInstance().getLocalLogPath()));
                add(new File(MyAppParams.getInstance().getTmpPicPath()));
                add(new File(MyAppParams.getInstance().getTmpVoicePath()));
                add(new File(MyAppParams.getInstance().getTmpVideoPath()));
            }
        };
        return list;
    }

}
