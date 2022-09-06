package com.iscloud.common.utils;

import com.iscloud.common.cst.BaseCst;
import com.iscloud.common.bo.LinuxSshBO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @Desc: Linux工具类
 * @Author: HYbrid
 * @Date: 2022/5/30 10:56
 */
@Slf4j
@SuppressWarnings("unused")
public class LinuxUtils {

    /**
     * @Desc:   获取文件的md5校验码
     * @Params: [file]
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/5/30
     */
    public static String md5sum(File file) {
        if (file != null && file.isFile()) {
            String cmd = BaseCst.LinuxCst.md5sum(file.getAbsolutePath());
            return getRes4RunCmd(cmd);
        }
        return null;
    }

    /**
     * @Desc:   获取文件的md5校验码
     * @Params: [absolutePath]
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/5/30
     */
    public static String md5sum(String absolutePath) {
        if (StringUtils.isNotBlank(absolutePath)) {
            File f = new File(absolutePath);
            if (f.exists() && f.isFile()) {
                String cmd = BaseCst.LinuxCst.md5sum(absolutePath);
                return getRes4RunCmd(cmd);
            }
        }
        return null;
    }

    /**
     * @Desc:   调用远程ssh命令
     * @Params: [cmd, ip, username, password]
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/5/30
     */
    public static String runCmd4Ssh(String cmd, String ip, String username, String password) {
        return new LinuxSshBO(ip, username, password).runCmd(cmd);
    }

    /**
     * @Desc:   运行命令
     * @Params: [cmd]
     * @Return: java.lang.Process
     * @Author: HYbrid
     * @Date:   2022/5/30
     */
    @SneakyThrows
    public static Process runCmd(String cmd, boolean closeAble) {
        Runtime r = Runtime.getRuntime();
        Process p = r.exec(cmd);
        p.waitFor();
        if (closeAble) {
            p.destroy();
            return null;
        }
        return p;
    }

    /**
     * @Desc:   运行命令获取结果
     * @Params: [cmd]
     * @Return: java.lang.Process
     * @Author: HYbrid
     * @Date:   2022/5/30
     */
    @SneakyThrows
    public static String getRes4RunCmd(String cmd) {
        Process p = runCmd(cmd, false);
        if (p != null) {
            String res = IOUtils.toString(p.getInputStream(), StandardCharsets.UTF_8);
            log.info("pid[{}]调用 {} -> 返回\n{}", p.pid(), cmd, res);
            p.destroy();
            return res;
        }
        return null;
    }
}
