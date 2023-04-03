package edu.elpeanuto.threads;

import edu.elpeanuto.threads.ThreadPool.ThreadPool;
import edu.elpeanuto.threads.ThreadPool.ThreadPoolImpl;
import edu.elpeanuto.threads.util.Task;
import edu.elpeanuto.threads.util.TaskProvider;

import java.util.Random;

public class App {

    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        ThreadPool<Task> threadPool = new ThreadPoolImpl(10, 2);
        TaskProvider taskProvider = new TaskProvider(() -> Thread.sleep((random.nextInt(7) + 4) * 1000), threadPool);

        threadPool.start();
        System.out.println("\nThreadPool started\n");
        taskProvider.start();

        Thread.sleep(10000);

        System.out.println("\nThreadPool paused\n");
        threadPool.pause();
        Thread.sleep(10000);
        threadPool.resume();
        System.out.println("\nThreadPool resumed\n");

        Thread.sleep(10000);

        System.out.println("\nStart soft shutdown\n");
        threadPool.softShutdown();

        taskProvider.interrupt();
        taskProvider.join();
    }
}
