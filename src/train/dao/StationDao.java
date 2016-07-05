package train.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;

import com.wnc.string.PatternUtil;

public class StationDao
{
    public static Map<String, String> stations = new HashMap<String, String>();
    public static String cityStations = "";// txt的完整内容

    /**
     * if not find ,return empty String
     * 
     * @param context
     * @param city
     * @return
     */
    public static String getCityCode(Activity context, String city)
    {
        if (stations.isEmpty())
        {
            getContent(context);
        }
        if (stations.containsKey(city))
        {
            return stations.get(city);
        }
        return "";
    }

    private static void getContent(Activity context)
    {
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
                parseStations(message);
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
    }

    private static void parseStations(String message) throws Exception
    {
        List<String> pairs = PatternUtil.getPatternStrings(message,
                "[\u4e00-\u9fa5]+\\|[A-Z]+");
        for (String pair : pairs)
        {
            int index = pair.indexOf("|");
            String name = "";
            String code = "";
            if (index != -1)
            {
                name = pair.substring(0, index);
                code = pair.substring(index + 1);
            }
            if (!name.equals("") && !code.equals(""))
            {
                stations.put(name, code);
            }
        }
    }
}
