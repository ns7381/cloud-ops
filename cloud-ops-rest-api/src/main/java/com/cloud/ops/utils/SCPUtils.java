package com.cloud.ops.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

import java.io.IOException;

/**
 * Created by Administrator on 2017/2/8.
 */
public class SCPUtils {
    public static void uploadFilesToServer(String hostIp, String username, String password, String localFile,
                                           String remoteFileName, String remoteTargetDirectory, String mode) throws IOException {
        Connection con = new Connection(hostIp);
        con.connect();
        if (con.authenticateWithPassword(username, password)) {
            System.out.println(hostIp +" scp auth success. Then transfer file " + remoteFileName);
            SCPClient scpClient = con.createSCPClient();
            scpClient.put(localFile, remoteFileName, remoteTargetDirectory, mode);
        }
        con.close();
    }

    public static void uploadFilesToServer(String hostIp, String username, String password, String localFile,
                                           String remoteTargetDirectory, String mode) throws IOException {
        Connection con = new Connection(hostIp);
        con.connect();
        if (con.authenticateWithPassword(username, password)) {
            System.out.println(hostIp + " scp auth success. Then transfer shell script.");
            SCPClient scpClient = con.createSCPClient();
            scpClient.put(localFile, remoteTargetDirectory, mode);
        }
        con.close();
    }
}
