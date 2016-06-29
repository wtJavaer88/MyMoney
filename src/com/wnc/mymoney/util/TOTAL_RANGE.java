package com.wnc.mymoney.util;

import com.wnc.mymoney.bean.DayRangePoint;

public enum TOTAL_RANGE
{
    CURRYEAR
    {
        @Override
        public DayRangePoint create()
        {
            return DayRangePointUtil.getCurrentYearPoint();
        }

        @Override
        public String getSearchHint()
        {
            return "本年";
        }

        @Override
        public DayRangePoint pre(String startDay)
        {
            return DayRangePointUtil.getPreYearPointFromCurr(startDay);
        }

        @Override
        public DayRangePoint next(String endDay)
        {
            return DayRangePointUtil.getNextYearPointFromCurr(endDay);
        }
    },
    CURRMONTH
    {
        @Override
        public DayRangePoint create()
        {
            return DayRangePointUtil.getCurrentMonthPoint();
        }

        @Override
        public String getSearchHint()
        {
            return "本月";
        }

        @Override
        public DayRangePoint pre(String startDay)
        {

            return DayRangePointUtil.getPreMonthPointFromCurr(startDay);
        }

        @Override
        public DayRangePoint next(String endDay)
        {
            return DayRangePointUtil.getNextMonthPointFromCurr(endDay);
        }

    },
    CURRWEEK
    {
        @Override
        public DayRangePoint create()
        {
            return DayRangePointUtil.getCurrentWeekPoint();
        }

        @Override
        public String getSearchHint()
        {
            return "本周";
        }

        @Override
        public DayRangePoint pre(String startDay)
        {
            return DayRangePointUtil.getPreWeekPointFromCurr(startDay);
        }

        @Override
        public DayRangePoint next(String endDay)
        {
            return DayRangePointUtil.getNextWeekPointFromCurr(endDay);
        }
    },
    CURRDAY
    {
        @Override
        public DayRangePoint create()
        {
            return DayRangePointUtil.getCurrentDayPoint();
        }

        @Override
        public String getSearchHint()
        {
            return "每日";
        }

        @Override
        public DayRangePoint pre(String startDay)
        {
            return DayRangePointUtil.getPreDayPointFromCurr(startDay);
        }

        @Override
        public DayRangePoint next(String endDay)
        {
            return DayRangePointUtil.getNextDayPointFromCurr(endDay);
        }
    },
    LASTWEEK
    {
        @Override
        public DayRangePoint create()
        {
            return DayRangePointUtil.getLastWeekPoint();
        }

        @Override
        public String getSearchHint()
        {
            return "近一周";
        }

        @Override
        public DayRangePoint pre(String startDay)
        {
            return CURRWEEK.create();
        }

        @Override
        public DayRangePoint next(String endDay)
        {
            return LASTWEEK.create();
        }
    },
    LASTYEAR
    {
        @Override
        public DayRangePoint create()
        {
            return DayRangePointUtil.getLastYearPoint();
        }

        @Override
        public String getSearchHint()
        {
            return "近一年";
        }

        @Override
        public DayRangePoint pre(String startDay)
        {

            return LASTYEAR.create();
        }

        @Override
        public DayRangePoint next(String endDay)
        {

            return LASTYEAR.create();
        }
    },
    LASTMONTH
    {
        @Override
        public DayRangePoint create()
        {
            return DayRangePointUtil.getLastMonthPoint();
        }

        @Override
        public String getSearchHint()
        {
            return "近一月";
        }

        @Override
        public DayRangePoint pre(String startDay)
        {
            return LASTMONTH.create();
        }

        @Override
        public DayRangePoint next(String endDay)
        {
            return LASTMONTH.create();
        }
    },
    CUSTOM
    {
        @Override
        public DayRangePoint create()
        {
            return CURRDAY.create();
        }

        @Override
        public String getSearchHint()
        {
            return "";
        }

        @Override
        public DayRangePoint pre(String startDay)
        {
            return CURRDAY.create();
        }

        @Override
        public DayRangePoint next(String endDay)
        {
            return CURRDAY.create();
        }
    };
    // abstract修饰方法，强制每个枚举实现该方法
    public abstract DayRangePoint create();

    public abstract DayRangePoint pre(String startDay);

    public abstract DayRangePoint next(String endDay);

    public abstract String getSearchHint();
}