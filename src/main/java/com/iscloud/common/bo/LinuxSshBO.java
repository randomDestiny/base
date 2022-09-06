package com.iscloud.common.bo;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.alibaba.fastjson.annotation.JSONField;
import com.iscloud.common.cst.BaseCst;
import com.iscloud.common.cst.BaseCst.LinuxCst;
import com.iscloud.common.utils.ResultUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @Desc: LinuxSSH命令
 * @Author: HYbrid
 * @Date: 2022/5/30 13:33
 */
@Slf4j
@Data
public final class LinuxSshBO {

    private String ip;
    private String username;
    private String password;
    @JSONField(serialize = false)
    private Connection conn;
    @JSONField(serialize = false)
    private Session session;

    public LinuxSshBO(String ip, String username, String password) {
        Assert.isTrue(StringUtils.isNotBlank(ip), "ip must not null");
        Assert.isTrue(StringUtils.isNotBlank(username), "username must not null");
        Assert.isTrue(StringUtils.isNotBlank(password), "password must not null");
        this.ip = ip;
        this.username = username;
        this.password = password;
    }

    /**
     * @Desc:   返回key
     * @Params: []
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/5/30
     */
    @SuppressWarnings("unused")
    public String key() {
        return this.ip + BaseCst.SymbolCst.LINE + this.username;
    }

    /**
     * @Desc:   执行远程ssh命令
     * @Params: [cmd]
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/5/30
     */
    @SneakyThrows
    private String exeCmd(String cmd) {
        this.session.requestPTY(LinuxCst.REQ_PTY);
        this.session.startShell();
        PrintWriter out = new PrintWriter(this.session.getStdin());
        out.println(cmd);
        out.flush();
        InputStream in = new StreamGobbler(this.session.getStdout());
        String res = IOUtils.toString(in, StandardCharsets.UTF_8);
        out.println(LinuxCst.CMD_EXIT);
        in.close();
        out.close();
        int exitCode = session.waitForCondition(
                ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS,
                LinuxCst.TIMEOUT);
        log.info("执行ssh命令-> {}, 返回状态-> {}\n返回结果-> {}", cmd, exitCode, res);
        this.close();
        return res;
    }

    /**
     * @Desc:   外部调用远程ssh命令
     * @Params: [cmd]
     * @Return: java.lang.String
     * @Author: HYbrid
     * @Date:   2022/5/30
     */
    @SneakyThrows
    public String runCmd(String cmd) {
        if (StringUtils.isNotBlank(cmd)) {
            if (this.conn == null || !this.conn.isAuthenticationComplete()) {
                this.close();
                if (this.open()) {
                    return this.exeCmd(cmd);
                }
            }
            if (this.session == null) {
                this.session = this.conn.openSession();
            }
            return this.exeCmd(cmd);
        }
        return null;
    }

    /**
     * @Desc:   打开连接
     * @Params: []
     * @Return: boolean
     * @Author: HYbrid
     * @Date:   2022/5/30
     */
    @SneakyThrows
    public boolean open() {
        this.conn = new Connection(this.ip);
        conn.connect();
        if (ResultUtils.check(conn.authenticateWithPassword(this.username, this.password),
                LinuxCst.loginFail(this.ip, this.username), LinuxCst.LOGIN_FAIL_ERR)) {
            log.info("ssh登录成功");
            this.session = this.conn.openSession();
            return true;
        }
        return false;
    }

    /**
     * @Desc:   关闭连接
     * @Params: []
     * @Return: void
     * @Author: HYbrid
     * @Date:   2022/5/30
     */
    @SneakyThrows
    public void close() {
        if (this.session != null) {
            this.session.close();
        }
        if (this.conn != null) {
            this.conn.close();
        }
        this.session = null;
        this.conn = null;
    }

}
