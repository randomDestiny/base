package com.iscloud.common.helper;

import com.iscloud.common.utils.SpringContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Desc:   List帮助类
 * @Author: HYbrid
 * @Date:   2022/5/18
 */
@SuppressWarnings({"unused", "unchecked"})
public final class ListHelper {

    public static final String NULL = "null", NON_NULL = "non-null";

    public static byte[] list2Bytes(List<Byte> list) {
        list = trimNull(list);
        if (CollectionUtils.isNotEmpty(list)) {
            int l = list.size();
            byte[] res = new byte[l];
            for (int i = 0; i < l; i++) {
                res[i] = list.get(i);
            }
            return res;
        }
        return null;
    }

    /**
     * @Desc:   按timeList顺序获取dataList中相应属性的数据，返回list
     * @Params: [timeList, dataList, k, v, defVal]
     * @Return: java.util.List<R>
     * @Author: HYbrid
     * @Date:   2022/5/16
     */
    public static <T, R, S> List<R> buildByTimeList(List<S> timeList, List<T> dataList, Function<T, S> k, Function<T, R> v, R defVal) {
        if (CollectionUtils.isEmpty(timeList) || k == null || v == null) {
            return Collections.emptyList();
        }
        Map<S, R> map = list2Map(k, v, dataList);
        List<R> res = new ArrayList<>(dataList.size());
        timeList.forEach(i -> res.add(map.getOrDefault(i, defVal)));
        return res;
    }

    /**
     * @Desc:   将数据库返回的List<Map<String, Object>>转换成自定义Map
     * @Params: [l, k, kc, v, kv]
     * @Return: java.util.Map<T,F>
     * @Author: HYbrid
     * @Date:   2022/5/11
     */
    public static <T, F> Map<T, F> listMap2Map(List<Map<String, Object>> l, Function<String, T> k, String kc,
                                               Function<String, F> v, String kv) {
        return listMap2Map(l, k, kc, v, kv, null);
    }

    public static <T, F> Map<T, F> listMap2Map(List<Map<String, Object>> l, Function<String, T> k, String kc,
                                               Function<String, F> v, String kv, T defaultKey) {
        if (CollectionUtils.isNotEmpty(l)) {
            Map<T, F> res = new HashMap<>(l.size());
            l.forEach(i -> {
                Object key = i.get(kc), val = i.get(kv);
                res.put(key == null ? defaultKey : k.apply(String.valueOf(key)), val == null ? null : v.apply(String.valueOf(val)));
            });
            return res;
        }
        return Collections.emptyMap();
    }

    public static <T> Set<T> addAll2Set(Collection<T> l1, Collection<T> l2, boolean nullable) {
        Set<T> res = null;
        if (CollectionUtils.isNotEmpty(l1)) {
            if (l1 instanceof Set) {
                res = (Set<T>) l1;
            } else {
                res = new HashSet<>(l1.size());
                res.addAll(l1);
            }
        }
        if (CollectionUtils.isNotEmpty(l2)) {
            if (res == null && l2 instanceof Set) {
                res = (Set<T>) l2;
            } else {
                if (res == null) {
                    res = new HashSet<>(l2.size());
                }
                res.addAll(l2);
            }
        }
        if (!nullable && CollectionUtils.isNotEmpty(res)) {
            res.remove(null);
        }
        return res;
    }

    /**
     * @Desc:   查找是否包含元素，如果元素是String则忽略大小写
     * @Params: [l, t]
     * @Return: boolean
     * @Author: HYbrid
     * @Date:   2022/2/25
     */
    public static <T> boolean containsIgnoreCase(Collection<T> l, T t) {
        if (CollectionUtils.isNotEmpty(l)) {
            for (T o : l) {
                if (Objects.equals(o, t)) {
                    return true;
                } else if (o instanceof String a && t instanceof String b && a.equalsIgnoreCase(b)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> boolean containsIgnoreCase(T[] l, T t) {
        if (ArrayUtils.isNotEmpty(l)) {
            return containsIgnoreCase(Arrays.asList(l), t);
        }
        return false;
    }

    public static <T, K> Map<String, List<T>> splitByNull(Collection<T> list, Function<? super T, ? extends K> k) {
        Collection<T> l = trimNull(list);
        Map<String, List<T>> res = new HashMap<>(2);
        res.put(NON_NULL, l.stream().filter(i -> k.apply(i) != null).collect(Collectors.toList()));
        res.put(NULL, l.stream().filter(i -> k.apply(i) == null).collect(Collectors.toList()));
        return res;
    }

    /**
     * @Desc: 将集合分组
     * @Author : HYbrid
     * @Date : 2020/12/30 0030 15:52
     * @Params : [list, k]
     * @Return : java.util.Map<K,java.util.List<T>>
     */
    public static <T, K> Map<K, List<T>> groupBy(Collection<T> list, Function<T, K> k) {
        Collection<T> l = trimNull(list);
        return CollectionUtils.isNotEmpty(l) ? l.stream().collect(Collectors.groupingBy(k)) : Collections.emptyMap();
    }

    public static <T, K, V> Map<K, List<V>> groupBy(Function<T, K> k, Function<T, V> v, Collection<T> list) {
        Collection<T> l = trimNull(list);
        if (CollectionUtils.isNotEmpty(l)) {
            Map<K, List<T>> m = groupBy(l, k);
            Map<K, List<V>> res = new HashMap<>(m.size());
            m.forEach((key, c) -> res.put(key, c.stream().map(v).collect(Collectors.toList())));
            return res;
        }
        return Collections.emptyMap();
    }

    /**
     * @Desc: 获取list中对象指定属性的集合
     * @Author : HYbrid
     * @Date : 2021/1/5 17:16
     * @Params : [k, list]
     * @Return : java.util.List<K>
     */
    public static <T, K> List<K> list2List(Function<? super T, ? extends K> k, Collection<T> list) {
        Collection<T> l = trimNull(list);
        return CollectionUtils.isNotEmpty(l) ? l.stream().map(k).collect(Collectors.toList()) : Collections.emptyList();
    }

    /**
     * @Desc:   获取list中对象指定属性的集合并trim
     * @Params: [k, list]
     * @Return: java.util.List<K>
     * @Author: HYbrid
     * @Date:   2022/2/9
     */
    public static <T, K> List<K> list2ListTrim(Function<? super T, ? extends K> k, Collection<T> list) {
        Collection<T> l = trimNull(list);
        return CollectionUtils.isNotEmpty(l)
                ? l.stream().map(k).filter(i -> i != null && (!(i instanceof String) || StringUtils.isNotBlank((String) i))).collect(Collectors.toList())
                : Collections.emptyList();
    }

    /**
     * @Desc: 获取list中对象指定属性的去重集合
     * @Author : HYbrid
     * @Date : 2021/1/5 17:16
     * @Params : [k, list]
     * @Return : java.util.List<K>
     */
    public static <T, K> Set<K> list2Set(Function<? super T, ? extends K> k, Collection<T> list) {
        Collection<T> l = trimNull(list);
        if (CollectionUtils.isNotEmpty(l)) {
            Set<K> r = l.stream().map(k).collect(Collectors.toSet());
            return trim2Set(r);
        }
        return Collections.emptySet();
    }

    /**
     * @Desc: 把collection集合转成map
     * @Author: Yu.Hua
     * @Date: 2020/10/26 0026 16:21
     * @Params: [collection]
     * @Return: java.util.Map<T, F>
     */
    public static <T, K, U> Map<K, U> list2Map(Function<T, K> k, Function<T, U> v, Collection<T> list) {
        Collection<T> l = trimNull(list);
        if (CollectionUtils.isNotEmpty(l)) {
            HashMap<K, U> m = new HashMap<>(l.size());
            l.forEach(i -> m.put(k.apply(i), v.apply(i)));
            return m;
        }
        return Collections.emptyMap();
    }

    public static <T> List<T> trimNull(Collection<T> l) {
        return CollectionUtils.isNotEmpty(l)
                ? l.stream().filter(i -> i != null && (!(i instanceof String) || StringUtils.isNotBlank((String) i))).collect(Collectors.toList())
                : Collections.emptyList();
    }

    public static <T> Set<T> trim2Set(Collection<T> l) {
        if (CollectionUtils.isNotEmpty(l)) {
            if (l instanceof Set && !(l.iterator().next() instanceof String)) {
                l.remove(null);
                return (Set<T>) l;
            }
            return l.stream().filter(i -> i != null && (!(i instanceof String) || StringUtils.isNotBlank((String) i))).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public static <T> Set<T> trim2Set(T... l) {
        if (ArrayUtils.isNotEmpty(l)) {
            return Arrays.stream(l).filter(i -> i != null && (!(i instanceof String) || StringUtils.isNotBlank((String) i))).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    /**
     * @Desc: 支持list的foreach同时返回对象和标记位<br>list.forEach(consumerIndex((item, i) -> log.info("{} = {}", i, item)));
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:16
     * @Params     : [consumer]
     * @Return     : java.util.function.Consumer<T>
     */
    public static <T> Consumer<T> consumerIndex(BiConsumer<T, Integer> consumer) {
        int[] ind = {0};
        return t -> consumer.accept(t, ind[0]++);
    }

    /**
     * @Desc: 构建默认值的list集合
     * @Author     : HYbrid
     * @Date       : 2021/8/10 15:18
     * @Params     : [o, size]
     * @Return     : java.util.List<T>
     */
    public static <T> List<T> build(Object o, int size) {
        T[] array = (T[]) Array.newInstance(o.getClass(), size);
        Arrays.fill(array, o);
        return Arrays.asList(array);
    }

    public static <T> Collection<T> fill(Collection<T> coll, Object o, int size) {
        if (coll == null) {
            return build(o, size);
        } else {
            for (int i = 0; i < size; i++) {
                coll.add((T) o);
            }
        }
        return coll;
    }

    /**
     * @Desc: 填充List
     * @Author: Yu.Hua
     * @Date: 2019/12/30 0030 16:58
     * @Params: [o, initSize, capacitySize]
     * @Return: java.util.List<T>
     */
    public static <T> List<T> build(T o, int initSize, int capacitySize) {
        List<T> l = new ArrayList<>(capacitySize);
        l.add(o);
        T oo;
        try {
            for (int i = 1; i < initSize; i++) {
                oo = (T) o.getClass().getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(o, oo, SpringContextUtils.getNullPropertyNames(o));
                l.add(oo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

}
