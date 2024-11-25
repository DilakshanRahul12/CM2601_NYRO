package org.example.Nyro;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HFTC {
    public static void main(String[] args) {
        try {
            String text = "Artificial intelligence is transforming industries.";
            ProcessBuilder processBuilder = new ProcessBuilder("python3", "src/main/resources/TextClassify.py", text);

            // Start the process
            Process process = processBuilder.start();

            // Capture the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Classification Results:");
                System.out.println(output.toString());
            } else {
                System.err.println("Error occurred while running the Python script.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
