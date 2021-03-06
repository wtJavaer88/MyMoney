package com.wnc.mymoney.backup.net;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.mymoney.backup.BackupFilesHolder;
import com.wnc.mymoney.backup.ZipPathFactory;
import com.wnc.mymoney.uihelper.Setting;
import com.wnc.mymoney.util.app.ShareUtil;
import com.wnc.mymoney.util.common.MailUtil;
import com.wnc.mymoney.util.common.ZipUtils;
import com.wnc.mymoney.util.enums.NetChannel;

public abstract class AbstractNetBackup implements FilesZip
{
    protected String tip = "";
    protected NetChannel channel;
    protected Activity activity;

    public void setTip(String tip)
    {
        this.tip = tip;
    }

    public void setChannel(NetChannel channel)
    {
        this.channel = channel;
    }

    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    public boolean backup()
    {
        String destZip = ZipPathFactory.getZipPath(this);
        Collection<File> list = getBackupList();
        boolean backCode = false;
        if (channel == NetChannel.EMAIL)
        {
            // backCode = zipAndSendEmail(list, destZip);// 调用安卓自带模块,有界面
            backCode = zipAndSendEmailBackGround(list, destZip);// 调用javamail,无界面
        }
        else if (channel == NetChannel.SHARE)
        {
            backCode = zipAndShare(list, destZip);
        }

        if (backCode)
        {
            BackupFilesHolder.clearBackupFiles();
        }
        return backCode;
    }

    protected abstract Collection<File> getBackupList();

    @Override
    public boolean zipAndShare(Collection<File> list, String destZip)
    {
        if (list != null && list.size() > 0)
        {
            try
            {
                ZipUtils.zipFiles(list, new File(destZip));
                if (BasicFileUtil.isExistFile(destZip))
                {
                    ShareUtil.shareFile(activity, destZip);
                }
            }
            catch (IOException e)
            {
                Log.e("backup", e.getMessage());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean zipAndSendEmail(Collection<File> list, String destZip)
    {
        if (list != null && list.size() > 0)
        {
            try
            {
                ZipUtils.zipFiles(list, new File(destZip));

                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] tos =
                { Setting.getEmail() };
                String[] ccs =
                {};
                String[] bccs =
                {};
                intent.putExtra(Intent.EXTRA_EMAIL, tos);
                intent.putExtra(Intent.EXTRA_CC, ccs);
                intent.putExtra(Intent.EXTRA_BCC, bccs);
                intent.putExtra(Intent.EXTRA_TEXT, "备份文件数目:" + list.size());
                intent.putExtra(Intent.EXTRA_SUBJECT, "随手记" + tip + "备份"
                        + BasicDateUtil.getCurrentDateString());

                intent.putExtra(Intent.EXTRA_STREAM,
                        Uri.parse("file://" + destZip));
                intent.setType("image/*");
                intent.setType("message/rfc882");
                Intent.createChooser(intent, "Choose Email Client");
                activity.startActivity(intent);
            }
            catch (Exception e)
            {
                Log.e("backup", e.getMessage());
                return false;
            }
        }
        else
        {
            Log.e("backup", "没有找到备份数据!");
        }
        return true;
    }

    @Override
    public boolean zipAndSendEmailBackGround(Collection<File> list,
            String destZip)
    {
        if (list != null && list.size() > 0)
        {
            try
            {
                ZipUtils.zipFiles(list, new File(destZip));

                MailUtil.sendQQMail(
                        "随手记" + tip + "备份"
                                + BasicDateUtil.getCurrentDateString(),
                        "备份文件数目:" + list.size(), destZip);
            }
            catch (Exception e)
            {
                Log.e("backup", e.getMessage());
                return false;
            }
        }
        else
        {
            Log.e("backup", "没有找到备份数据!");
        }
        return true;
    }

}
