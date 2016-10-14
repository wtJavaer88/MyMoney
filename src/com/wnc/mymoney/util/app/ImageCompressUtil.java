package com.wnc.mymoney.util.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageCompressUtil
{
    public static boolean transImage(String fromFile, String toFile, int width,
            int height, int quality)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        // 缩放图片的尺寸
        float scaleWidth = (float) width / bitmapWidth;
        float scaleHeight = (float) height / bitmapHeight;
        return transImage(fromFile, toFile, scaleWidth, scaleHeight, quality);
    }

    public static boolean transImageToMaxSize(String fromFile, String toFile,
            int maxwidth, int maxheight, int quality)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        if (bitmapWidth < maxwidth || bitmapHeight < maxheight)
        {
            return false;
        }
        // 缩放图片的尺寸
        float scaleWidth = (float) maxwidth / bitmapWidth;
        float scaleHeight = (float) maxheight / bitmapHeight;
        float min = Math.min(scaleWidth, scaleHeight);
        return transImage(fromFile, toFile, min, min, quality);
    }

    public static boolean transImage(String fromFile, String toFile,
            float scaleWidth, float scaleHeight, int quality)
    {
        try
        {
            Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 产生缩放后的Bitmap对象
            Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmapWidth, bitmapHeight, matrix, false);
            // save file
            File myCaptureFile = new File(toFile);
            FileOutputStream out = new FileOutputStream(myCaptureFile);
            if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out))
            {
                out.flush();
                out.close();
                return true;
            }
            if (!bitmap.isRecycled())
            {
                bitmap.recycle();// 记得释放资源，否则会内存溢出
            }
            if (!resizeBitmap.isRecycled())
            {
                resizeBitmap.recycle();
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
}
