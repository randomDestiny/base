package com.iscloud.common.utils;

import com.iscloud.common.bo.InetInfo;

/**
 * @Desc: 获取本地网络相关信息工具类
 * @Author: HYbrid
 * @Date: 2022/3/29 15:25
 */
@SuppressWarnings("unused")
public class InetInfoUtils {

    private static final ThreadLocal<InetInfo> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * @Desc:   获取实例
     * @Params: []
     * @Return: com.indexsmart.common.bo.InetInfo
     * @Author: HYbrid
     * @Date:   2022/3/29
     */
    public static InetInfo getInst() {
        InetInfo i = THREAD_LOCAL.get();
        if (i == null) {
            i = new InetInfo();
            THREAD_LOCAL.set(i);
        }
        return i;
    }

    /**
     * @Desc:   获取本机mac地址
     * @Params: []
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/5/31
     */
    public static String getMac() {
        return getInst().getMac();
    }

    /**
     * @Desc:   获取本地ip地址
     * @Params: []
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/5/31
     */
    public static String getIp() {
        return getInst().getIp();
    }

    /**
     * @Desc:   获取本地主机名称
     * @Params: []
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/5/31
     */
    public static String getHostName() {
        return getInst().getHost();
    }

    /**
     * @Desc:   清空
     * @Params: []
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/5/31
     */
    public static void clear() {
        THREAD_LOCAL.remove();
    }
}
