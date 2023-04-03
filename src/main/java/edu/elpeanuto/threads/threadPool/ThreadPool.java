package edu.elpeanuto.threads.threadPool;

public interface ThreadPool<T> {
    void start();

    void add(T t) throws InterruptedException;

    void pause();

    void resume();

    void shutdown() throws InterruptedException;

    void softShutdown() throws InterruptedException;
}
