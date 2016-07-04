package train.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.wnc.basic.BasicStringUtil;

public class StationDao
{
    public static Map<String, String> stations = new HashMap<String, String>();
    public static String cityStations = "";
    static
    {
        stations.put("黄石", "HSN");
        stations.put("武汉", "WHN");
    }

    public static String getCityCode(Activity context, String city)
    {
        String content = getContent(context);
        int i = content.indexOf("|" + city + "|");
        if (i != -1)
        {
            String newContent = content.substring(i + city.length() + 2);
            int j = newContent.indexOf("|");
            String code = newContent.substring(0, j);
            // System.out.println("Code:" + code);
            return code;
        }
        return "";
    }

    private static String getContent(Activity context)
    {
        if (BasicStringUtil.isNotNullString(cityStations))
        {
            return cityStations;
        }
        InputStream in = null;
        InputStreamReader bis = null;
        BufferedReader br = null;
        try
        {
            in = context.getAssets().open("station.txt");
            bis = new InputStreamReader(in, "GBK");
            br = new BufferedReader(bis);

            String message = null;

            while ((message = br.readLine()) != null)
            {
                cityStations = message;
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
        return cityStations;
    }
}
