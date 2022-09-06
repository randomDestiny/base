package com.iscloud.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iscloud.common.cst.BaseCst;
import com.iscloud.common.entity.PageEntity;
import com.iscloud.common.vo.req.QueryParamsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: HYbrid
 */
@Slf4j
@SuppressWarnings({"unused", "rawtypes"})
public class LocalCacheUtils {

    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    public static final QueryParamsVO QUERY_PARAM_DEFAULT = new QueryParamsVO();
    public static final PageEntity PAGE_DEFAULT = new PageEntity<>(1, Integer.MAX_VALUE);

    private static String buildKey(String k, String pre) {
        return StringUtils.isNotBlank(pre) ? pre + BaseCst.SymbolCst.POINT + k : k;
    }

    public static void put(Object obj, String prefix) {
        if (obj != null) {
            Object o = JSON.toJSON(obj);
            if (o instanceof JSONObject) {
                ((JSONObject) o).getInnerMap().forEach((k, v) -> put(buildKey(k, prefix), v));
            }
        }
    }

    public static void putIfAbsent(Object obj, String prefix) {
        if (obj != null) {
            Object o = JSON.toJSON(obj);
            if (o instanceof JSONObject) {
                ((JSONObject) o).getInnerMap().forEach((k, v) -> putIfAbsent(buildKey(k, prefix), v));
            }
        }
    }

    public static void put(String json, String prefix) {
        if (StringUtils.isNotBlank(json)) {
            Object o = JSON.parse(json);
            if (o instanceof JSONObject) {
                ((JSONObject) o).getInnerMap().forEach((k, v) -> put(buildKey(k, prefix), v));
            }
        }
    }

    public static void putIfAbsent(String json, String prefix) {
        if (StringUtils.isNotBlank(json)) {
            Object o = JSON.parse(json);
            if (o instanceof JSONObject) {
                ((JSONObject) o).getInnerMap().forEach((k, v) -> putIfAbsent(buildKey(k, prefix), v));
            }
        }
    }

    public static void put(Map<String, Object> m, String prefix) {
        if (MapUtils.isNotEmpty(m)) {
            m.forEach((k, v) -> put(buildKey(k, prefix), v));
        }
    }

    public static void putIfAbsent(Map<String, Object> m, String prefix) {
        if (MapUtils.isNotEmpty(m)) {
            m.forEach((k, v) -> putIfAbsent(buildKey(k, prefix), v));
        }
    }

    public static void put(String k, Object v) {
        if (StringUtils.isNotBlank(k)) {
            CACHE.put(k, v);
        }
    }

    public static void putIfAbsent(String k, Object v) {
        if (StringUtils.isNotBlank(k)) {
            CACHE.putIfAbsent(k, v);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String k) {
        if (StringUtils.isNotBlank(k)) {
            Object v = CACHE.get(k);
            return (T) v;
        }
        return null;
    }
}
