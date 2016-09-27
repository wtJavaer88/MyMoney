package com.wnc.mymoney.uihelper;

import java.util.ArrayList;
import java.util.List;

import com.wnc.basic.BasicNumberUtil;
import com.wnc.mymoney.util.app.AssertsUtil;
import com.wnc.string.PatternUtil;

/**
 * 自动根据给定的数字匹配消费类型
 * 
 * @author wnc
 * 
 */
public class AutoCostTypeRec
{
    static List<Cost_Mapper> mappers = new ArrayList<Cost_Mapper>();
    static
    {
        List<String> content = AssertsUtil.getContent("cost_mapper.txt", "GBK");
        for (String string : content)
        {

            for (String money : PatternUtil.getFirstPattern(string, "/.*?/")
                    .split(","))
            {
                Cost_Mapper mapper = new Cost_Mapper();
                mapper.setCost_Types(PatternUtil.getFirstPattern(string,
                        "\\d+-\\d+"));
                mapper.setMoney(money.trim().replace("/", ""));
                mapper.setDescription(PatternUtil.getLastPattern(string,
                        "/[^/]*+"));
                // System.out.println(mapper);
                mappers.add(mapper);
            }

        }

    }

    static class Cost_Mapper
    {
        private String cost_types;
        private String money;
        private String description;

        public String getCost_Types()
        {
            return cost_types;
        }

        public void setCost_Types(String cost_types)
        {
            this.cost_types = cost_types;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        @Override
        public String toString()
        {
            return "Cost_Mapper [cost_types=" + cost_types + ", money=" + money
                    + ", description=" + description + "]";
        }

        public String getMoney()
        {
            return money;
        }

        public void setMoney(String money)
        {
            this.money = money;
        }
    }

    /**
     * 看看是否匹配
     * 
     * @param cost1
     * @param type_id1
     * @return
     */
    public static String getMatchedTypes(double cost1)
    {
        String ret = "0-0";
        if (cost1 == 0)
        {
            return ret;
        }
        for (Cost_Mapper mapper : mappers)
        {
            if (cost1 == BasicNumberUtil.getDouble(mapper.getMoney()))
            {
                return mapper.getCost_Types();
            }
        }
        return ret;
    }
}
