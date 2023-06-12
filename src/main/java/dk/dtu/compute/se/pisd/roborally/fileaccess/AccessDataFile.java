package dk.dtu.compute.se.pisd.roborally.fileaccess;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AccessDataFile {
    private static Set<String> locks = ConcurrentHashMap.newKeySet();

    public static boolean requestFileAccess(String fileName) {
        // Check if the file is already being accessed
        if (locks.contains(fileName)) {
            return false;
        } else {
            // Add the file to the set of currently accessed files
            locks.add(fileName);
            return true;
        }
    }

    public static boolean releaseFileAccess(String fileName) {
        return locks.remove(fileName);
    }
}
