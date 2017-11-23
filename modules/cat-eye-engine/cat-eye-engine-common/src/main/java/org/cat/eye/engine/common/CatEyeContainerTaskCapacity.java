package org.cat.eye.engine.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kotov on 29.10.2017.
 */
public class CatEyeContainerTaskCapacity {

    private AtomicInteger totalTaskLimit;

    private AtomicInteger obtained;

    private final Object lock = new Object();

    public CatEyeContainerTaskCapacity(int maxCapacity) {
        obtained = new AtomicInteger(0);
        totalTaskLimit = new AtomicInteger(maxCapacity);
    }

    public int getRemaining() {
        return totalTaskLimit.get() - obtained.get();
    }

    public boolean tryConsume() {

        if (totalTaskLimit.get() <= obtained.get()) {
            obtained.getAndIncrement();
            return true;
        }

        return false;
    }

    public void consume() {
        while (!tryConsume()) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    //
                }
            }
        }
    }

    public void release() {

        int i;

        while ((i = obtained.get()) > 0) {
            if (obtained.compareAndSet(i, i-1)) break;
        }

        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void await() {
        while (totalTaskLimit.get() <= obtained.get()) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    //
                }
            }
        }
    }

    public void setTotalTaskLimit(int totalTaskLimit) {
        this.totalTaskLimit.set(totalTaskLimit);
    }
}
