package com.wnc.mymoney.backup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.mymoney.util.MyAppParams;
import com.wnc.mymoney.util.SysInit;
import com.wnc.mymoney.util.ToastUtil;

public class BackUpDataUtil
{
    static final String subjectDb = "money.db";
    static final int maxBackupCount = 10;
    static List<File> backupFiles = new ArrayList<File>();

    public static void clearBackupPics()
    {
        backupFiles.clear();
    }

    public static void clearTmpZips()
    {
        BasicFileUtil.deleteFolder(MyAppParams.getInstance().getZipPath());
        BasicFileUtil.makeDirectory(MyAppParams.getInstance().getZipPath());
    }

    public static List<File> getBackupFiles()
    {
        return backupFiles;
    }

    public static void addBackupFile(String f)
    {
        System.out.println("backupFile:" + f);
        if (BasicFileUtil.isExistFile(f))
        {
            backupFiles.add(new File(f));
        }
        else
        {
            Log.e("pic", "备份文件[" + f + "]路径不存在!");
        }
    }

    public static void backup(Activity activity, BackupTimeModel model,
            NetChannel channel)
    {
        if (!SysInit.canBackUpDb)
        {
            return;
        }

        String newdb = backupDatabase(activity);
        addBackupFile(newdb);
        addBackupFile(getLatestLog());

        NetBackupFactory.getNetBackup(model, channel, activity).backup();

        SysInit.canBackUpDb = false;
    }

    private static String getLatestLog()
    {
        File logFolder = new File(MyAppParams.getInstance().getLocalLogPath());
        if (logFolder != null && logFolder.exists())
        {
            File[] logfiles = logFolder.listFiles();
            Arrays.sort(logfiles);
            String name;
            for (int i = logfiles.length - 1; i >= 0; i--)
            {
                name = logfiles[i].getName();
                if (!name.matches("^\\d+?.+"))
                {
                    continue;
                }
                return logfiles[i].getAbsolutePath();
            }
        }
        return "";
    }

    private static String backupDatabase(Context context)
    {
        File dbFile = context.getDatabasePath(subjectDb);
        String newFilePath = BasicFileUtil.getMakeFilePath(MyAppParams
                .getInstance().getBackupDbPath(),
        // "/data/data/db",
                BasicDateUtil.getCurrentDateTimeString() + "_" + subjectDb);
        File[] files = new File(MyAppParams.getInstance().getBackupDbPath())
                .listFiles();
        Arrays.sort(files);

        final int len = files.length;

        if (BasicFileUtil.CopyFile(dbFile, new File(newFilePath)))
        {
            int i = len;
            while (i + 1 > maxBackupCount)
            {
                String filePath = files[len - i].getAbsolutePath();
                Log.i("BackUpDataUtil", "删除数据库-->" + filePath);
                if (!BasicFileUtil.deleteFile(filePath))
                {
                    ToastUtil
                            .showShortToast(context, "删除" + filePath + "文件失败!");
                }
                i--;
            }
        }
        else
        {
            ToastUtil.showShortToast(context, "复制" + subjectDb + "文件到<"
                    + newFilePath + ">失败!");
        }

        return newFilePath;
    }
}
