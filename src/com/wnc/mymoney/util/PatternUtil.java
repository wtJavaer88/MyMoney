package com.wnc.mymoney.util;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wnc.string.FileUtil;

public class PatternUtil
{

    public static void main(String[] args)
    {
        final String regex = "(http.*?)\"";
        final String testStr = "src=\"http://www.baidu.com\" http:\\x.cn\"";
        System.out.println(getLastPatternGroup(testStr, regex));
        System.out.println(getAllPatternGroup(testStr, regex));
    }

    public static String getFirstPatternGroup(String s, String PatternStr)
    {
        return getPatternGroup(s, PatternStr, true);
    }

    private static String getPatternGroup(String s, String PatternStr,
            boolean isFirst)
    {
        String ret = "";
        Pattern p = Pattern.compile(PatternStr);
        Matcher matcher = p.matcher(s);
        // 浣跨敤while灏嗕細瀵规瘡涓�釜鍖归厤鐨勫璞￠兘鎵惧嚭鍒嗙粍
        while (matcher.find())
        {
            int gc = matcher.groupCount();
            if (gc == 1)
            {
                ret = matcher.group(1);
                if (isFirst)
                {
                    break;
                }
            }
            else if (gc == 0)
            {
                ret = matcher.group(0);
                if (isFirst)
                {
                    break;
                }
            }
        }
        return ret;
    }

    public static List<String> getAllPatternGroup(String s, String PatternStr)
    {
        List<String> list = new ArrayList<String>();
        Pattern p = Pattern.compile(PatternStr);
        Matcher matcher = p.matcher(s);
        // 浣跨敤while灏嗕細瀵规瘡涓�釜鍖归厤鐨勫璞￠兘鎵惧嚭鍒嗙粍
        while (matcher.find())
        {
            int gc = matcher.groupCount();
            String ret = "";
            if (gc == 1)
            {
                ret = matcher.group(1);
                list.add(ret);
            }
            else if (gc == 0)
            {
                // 涓嶅甫鎷彿灏辨槸0涓�
                ret = matcher.group(0);
                list.add(ret);
            }
        }
        return list;
    }

    public static String getLastPatternGroup(String s, String PatternStr)
    {
        return getPatternGroup(s, PatternStr, false);
    }

    /**
     * 鑾峰緱鍖归厤瀛楃涓茬殑鍒楄〃锛岀敤鍒颁簡Pattern.compile
     * 
     * @param s
     *            涓诲瓧绗︿覆
     * @param PatternStr
     *            瑕佸尮閰嶇殑瀛楃涓�
     * @return 鍒楄〃瀵硅薄淇濆瓨姣忎釜鍖归厤濂界殑瀛楃涓�
     */
    public static List<String> getPatternStrings(String s, String PatternStr)
    {
        Pattern p = Pattern.compile(PatternStr);
        List<String> list = new ArrayList<String>();
        Matcher m = p.matcher(s);
        while (m.find())
        {
            list.add(m.group());
        }

        return list;
    }

    /**
     * 鑾峰緱鍖归厤鍒楄〃涓綅浜庢寚瀹氫綅缃殑瀛楃涓�
     * 
     * @param s
     *            涓诲瓧绗︿覆
     * @param p
     *            瑕佸尮閰嶇殑瀛楃涓�
     * @param index
     *            闇�鍙栧嚭鐨勪綅缃�
     * @return 姝や綅缃笂鐨勫瓧绗︿覆
     */
    public static String getPatternByIndex(String s, String p, int index)
    {
        List<String> list = getPatternStrings(s, p);
        if (list.size() < index + 1)
        {
            return "";
        }
        return getPatternStrings(s, p).get(index);
    }

    /**
     * 鑾峰緱鍖归厤鍒楄〃涓殑绗竴涓瓧绗︿覆
     * 
     * @param s
     *            涓诲瓧绗︿覆
     * @param p
     *            瑕佸尮閰嶇殑瀛楃涓�
     * @return 绗竴涓瓧绗︿覆
     */
    public static String getFirstPattern(String s, String p)
    {
        List<String> list = getPatternStrings(s, p);
        if (list.size() == 0)
        {
            return "";
        }
        return getPatternStrings(s, p).get(0);
    }

    /**
     * 鑾峰緱鍖归厤鍒楄〃涓殑鏈�悗涓�釜瀛楃涓�
     * 
     * @param s
     *            涓诲瓧绗︿覆
     * @param p
     *            瑕佸尮閰嶇殑瀛楃涓�
     * @return 鏈�悗涓�釜瀛楃涓�
     */
    public static String getLastPattern(String s, String p)
    {
        List<String> list = getPatternStrings(s, p);
        if (list.size() == 0)
        {
            return "";
        }
        return list.get(list.size() - 1);
    }

    /**
     * 鑾峰緱鎸囧畾鍖洪棿涓婄殑瀛楃涓插垪琛�
     * 
     * @param s
     *            涓诲瓧绗︿覆
     * @param p
     *            瑕佸尮閰嶇殑瀛楃涓�
     * @param start_id
     *            璧峰浣嶇疆
     * @param count
     *            瑕佸彇鍑虹殑鏁伴噺锛屽鏋滀负-1鍒欏悗闈㈢殑鍏ㄨ
     * @return 瀛樺叆瀛楃涓插垪琛�
     */
    public static List<String> getPatternByPage(String s, String p,
            int start_id, int count)
    {
        List<String> getList = new ArrayList<String>();
        List<String> list = getPatternStrings(s, p);
        int end_id = 0;
        if (count == -1)
        {
            end_id = list.size();
        }
        else
        {
            end_id = start_id + count;
        }

        for (int i = start_id; i < list.size() && i < end_id; i++)
        {
            getList.add(list.get(i));
        }

        return getList;
    }

    /**
     * 灏嗗尯闂村唴鐨勫瓧绗︿覆鍏ㄩ儴鏇挎崲鎴愬悓涓�釜瀛楃涓�
     * 
     * @param s
     *            涓诲瓧绗︿覆
     * @param p
     *            瑕佸尮閰嶇殑瀛楃涓�
     * @param start_id
     *            璧峰浣嶇疆
     * @param count
     *            鍙栧嚭鐨勬暟鐩�
     * @param replaceStr
     *            鏇挎崲瀛楃涓�
     * @return 鏇挎崲鍚庣殑鏂板瓧绗︿覆
     */
    public static String replaceByPage(String s, String p, int start_id,
            int count, String replaceStr)
    {

        String newStr = s;
        List<String> list = getPatternStrings(s, p);
        int end_id = 0;
        if (count == -1)
        {
            end_id = list.size();
        }
        else
        {
            end_id = start_id + count;
        }

        for (int i = 0; i < start_id; i++)
        {
            newStr = newStr.replaceFirst(list.get(i), "鐜嬭兘鎵峗_=,;'=@@鏇挎崲" + i);
        }
        for (int i = start_id; i < list.size() && i < end_id; i++)
        {
            newStr = newStr.replaceFirst(list.get(i), "鐜嬭兘鎵峗_=,;'=@@鏇挎崲Good"
                    + replaceStr);
        }
        for (int i = 0; i < start_id; i++)
        {
            newStr = newStr.replaceFirst("鐜嬭兘鎵峗_=,;'=@@鏇挎崲" + i, list.get(i));
        }
        newStr = newStr.replaceAll("鐜嬭兘鎵峗_=,;'=@@鏇挎崲Good" + replaceStr,
                replaceStr);
        return newStr;
    }

    /**
     * 鍙皢瀛楃涓蹭腑鎸囧畾浣嶇疆涓婄殑杩欎竴涓瓧绗︿覆鏇挎崲鎺�
     * 
     * @param s
     *            涓诲瓧绗︿覆
     * @param p
     *            瑕佹浛鎹㈢殑瀛楃涓�
     * @param index
     *            瑕佹浛鎹㈢殑浣嶇疆
     * @param replaceStr
     *            鏇挎崲瀛楃涓�
     * @return 鏂板瓧绗︿覆
     */
    public static String replaceFirstByIndex(String s, String p, int index,
            String replaceStr)
    {
        String newStr = s;
        List<String> list = getPatternStrings(s, p);
        for (int i = 0; i < index; i++)
        {
            newStr = newStr.replaceFirst(list.get(i), "鐜嬭兘鎵峗_=,;'=@@鏇挎崲" + i);
        }
        newStr = newStr.replaceFirst(list.get(index), replaceStr);
        for (int i = 0; i < index; i++)
        {
            newStr = newStr.replaceFirst("鐜嬭兘鎵峗_=,;'=@@鏇挎崲" + i, list.get(i));
        }
        return newStr;
    }

    /**
     * 灏嗗瓧绗︿覆涓笌鎸囧畾浣嶇疆涓婄殑杩欎竴涓瓧绗︿覆鐩稿悓鐨勯兘鏇挎崲鎺夛紝
     * 
     * @param s
     *            涓诲瓧绗︿覆
     * @param p
     *            瑕佹浛鎹㈢殑瀛楃涓�
     * @param index
     *            瑕佹浛鎹㈢殑浣嶇疆
     * @param replaceStr
     *            鏇挎崲瀛楃涓�
     * @return 鏂板瓧绗︿覆
     */
    public static String replaceAllByIndex(String s, String p, int index,
            String replaceStr)
    {
        String newStr = s;
        List<String> list = getPatternStrings(s, p);

        newStr = newStr.replaceAll(list.get(index), replaceStr);

        return newStr;
    }

    /**
     * 鑾峰緱绗竴涓瓙瀛楃涓叉墍鍦ㄤ綅缃�
     * 
     * @param str
     *            涓诲瓧绗︿覆
     * @param subStr
     *            瀛愬瓧绗︿覆
     * @return 浣嶇疆id
     */
    public static int firstIndexOf(String str, String subStr)
    {
        int index = -1;

        int l_sub = 0;
        int l_s = 0;
        if (FileUtil.isLegalStr(subStr) && FileUtil.isLegalStr(subStr))
        {
            l_sub = subStr.length();
            l_s = str.length();
            if (l_sub <= l_s)
            {
                for (int i = 0; i <= l_s - l_sub; i++)
                {
                    if (str.substring(i, i + l_sub).equals(subStr))
                    {
                        index = i;
                        break;
                    }
                }
            }
        }
        return index;
    }

    public static boolean isUrl(String str)
    {
        return str.matches("[a-zA-z]+://[^\\s]*");
    }

    public void t()
    {
        String s = "sdsdsd<map>a</map><map>b</map><map>e</map><map>b</map>21<map>c</map>21";
        Pattern p = Pattern.compile("<map>[a-zA-Z0-9]*</map>");
        System.out.println(replaceAllByIndex(s, "<map>[a-zA-Z0-9]*</map>", 2,
                "xxx"));
        System.out.println(getLastPattern(s, "<map>[a-zA-Z0-9]*</map>"));
        // System.out.println(s.replace("<map>[a-zA-Z0-9]*</map>", "xxx"));
        // getPatternStrings(s, p);
        System.out.println(replaceByPage(s, "<map>[a-zA-Z0-9]*</map>", 1, -1,
                "<map>鐜嬭兘鎵�/map>"));
        System.out.println(replaceAllByIndex(s, "<map>[a-zA-Z0-9]*</map>", 1,
                "<map>鐜嬭兘鎵�/map>"));
        System.out.println(firstIndexOf("aaaaawddsdswwwwdwewwdwdwq", "wd"));
    }
}
