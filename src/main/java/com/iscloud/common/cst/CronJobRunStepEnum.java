package com.iscloud.common.cst;

import com.alibaba.fastjson.JSONArray;
import com.iscloud.common.utils.JsonUtils;

/**
 * @Author: HYbrid
 */
@SuppressWarnings("unused")
public enum CronJobRunStepEnum {

    // 定时任务步骤
    NOT_START(0, "未开始"),
    FETCH(1, "获取待处理数据"),
    BEFORE(2, "前置处理"),
    HANDLE(3, "核心处理"),
    AFTER(4, "后置处理"),
    FINISH(5, "已结束"),
    ;

    private final String value;
    private final int code;

    CronJobRunStepEnum(int code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return this.value;
    }
    public int getCode() {
        return this.code;
    }

    public static CronJobRunStepEnum getByVal(String v) {
        if (v != null) {
            for (CronJobRunStepEnum t : CronJobRunStepEnum.values()) {
                if (t.getValue().equals(v)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static CronJobRunStepEnum getByCode(int code) {
        for (CronJobRunStepEnum t : CronJobRunStepEnum.values()) {
            if (code == t.getCode()) {
                return t;
            }
        }
        return null;
    }

    public static JSONArray toArray() {
        JSONArray res = new JSONArray();
        String name = "定时任务步骤";
        for (CronJobRunStepEnum t : CronJobRunStepEnum.values()) {
            res.add(JsonUtils.buildSysCfg(name, t, t.getCode(), t.getValue()));
        }
        return res;
    }

}
