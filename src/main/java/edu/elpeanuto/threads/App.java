package edu.elpeanuto.threads;

import edu.elpeanuto.threads.threadPool.ThreadPool;
import edu.elpeanuto.threads.threadPool.ThreadPoolImpl;
import edu.elpeanuto.threads.util.Task;
import edu.elpeanuto.threads.util.TaskProvider;

import java.util.Random;

public class App {

    private static final Random random = new Random();

    private static final int QUEUE_SIZE = 10;
    private static final int POOL_SIZE = 2;
    private static final int MIN_TIME_FOR_TASK = 4;
    private static final int MAX_TIME_FOR_TASK = 10;
    private static final int PAUSE_TIME = 10_000;
    private static final int RUNNING_TIME = 30_000;
    private static final int PROVIDER_INTERVAL = 1000;

    public static void main(String[] args) throws InterruptedException {
        ThreadPool<Task> threadPool = new ThreadPoolImpl(QUEUE_SIZE, POOL_SIZE);
        TaskProvider taskProvider = new TaskProvider(
                () -> Thread.sleep((random.nextInt(MAX_TIME_FOR_TASK - MIN_TIME_FOR_TASK + 1) + MIN_TIME_FOR_TASK) * 1000),
                threadPool, PROVIDER_INTERVAL);

        threadPool.start();
        System.out.println("\nThreadPool started\n");
        taskProvider.start();

        Thread.sleep(RUNNING_TIME);

        System.out.println("\nThreadPool paused\n");
        threadPool.pause();
        Thread.sleep(PAUSE_TIME);
        System.out.println("\nThreadPool resumed\n");
        threadPool.resume();

        Thread.sleep(RUNNING_TIME);

        System.out.println("\nStart soft shutdown\n");

        taskProvider.interrupt();
        taskProvider.join();

        threadPool.softShutdown();
        threadPool.printStatistic();
    }
}
