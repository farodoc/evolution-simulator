package agh.ics.oop.model;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SettingsHandler {
    private static final String CSV_FILE = "src/main/resources/settings.csv";

    public static List<String[]> read() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE));

        List<String[]> options = new LinkedList<>();
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            options.add(line.split(","));
        }
        return options;
    }

    public static void add(String name, String[] config) throws Exception {
        FileWriter writer = new FileWriter(CSV_FILE, true);

        String line = String.join(",", config);
        line = name + "," + line;
        line += "\n";
        writer.write(line);

        writer.flush();
        writer.close();
    }

    public static String[] findConfig(String name) throws FileNotFoundException {
        List<String[]> configs = read();
        for (String[] config : configs) {
            if (config[0].equals(name)) {
                return Arrays.copyOfRange(config, 1, config.length);
            }
        }
        return null;
    }

    public static String[] getConfigNames() throws FileNotFoundException {
        List<String[]> configs = read();
        String[] list = new String[configs.size() - 1];
        for (int i = 0; i < list.length; i++) {
            list[i] = configs.get(i + 1)[0];
        }
        return list;
    }
}