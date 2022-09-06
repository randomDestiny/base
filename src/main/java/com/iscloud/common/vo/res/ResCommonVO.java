package com.iscloud.common.vo.res;

import com.iscloud.common.utils.SpringContextUtils;
import lombok.SneakyThrows;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * @Desc: ResCommonVO
 * @Author: HYbrid
 * @Date: 2022/6/15 11:27
 */
@SuppressWarnings("unused")
public interface ResCommonVO {

    /**
     * @Desc:   指定对象转换成本对象
     * @Params: [t]
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/6/15
     */
    default <T> void build(T t) {
        if (t != null) {
            BeanUtils.copyProperties(t, this, SpringContextUtils.getNullPropertyNames(t));
        }
    }

    /**
     * @Desc:   将本对象转换成别的对象
     * @Params: [cls, paramTypes]
     * @Return: R
     * @Author: HYbrid
     * @Date:   2022/6/15
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows
    default  <R> R transfer(Class<R> cls, Map<Class<?>, Object> paramTypes) {
        if (cls == null) {
            return null;
        }
        R r;
        if (MapUtils.isNotEmpty(paramTypes)) {
            int l = paramTypes.size();
            Class<?>[] types = new Class<?>[l];
            Object[] values = new Object[l];
            Iterator<Map.Entry<Class<?>, Object>> it = paramTypes.entrySet().iterator();
            int i = 0;
            while (it.hasNext()) {
                Map.Entry<Class<?>, Object> entry = it.next();
                types[i] = entry.getKey();
                values[i] = entry.getValue();
                i++;
            }
            r = cls.getDeclaredConstructor(types).newInstance(values);
        } else {
            r = (R) cls.getDeclaredConstructors()[0].newInstance();
        }
        BeanUtils.copyProperties(this, r, SpringContextUtils.getNullPropertyNames(this));
        return r;
    }

}
