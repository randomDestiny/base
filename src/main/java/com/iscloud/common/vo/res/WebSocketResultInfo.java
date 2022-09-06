package com.iscloud.common.vo.res;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @Desc: 接口返回信息标准化
 * @Author ：HYbrid
 * @Date ：2021/5/12 16:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class WebSocketResultInfo<T> extends ResultInfo<T> {

    /**
     * @Desc:   业务模块，用于前后端识别推送消息
     */
    protected String module;

    public WebSocketResultInfo(String module, String message) {
        this.message = message;
        this.module = module;
    }

    public WebSocketResultInfo(T data, String module) {
        this.data = data;
        this.module = module;
    }

    public WebSocketResultInfo(String message, int errorCode, String module) {
        this.errorCode = errorCode;
        this.message = message;
        this.module = module;
    }

    public WebSocketResultInfo(String message, T data, String module) {
        this.data = data;
        this.message = message;
        this.module = module;
    }

    public WebSocketResultInfo(String message, T data, int errorCode, String module) {
        this.data = data;
        this.message = message;
        this.errorCode = errorCode;
        this.module = module;
    }

    public WebSocketResultInfo(String message, T data, int errorCode, int status, String module) {
        this.data = data;
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.module = module;
    }
}
