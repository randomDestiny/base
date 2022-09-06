package com.iscloud.common.cst;

import com.alibaba.fastjson.JSONArray;
import com.iscloud.common.utils.JsonUtils;

/**
 * @Desc: 版本环境
 * @Author: HYbrid
 * @Date: 2022/8/27 16:11
 */
@SuppressWarnings("unused")
public enum EnvEnum {

    // 版本环境
    DEV("dev", "开发环境"),
    SINGLE("single", "单例环境"),
    PROD("prod", "生产环境"),
    TEST("test", "测试环境"),
    ;

    private final String value;
    private final String code;

    EnvEnum(String code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return this.value;
    }
    public String getCode() {
        return this.code;
    }

    public static EnvEnum getByVal(String v) {
        if (v != null) {
            for (EnvEnum t : EnvEnum.values()) {
                if (t.getValue().equals(v)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static EnvEnum getByCode(String code) {
        if (code != null) {
            for (EnvEnum t : EnvEnum.values()) {
                if (t.getCode().equals(code)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static JSONArray toArray() {
        JSONArray res = new JSONArray();
        String name = "版本环境";
        for (EnvEnum t : EnvEnum.values()) {
            res.add(JsonUtils.buildSysCfg(name, t, t.getCode(), t.getValue()));
        }
        return res;
    }

}
