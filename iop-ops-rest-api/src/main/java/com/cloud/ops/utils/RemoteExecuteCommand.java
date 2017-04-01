package com.cloud.ops.utils;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 远程执行linux的shell script
 *
 * @author Ickes
 * @since V0.1
 */
public class RemoteExecuteCommand {
    //字符编码默认是utf-8
    private static String DEFAULTCHART = "UTF-8";
    private Connection conn;
    private String ip;
    private String userName;
    private String userPwd;

    public RemoteExecuteCommand(String ip, String userName, String userPwd) {
        this.ip = ip;
        this.userName = userName;
        this.userPwd = userPwd;
    }

    public Boolean login() {
        boolean flg = false;
        try {
            conn = new Connection(ip);
            conn.connect();//连接
            flg = conn.authenticateWithPassword(userName, userPwd);//认证
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flg;
    }

    public RemoteExecuteResult execute(String cmd) throws IOException {
        RemoteExecuteResult result = new RemoteExecuteResult();
        if (login()) {
            System.out.println(ip + " login success.then exe " + cmd);
            Session session = conn.openSession();//打开一个会话
            session.execCommand(cmd);//执行命令
            result.setMessage(getSessionOut(session));
            result.setExitCode(session.getExitStatus());
            if (session.getExitStatus()!=null) {
                System.out.println("session exit status null-------");
                conn.close();
                session.close();
            }
        }
        return result;
    }


    private String getSessionOut(Session sess) throws IOException {
        InputStream stdout = sess.getStdout();
        InputStream stderr = sess.getStderr();
        byte[] buffer = new byte[8192];

        while (true) {
            if ((stdout.available() == 0) && (stderr.available() == 0)) {
                    /* Even though currently there is no data available, it may be that new data arrives
                     * and the session's underlying channel is closed before we call waitForCondition().
					 * This means that EOF and STDOUT_DATA (or STDERR_DATA, or both) may
					 * be set together.
					 */

                int conditions = sess.waitForCondition(ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA
                        | ChannelCondition.EOF, 1000*10);

					/* Wait no longer than 2 seconds (= 2000 milliseconds) */

                if ((conditions & ChannelCondition.TIMEOUT) != 0) {
						/* A timeout occured. */
                    return "等待脚本执行结果超时";
                }

					/* Here we do not need to check separately for CLOSED, since CLOSED implies EOF */

                if ((conditions & ChannelCondition.EOF) != 0) {
						/* The remote side won't send us further data... */

                    if ((conditions & (ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA)) == 0) {
							/* ... and we have consumed all data in the local arrival window. */
                        break;
                    }
                }

					/* OK, either STDOUT_DATA or STDERR_DATA (or both) is set. */

                // You can be paranoid and check that the library is not going nuts:
                // if ((conditions & (ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA)) == 0)
                //	throw new IllegalStateException("Unexpected condition result (" + conditions + ")");
            }

				/* If you below replace "while" with "if", then the way the output appears on the local
				 * stdout and stder streams is more "balanced". Addtionally reducing the buffer size
				 * will also improve the interleaving, but performance will slightly suffer.
				 * OKOK, that all matters only if you get HUGE amounts of stdout and stderr data =)
				 */

            while (stdout.available() > 0) {
                int len = stdout.read(buffer);
                if (len > 0) // this check is somewhat paranoid
                    System.out.write(buffer, 0, len);
            }

            while (stderr.available() > 0) {
                int len = stderr.read(buffer);
                if (len > 0) // this check is somewhat paranoid
                    System.err.write(buffer, 0, len);
            }
        }
        return IOUtils.toString(buffer, String.valueOf(StandardCharsets.UTF_8));
    }

    public static void setCharset(String charset) {
        DEFAULTCHART = charset;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }
}
