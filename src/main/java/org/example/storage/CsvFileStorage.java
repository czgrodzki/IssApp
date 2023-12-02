package org.example.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvFileStorage {


    public static void writeToCsv(String file, boolean shouldAppend, String... arguments) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, shouldAppend))) {
            StringBuilder line = new StringBuilder();
            for (String argument : arguments) {
                line.append(argument).append(",");
            }
            line.append("\n");
            line.deleteCharAt(line.length() - 2);
            writer.write(line.toString());
        }

    }

    public static List<String> readSpeedFromCsvFile(String file) throws IOException {
        List<String> speedValues = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] split = line.split(",");
                speedValues.add(split[1]);
            }
        }
        return speedValues;
    }

}
