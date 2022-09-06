package com.iscloud.common.utils;

import com.iscloud.common.cst.BaseCst.ResultCst;
import com.iscloud.common.cst.BaseCst.SymbolCst;
import com.iscloud.common.vo.res.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * @Desc: 本地线程的返回信息
 * @Author     : HYbrid
 * @Date       : 2021/5/13 19:39
 */
@Slf4j
@SuppressWarnings({"unused", "unchecked"})
public class ResultUtils {

	private static final ThreadLocal<ResultInfo<?>> LOCAL = new ThreadLocal<>();
	public static final ResultInfo<?> UNSUPPORTED = new ResultInfo<>("不支持的当前操作！", false, ResultCst.ERROR_UNSUPPORTED);

	public static ResultInfo<?> getResultInfo() {
		return LOCAL.get();
	}

	public static void clear() {
		LOCAL.remove();
	}

	public static boolean check(Object v, String m, int e) {
		if (v == null) {
			ResultUtils.failure(e, m, null);
			return false;
		} else if (v instanceof CharSequence && StringUtils.isBlank((CharSequence) v)) {
			ResultUtils.failure(e, m, null);
			return false;
		} else if (v instanceof Boolean && !(Boolean) v) {
			ResultUtils.failure(e, m, v);
			return false;
		} else if (v instanceof Map || v instanceof Object[] || v instanceof Iterable || v instanceof Iterator || v instanceof Enumeration) {
			if (CollectionUtils.sizeIsEmpty(v)) {
				ResultUtils.failure(e, m, null);
				return false;
			}
		}
		return true;
	}

	public static <T> void addResultInfo(ResultInfo<T> res) {
		ResultInfo<?> rs = LOCAL.get();
		if (rs != null && StringUtils.isNotBlank(rs.getMessage())) {
			res.setMessage(rs.getMessage() + SymbolCst.NEW_LINE + res.getMessage());
		}
		LOCAL.set(res);
	}

	public static boolean isSuccess() {
		ResultInfo<?> res = LOCAL.get();
		return res == null || (ResultCst.ERROR_NONE == res.getErrorCode() && ResultCst.STATUS_SUCCESS == res.getStatus());
	}

	public static ResultInfo<?> getAndClear() {
		ResultInfo<?> status = LOCAL.get();
		if (status != null) {
			ResultInfo<?> model = new ResultInfo<>();
			BeanUtils.copyProperties(status, model);
			LOCAL.remove();
			return model;
		}
		return null;
	}

	public static <T> ResultInfo<?> buildResult(T t) {
		ResultInfo<?> model = t instanceof ResultInfo ? (ResultInfo<?>) t : new ResultInfo<>(t), info = getAndClear();
		if (info != null) {
			// 优先使用返回的object
			if (model.getData() != null) {
				info.setData(null);
			}
			BeanUtils.copyProperties(info, model, SpringContextUtils.getNullPropertyNames(info));
		}
		return model;
	}

	public static <T> ResultInfo<T> success(T data) {
		return new ResultInfo<>(data);
	}

	public static <T> ResultInfo<T> success(String message) {
		ResultInfo<T> res = new ResultInfo<>();
		res.setMessage(message);
		return res;
	}

	public static <T> ResultInfo<T> success(String message, T data) {
		return new ResultInfo<>(message, data);
	}

	public static <T> ResultInfo<T> failureErrCode(int errorCode, String message) {
		return failureErrCode(errorCode, message, false);
	}

	public static <T> ResultInfo<T> failureErrCode(int errorCode, String message, boolean append) {
		ResultInfo<?> rs = LOCAL.get();
		if (rs != null) {
			rs.setErrorCode(errorCode);
			if (append && StringUtils.isNotBlank(rs.getMessage())) {
				rs.setMessage(rs.getMessage() + SymbolCst.NEW_LINE + message);
			} else {
				rs.setMessage(message);
			}
		} else {
			rs = new ResultInfo<>(message, errorCode);
		}
		LOCAL.set(rs);
		return (ResultInfo<T>) getResultInfo();
	}

	public static <T> ResultInfo<T> failure(int errorCode, String message) {
		addResultInfo(new ResultInfo<>(message, errorCode));
		return (ResultInfo<T>) getResultInfo();
	}

	@SuppressWarnings("UnusedReturnValue")
	public static <T> ResultInfo<T> failure(int errorCode, String message, T data) {
		addResultInfo(new ResultInfo<>(message, data, errorCode));
		log.info("错误码[{}]->提示[{}]->数据\n{}", errorCode, message, GsonUtils.toJson(data));
		return (ResultInfo<T>) getResultInfo();
	}

	public static <T> ResultInfo<T> failure(int errorCode, String message, T data, int status) {
		addResultInfo(new ResultInfo<>(message, data, errorCode, status));
		return (ResultInfo<T>) getResultInfo();
	}

	public static boolean unsupported() {
		addResultInfo(UNSUPPORTED);
		return false;
	}

}
