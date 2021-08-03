package me.scrim.monitor;

import io.sentry.Sentry;
import lombok.Getter;
import me.scrim.monitor.redis.Redis;
import me.scrim.monitor.task.impl.finishline.FinishlineReleasesTask;
import me.scrim.monitor.task.impl.finishline.FinishlineTask;
import me.scrim.monitor.task.impl.footsites.Footsites;
import me.scrim.monitor.task.impl.footsites.FootsitesQueueTask;
import me.scrim.monitor.task.impl.footsites.FootsitesTask;
import me.scrim.monitor.task.impl.footsites.impl.FootlockerTask;
import me.scrim.monitor.task.impl.hibbett.HibbettTask;
import me.scrim.monitor.task.impl.shopify.ShopifyInfoTask;
import me.scrim.monitor.task.impl.shopify.ShopifyPasswordTask;
import me.scrim.monitor.task.impl.stockx.StockXMonitor;
import me.scrim.monitor.task.impl.stockx.StockXProduct;
import me.scrim.monitor.task.impl.walmart.WalmartTask;

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

        //6912010
        final FootsitesTask footLocker = new FootlockerTask("6912010");
        footLocker.run();
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
