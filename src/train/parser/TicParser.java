package train.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import train.entity.TicketInfo;
import train.model.QueryModel;
import train.util.HttpsConnUtil;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;

public class TicParser
{
    private String resultJson;
    List<TicketInfo> ticketInfos;

    public TicParser(String json)
    {
        this.resultJson = json;
        ticketInfos = new ArrayList<TicketInfo>();
    }

    public void refresh()
    {
        this.ticketInfos.clear();
        clearAssignTrain();
    }

    public List<TicketInfo> getTicketInfos()
    {
        return ticketInfos;
    }

    public String getResultJson()
    {
        return resultJson;
    }

    public void parse()
    {
        String[] data = resultJson.split("\"train_no\":");
        if (data.length == 0)
        {
            return;
        }
        for (String train : data)
        {
            parseTicketsFromTrain(train);
        }
    }

    private void parseTicketsFromTrain(String train)
    {
        System.out.println("15:" + train);
        TicketInfo tInfo = new TicketInfo();
        String code = firstMatch(train, "station_train_code\":\"([\\S]+?)\",");
        String zyString = firstMatch(train, "\"zy_num\":\"([\\S]+?)\"");
        String zeString = firstMatch(train, "\"ze_num\":\"([\\S]+?)\"");
        String wzString = firstMatch(train, "\"wz_num\":\"(.+?)\"");
        System.out.println("wzString:" + wzString);
        String yzString = firstMatch(train, "\"yz_num\":\"([\\S]+?)\"");
        String stString = firstMatch(train, "\"start_time\":\"([\\S]+?)\"");
        String arString = firstMatch(train, "\"arrive_time\":\"([\\S]+?)\"");
        tInfo.setTrainCode(code);
        tInfo.setYiCount(wrapCount(zyString));
        tInfo.setErCount(wrapCount(zeString));
        tInfo.setWzCount(wrapCount(wzString));
        tInfo.setYzCount(wrapCount(yzString));
        tInfo.setStartTime(stString);
        tInfo.setArriveTime(arString);
        if (BasicStringUtil.isNotNullString(code.trim()))
        {
            ticketInfos.add(tInfo);
        }
        // System.out.println(tInfo);
    }

    private int wrapCount(String count)
    {
        if (BasicNumberUtil.isNumberString(count))
        {
            return BasicNumberUtil.getNumber(count);
        }
        else if (count.equals("有"))
        {
            return 666;
        }
        return 0;
    }

    private String firstMatch(String testStr, String regex)
    {
        String ret = "";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(testStr);
        // 使用while将会对每一个匹配的对象都找出分组
        while (matcher.find())
        {
            ret = matcher.group(1);
        }
        return ret;
    }

    List<String> trains = new ArrayList<String>();

    /**
     * 添加指定的班次
     * 
     * @param code
     */
    public void addAssignTrain(String code)
    {
        if (BasicStringUtil.isNotNullString(code.trim()))
        {
            trains.add(code);
        }
    }

    public void clearAssignTrain()
    {
        trains.clear();
    }

    public boolean checkAvaliable()
    {
        for (TicketInfo info : ticketInfos)
        {
            if (trains.size() == 0 || trains.contains(info.getTrainCode()))
            {
                if (info.getYiCount() > 0 || info.getErCount() > 0
                        || info.getWzCount() > 0 || info.getYzCount() > 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args)
    {
        QueryModel queryModel = new QueryModel();
        String url = queryModel.build("2016-05-16", "HSN", "WHN");
        TicParser ticParser = new TicParser(HttpsConnUtil.httpGet(url));
        ticParser.parse();
        ticParser.addAssignTrain("Z26");
        ticParser.addAssignTrain("Z32");
        // ticParser.addAssignTrain("D5248");
        Boolean b = ticParser.checkAvaliable();
        System.out.println(b);
    }
}
