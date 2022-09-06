package com.iscloud.common.cst;

import com.alibaba.fastjson.JSONArray;
import com.iscloud.common.utils.JsonUtils;

/**
 * @Desc: 附件类别
 * @Author: HYbrid
 * @Date: 2022/8/27 16:11
 */
@SuppressWarnings("unused")
public enum AttachCategoryEnum {

    // 附件类别
    SALE_AFTER_APPLY("sale_after_apply", "售后申请"),
    MAINTAIN_FINISH("maintain_finish", "维保完成"),
    MAINTAIN_CONFIRM("maintain_confirm", "维保确认"),
    MAINTAIN_REJECT("maintain_reject", "维保拒绝"),
    ;

    private final String value;
    private final String code;

    AttachCategoryEnum(String code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return this.value;
    }
    public String getCode() {
        return this.code;
    }

    public static AttachCategoryEnum getByVal(String v) {
        if (v != null) {
            for (AttachCategoryEnum t : AttachCategoryEnum.values()) {
                if (t.getValue().equals(v)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static AttachCategoryEnum getByCode(String code) {
        if (code != null) {
            for (AttachCategoryEnum t : AttachCategoryEnum.values()) {
                if (t.getCode().equals(code)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static JSONArray toArray() {
        JSONArray res = new JSONArray();
        String name = "附件类别";
        for (AttachCategoryEnum t : AttachCategoryEnum.values()) {
            res.add(JsonUtils.buildSysCfg(name, t, t.getCode(), t.getValue()));
        }
        return res;
    }

}
