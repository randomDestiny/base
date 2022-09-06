package com.iscloud.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;

import java.util.concurrent.TimeUnit;

/**
 * @Desc: redis实体对象
 * @Author ：HYbrid
 * @Date ：2021/7/27 17:12
 */
@SuppressWarnings("unused")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RedisEntity<T> {
    protected String key;
    protected T value;
    /**
     * @Desc: key映射的类
     */
    protected String clazzName;
    /**
     * @Desc: key映射的类名（不带包名）
     */
    protected TimeUnit expireUnit = TimeUnit.SECONDS;
    /**
     * @Desc: 设置的失效时间戳的时间单位
     */
    protected long expireTime;
    /**
     * @Desc: 设置的失效时间戳
     */
    protected Class<?> clazz;

    public RedisEntity(String k, T v, long t) {
        Asserts.notNull(v, "实例化RedisEntity时value为空！");
        Asserts.notBlank(k, "实例化RedisEntity时key为无效字符！");
        Asserts.check(t > 0, "失效时间戳必须大于0！");
        this.key = k;
        this.value = v;
        this.clazz = v.getClass();
        this.clazzName = v.getClass().getSimpleName();
        this.expireTime = t;
    }
    public RedisEntity(String k, T v) {
        Asserts.notNull(v, "实例化RedisEntity时value为空！");
        Asserts.notBlank(k, "实例化RedisEntity时key为无效字符！");
        this.key = k;
        this.value = v;
        this.clazz = v.getClass();
        this.clazzName = v.getClass().getSimpleName();
    }
    public RedisEntity(String k, Class<?> clazz) {
        Asserts.notNull(clazz, "实例化RedisEntity时clazz为空！");
        Asserts.notBlank(k, "实例化RedisEntity时key为无效字符！");
        this.key = k;
        this.clazz = clazz;
        this.clazzName = clazz.getSimpleName();
    }
    public RedisEntity(String k) {
        Asserts.notBlank(k, "实例化RedisEntity时key为无效字符！");
        this.key = k;
    }

    public boolean checkGet() {
        return StringUtils.isNotBlank(key);
    }
    public boolean checkExpire() {
        return StringUtils.isNotBlank(key) && expireTime > 0 && expireUnit != null;
    }
    public boolean checkSet() {
        return StringUtils.isNotBlank(key) && value != null;
    }
}
