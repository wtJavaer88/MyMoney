package train.model;

public class QueryModel
{
    private String dateString;
    private String sname;
    private String dname;

    public String build(String date, String sname, String ename)
    {
        StringBuilder accum = new StringBuilder();
        accum.append("https://kyfw.12306.cn/otn/leftTicket/query?leftTicketDTO.train_date=");
        accum.append(date);

        accum.append("&leftTicketDTO.from_station=");
        accum.append(sname);
        accum.append("&leftTicketDTO.to_station=");
        accum.append(ename);
        accum.append("&purpose_codes=ADULT");
        return accum.toString();
    }
}
