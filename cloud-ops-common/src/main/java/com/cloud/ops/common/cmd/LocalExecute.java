package com.cloud.ops.common.cmd;

import com.cloud.ops.common.exception.OpsException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by Administrator on 2017/2/4.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalExecute {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalExecute.class);

    public static void execute(String cmd, File dir) {
        String osName = System.getProperties().getProperty("os.name");
        String command = "";
        String windowsPre = "c:\\windows\\system32\\cmd.exe /c ";
        if (osName.startsWith("Windows")) {
            command += windowsPre + cmd;
            executeAndPrintResult(command, dir);
        } else if (osName.equals("Linux")) {
            command += cmd;
            if (cmd.contains("gradlew")) {
                executeAndPrintResult("dos2unix gradlew", dir);
                executeAndPrintResult("sh " + command, dir);
            } else {
                executeAndPrintResult("sh " + command, dir);
            }

        }
    }

    private static void executeAndPrintResult(String command, File dir) {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(command, null, dir);
            printMessage(process.getInputStream());
            printMessage(process.getErrorStream());
            int result = process.waitFor();
            LOGGER.info("%s exec result: %d", command, result);
            if (result != 0) throw new OpsException("local execute command result is failure");
        } catch (IOException | InterruptedException e) {
            LOGGER.error("local execute command error", e);
            throw new OpsException("local execute command error");
        }

    }

    private static void printMessage(final InputStream input) {
        new Thread(() -> {
            Reader reader = new InputStreamReader(input);
            BufferedReader bf = new BufferedReader(reader);
            String line = null;
            try {
                while ((line = bf.readLine()) != null) {
                    LOGGER.info(line);
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }).start();
    }
}
