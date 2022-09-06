package com.iscloud.common.utils;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iscloud.common.cst.BaseCst;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class JsonUtils implements BaseCst.ResultCst {

    public static final String X = "xAxis", Y = "yAxis", DATA = "data";

    public static final SimplePropertyPreFilter filter = new SimplePropertyPreFilter();

    static {
        filter.getExcludes().add("orders");
        filter.getExcludes().add("searchCount");
        filter.getExcludes().add("optimizeCountSql");
    }

    public static final SerializerFeature[] SERIALIZER_FEATURES_DEF = {
            SerializerFeature.DisableCircularReferenceDetect
    };

    public static final SerializerFeature[] SERIALIZER_FEATURES = {
            SerializerFeature.DisableCircularReferenceDetect
            , SerializerFeature.WriteNullListAsEmpty
            , SerializerFeature.WriteNullStringAsEmpty
            , SerializerFeature.WriteMapNullValue
//				, SerializerFeature.WriteNullBooleanAsFalse
//				, SerializerFeature.WriteNullNumberAsZero
    };

    public static FastJsonConfig buildDefault() {
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(SERIALIZER_FEATURES);
        return config;
    }

    public static FastJsonConfig build(SerializerFeature... serializerFeatures) {
        FastJsonConfig config = new FastJsonConfig();
        if (ArrayUtils.isNotEmpty(serializerFeatures)) {
            config.setSerializerFeatures(serializerFeatures);
        }
        return config;
    }

    /**
     * @Desc:   将一个对象强转成另一个对象
     * @Params: [o, clazz]
     * @Return: T
     * @Author: HYbrid
     * @Date:   2022/1/14
     */
    public static <T> T parse(Object o, Class<T> clazz) {
        if (o == null || clazz == null) {
            return null;
        }
        String m = JSON.toJSONString(o);
        if (isEmptyObject(m)) {
            return null;
        }
        return JSON.parseObject(m, clazz);
    }

    /**
     * @Desc: 是否为空内容
     * @Author     : HYbrid
     * @Date       : 2021/9/22 9:43
     * @Params     : [o]
     * @Return     : boolean
     */
    public static boolean isEmptyObject(Object o) {
        return o == null
                || (o instanceof CharSequence && (StringUtils.isBlank((CharSequence) o) || EMPTY_OBJECT.equals(o)))
                || EMPTY_OBJECT.equals(JSON.toJSONString(o));
    }

    /**
     * @Desc: 构成初始化page对象
     * @Author:      Yu.Hua
     * @Date:        2020/11/23 0023 18:44
     * @Params:      [entity]
     * @Return:      com.alibaba.fastjson.JSONObject
     */
    public static <T> JSONObject initPage(T entity) {
        Object o = JSON.toJSON(entity);
        int curr = 1, size = Integer.MAX_VALUE;
        if (o instanceof JSONObject) {
            Integer n = ((JSONObject) o).getInteger("pageNum"), s = ((JSONObject) o).getInteger("pageSize");
            if (n != null && n > 0) {
                curr = n;
            }
            if (s != null && s > 0) {
                size = s;
            }
        }
        return initPage(curr, size);
    }

    /**
     * @Desc: 构成初始化page对象
     * @Author:      Yu.Hua
     * @Date:        2020/8/22 0022 16:05
     * @Params:      [pageNum, pageSize]
     * @Return:      com.alibaba.fastjson.JSONObject
     */
    public static JSONObject initPage(Integer pageNum, Integer pageSize) {
        JSONObject res = new JSONObject();
        res.put("current", pageNum == null ? 1 : pageNum);
        res.put("pages", 0);
        res.put("size", pageSize == null ? Integer.MAX_VALUE : pageSize);
        res.put("total", 0);
        res.put("records", "");
        return res;
    }

    /**
     * @Desc: 把对象转成JSONArray
     * @Author:      Yu.Hua
     * @Date:        2020/4/28 0028 17:08
     * @Params:      [obj]
     * @Return:      com.alibaba.fastjson.JSONArray
     */
    public static JSONArray toJSONArray(Object obj) {
        String json = JSON.toJSONString(obj);
        return JSONArray.parseArray(json);
    }

    /**
     * @Desc: 把对象转成JSONObject
     * @Author:      Yu.Hua
     * @Date:        2020/4/28 0028 17:08
     * @Params:      [obj]
     * @Return:      com.alibaba.fastjson.JSONObject
     */
    public static JSONObject toJSONObject(Object obj) {
        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        } else if (obj instanceof JSONArray) {
            throw new RuntimeException("当前对象无法转换为JSONObject：\n" + JSON.toJSONString(obj));
        }
        String json = JSON.toJSONString(obj);
        return JSONObject.parseObject(json);
    }

    /**
     * @Desc: 把对象转成JSONObject，并添加属性数组
     * @Author:      Yu.Hua
     * @Date:        2020/4/28 0028 17:09
     * @Params:      [object, propertyName, array]
     * @Return:      com.alibaba.fastjson.JSONObject
     */
    public static JSONObject toJSONObject(Object object, String propertyName, Object array) {
        JSONArray arr = JSONArray.parseArray(JSON.toJSONString(array));
        JSONObject obj = JSONObject.parseObject(JSON.toJSONString(object));
        obj.put(propertyName, arr);
        return obj;
    }

    /**
     * @Desc: 把对象转成JSONObject，并添加属性对象
     * @Author:      Yu.Hua
     * @Date:        2020/4/28 0028 17:09
     * @Params:      [obj1, propertyName, obj2]
     * @Return:      com.alibaba.fastjson.JSONObject
     */
    public static JSONObject toJSONObject2(Object obj1, String propertyName, Object obj2) {
        JSONObject o1 = JSONObject.parseObject(JSON.toJSONString(obj1));
        Object o2 = null;
        if (obj2 != null) {
            if (obj2 instanceof String || obj2 instanceof BigDecimal) {
                o2 = obj2;
            } else {
                o2 = JSONObject.parseObject(JSON.toJSONString(obj2));
            }
        }
        o1.put(propertyName, o2);
        return o1;
    }

    public static byte[] jsonObjTrim2Bytes(String json) {
        return jsonTrim(json).getBytes(StandardCharsets.UTF_8);
    }

    public static String jsonTrim(String json) {
        Object o = JSON.parse(json);
        if (o instanceof JSONArray a) {
            jsonArrTrim(a);
            return a.toString();
        }
        return jsonObjTrim((JSONObject) o).toJSONString();
    }

    public static JSONObject jsonObjTrim(JSONObject json) {
        removeNonKey(json, false);
        if (json != null) {
            json.forEach((k, v) -> {
                if (v instanceof String) {
                    json.put(k, ((String) v).trim());
                } else if (v instanceof JSONObject) {
                    jsonObjTrim((JSONObject) v);
                } else if (v instanceof JSONArray) {
                    jsonArrTrim((JSONArray) v);
                }
            });
        }
        return json;
    }

    public static void jsonArrTrim(JSONArray array) {
        if (array != null && !array.isEmpty()) {
            Object v;
            for (int i = 0, l = array.size(); i < l; i++) {
                v = array.get(i);
                if (v instanceof String) {
                    array.set(i, ((String) v).trim());
                } else if (v instanceof JSONObject) {
                    jsonObjTrim((JSONObject) v);
                } else if (v instanceof JSONArray) {
                    jsonArrTrim((JSONArray) v);
                }
            }
        }
    }

    /**
     * @Desc:   只删除本层级的空的key
     * @Params: [o]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/6/27
     */
    public static void removeNonKeySelf(JSONObject o) {
        removeNonKey(o, false);
    }

    /**
     * @Desc:   删除空的key
     * @Params: [o, childAble]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/3/25
     */
    public static void removeNonKey(JSONObject o, boolean childAble) {
        if (o != null) {
            o.remove(null);
            o.remove(StringUtils.EMPTY);
            if (childAble) {
                o.forEach((k, v) -> {
                    if (v instanceof JSONObject) {
                        removeNonKey((JSONObject) v, true);
                    } else if (v instanceof JSONArray) {
                        removeNonKey((JSONArray) v, true);
                    }
                });
            }
        }
    }

    /**
     * @Desc:   删除空的key
     * @Params: [a, childAble]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/3/25
     */
    @SuppressWarnings("unchecked")
    public static void removeNonKey(JSONArray a, boolean childAble) {
        if (a != null) {
            a.forEach(i -> {
                if (i instanceof JSONObject) {
                    removeNonKey((JSONObject) i, childAble);
                } else if (i instanceof JSONArray) {
                    removeNonKey((JSONArray) i, childAble);
                }
            });
        }
    }

    /**
     * @Desc:   删除数组
     * @Params: [o, childAble]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/3/25
     */
    public static JSONObject removeArray(JSONObject o, boolean childAble) {
        JSONObject res = new JSONObject();
        if (o != null && !o.isEmpty()) {
            o.forEach((k, v) -> {
                if (v != null && !(v instanceof JSONArray)) {
                    if (childAble && v instanceof JSONObject) {
                        v = removeArray((JSONObject) v, true);
                    }
                    res.put(k, v);
                }
            });
        }
        return res;
    }

    public static JSONObject buildCharts(Object x, Object y, Object data) {
        JSONObject res = new JSONObject();
        res.put(X, x);
        res.put(Y, y);
        res.put(DATA, data);
        return res;
    }

    public static JSONObject buildChartsEmpty() {
        JSONObject res = new JSONObject();
        res.put(X, StringUtils.EMPTY);
        res.put(Y, StringUtils.EMPTY);
        res.put(DATA, StringUtils.EMPTY);
        return res;
    }

    public static JSONObject buildSysCfg(String categoryName, Enum<?> e, Object code, Object name) {
        JSONObject o = new JSONObject();
        o.put("category", e.getClass().getSimpleName());
        o.put("categoryName", categoryName);
        o.put("parentCode", BaseCst.SymbolCst.OBLIQUE_LINE);
        o.put("sorted", e.ordinal());
        o.put("val", e.name());
        o.put("editable", false);
        o.put("code", code);
        o.put("name", name);
        return o;
    }
}
