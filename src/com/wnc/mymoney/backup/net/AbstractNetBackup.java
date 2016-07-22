package com.wnc.mymoney.backup.net;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.wnc.basic.BasicDateUtil;
import com.wnc.mymoney.backup.BackupFilesHolder;
import com.wnc.mymoney.backup.NetChannel;
import com.wnc.mymoney.backup.ZipPathFactory;
import com.wnc.mymoney.ui.helper.Setting;
import com.wnc.mymoney.util.ZipUtils;

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
        List<File> list = getBackupList();
        boolean backCode = false;
        if (channel == NetChannel.EMAIL)
        {
            backCode = zipAndSendEmail(list, destZip);
        }
        else if (channel == NetChannel.SHARE)
        {
            backCode = zipAndShare(list, destZip);
        }

        if (backCode)
        {
            BackupFilesHolder.clearBackupPics();
        }
        return backCode;
    }

    protected abstract List<File> getBackupList();

    @Override
    public boolean zipAndShare(List<File> list, String destZip)
    {
        if (list != null && list.size() > 0)
        {
            try
            {
                ZipUtils.zipFiles(list, new File(destZip));
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM,
                        Uri.fromFile(new File(destZip)));

                share.setType("*/*");// 此处可发送多种文件
                activity.startActivity(Intent.createChooser(share, "随手记Share"));
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
    public boolean zipAndSendEmail(List<File> list, String destZip)
    {
        if (list != null && list.size() > 0)
        {

            try
            {
                ZipUtils.zipFiles(list, new File(destZip));
            }
            catch (IOException e)
            {
                Log.e("backup", e.getMessage());
                return false;
            }

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

            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + destZip));
            intent.setType("image/*");
            intent.setType("message/rfc882");
            Intent.createChooser(intent, "Choose Email Client");
            activity.startActivity(intent);
        }
        return true;
    }

}
