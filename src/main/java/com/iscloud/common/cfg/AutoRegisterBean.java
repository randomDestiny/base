package com.iscloud.common.cfg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iscloud.common.utils.JsonUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Desc:   mapping的key为beanName
 * @Author: HYbrid
 * @Date:   2022/3/25
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "register", ignoreInvalidFields = true)
public class AutoRegisterBean {
    public static final String BEAN = "AutoRegisterBean";

    private JSONObject mapping;

    public AutoRegisterBean(JSONObject mapping) {
        setMapping(mapping);
    }

    public AutoRegisterBean(Map<String, Object> mapping) {
        setMapping(mapping);
    }

    public AutoRegisterBean(String json) {
        setMapping(json);
    }

    public void setMapping(JSONObject mapping) {
        if (mapping != null && !mapping.isEmpty()) {
            this.mapping = mapping;
            check();
        }
    }

    public void setMapping(Map<String, Object> mapping) {
        if (MapUtils.isNotEmpty(mapping)) {
            this.mapping = JsonUtils.toJSONObject(mapping);
            check();
        }
    }

    public void setMapping(String json) {
        if (StringUtils.isNotBlank(json)) {
            this.mapping = JSON.parseObject(json);
            check();
        }
    }

    public void check() {
        JsonUtils.removeNonKey(this.mapping, true);
        this.mapping = JsonUtils.removeArray(this.mapping, false);
    }
}
