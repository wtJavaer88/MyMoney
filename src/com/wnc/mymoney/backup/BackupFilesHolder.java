package com.wnc.mymoney.backup;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import android.util.Log;

import com.wnc.basic.BasicFileUtil;

public class BackupFilesHolder
{
    static Set<File> backupFiles = new HashSet<File>();

    public static void clearBackupFiles()
    {
        backupFiles.clear();
    }

    public static Set<File> getBackupFiles()
    {
        return backupFiles;
    }

    /**
     * 添加之前判断列表是否已经保存了该文件
     * 
     * @param f
     * @return
     */
    public static void addBackupFile(String f)
    {
        if (BasicFileUtil.isExistFile(f))
        {
            Log.i("backupFile", "备份文件[" + f + "]");
            boolean addFlag = backupFiles.add(new File(f));
            if (!addFlag)
            {
                Log.i("backupFile", "该文件已经在列表中[" + f + "]");
            }
        }
        else
        {
            Log.e("backup", "备份文件[" + f + "]路径不存在!");
        }
    }
}
