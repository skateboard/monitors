package me.scrim.monitor.task.impl.shopify;

import me.scrim.monitor.task.AbstractTask;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Brennan
 * @since 8/2/21
 **/
public class ShopifyTask extends AbstractTask {
    private String site;

    private final long cycleTime;
    private final long stockCycleTime;

    private ZonedDateTime lastUpdate;
    private ZonedDateTime lastStockUpdate;
    private Map<ShopifyProduct, ShopifyProduct> productMap;
    private boolean silent;

    private long lastCycle;
    private long lastStockCycle;
    private boolean error;

    public ShopifyTask(String site) {
        super(site);
        this.site = site;
        lastUpdate = null;
        lastStockUpdate = ZonedDateTime.now();
        productMap = new HashMap<>();
        lastCycle = 0l;
        lastStockCycle = 0l;

        this.cycleTime = 30000;
        this.stockCycleTime = 3600000;
        error = false;

        setStarted(true);
    }

    @Override
    public void run() {
        while (isStarted()) {
            long currentCycle = System.currentTimeMillis();

            if (currentCycle - lastCycle >= cycleTime) {
                refresh(currentCycle);

                lastCycle = currentCycle;

                long sleepTime = cycleTime - (System.currentTimeMillis() - currentCycle);
                sleep((sleepTime > 0) ? sleepTime : 0);
            }
        }
    }

    private void refresh(long currentCycle) {
        ZonedDateTime thisUpdate = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.systemDefault());

        // check for any changes since last update
        if (lastUpdate == null || thisUpdate.isAfter(lastUpdate)) {
            refreshProducts(currentCycle);

            lastUpdate = thisUpdate;
        }

        if (error) {
            error = false;

          //  notifyResolved();
        }
    }

    private void refreshProducts(long currentCycle) {
        boolean stockUpdated = false;
        ZonedDateTime latestUpdatedAt = null;

        List<ShopifyProduct> newProducts = new ArrayList<>();
        int page = 1;

        List<ShopifyProduct> next;
//        while (!(next = getProductsByPage(page)).isEmpty()) {
//            newProducts.addAll(next);
//            page++;
//        }

        for (ShopifyProduct newProduct : newProducts) {

        }
    }
}
