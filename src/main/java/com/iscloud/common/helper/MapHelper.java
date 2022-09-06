package com.iscloud.common.helper;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.LinkedMap;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * @author Hybrid
 */
@SuppressWarnings("unused")
public class MapHelper {

	public static final String BEAN = "MapHelper";

	/**
	 * @Desc:   提取k和v的某个属性，不会返回k为null的数据
	 * @Params: [m, fK, fV]
	 * @Return: java.util.Map<F,T>
	 * @Author: HYbrid
	 * @Date:   2022/7/15
	 */
	public static <K, V, F, T> Map<F, T> fetch(Map<K, V> m, Function<K, F> fK, Function<V, T> fV) {
		if (MapUtils.isNotEmpty(m) && fK != null && fV != null) {
			Map<F, T> res = new HashMap<>(m.size());
			m.forEach((k, v) -> res.put(k == null ? null : fK.apply(k), v == null ? null : fV.apply(v)));
			res.remove(null);
			return res;
		}
		return Collections.emptyMap();
	}

	/**
	 * @Desc:   提取v的某个属性，不会返回k为null的数据
	 * @Params: [m, f]
	 * @Return: java.util.Map<K,T>
	 * @Author: HYbrid
	 * @Date:   2022/7/15
	 */
	public static <K, V, T> Map<K, T> fetch(Map<K, V> m, Function<V, T> f) {
		if (MapUtils.isNotEmpty(m) && f != null) {
			Map<K, T> res = new HashMap<>(m.size());
			m.forEach((k, v) -> res.put(k, v == null ? null : f.apply(v)));
			res.remove(null);
			return res;
		}
		return Collections.emptyMap();
	}

	/**
	 * @Desc: map排序
	 * @Author     : HYbrid
	 * @Date       : 2021/8/10 15:19
	 * @Params     : [map]
	 * @Return     : java.util.Map<K,V>
	 */
	public static <K, V> Map<K, V> sortedByKey(Map<K, V> map) {
		Map<K, V> newMap = new LinkedMap<>();
		List<K> list = new LinkedList<>();
		for (Entry<K, V> entry : map.entrySet()) {
			list.add(entry.getKey());
		}
		list.sort(Comparator.comparing(String::valueOf));
		for (K i : list) {
			newMap.put(i, map.get(i));
		}
		return newMap;
	}
}
