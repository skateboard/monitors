package me.scrim.monitor.task.impl.shopify.misc;

import com.google.gson.JsonObject;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import me.scrim.monitor.ScrimMonitors;
import me.scrim.monitor.discord.utils.DiscordEmbeds;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.AbstractTask;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Brennan
 * @since 7/29/21
 **/
public class ShopifyInfoTask extends AbstractTask {
    private String cachedDomain;

    public ShopifyInfoTask(String websiteURL) {
        super(String.format("%s/meta.json", websiteURL));

        this.cachedDomain = getDomain();
        if(cachedDomain == null) {
            Sentry.captureMessage("Shopify Info Task [" + getId() + "] Initial Grabbing of Domain failed!", SentryLevel.ERROR);
            return;
        }

        setStarted(true);
    }

    @Override
    public void run() {
        while (isStarted()) {
            String reloadedDomain = getDomain();

            if(reloadedDomain != null) {
                if(!reloadedDomain.equalsIgnoreCase(cachedDomain)) {
                    continuousNoUpdates = 0;

                    ScrimMonitors
                            .INSTANCE
                            .getRedis()
                            .sendShopifyDomainUpdate(reloadedDomain, cachedDomain);
                } else {
                    continuousNoUpdates++;
                }

                cachedDomain = reloadedDomain;
                sleep(continuousNoUpdates > 7 ? 3000 : 500);
            }
        }
    }

    private String getDomain() {
        try {
            final Request request = new Request.Builder()
                    .url(getWebsiteUrl())
                    .get()
                    .build();

            try(Response response = getClient().newCall(request).execute()) {
                final int responseCode = response.code();

                switch (responseCode) {
                    case 200 -> {
                        final JsonObject jsonObject = JsonReader.readJson(response);

                        return jsonObject.get("url").getAsString();
                    }
                    case 400 -> {

                    }
                    case 403 -> {
                        continuousBans++;
                        System.out.println("Proxy Banned");
                        return getDomain();
                    }
                    default -> Sentry.captureMessage("Shopify Info Task [" + getId() + "] Unknown response code! " + responseCode, SentryLevel.DEBUG);
                }
            }
        } catch (Exception e) {
            Sentry.captureMessage("Shopify Info Task [" + getId() + "] " + e.getMessage(), SentryLevel.ERROR);
        }

        return null;
    }
}
