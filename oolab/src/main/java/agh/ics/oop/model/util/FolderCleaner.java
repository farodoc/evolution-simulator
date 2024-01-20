package agh.ics.oop.model.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FolderCleaner {
    private final static String path = "src/main/resources/stats";

    public static void clearStatsDirectory() throws NotDirectoryException {
        File directory = new File(path);

        if (!directory.exists()) {
            try {
                createDirectoryIfNotExists(directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        } else {
            throw new NotDirectoryException(path + " is not a directory!");
        }
    }

    private static void createDirectoryIfNotExists(File directory) throws IOException {
        Path path = Paths.get(directory.getAbsolutePath());
        java.nio.file.Files.createDirectories(path);
    }
}
