package dk.dtu.compute.se.pisd.roborally.fileaccess;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

public class ThreadPoolManager {
    private final ExecutorService executorService;

    public ThreadPoolManager(int numThreads) {
        this.executorService = Executors.newFixedThreadPool(numThreads);
    }

    // Submit a task for execution and return a Future representing that task
    public <T> Future<T> submitTask(Callable<T> task) {
        return executorService.submit(task);
    }

    // Submit a task for execution
    public void submitTask(Runnable task) {
        executorService.submit(task);
    }

    // Gracefully shutdown the thread pool
    public void shutdown() {
        executorService.shutdown();
    }
}