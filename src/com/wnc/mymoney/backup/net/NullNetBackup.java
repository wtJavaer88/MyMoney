package com.wnc.mymoney.backup.net;

import java.io.File;
import java.util.List;

public class NullNetBackup extends AbstractNetBackup
{
    @Override
    public boolean backup()
    {
        return false;
    }

    @Override
    protected List<File> getBackupList()
    {
        return null;
    }

}
