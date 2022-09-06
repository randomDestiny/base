package com.iscloud.common.cst;

import com.alibaba.fastjson.JSONArray;
import com.iscloud.common.utils.JsonUtils;

/**
 * @Desc: Sts枚举
 * @Author: HYbrid
 * @Date: 2022/8/27 16:11
 */
@SuppressWarnings("unused")
public enum DelEnum {

    // 数据状态
    DELETED(1, "无效"),
    UNDELETED(0, "有效")
    ;

    private final String value;
    private final int code;

    DelEnum(int code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return this.value;
    }
    public int getCode() {
        return this.code;
    }

    public static DelEnum getByVal(String v) {
        if (v != null) {
            for (DelEnum t : DelEnum.values()) {
                if (t.getValue().equals(v)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static DelEnum getByCode(int code) {
        for (DelEnum t : DelEnum.values()) {
            if (t.getCode() == code) {
                return t;
            }
        }
        return null;
    }

    public static JSONArray toArray() {
        JSONArray res = new JSONArray();
        String name = "数据状态";
        for (DelEnum t : DelEnum.values()) {
            res.add(JsonUtils.buildSysCfg(name, t, t.getCode(), t.getValue()));
        }
        return res;
    }

}
