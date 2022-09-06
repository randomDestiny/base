package com.iscloud.common.cst;

import com.alibaba.fastjson.JSONArray;
import com.iscloud.common.utils.JsonUtils;

/**
 * @Desc: 预警状态
 * @Author: HYbrid
 * @Date: 2022/8/27 16:11
 */
@SuppressWarnings("unused")
public enum WarnEnum {

    // 预警状态int PENDING = 0, DOING = 10, FINISH = 20;
    START(0, "开始预警"),
    CONTINUING(10, "持续预警"),
    FINISH(20, "预警终止"),
    ;

    private final String value;
    private final int code;

    WarnEnum(int code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return this.value;
    }
    public int getCode() {
        return this.code;
    }

    public static WarnEnum getByVal(String v) {
        if (v != null) {
            for (WarnEnum t : WarnEnum.values()) {
                if (t.getValue().equals(v)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static WarnEnum getByCode(int code) {
        for (WarnEnum t : WarnEnum.values()) {
            if (t.getCode() == code) {
                return t;
            }
        }
        return null;
    }

    public static JSONArray toArray() {
        JSONArray res = new JSONArray();
        String name = "预警状态";
        for (WarnEnum t : WarnEnum.values()) {
            res.add(JsonUtils.buildSysCfg(name, t, t.getCode(), t.getValue()));
        }
        return res;
    }

}
