package com.cloud.ops.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import com.cloud.ops.entity.Location.LocationLocal;

import java.io.IOException;

/**
 * Created by Administrator on 2017/2/8.
 */
public class SCPUtils {
    public static void uploadFilesToServer(LocationLocal host, String localFile, String remoteFileName, String remoteTargetDirectory, String mode) throws IOException {
        Connection con = new Connection(host.getIp());
        con.connect();
        if (con.authenticateWithPassword(host.getUsername(), host.getPassword())) {
            System.out.println(host.getIp() +" scp auth success. Then transfer file " + remoteFileName);
            SCPClient scpClient = con.createSCPClient();
            scpClient.put(localFile, remoteFileName, remoteTargetDirectory, mode);
        }
        con.close();
    }

    public static void uploadFilesToServer(LocationLocal host, String localFile, String remoteTargetDirectory, String mode) throws IOException {
        Connection con = new Connection(host.getIp());
        con.connect();
        if (con.authenticateWithPassword(host.getUsername(), host.getPassword())) {
            System.out.println(host.getIp() + " scp auth success. Then transfer shell script.");
            SCPClient scpClient = con.createSCPClient();
            scpClient.put(localFile, remoteTargetDirectory, mode);
        }
        con.close();
    }
}
