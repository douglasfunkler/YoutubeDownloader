package com.example.app.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class ProcessExecutor {

    public static void execute(String[] command, Consumer<String> outputHandler) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                outputHandler.accept(line);
            }

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
