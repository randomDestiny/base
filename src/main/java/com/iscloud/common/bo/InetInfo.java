package com.iscloud.common.bo;

import com.iscloud.common.helper.StringHelper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * @Desc: 本地网络信息
 * @Author: HYbrid
 * @Date: 2022/3/29 14:39
 */
@Getter
@EqualsAndHashCode
public final class InetInfo {

    private final String mac;
    private final String ip;
    private final String host;

    @SneakyThrows
    public InetInfo() {
        this.host = System.getenv().get("COMPUTERNAME");
        System.out.println("local host -> " + this.host);
        InetAddress address = InetAddress.getLocalHost();
        this.ip = address.getHostAddress();
        System.out.println("local ip -> " + this.ip);
        this.mac = StringHelper.bytes2Hex(NetworkInterface.getByInetAddress(address).getHardwareAddress());
        System.out.println("local mac -> " + this.mac);
    }

}
