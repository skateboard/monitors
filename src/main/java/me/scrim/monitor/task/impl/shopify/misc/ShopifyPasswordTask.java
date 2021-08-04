package me.scrim.monitor.task.impl.shopify.misc;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import me.scrim.monitor.ScrimMonitors;
import me.scrim.monitor.discord.utils.DiscordEmbeds;
import me.scrim.monitor.task.AbstractTask;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Brennan
 * @since 7/29/21
 **/
public class ShopifyPasswordTask extends AbstractTask {

    public ShopifyPasswordTask(String websiteURL) {
        super(websiteURL);

        setStarted(true);
    }

    @Override
    public void run() {
        while (isStarted()) {
            boolean hasPassword = checkPassword();

            while (!hasPassword) {
                System.out.println("Password page not found!");
                hasPassword = checkPassword();
                sleep(1000);
            }

            System.out.println("Password page is live");
            ScrimMonitors.INSTANCE.getRedis().sendShopifyPasswordUpdate(getWebsiteUrl(), true);

            boolean didPasswordGoDown = false;

            while (!didPasswordGoDown) {
                boolean passwordStillUp = checkPassword();

                if(passwordStillUp) {
                    System.out.println("Password is still up");
                    sleep(1500);
                } else {
                    System.out.println("Password is down!");
                    ScrimMonitors.INSTANCE.getRedis().sendShopifyPasswordUpdate(getWebsiteUrl(), false);

                    didPasswordGoDown = true;
                }
            }

//            sleep(continuousNoUpdates > 7 ? 3000 : 500);
        }
    }

    private boolean checkPassword() {
        try {
            final Request request = new Request.Builder()
                    .url(getWebsiteUrl())
                    .get()
                    .build();

            try(Response response = getClient().newCall(request).execute()) {
                final int responseCode = response.code();

                switch (responseCode) {
                    case 200 -> {
                        return response.request().url().toString().contains("password");
                    }
                    case 400 -> {

                    }
                    case 403 -> {
                        continuousBans++;
                        System.out.println("Proxy Banned");
                        return checkPassword();
                    }
                    default -> Sentry.captureMessage("Shopify Password Task [" + getId() + "] Unknown response code! " + responseCode, SentryLevel.DEBUG);
                }
            }
        } catch (Exception e) {
            Sentry.captureMessage("Shopify Password Task [" + getId() + "] " + e.getMessage(), SentryLevel.ERROR);
        }

        return false;
    }
}
