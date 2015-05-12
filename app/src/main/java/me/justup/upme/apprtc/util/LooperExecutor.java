package me.justup.upme.apprtc.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGW;

/**
 * Looper based executor class.
 */
public class LooperExecutor extends Thread implements Executor {
    private static final String TAG = "LooperExecutor";

    // Object used to signal that looper thread has started and Handler instance
    // associated with looper thread has been allocated.
    private final Object looperStartedEvent = new Object();
    private Handler handler = null;
    private boolean running = false;
    private long threadId;

    @Override
    public void run() {
        Looper.prepare();
        synchronized (looperStartedEvent) {
            LOGD(TAG, "Looper thread started.");
            handler = new Handler();
            threadId = Thread.currentThread().getId();
            looperStartedEvent.notify();
        }

        Looper.loop();
    }

    public synchronized void requestStart() {
        if (running) {
            return;
        }
        running = true;
        handler = null;
        start();
        // Wait for Hander allocation.
        synchronized (looperStartedEvent) {
            while (handler == null) {
                try {
                    looperStartedEvent.wait();
                } catch (InterruptedException e) {
                    LOGE(TAG, "Can not start looper thread");
                    running = false;
                }
            }
        }
    }

    public synchronized void requestStop() {
        if (!running) {
            return;
        }
        running = false;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Looper.myLooper().quit();
                LOGD(TAG, "Looper thread finished.");
            }
        });
    }

    // Checks if current thread is a looper thread.
    public boolean checkOnLooperThread() {
        return (Thread.currentThread().getId() == threadId);
    }

    @Override
    public synchronized void execute(final Runnable runnable) {
        if (!running) {
            LOGW(TAG, "Running looper executor without calling requestStart()");
            return;
        }
        if (Thread.currentThread().getId() == threadId) {
            runnable.run();
        } else {
            handler.post(runnable);
        }
    }

}
