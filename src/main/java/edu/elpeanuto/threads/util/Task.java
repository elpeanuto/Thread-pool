package edu.elpeanuto.threads.util;

@FunctionalInterface
public interface Task {
    void task() throws InterruptedException;
}
