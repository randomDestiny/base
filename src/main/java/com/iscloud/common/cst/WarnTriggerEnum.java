package com.iscloud.common.cst;

import com.alibaba.fastjson.JSONArray;
import com.iscloud.common.utils.JsonUtils;

/**
 * @Desc: 预警触发状态
 * @Author: HYbrid
 * @Date: 2022/8/27 16:11
 */
@SuppressWarnings("unused")
public enum WarnTriggerEnum {

    // 预警触发状态
    TRIGGER_STS_DOING(10, "触发中"),
    TRIGGER_STS_DONE(20, "已触发"),
    TRIGGER_STS_FINISH(30, "已终止"),
    TRIGGER_STS_UPDATE(40, "已变更"),
    ;

    private final String value;
    private final int code;

    WarnTriggerEnum(int code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return this.value;
    }
    public int getCode() {
        return this.code;
    }

    public static WarnTriggerEnum getByVal(String v) {
        if (v != null) {
            for (WarnTriggerEnum t : WarnTriggerEnum.values()) {
                if (t.getValue().equals(v)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static WarnTriggerEnum getByCode(int code) {
        for (WarnTriggerEnum t : WarnTriggerEnum.values()) {
            if (t.getCode() == code) {
                return t;
            }
        }
        return null;
    }

    public static JSONArray toArray() {
        JSONArray res = new JSONArray();
        String name = "预警触发状态";
        for (WarnTriggerEnum t : WarnTriggerEnum.values()) {
            res.add(JsonUtils.buildSysCfg(name, t, t.getCode(), t.getValue()));
        }
        return res;
    }

}
