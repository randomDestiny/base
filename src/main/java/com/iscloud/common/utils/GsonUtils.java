package com.iscloud.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * @Desc: gson工具类
 * @Author ：HYbrid
 * @Date ：2021/2/23 20:25
 */
@SuppressWarnings("unused")
public class GsonUtils {
    public static final ThreadLocal<Gson> LOCAL = new ThreadLocal<>();

    public static Gson getInst() {
        Gson gson = LOCAL.get();
        if (gson == null) {
            gson = new GsonBuilder().disableHtmlEscaping().setDateFormat(DateFormatUtils.DateFormatter.PATTERN_19).create();
            LOCAL.set(gson);
        }
        return gson;
    }

    public static <T> T parse(String json, Class<T> t) {
        return getInst().fromJson(json, t);
    }

    public static String toJson(Object o) {
        return getInst().toJson(o);
    }

    public static JsonElement toJsonTree(Object o) {
        return getInst().toJsonTree(o);
    }

    public static void clear() {
        LOCAL.remove();
    }
}
