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
    private static final ConcurrentHashMap<String, Boolean> locks = new ConcurrentHashMap<>();

    public static boolean requestFileAccess(String fileName) {
        // Check if the file is already being accessed
        return locks.putIfAbsent(fileName, true) == null;
    }

    public static void releaseFileAccess(String fileName) {
        locks.remove(fileName);
    }
}
