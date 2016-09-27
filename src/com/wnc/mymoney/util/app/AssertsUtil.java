package com.wnc.mymoney.util.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;

import com.wnc.mymoney.uihelper.MyAppParams;

public class AssertsUtil
{
    public static List<String> getContent(String fileName, String charSet)
    {
        return getContent(MyAppParams.getAssertMgr(), fileName, charSet);
    }

    public static List<String> getContent(Context context, String fileName,
            String charSet)
    {
        return getContent(context.getAssets(), fileName, charSet);
    }

    public static List<String> getContent(AssetManager assetMgr,
            String fileName, String charSet)
    {
        List<String> list = new ArrayList<String>();
        InputStream in = null;
        InputStreamReader bis = null;
        BufferedReader br = null;
        try
        {
            in = getInputStream(assetMgr, fileName);
            bis = new InputStreamReader(in, charSet);
            br = new BufferedReader(bis);

            String message = null;

            while ((message = br.readLine()) != null)
            {
                list.add(message);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                br.close();
                bis.close();
                in.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static InputStream getInputStream(AssetManager assetMgr,
            String fileName) throws IOException
    {
        return assetMgr.open(fileName);
    }

    public static InputStream getInputStream(Context context, String fileName)
            throws IOException
    {
        return context.getAssets().open(fileName);
    }

}
