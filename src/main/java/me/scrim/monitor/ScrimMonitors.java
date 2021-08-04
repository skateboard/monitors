package me.scrim.monitor;

import io.sentry.Sentry;
import lombok.Getter;
import me.scrim.monitor.redis.Redis;
import me.scrim.monitor.task.impl.retail.bestbuy.BestBuyTask;
import me.scrim.monitor.task.impl.retail.newegg.NewEggProduct;
import me.scrim.monitor.task.impl.retail.newegg.NewEggTask;

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

        final BestBuyTask bestBuyTask = new BestBuyTask("3720002");
        bestBuyTask.run();

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
