package train.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;

import com.wnc.mymoney.util.app.AssertsUtil;
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
        for (String message : AssertsUtil.getContent(context, "station.txt",
                "GBK"))
        {
            parseStations(message);
        }
    }

    private static void parseStations(String message)
    {
        List<String> pairs = PatternUtil.getPatternStrings(message,
                "[\u4e00-\u9fa5]+\\|[A-Z]+");
        System.out.println("pairs.size():" + pairs.size());
        for (String pair : pairs)
        {
            int index = pair.indexOf("|");
            String name = "";
            String code = "";
            if (index != -1)
            {
                name = pair.substring(0, index);
                code = pair.substring(index + 1);
                if (!name.equals("") && !code.equals(""))
                {
                    stations.put(name, code);
                }
            }
        }
    }
}
