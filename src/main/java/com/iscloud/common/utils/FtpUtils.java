package com.iscloud.common.utils;

import com.iscloud.common.cst.BaseCst.SymbolCst;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Desc: ftp工具类
 * @Author ：HYbrid
 * @Date ：2021/1/5 13:48
 */
@SuppressWarnings("unused")
@Component(FtpUtils.BEAN)
public final class FtpUtils {

    public static final String BEAN = "FtpUtils";

    public static void mock(String filePath, String logInfo) throws IOException {
        FTPClient client = getInstance("192.168.1.112", 21, "Eric", "Eric96", "\\");
        String info = "\n" + logInfo + System.currentTimeMillis();
        client.appendFile(filePath, new ByteArrayInputStream(info.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * @Desc: 远程文件追加内容
     * @Author     : HYbrid
     * @Date       : 2021/1/5 16:54
     * @Params     : [info, filePathFtp, client]
     * @Return     : void
     */
    public static void appendInfo(String info, String filePathFtp, FTPClient client) {
        String append = SymbolCst.NEW_LINE + info;
        try {
            client.appendFile(filePathFtp, new ByteArrayInputStream(append.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Desc: 创建ftp客户端连接，并指定远程目录
     * @Author     : HYbrid
     * @Date       : 2021/1/5 14:57
     * @Params     : [ip, port, username, password, path]
     * @Return     : org.apache.commons.net.ftp.FTPClient
     */
    public static FTPClient getInstance(String ip, int port, String username, String password, String path) {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(ip, port);
            ftpClient.login(username, password);
            if (path != null && path.length() > 0) {
                ftpClient.changeWorkingDirectory(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ftpClient;
    }

    /**
     * @Desc: 关闭ftp客户端
     * @Author     : HYbrid
     * @Date       : 2021/1/5 14:57
     * @Params     : [ftpClient]
     * @Return     : void
     */
    public static void close(FTPClient ftpClient) {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
