package com.iscloud.common.utils;

import com.iscloud.common.cst.BaseCst;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: yu_hua
 * @Desc: 日期比较工具类
 */
@SuppressWarnings({"unused"})
public final class DateCompareUtils {

    public static final String BEAN = "DateCompareUtils";
    public static final long HOUR_TIMEMILLIS = 3600000, DAY_TIMEMILLIS = HOUR_TIMEMILLIS * 24;

    public static boolean isMonday(Date date) {
        date = date == null ? new Date() : date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH) == 2;// 1=周日，2=周一
    }

    public static boolean isMonth1stDay(Date date) {
        date = date == null ? new Date() : date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH) == 1;
    }

    public static boolean isYear1stDay(Date date) {
        date = date == null ? new Date() : date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH) == 1 && calendar.get(Calendar.MONTH) == Calendar.JANUARY;
    }

    /**
     * @Desc: 返回两个时间的时间戳
     * @Author     : HYbrid
     * @Date       : 2021/2/22 15:10
     * @Params     : [startTime, endTime]
     * @Return     : long
     */
    public static long compare(Object startTime, Object endTime) {
        if (startTime == null || endTime == null) {
            return -1;
        }
        Date s = null, e = null;
        if (startTime instanceof String) {
            if (StringUtils.isBlank((String) startTime)) {
                return -1;
            }
            s = DateFormatUtils.parseDefault((String) startTime);
        }
        if (endTime instanceof String) {
            if (StringUtils.isBlank((String) endTime)) {
                return -1;
            }
            e = DateFormatUtils.parseDefault((String) endTime);
        }
        if (startTime instanceof Date) {
            s = (Date) startTime;
        }
        if (endTime instanceof Date) {
            e = (Date) endTime;
        }
        if (s == null || e == null) {
            return -1;
        }
        return e.getTime() - s.getTime();
    }

    public static long compareCurrent(String startTime) {
        if (StringUtils.isBlank(startTime)) {
            return -1;
        }
        long curr = System.currentTimeMillis(), start = DateFormatUtils.parseDefault(startTime).getTime();
        return start - curr;
    }

    /**
     * @Desc: 根据时间戳获取时间间隔
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:11
     * @Params     : [startTime, endTime, timestamp, scale]
     * @Return     : java.math.BigDecimal
     */
    public static BigDecimal getIntervalTime(String startTime, String endTime, long timestamp, int scale) {
        long compare = compare(startTime, endTime);
        if (compare <= 0) {
            return BigDecimal.ZERO;
        }
        if (scale <= 0) {
            scale = 2;
        }
        if (timestamp <= 0) {
            timestamp = HOUR_TIMEMILLIS;
        }
        return BigDecimal.valueOf(compare).divide(BigDecimal.valueOf(timestamp), scale, RoundingMode.HALF_UP);
    }

    /**
     * @Desc: 根据时间间隔精度计算起止时间的时间间隔，precision为Calendar中时间类型常量：HOUR_OF_DAY、MONTH等
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:17
     * @Params     : [startTime, endTime, precision]
     * @Return     : long
     */
    public static long getIntervalTime(String startTime, String endTime, int precision) {
        if (StringUtils.isAnyBlank(startTime, endTime)) {
            return -1;
        }
        if (precision <= 0) {
            precision = Calendar.HOUR_OF_DAY;
        }
        Date start = DateFormatUtils.parseDefault(startTime), end = DateFormatUtils.parseDefault(endTime);
        Calendar s = Calendar.getInstance(), e = Calendar.getInstance();
        s.setTime(start);
        e.setTime(end);
        long increase = 0;
        while (!s.after(e)) {
            increase++;
            s.add(precision, 1);
        }
        return increase;
    }

    /**
     * @Desc: 根据时间获取当天的最开始时间
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:32
     * @Params     : [date]
     * @Return     : java.lang.String
     */
    public static String firstSecondWithToday2Str(Date date) {
        date = addDate(date == null ? new Date() : date, Calendar.DAY_OF_MONTH, 0);
        return DateFormatUtils.formatDefault(date);
    }

    public static long fistSecToday() {
        return addDate(new Date(), Calendar.DAY_OF_MONTH, 0).getTime();
    }

    /**
     * @Desc: 根据时间获取当天的最开始时间
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:32
     * @Params     : [date]
     * @Return     : java.lang.String
     */
    public static String firstSecondWithToday2Str(Object date) {
        if (date == null) {
            date = new Date();
        }
        Date d = date instanceof String ? DateFormatUtils.parseDefault((String) date) : (Date) date;
        d = addDate(d, Calendar.DAY_OF_MONTH, 0);
        return addDateCommon(d, Calendar.SECOND, -1);
    }

    /**
     * @Desc: 根据时间获取当天的最后一秒时间
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:32
     * @Params     : [date]
     * @Return     : java.lang.String
     */
    public static String lastSecondWithToday2Str(Object date) {
        if (date == null) {
            date = new Date();
        }
        Date d = date instanceof String ? DateFormatUtils.parseDefault((String) date) : (Date) date;
        d = addDate(d, Calendar.DAY_OF_MONTH, 1);
        return addDateCommon(d, Calendar.SECOND, -1);
    }

    /**
     * @Desc: 根据时间获取当天的最后一秒时间
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:33
     * @Params     : [date]
     * @Return     : java.util.Date
     */
    public static Date lastSecondWithToday(Date date) {
        date = addDate(date == null ? new Date() : date, Calendar.DAY_OF_MONTH, 1);
        return addDate(date, Calendar.SECOND, -1);
    }

    /**
     * @Desc: 根据时间获取该周周一
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:33
     * @Params     : [d]
     * @Return     : java.util.Date
     */
    public static Date week1st(Date d) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(d == null ? new Date() : d);
        c.set(Calendar.DAY_OF_WEEK, 2);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    /**
     * @Desc: 根据时间获取该周周末
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:33
     * @Params     : [d]
     * @Return     : java.util.Date
     */
    public static Date weekend(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d == null ? new Date() : d);
        c.add(Calendar.WEEK_OF_MONTH, 0);
        c.set(Calendar.DAY_OF_WEEK, 2);
        c.add(Calendar.DAY_OF_MONTH, 6);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    /**
     * @Desc: 通过日期获取本年上/下半年第一天
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:33
     * @Params     : [date]
     * @Return     : java.util.Date
     */
    public static Date halfYearDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.MONTH) >= 6) {
            initCalendar(calendar, Calendar.YEAR);
            add(calendar, Calendar.YEAR, 0);
            calendar.set(Calendar.MONTH, 6);
            add(calendar, Calendar.DAY_OF_MONTH, 0);
        } else {
            initCalendar(calendar, Calendar.YEAR);
            add(calendar, Calendar.YEAR, 0);
            calendar.set(Calendar.MONTH, 0);
            add(calendar, Calendar.DAY_OF_MONTH, 0);
        }
        return calendar.getTime();
    }

    /**
     * @Desc: 根据时间获取季度第一天
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:38
     * @Params     : [date]
     * @Return     : java.util.Date
     */
    public static Date quarterlyFirstDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        int month = calendar.get(Calendar.MONTH);
        addTime(calendar, Calendar.YEAR, 0);
        if (month >= 9) {
            calendar.set(Calendar.MONTH, 9);
        } else if (month >= 6) {
            calendar.set(Calendar.MONTH, 6);
        } else if (month >= 3) {
            calendar.set(Calendar.MONTH, 3);
        }
        return calendar.getTime();
    }

    public static Date quarterlyFirstDay(String date) {
        Date d = StringUtils.isBlank(date) ? null : DateFormatUtils.parseDefault(date);
        return quarterlyFirstDay(d);
    }

    /**
     * @Desc:   季度最后一天最后一秒
     * @Params: [date]
     * @Return: java.util.Date
     * @Author: HYbrid
     * @Date:   2022/5/11
     */
    public static Date quarterlyLastSecondDay(Date date) {
        Date d = quarterlyFirstDay(date);
        return addDate(addDate(d, Calendar.MONTH, 3), Calendar.SECOND, -1);
    }

    public static String quarterlyLastSecondDayStr(Date date) {
        return DateFormatUtils.formatDefault(quarterlyLastSecondDay(date));
    }

    /**
     * @Desc: 根据时间获取该年最后一天，精确到天
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:45
     * @Params     : [date]
     * @Return     : java.util.Date
     */
    public static Date yearLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date == null ? new Date() : date);
        initCalendar(calendar, Calendar.YEAR);
        add(calendar, Calendar.YEAR, 1);
        add(calendar, Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }
    /**
     * @Desc: 根据时间获取该年最后一天，精确到时分秒
     * @Author     : HYbrid
     * @Date       : 2021/10/28 11:36
     * @Params     : [date]
     * @Return     : java.util.Date
     */
    public static Date yearLastSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date == null ? new Date() : date);
        initCalendar(calendar, Calendar.YEAR);
        add(calendar, Calendar.YEAR, 1);
        add(calendar, Calendar.SECOND, -1);
        return calendar.getTime();
    }
    public static String yearLastSecondStr(String date) {
        Date d = DateFormatUtils.dateComplete2Date(date);
        return d == null ? null : DateFormatUtils.formatDefault(yearLastSecond(d));
    }

    /**
     * @Desc: 根据时间获取该年第一天
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:45
     * @Params     : [date]
     * @Return     : java.util.Date
     */
    public static Date yearFirstDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date == null ? new Date() : date);
        addTime(calendar, Calendar.YEAR, 0);
        return calendar.getTime();
    }

    public static String monthLastDay2Str(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        initCalendar(calendar, Calendar.MONTH);
        add(calendar, Calendar.MONTH, 1);
        add(calendar, Calendar.SECOND, -1);
        return DateFormatUtils.formatDefault(calendar.getTime());
    }

    /**
     * @Desc: 根据时间获取该月最后一天
     * @Author     : HYbrid
     * @Date       : 2021/8/10 14:45
     * @Params     : [date]
     * @Return     : java.util.Date
     */
    public static Date monthLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        initCalendar(calendar, Calendar.MONTH);
        add(calendar, Calendar.MONTH, 1);
        add(calendar, Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    /**
     * @Desc: 根据时间获取该月第一天
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:04
     * @Params     : [date]
     * @Return     : java.util.Date
     */
    public static Date monthFirstDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date == null ? new Date() : date);
        addTime(calendar, Calendar.MONTH, 0);
        return calendar.getTime();
    }

    /**
     * @Desc: 根据时间添加时间增量
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:04
     * @Params     : [date, precision, count]
     * @Return     : java.lang.String
     */
    public static String addDateCommon(String date, int precision, int count) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateFormatUtils.parseDefault(date));
        cal.add(precision, count);
        return DateFormatUtils.formatDefault(cal.getTime());
    }

    /**
     * @Desc: 根据时间添加时间增量
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:05
     * @Params     : [date, precision, count]
     * @Return     : java.lang.String
     */
    public static String addDateCommon(Date date, int precision, int count) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date == null ? new Date() : date);
        cal.add(precision, count);
        return DateFormatUtils.formatDefault(cal.getTime());
    }

    public static Date addDate(Date date, int precision, int count) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return addTime(calendar, precision, count).getTime();
    }

    public static String addDate2Str(Date date, int precision, int count) {
        return DateFormatUtils.formatDefault(addDate(date, precision, count));
    }

    public static String addDate2Str(String dateStr, int precision, int count) {
        return DateFormatUtils.formatDefault(addDate(dateStr, precision, count));
    }

    public static Date addDate(String dateStr, int precision, int count) {
        Date date = DateFormatUtils.parse(dateStr, DateFormatUtils.DateFormatter.PATTERN_19);
        return addDate(date, precision, count);
    }

    public static Calendar addTime(Calendar calendar, int precision, int count) {
        initCalendar(calendar, precision);
        if (count != 0) {
            add(calendar, precision, count);
        }
        return calendar;
    }

    private static void add(Calendar calendar, int precision, int count) {
        calendar = calendar == null ? Calendar.getInstance() : calendar;
        if (precision == Calendar.HOUR || precision == Calendar.HOUR_OF_DAY) {
            calendar.add(Calendar.HOUR_OF_DAY, count);
        } else if (precision == Calendar.MINUTE || precision == Calendar.SECOND || precision == Calendar.MILLISECOND
                || precision == Calendar.DATE || precision == Calendar.MONTH || precision == Calendar.YEAR) {
            calendar.add(precision, count);
        }
    }

    /**
     * @Desc: 根据精度初始化Calendar
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:07
     * @Params     : [calendar, precision]
     * @Return     : void
     */
    public static void initCalendar(Calendar calendar, int precision) {
        calendar = calendar == null ? Calendar.getInstance() : calendar;
        if (precision == Calendar.HOUR || precision == Calendar.HOUR_OF_DAY) {
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
        } else if (precision == Calendar.MINUTE) {
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
        } else if (precision == Calendar.SECOND) {
            calendar.set(Calendar.MILLISECOND, 0);
        } else if (precision == Calendar.DAY_OF_MONTH) {
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        } else if (precision == Calendar.MONTH) {
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
        } else if (precision == Calendar.YEAR) {
            calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, 1, 0, 0, 0);
        }
    }

    /**
     * @Desc: 生成所有小时单位的时间点，返回Date
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:07
     * @Params     : [startDate, endDate]
     * @Return     : java.util.List<java.util.Date>
     */
    public static List<Date> allHourDate(Date startDate, Date endDate) {
        List<Date> hours = new LinkedList<>();
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.setTime(startDate);
        end.setTime(endDate);
        // 设置精确度到小时
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);
        // step1:add start date
        hours.add(start.getTime());
        while (start.before(end)) {
            start.add(Calendar.HOUR_OF_DAY, 1);
            hours.add(start.getTime());
        }
        return hours;
    }

    /**
     * @Desc: 生成所有小时单位的时间点，返回Date
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:07
     * @Params     : [startDate, endDate]
     * @Return     : java.util.List<java.util.Date>
     */
    public static List<Date> allHourDate(String startDate, String endDate) {
        Date start = DateFormatUtils.parse(startDate, DateFormatUtils.DateFormatter.PATTERN_19);
        Date end = DateFormatUtils.parse(endDate, DateFormatUtils.DateFormatter.PATTERN_19);
        return allHourDate(start, end);
    }

    /**
     * @Desc:   获取两个时间之间的天字符串
     * @Params: [startDate, endDate]
     * @Return: java.util.List<java.lang.String>
     * @Author: HYbrid
     * @Date:   2022/6/27
     */
    public static List<String> dayStrJust(Date startDate, Date endDate) {
        return getHourStr(startDate, endDate, 8, 10);
    }

    /**
     * @Desc:        获取两个时间之间的小时字符串，格式详见return
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:07
     * @Params     : [startDate, endDate]
     * @Return     : java.util.List<java.lang.String>
     */
    public static List<String> hourMinStrJust(Date startDate, Date endDate) {
        return getHourStr(startDate, endDate, 11, 16);
    }

    /**
     * @Desc: 获取两个时间之间的小时字符串，格式详见return
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:07
     * @Params     : [startDate, endDate]
     * @Return     : java.util.List<java.lang.String>
     */
    public static List<String> hourMinStrJust(String startDate, String endDate) {
        Date start = DateFormatUtils.parse(startDate, DateFormatUtils.DateFormatter.PATTERN_19);
        Date end = DateFormatUtils.parse(endDate, DateFormatUtils.DateFormatter.PATTERN_19);
        return hourMinStrJust(start, end);
    }


    /**
     * @Desc: 获取小时字符串，总格式：yyyy-MM-dd HH:mm:ss
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:07
     * @Params     : [startDate, endDate, start, end]
     * @Return     : java.util.List<java.lang.String>
     */
    @SuppressWarnings("")
    private static List<String> getHourStr(Date startDate, Date endDate, int start, int end) {
        List<Date> hours = allHourDate(startDate, endDate);
        List<String> hourStrings = new ArrayList<>(hours.size());
        DateFormat format = DateFormatUtils.getDateFormat(DateFormatUtils.DateFormatter.PATTERN_19);
        for (Date hour : hours) {
            hourStrings.add(format.format(hour).substring(start, end));
        }
        return hourStrings;
    }

    /**
     * @Desc: 生成所有小时单位的时间点，返回字符串
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:07
     * @Params     : [startDate, endDate]
     * @Return     : java.util.List<java.lang.String>
     */
    public static List<String> allHourStr(Date startDate, Date endDate) {
        return getHourStr(startDate, endDate, 0, 16);
    }

    /**
     * @Desc: 获取两个时间之间的“小时”时间点
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:08
     * @Params     : [startDate, endDate]
     * @Return     : java.util.List<java.lang.String>
     */
    public static List<String> allHourStr(String startDate, String endDate) {
        Date start = DateFormatUtils.parse(startDate, DateFormatUtils.DateFormatter.PATTERN_19);
        Date end = DateFormatUtils.parse(endDate, DateFormatUtils.DateFormatter.PATTERN_19);
        return allHourStr(start, end);
    }

    /**
     * @Desc: 获取两个时间之间的“小时”时间点，返回格式yyyy-MM-dd HH:mm:ss
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:08
     * @Params     : [startDate, endDate]
     * @Return     : java.util.List<java.lang.String>
     */
    public static List<String> allHourStrComplete(String startDate, String endDate) {
        List<Date> hours = allHourDate(startDate, endDate);
        List<String> hourList = new ArrayList<>(hours.size());
        DateFormat format = DateFormatUtils.getDateFormat(DateFormatUtils.DateFormatter.PATTERN_19);
        hours.forEach(i -> hourList.add(format.format(i)));
        return hourList;
    }

    public static List<Date> buildDates(String start, String end, int precision, int increase) {
        Date startDate = DateFormatUtils.parseDefault(start), endDate = DateFormatUtils.parseDefault(end);
        Calendar calendarS = Calendar.getInstance(), calendarE = Calendar.getInstance();
        calendarS.setTime(startDate);
        calendarE.setTime(endDate);
        initCalendar(calendarS, precision);
        initCalendar(calendarE, precision);
        List<Date> dates = new LinkedList<>();
        while (!calendarS.after(calendarE)) {
            dates.add(calendarS.getTime());
            calendarS.add(precision, increase);
        }
        return dates;
    }

    /**
     * @Desc:   根据精度precision（Calendar.[HOUR,HOUR_OF_DAY,MINUTE,SECOND,MILLISECOND,DATE,MONTH,YEAR]）
     * 每increase个时间间隔补全时间，返回pattern格式的时间字符串
     * @Params: [start, end, precision, increase, pattern]
     * @Return: java.util.List<java.lang.String>
     * @Author: HYbrid
     * @Date:   2022/6/27
     */
    public static List<String> dateStrComplete(String start, String end, int precision, int increase, String pattern) {
        List<Date> dates = buildDates(start, end, precision, increase);
        List<String> dateStrings = new ArrayList<>(dates.size());
        DateFormat format = DateFormatUtils.getDateFormat(pattern);
        for (Date d : dates) {
            dateStrings.add(format.format(d));
        }
        return dateStrings;
    }

    public static List<String> dateStrComplete(String start, String end, String timeType, String pattern) {
        int precision, increase = 1;
        if (BaseCst.TimeTypeCst.HOUR.equalsIgnoreCase(timeType)) {
            precision = Calendar.HOUR_OF_DAY;
        } else if (BaseCst.TimeTypeCst.DAY.equalsIgnoreCase(timeType)) {
            precision = Calendar.DAY_OF_MONTH;
        } else if (BaseCst.TimeTypeCst.MINUTE.equals(timeType)) {
            precision = Calendar.MINUTE;
        } else if (BaseCst.TimeTypeCst.TIMESTAMP.equalsIgnoreCase(timeType)) {
            precision = Calendar.MILLISECOND;
        } else if (BaseCst.TimeTypeCst.SECOND.equalsIgnoreCase(timeType)) {
            precision = Calendar.SECOND;
        } else if (BaseCst.TimeTypeCst.YEAR.equalsIgnoreCase(timeType)) {
            precision = Calendar.YEAR;
        } else if (BaseCst.TimeTypeCst.MONTH.equals(timeType)) {
            precision = Calendar.MONTH;
        } else if (BaseCst.TimeTypeCst.WEEK.equalsIgnoreCase(timeType)) {
            return new LinkedList<>() {{
                add("周一");
                add("周二");
                add("周三");
                add("周四");
                add("周五");
                add("周六");
                add("周日");
            }};
        } else {
            return Collections.emptyList();
        }
        List<Date> dates = buildDates(start, end, precision, increase);
        Set<String> res = new HashSet<>(dates.size());
        String p = StringUtils.isBlank(pattern) ? BaseCst.TimeTypeCst.pattern(timeType) : pattern;
        dates.forEach(i -> res.add(DateFormatUtils.format(i, p)));
        return res.stream().sorted(Comparator.comparing(String::valueOf)).collect(Collectors.toList());
    }
}
