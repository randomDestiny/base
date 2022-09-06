package com.iscloud.common.utils;

import com.iscloud.common.cst.BaseCst;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Desc: 日期时间格式化工具类
 * @Author: yu_hua
 */
public final class DateFormatUtils {

	private static final ThreadLocal<DateFormatter> THREAD_LOCAL = new ThreadLocal<>();

	@SuppressWarnings("unused")
	public static void remove() {
		THREAD_LOCAL.remove();
	}

	public static DateFormat getDateFormat(String pattern) {
		DateFormatter df = THREAD_LOCAL.get();
		if (df == null) {
			df = new DateFormatter();
			THREAD_LOCAL.set(df);
		}
		return df.getDateFormat(pattern);
	}

	/**
	 * @Desc: 格式化日期
	 * @Author     : HYbrid
	 * @Date       : 2021/2/22 15:11
	 * @Params     : [date, pattern]
	 * @Return     : java.lang.String
	 */
	public static String format(Date date, String pattern) {
		if (date == null) {
			return null;
		}
		return getDateFormat(pattern).format(date);
	}

	public static String formatDefault(Date date) {
		return format(date, DateFormatter.PATTERN_19);
	}

	public static Date parse(String strDate, String pattern) {
		Date date = null;
		try {
			DateFormat df = getDateFormat(pattern);
			if (df != null) {
				String d = strDate.contains(BaseCst.SymbolCst.POINT)
						? strDate.split(BaseCst.SymbolCst.POINT_SUFFIX)[0]
						: strDate;
				date = df.parse(d);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date parseDefault(String strDate) {
		return parse(dateComplete(strDate), DateFormatter.PATTERN_19);
	}

	public static String dateComplete(String date) {
		String d = null;
		if (StringUtils.isNotBlank(date)) {
			int l = date.length();
			if (DateFormatter.PATTERN_4.length() == l) {
				d = date + "-01-01 00:00:00";
			} else if (DateFormatter.PATTERN_7.length() == l) {
				d = date + "-01 00:00:00";
			} else if (DateFormatter.PATTERN_10.length() == l) {
				d = date + " 00:00:00";
			} else if (DateFormatter.PATTERN_13.length() == l) {
				d = date + ":00:00";
			} else if (DateFormatter.PATTERN_16.length() == l) {
				d = date + ":00";
			} else if (DateFormatter.PATTERN_19.length() == l) {
				d = date;
			} else if (date.contains(BaseCst.SymbolCst.POINT)) {
				d = date.split(BaseCst.SymbolCst.POINT_SUFFIX)[0];
			}
		}
		return d;
	}

	public static Date dateComplete2Date(String date) {
		String d = dateComplete(date);
		return StringUtils.isBlank(d) ? null : DateFormatUtils.parseDefault(d);
	}

	/**
	 * @Desc: DateFormatter对象用于存储各DateFormat的实例
	 * @Author: yu_hua
	 */
	public static class DateFormatter {

		public static final String PATTERN_7 = "yyyy-MM";
		public static final String PATTERN_10 = "yyyy-MM-dd";
		public static final String PATTERN_13 = "yyyy-MM-dd HH";
		public static final String PATTERN_16 = "yyyy-MM-dd HH:mm";
		public static final String PATTERN_19 = "yyyy-MM-dd HH:mm:ss";
		public static final String PATTERN_23 = "yyyy-MM-dd HH:mm:ss sss";
		public static final String PATTERN_YYYYMMDD = "yyyyMMdd";
		public static final String PATTERN_4 = "yyyy";
		public static final String PATTERN_HH = "HH";
		public static final String PATTERN_HH_MM = "HH:mm";
		public static final String PATTERN_YYYYMMDDHH = "yyyyMMddHH";
		public static final String PATTERN_YYYYMMDDHHMISS = "yyyyMMddHHmmss";

		private static final Map<String, DateFormat> FORMAT_MAP = new HashMap<>(12);

		static {
			FORMAT_MAP.put(PATTERN_4, new SimpleDateFormat(PATTERN_4));
			FORMAT_MAP.put(PATTERN_7, new SimpleDateFormat(PATTERN_7));
			FORMAT_MAP.put(PATTERN_10, new SimpleDateFormat(PATTERN_10));
			FORMAT_MAP.put(PATTERN_13, new SimpleDateFormat(PATTERN_13));
			FORMAT_MAP.put(PATTERN_16, new SimpleDateFormat(PATTERN_16));
			FORMAT_MAP.put(PATTERN_19, new SimpleDateFormat(PATTERN_19));
			FORMAT_MAP.put(PATTERN_23, new SimpleDateFormat(PATTERN_23));
			FORMAT_MAP.put(PATTERN_YYYYMMDD, new SimpleDateFormat(PATTERN_YYYYMMDD));
			FORMAT_MAP.put(PATTERN_HH, new SimpleDateFormat(PATTERN_HH));
			FORMAT_MAP.put(PATTERN_HH_MM, new SimpleDateFormat(PATTERN_HH_MM));
			FORMAT_MAP.put(PATTERN_YYYYMMDDHH, new SimpleDateFormat(PATTERN_YYYYMMDDHH));
			FORMAT_MAP.put(PATTERN_YYYYMMDDHHMISS, new SimpleDateFormat(PATTERN_YYYYMMDDHHMISS));
		}

		public DateFormat getDateFormat(String pattern) {
			if (StringUtils.isNotBlank(pattern)) {
				DateFormat df = FORMAT_MAP.get(pattern);
				if (df == null) {
					df = new SimpleDateFormat(pattern);
					FORMAT_MAP.put(pattern, df);
				}
				return df;
			}
			return null;
		}
	}

}
