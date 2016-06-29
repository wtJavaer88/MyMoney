package com.wnc.mymoney.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.wnc.basic.BasicFileUtil;

public class MoveDbUtil
{
    public static void moveDb(String DB_NAME, final Context context)
    {
        File dbFile = context.getDatabasePath(DB_NAME);
        String DB_PATH = dbFile.getPath();
        File folder = dbFile.getParentFile();
        if (!folder.exists())
        {
            if (!folder.exists())
            {
                Log.i("", "db文件夹不存在");
                folder.mkdir();
            }
        }
        BasicFileUtil.deleteFile(DB_PATH);
        if (!BasicFileUtil.isExistFile(DB_PATH))
        {
            AssetManager assetManager = context.getAssets();
            copy(DB_PATH, DB_NAME, assetManager);
            Log.i("moveDb", DB_NAME + "数据初始完毕!");
            // ToastUtil.showShortToast(context, "数据初始完毕!");
        }
        else
        {
            Log.i("moveDb", "数据文件已经存在!");
            // ToastUtil.showShortToast(context, "数据文件已经存在!");
        }
    }

    private static void copy(String DB_PATH, String DB_NAME,
            AssetManager assetManager)
    {
        Log.i("moveDb", "开始移动" + DB_NAME + "数据库!!!!");
        InputStream is = null;
        OutputStream os = null;
        try
        {
            is = assetManager.open(DB_NAME);
            os = new FileOutputStream(DB_PATH);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1)
            {
                os.write(buffer, 0, length);
            }
        }
        catch (Exception e)
        {
            Log.i("moveDb", "异常" + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                os.flush();
                os.close();
                is.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
