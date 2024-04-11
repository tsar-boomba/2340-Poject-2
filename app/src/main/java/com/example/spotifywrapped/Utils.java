package com.example.spotifywrapped;

public class Utils {
    /**
     * Runs toRun in another thread, making sure to not block the current thread.
     * @param toRun the function to run in another thread
     */
    public static void unblock(Runnable toRun) {
        new Thread(toRun).start();
    }
}
