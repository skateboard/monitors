package me.scrim.monitor;

import io.sentry.Sentry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Brennan
 * @since 7/26/2021
 **/
public enum ScrimMonitors {
    INSTANCE;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void start() {
        initializeSentry();
    }

    private void initializeSentry() {
        Sentry.init(options -> {
            options.setDsn("https://af01435b271c424b846a93e91b42bd61@o663814.ingest.sentry.io/5880931");
            // Set traces_sample_rate to 1.0 to capture 100% of transactions for performance monitoring.
            // We recommend adjusting this value in production.
            options.setTracesSampleRate(1.0);
            // When first trying Sentry it's good to see what the SDK is doing:
            options.setDebug(true);
        });
    }
}
