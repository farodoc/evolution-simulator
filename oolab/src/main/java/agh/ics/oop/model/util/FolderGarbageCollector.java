package agh.ics.oop.model.util;

import java.io.File;
import java.nio.file.NotDirectoryException;

public class FolderGarbageCollector {
    private final static String path = "src/main/resources/stats";

    public static void clearStatsDirectory() throws NotDirectoryException{
        File directory = new File(path);

        if (directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }

        }
        else throw new NotDirectoryException(path + " is not a directory!");
    }
}
