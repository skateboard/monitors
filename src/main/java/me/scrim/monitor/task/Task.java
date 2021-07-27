package me.scrim.monitor.task;

/**
 * @author Brennan
 * @since 7/27/21
 **/
public interface Task extends Runnable {

    String getId();

    String getWebsiteUrl();

    void run();

    boolean isStarted();

    default void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {

        }
    }

}
