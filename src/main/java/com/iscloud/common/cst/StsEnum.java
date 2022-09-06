package com.iscloud.common.cst;

import com.alibaba.fastjson.JSONArray;
import com.iscloud.common.utils.JsonUtils;

/**
 * @Desc: Sts枚举
 * @Author: HYbrid
 * @Date: 2022/8/27 16:11
 */
@SuppressWarnings("unused")
public enum StsEnum {

    // 数据状态
    VALID(1, "有效"),
    INVALID(0, "无效")
    ;

    private final String value;
    private final int code;

    StsEnum(int code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return this.value;
    }
    public int getCode() {
        return this.code;
    }

    public static StsEnum getByVal(String v) {
        if (v != null) {
            for (StsEnum t : StsEnum.values()) {
                if (t.getValue().equals(v)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static StsEnum getByCode(int code) {
        for (StsEnum t : StsEnum.values()) {
            if (t.getCode() == code) {
                return t;
            }
        }
        return null;
    }

    public static JSONArray toArray() {
        JSONArray res = new JSONArray();
        String name = "数据状态";
        for (StsEnum t : StsEnum.values()) {
            res.add(JsonUtils.buildSysCfg(name, t, t.getCode(), t.getValue()));
        }
        return res;
    }

}
