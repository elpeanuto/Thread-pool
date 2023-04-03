package edu.elpeanuto.threads.threadPool;

import edu.elpeanuto.threads.util.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolImpl implements ThreadPool<Task> {

    private final BlockingQueue<Task> queueOne;
    private final BlockingQueue<Task> queueTwo;
    private final List<Worker> firstQueuePool;
    private final List<Worker> secondQueuePool;

    private final int queueSize;

    private boolean isRunning = false;
    private boolean isPaused = false;
    private boolean softShutDown = false;

    private int abandonedTasksCounter = 0;
    AtomicInteger executedTasksCounter = new AtomicInteger(0);
    private int addCallCounter = 0;

    public ThreadPoolImpl(int queueSize, int poolSize) {
        this.queueSize = queueSize;
        this.queueOne = new LinkedBlockingQueue<>(queueSize);
        this.queueTwo = new LinkedBlockingQueue<>(queueSize);
        firstQueuePool = new ArrayList<>();
        secondQueuePool = new ArrayList<>();

        for (int i = 0; i < poolSize; i++) {
            firstQueuePool.add(new Worker(queueOne, "Queue#1 worker#" + (i + 1)));
        }

        for (int i = 0; i < poolSize; i++) {
            secondQueuePool.add(new Worker(queueTwo, "Queue#2 worker#" + (i + 1)));
        }
    }

    public synchronized void start() {
        if (isRunning) {
            System.err.println("ThreadPool is already running");
            return;
        }

        for (Thread thread : firstQueuePool) {
            thread.start();
        }

        for (Thread thread : secondQueuePool) {
            thread.start();
        }

        isRunning = true;
    }

    public synchronized void add(Task task) throws InterruptedException {
        addCallCounter++;
        if (!isRunning) {
            System.err.println("ThreadPool isn't working");
            abandonedTasksCounter++;
            return;
        }

        if(isPaused) {
            System.out.println("ThreadPool is paused");
            abandonedTasksCounter++;
            return;
        }

        if (queueOne.size() <= queueTwo.size() && queueOne.size() < queueSize) {
            queueOne.put(task);
        } else if (queueTwo.size() < queueSize) {
            queueTwo.put(task);
        } else {
            System.out.println("Task is rejected because both queues are full");
            abandonedTasksCounter++;
        }

        System.out.println("\nFirst queue size:" + queueOne.size() +
                "\nSecond queue size:" + queueTwo.size() + "\n");
    }

    public synchronized void pause() {
        if (!isRunning || isPaused) {
            System.err.println("ThreadPool isn't working");
            return;
        }

        isPaused = true;
    }

    public synchronized void resume() {
        if (!isRunning || !isPaused) {
            System.err.println("ThreadPool isn't working");
            return;
        }

        for (Worker worker : firstQueuePool) {
            worker.resumeWorker();
        }

        for (Worker worker : secondQueuePool) {
            worker.resumeWorker();
        }

        isPaused = false;
    }

    public synchronized void shutdown() throws InterruptedException {
        for (Thread thread : firstQueuePool) {
            thread.interrupt();
        }

        for (Thread thread : secondQueuePool) {
            thread.interrupt();
        }

        for (Thread thread : firstQueuePool) {
            thread.join();
        }

        for (Thread thread : secondQueuePool) {
            thread.join();
        }

        isRunning = false;
    }

    public synchronized void softShutdown() throws InterruptedException {
        softShutDown = true;

        for (Thread thread : firstQueuePool) {
            thread.join();
        }

        for (Thread thread : secondQueuePool) {
            thread.join();
        }

        isRunning = false;
    }

    public void printStatistic() {
        System.out.println("addCallCounter: " +  addCallCounter);
        System.out.println("abandonedTasksCounter: " +  abandonedTasksCounter);
        System.out.println("executedTasksCounter: " +  executedTasksCounter.get());
        System.out.println("firstQueueSize: " +  queueOne.size());
        System.out.println("secondQueueSize: " +  queueTwo.size());
    }

    private class Worker extends Thread {

        private final BlockingQueue<Task> queue;

        public Worker(BlockingQueue<Task> queue, String name) {
            super(name);
            this.queue = queue;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted() && !softShutDown) {

                try {
                    if (isPaused) {
                        synchronized (this) {
                            wait();
                        }
                    }

                    Task task = queue.take();
                    System.out.println(Thread.currentThread().getName() + " got task from: " + Integer.toHexString(System.identityHashCode(queue)));
                    task.task();
                    executedTasksCounter.incrementAndGet();
                    System.out.println(Thread.currentThread().getName() + " task is done");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public synchronized void resumeWorker(){
            notify();
        }
    }
}
