package com.wnc.train;

import java.util.HashMap;
import java.util.Map;

public class CityTrainsHolder
{
    static Map<String, String[]> cityTrains = new HashMap<String, String[]>();

    public static boolean containsCityInfo(String key)
    {
        return cityTrains.containsKey(key);
    }

    public static String[] getTrains(String key)
    {
        return cityTrains.get(key);
    }

    public static void putTrainsBetweenCities(String key, String[] value)
    {
        cityTrains.put(key, value);
    }
}
