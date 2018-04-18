package com.cloud.ops.common.cmd;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoteExecute {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteExecute.class);

    public static Connection getConnection(String ip, String userName, String userPwd) throws IOException {
        Connection conn;
        conn = new Connection(ip);
        conn.connect();
        conn.authenticateWithPassword(userName, userPwd);
        return conn;
    }

    public static RemoteExecuteResult execute(Connection conn, String cmd) throws IOException {
        RemoteExecuteResult result = new RemoteExecuteResult();
        Session session = conn.openSession();
        session.execCommand(cmd);
        result.setMessage(getSessionOut(session));
        result.setExitCode(session.getExitStatus());
        session.close();
        return result;
    }


    public static void uploadFileToServer(Connection conn, String localFile, String remoteFileName, String remoteTargetDirectory) throws IOException {
        SCPClient scpClient = conn.createSCPClient();
        scpClient.put(localFile, remoteFileName, remoteTargetDirectory, "0644");
    }


    private static String getSessionOut(Session sess) throws IOException {
        InputStream stdout = sess.getStdout();
        InputStream stderr = sess.getStderr();
        byte[] buffer = new byte[8192];
        StringBuilder result = new StringBuilder();
        while (true) {
            if ((stdout.available() == 0) && (stderr.available() == 0)) {
                int conditions = sess.waitForCondition(ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA
                        | ChannelCondition.EOF, 120000L);
                if ((conditions & ChannelCondition.TIMEOUT) != 0) {
                    return "等待脚本执行结果超时";
                }
                if ((conditions & ChannelCondition.EOF) != 0
                        && ((conditions & (ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA)) == 0)) {
                    break;
                }
            }
            while (stdout.available() > 0) {
                int len = stdout.read(buffer);
                if (len > 0) {
                    result.append(new String(buffer, 0, len));
                }
            }

            while (stderr.available() > 0) {
                int len = stderr.read(buffer);
                if (len > 0) {
                    result.append(new String(buffer, 0, len));
                }
            }
        }
        return String.valueOf(result);
    }
}
