package com.wnc.mymoney.backup;

import java.io.File;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.mymoney.ui.helper.MyAppParams;
import com.wnc.mymoney.util.ToastUtil;

public class BackUpDataUtil
{
    /**
     * 只有数据库做了一点点修改, 就可以设为true
     */
    public static boolean canBackUpDb = false;
    static final String subjectDb = "money.db";
    static final int maxBackupCount = 10;

    static String zipdir = MyAppParams.getInstance().getZipPath();
    static int keepdays = 3;

    /**
     * 打开应用的时候清空3天前的临时zip文件
     */
    public static void clearTmpZips()
    {
        if (!BasicFileUtil.isExistFolder(zipdir))
        {
            Log.i("tmpDel", "dir not exist");
            return;
        }
        File[] files = new File(zipdir).listFiles();
        long currentTimeMillis = System.currentTimeMillis();
        for (File file : files)
        {
            if (file.isFile()
                    && file.lastModified() < currentTimeMillis - keepdays * 24
                            * 3600L * 1000)
            {
                Log.i("tmpDel", "删除" + file.getName());
                file.delete();
            }
        }
    }

    public static void backup(Activity activity, BackupTimeModel model,
            NetChannel channel)
    {

        String newdb = backupDatabase(activity);
        BackupFilesHolder.addBackupFile(newdb);
        BackupFilesHolder.addBackupFile(getLatestLog());

        NetBackupFactory.getNetBackup(model, channel, activity).backup();

        canBackUpDb = false;
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

    public static String backupDatabase(Context context)
    {
        if (!canBackUpDb)
        {
            return "";
        }
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

    public static boolean clearAllTmpZips()
    {
        try
        {
            File[] files = new File(zipdir).listFiles();
            for (File file : files)
            {
                file.delete();
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
}
