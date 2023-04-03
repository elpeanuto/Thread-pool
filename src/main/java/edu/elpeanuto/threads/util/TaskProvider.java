package edu.elpeanuto.threads.util;

import edu.elpeanuto.threads.threadPool.ThreadPool;

public class TaskProvider extends Thread {

    private final ThreadPool<Task> threadPool;
    private final Task task;

    public TaskProvider(Task task, ThreadPool<Task> threadPool) {
        this.task = task;
        this.threadPool = threadPool;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                threadPool.add(task);

                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
