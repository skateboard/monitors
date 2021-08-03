package me.scrim.monitor.task.impl.shopify;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import me.scrim.monitor.ScrimMonitors;
import me.scrim.monitor.task.AbstractTask;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Brennan
 * @since 8/3/21
 **/
public class ShopifyCheckpointTask extends AbstractTask {

    public ShopifyCheckpointTask(String websiteURL) {
        super(websiteURL);

        setStarted(true);
    }

    @Override
    public void run() {
        while (isStarted()) {
            boolean hasCheckpoint = checkCheckpoint();

            while (!hasCheckpoint) {
                System.out.println("Password page not found!");
                hasCheckpoint = checkCheckpoint();
                sleep(1000);
            }

            System.out.println("Checkpoint page is live");
            ScrimMonitors.INSTANCE.getRedis().sendShopifyCheckpoint(getWebsiteUrl(), true);

            boolean didCheckpointGoDown = false;

            while (!didCheckpointGoDown) {
                boolean checkpointStillUp = checkCheckpoint();

                if(checkpointStillUp) {
                    System.out.println("Password is still up");
                    sleep(1500);
                } else {
                    System.out.println("Password is down!");
                    ScrimMonitors.INSTANCE.getRedis().sendShopifyCheckpoint(getWebsiteUrl(), false);

                    didCheckpointGoDown = true;
                }
            }
        }
    }

    private boolean checkCheckpoint() {
        try {
            final Request request = new Request.Builder()
                    .url(getWebsiteUrl())
                    .get()
                    .build();

            try(Response response = getClient().newCall(request).execute()) {
                final int responseCode = response.code();

                switch (responseCode) {
                    case 200 -> {
                        return response.request().url().toString().contains("checkpoint");
                    }
                    case 400 -> {

                    }
                    case 403 -> {
                        continuousBans++;
                        System.out.println("Proxy Banned");
                        return checkCheckpoint();
                    }
                    default -> Sentry.captureMessage("Shopify Checkpoint Task [" + getId() + "] Unknown response code! " + responseCode, SentryLevel.DEBUG);
                }
            }
        } catch (Exception e) {
            Sentry.captureMessage("Shopify Checkpoint Task [" + getId() + "] " + e.getMessage(), SentryLevel.ERROR);
        }

        return false;
    }
}
