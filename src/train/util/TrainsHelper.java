package train.util;

import java.util.ArrayList;
import java.util.List;

import train.entity.TicketInfo;
import train.parser.TicParser;
import android.util.Log;

import com.wnc.basic.BasicNumberUtil;

public class TrainsHelper
{
    public static String[] getTrainsArr(TicParser ticParser)
    {
        List<String> patternStrings = new ArrayList<String>();
        for (TicketInfo ticketInfo : ticParser.getTicketInfos())
        {
            patternStrings.add(getCombTrainInfo(ticketInfo));
        }
        if (patternStrings.size() > 0)
        {
            return patternStrings.toArray(new String[patternStrings.size()]);
        }
        return null;
    }

    private static String getCombTrainInfo(TicketInfo ticketInfo)
    {
        int costTime = getCostMinutes(ticketInfo);
        StringBuilder accum = new StringBuilder(32);
        accum.append(ticketInfo.getTrainCode());
        accum.append("  [");
        accum.append(ticketInfo.getStartTime());
        accum.append(" -- ");
        accum.append(ticketInfo.getArriveTime());
        accum.append("] ");
        accum.append(" " + costTime);
        accum.append("分钟");
        return accum.toString();
    }

    private static int getCostMinutes(TicketInfo ticketInfo)
    {
        String startTime = ticketInfo.getStartTime().replace(":", "");
        String arriveTime = ticketInfo.getArriveTime().replace(":", "");
        int h = 0;
        int m = 0;
        try
        {
            if (BasicNumberUtil.isNumberString(startTime)
                    && BasicNumberUtil.isNumberString(startTime))
            {

                int start = Integer.parseInt(startTime);
                int arrive = Integer.parseInt(arriveTime);
                if (start > arrive)
                {
                    arrive = arrive + 2400;
                }
                h = (arrive / 100 - start / 100);
                m = (arrive % 100 - start % 100);
            }
        }
        catch (Exception ex)
        {
            Log.e("err", "计算列车时间失败:" + ex.getMessage());
        }
        return h * 60 + m;
    }
}
