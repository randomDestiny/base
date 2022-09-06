package com.iscloud.common.vo.res;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iscloud.common.cst.BaseCst.ResultCst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Desc: 接口返回信息标准化
 * @Author ：HYbrid
 * @Date ：2021/5/12 16:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class ResultInfo<T> implements Serializable {

    protected int status = ResultCst.STATUS_SUCCESS;
    protected String message;
    protected T data;
    protected int errorCode = ResultCst.ERROR_NONE;

    @JsonIgnore
    @JSONField(deserialize = false,serialize = false)
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("status", this.status);
        map.put("message", this.message);
        map.put("data", this.data);
        map.put("errorCode", this.errorCode);
        return map;
    }

    @JsonIgnore
    @JSONField(deserialize = false,serialize = false)
    public T getDataDefault(T t) {
        return this.data == null ? t : this.data;
    }

    public ResultInfo(T data) {
        this.data = data;
    }

    public ResultInfo(String message, int errorCode) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ResultInfo(String message, T data) {
        this.data = data;
        this.message = message;
    }

    public ResultInfo(String message, T data, int errorCode) {
        this.data = data;
        this.message = message;
        this.errorCode = errorCode;
    }

    public ResultInfo(String message, T data, int errorCode, int status) {
        this.data = data;
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
    }

}
