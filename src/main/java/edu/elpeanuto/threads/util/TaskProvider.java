package edu.elpeanuto.threads.util;

import edu.elpeanuto.threads.threadPool.ThreadPool;

public class TaskProvider extends Thread {

    private final ThreadPool<Task> threadPool;
    private final Task task;
    private final int millis;

    public TaskProvider(Task task, ThreadPool<Task> threadPool, int millis) {
        this.task = task;
        this.threadPool = threadPool;
        this.millis = millis;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                threadPool.add(task);
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
