package com.iscloud.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iscloud.common.cst.BaseCst.SymbolCst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

/**
 * @Desc:   properties文件工具类
 * @Author: HYbrid
 * @Date:   2022/5/30
 */
@Slf4j
@Component(PropertiesUtils.BEAN_DEFAULT)
@SuppressWarnings("unused")
public class PropertiesUtils {
    public static final String BEAN_DEFAULT = "PropertiesUtils";

    public static void save(JSONObject json, String path) {
        if (json != null) {
            save(json.getInnerMap(), path, StandardCharsets.UTF_8);
        }
    }

    public static void save(JSONObject json, String path, Charset charset) {
        if (json != null) {
            save(json.getInnerMap(), path, charset);
        }
    }

    public static void save(Map<String, Object> map, String path) {
        save(map, path, StandardCharsets.UTF_8);
    }

    public static void save(Map<String, Object> map, String path, Charset charset) {
        StringBuilder sb = new StringBuilder();
        if (MapUtils.isNotEmpty(map)) {
            map.forEach((k, v) -> {
                if (StringUtils.isNotBlank(k)) {
                    sb.append(k.trim()).append("=");
                    if (v != null) {
                        sb.append(GsonUtils.toJson(v));
                    }
                    sb.append("\n");
                }
            });
            if (StringUtils.isNotBlank(sb)) {
                try (FileOutputStream is = new FileOutputStream(path)) {
                    IOUtils.write(sb.toString().trim(), is, charset);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @Desc: 获取配置信息
     * @Author:      Yu.Hua
     * @Date:        2020/11/6 0006 14:06
     * @Params:      []
     * @Return:      java.util.Properties
     */
    public static Properties getProperties(String filePath) {
        Properties properties = new Properties();
        if (StringUtils.isBlank(filePath)) {
            throw new RuntimeException("无效的配置文件路径！");
        }
        try (InputStream inputStream = new FileInputStream(filePath)) {
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static String append(String... str) {
        if (ArrayUtils.isEmpty(str)) {
            return StringUtils.EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : str) {
            builder.append(s).append(SymbolCst.POINT);
        }
        builder.setLength(builder.length() -1);
        return builder.toString();
    }

    public static <T> T getVal(Properties properties, Class<T> t, String... str) {
        String k = append(str);
        if (StringUtils.isBlank(k)) {
            return null;
        }
        return JSON.parseObject(properties.getProperty(k), t);
    }

    public static String getVal(Properties properties, String... str) {
        String k = append(str);
        if (StringUtils.isBlank(k)) {
            return null;
        }
        return properties.getProperty(k);
    }

}
