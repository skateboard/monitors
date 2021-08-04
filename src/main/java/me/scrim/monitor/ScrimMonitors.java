package me.scrim.monitor;

import io.sentry.Sentry;
import lombok.Getter;
import me.scrim.monitor.redis.Redis;
import me.scrim.monitor.task.impl.amazon.AmazonTask;
import me.scrim.monitor.task.impl.shopify.OldTask;
import me.scrim.monitor.task.impl.shopify.newtask.ShopifyTask;

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

        final AmazonTask amazonTask = new AmazonTask("B01N6YAVNM",
                "9zEvVeQU6Ijm%2Ft5UVK7RCcnbgElgeqc9RInvmrDgVjKsaw4scYPzoNi7cIVPuQB%2FC6MHjFFr2Rfa4dHk7N6%2B0UXBSDGKTPYa%2Bn%2FLSln802%2BM3MxQrMnSva%2BKXqqGfpqbiDiIr5ytO%2Fl%2FtiESfmZM1w%3D%3D");
        amazonTask.run();
//       final ShopifyTask shopifyTask = new ShopifyTask("https://testmonitors.myshopify.com");
//        shopifyTask.run();
//        final OldTask oldTask = new OldTask("testmonitors.myshopify.com");
//        oldTask.run();

//        final WalmartTask walmartTask = new WalmartTask("36205314");
//        walmartTask.run();
//        final FinishlineTask task = new FinishlineTask("prod2798222", "White/Gym Red-Black");
//        task.run();
//        final FinishlineReleasesTask task = new FinishlineReleasesTask();
//        task.run();
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
