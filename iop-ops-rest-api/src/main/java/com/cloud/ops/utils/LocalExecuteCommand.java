package com.cloud.ops.utils;

import java.io.*;

/**
 * Created by Administrator on 2017/2/4.
 */
public class LocalExecuteCommand {
    public static void execute(String cmd, File dir) {
        try {
            String osName = System.getProperties().getProperty("os.name");
            String command = "";
            String windowsPre = "c:\\windows\\system32\\cmd.exe /c ";
            Runtime r = Runtime.getRuntime();
            Process process = null;
            System.out.println(osName);
            if (osName.startsWith("Windows")) {
                command += windowsPre + cmd;
                process = r.exec(command, null, dir);
                printMessage(process.getInputStream());
                printMessage(process.getErrorStream());
                int value = process.waitFor();
                System.out.println(value);
            } else if (osName.equals("Linux")) {
                command += cmd;
                if (cmd.contains("gradlew")) {
                    process = r.exec("dos2unix gradlew", null, dir);
                    printMessage(process.getInputStream());
                    printMessage(process.getErrorStream());
                    System.out.println(process.waitFor());
                    if (process.waitFor() == 0) {
                        process = r.exec("sh " + command, null, dir);
                        printMessage(process.getInputStream());
                        printMessage(process.getErrorStream());
                        System.out.println(process.waitFor());
                    }
                } else {
                    process = r.exec("sh " + command, null, dir);
                    printMessage(process.getInputStream());
                    printMessage(process.getErrorStream());
                    System.out.println(process.waitFor());
                }
            }

            /*BufferedReader br = new BufferedReader(new InputStreamReader(p
                    .getInputStream()));
            StringBuffer sb = new StringBuffer();
            String inline;
            while (null != (inline = br.readLine())) {
                sb.append(inline).append("\n");
            }
            System.out.println(sb.toString());*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printMessage(final InputStream input) {
        new Thread(new Runnable() {

            public void run() {
                Reader reader = new InputStreamReader(input);
                BufferedReader bf = new BufferedReader(reader);
                String line = null;
                try {
                    while ((line = bf.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                }
        }).start();
    }
}
