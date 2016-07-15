package com.wnc.mymoney.backup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.wnc.basic.BasicFileUtil;

public class BackupFilesHolder
{
    static List<File> backupFiles = new ArrayList<File>();

    public static void clearBackupPics()
    {
        backupFiles.clear();
    }

    public static List<File> getBackupFiles()
    {
        return backupFiles;
    }

    public static void addBackupFile(String f)
    {
        if (BasicFileUtil.isExistFile(f))
        {
            Log.i("backupFile", "备份文件[" + f + "]");
            backupFiles.add(new File(f));
        }
        else
        {
            Log.e("backup", "备份文件[" + f + "]路径不存在!");
        }
    }
}
