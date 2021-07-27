package me.scrim.monitor;

import io.sentry.Sentry;
import lombok.Getter;
import me.scrim.monitor.redis.Redis;
import me.scrim.monitor.task.impl.finishline.FinishlineTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Brennan
 * @since 7/26/2021
 **/
@Getter
public enum ScrimMonitors {
    INSTANCE;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Redis redis;

    public void start() {
        initializeSentry();

        this.redis = new Redis();

        final FinishlineTask task = new FinishlineTask("prod2798222", "White/Gym Red-Black");
        task.run();
    }

    private void initializeSentry() {
        Sentry.init(options -> {
            options.setDsn("https://af01435b271c424b846a93e91b42bd61@o663814.ingest.sentry.io/5880931");
            options.setTracesSampleRate(1.0);
            options.setDebug(true);
            options.setEnvironment(System.getenv("debug").equalsIgnoreCase("true") ? "debug" : "environment");
        });
    }
}
