package me.scrim.monitor.task.impl.finishline;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import me.scrim.monitor.ScrimMonitors;
import me.scrim.monitor.discord.utils.DiscordEmbeds;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.AbstractTask;
import me.scrim.monitor.task.Task;
import me.scrim.monitor.task.impl.finishline.product.FinishlineReleaseProduct;
import okhttp3.Request;
import okhttp3.Response;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author Brennan
 * @since 7/27/21
 **/
public class FinishlineReleasesTask extends AbstractTask {

    private List<FinishlineReleaseProduct> cachedProducts;
    private List<FinishlineReleaseProduct> reloadedProducts;

    public FinishlineReleasesTask() {
        super("https://prodmobloy2.finishline.com/api/shop/drops");

        addHeader("Host", "prodmobloy2.finishline.com");
        addHeader("welove", "maltliquor");
        addHeader("X-Api-Version", "3.0");
        addHeader("Content-Type", "application/json; charset=utf-8");
        addHeader("x-Banner", "FINL");
        addHeader("User-Agent", "Finish Line/2.7.3  (Android 2.7.3; Build/2.7.3)");

        this.cachedProducts = grabReleases();
        if(cachedProducts.isEmpty()) {
            Sentry.captureMessage(
                    "Finishline Release Task [" + getId() + "] Failed to grab initial releases!",
                    SentryLevel.DEBUG);

            setStarted(false);
            return;
        }

        setStarted(true);
    }

    @Override
    public void run() {
        while (isStarted()) {
            if(!cachedProducts.isEmpty()) {
                reloadedProducts = grabReleases();

                if(!reloadedProducts.isEmpty()) {
                    final FinishlineReleaseProduct finishlineReleaseProduct = new FinishlineReleaseProduct();
                    finishlineReleaseProduct.setProductUrl("424", "dasd");
                    finishlineReleaseProduct.setInformation("prodtest",
                            "Test Jordans",
                            "https://i8.amplience.net/i/finishline/CN8094_102_P1?$Thumbnail$",
                            "White/Black",
                            "NIKE",
                            new Date().getTime());
                    finishlineReleaseProduct.setPrice("11000");
                    reloadedProducts.add(finishlineReleaseProduct);

                    if(reloadedProducts.size() != cachedProducts.size()) {
                        continuousNoUpdates = 0;

                        for(FinishlineReleaseProduct releaseProduct : reloadedProducts) {
                            int daysBetween = daysBetween(new Date(), releaseProduct.getReleaseDate());
                            System.out.println(daysBetween);

                            if(daysBetween == 0) {
                                DiscordEmbeds.sendRelease(releaseProduct,
                                        "https://discord.com/api/webhooks/869519299998531585/IVsinhLZEsBK2BcIqWPF4eGiJmKtKaMNPGxxnW5t8-nH0h-Vuj7UFjaKYQCyn-fp3Aa3");

                                ScrimMonitors
                                        .INSTANCE
                                        .getRedis()
                                        .sendReleases(releaseProduct);

                                Sentry.setTag("type", "successful_releases_webhooks");
                                Sentry
                                        .captureMessage("Finishline Release Task [" + getId() + "] Successfully sent webhooks!",
                                                SentryLevel.INFO);
                            }
                        }
                    } else {
                        continuousNoUpdates++;
                    }
                }

                this.cachedProducts = reloadedProducts;
                this.reloadedProducts = null;

                sleep(continuousNoUpdates > 7 ? 3000 : 500);
            }
        }
    }

    private List<FinishlineReleaseProduct> grabReleases() {
        final List<FinishlineReleaseProduct> products = new LinkedList<>();

        try {
            final Request request = new Request.Builder()
                    .url(getWebsiteUrl())
                    .headers(getHeaders())
                    .get()
                    .build();

            try(Response response = getClient().newCall(request).execute()) {
                final int responseCode = response.code();

                switch (responseCode) {
                    case 200 -> {
                        final JsonObject responseObject = JsonReader.readJson(response);

                        final JsonArray dropProducts = responseObject.getAsJsonArray("dropProducts");

                        for (int i = 0; i < dropProducts.size(); i++) {
                            final JsonObject productObject = dropProducts.get(i).getAsJsonObject();

                            String color = "N/A";
                            if(productObject.get("colorDescription") != null) {
                                color = productObject.get("colorDescription").toString();
                            }

                            final FinishlineReleaseProduct finishlineReleaseProduct = new FinishlineReleaseProduct();
                            finishlineReleaseProduct.setProductUrl(productObject.get("style").getAsString(), productObject.get("color").getAsString());
                            finishlineReleaseProduct.setInformation(productObject.get("productId").getAsString(),
                                    productObject.get("displayName").getAsString(),
                                    productObject.get("thumbnailUrl").getAsString(),
                                    color,
                                    productObject.get("brand").getAsString(),
                                    productObject.get("releaseDate").getAsLong());
                            finishlineReleaseProduct.setPrice(productObject.get("listPriceCents").getAsString());

                            products.add(finishlineReleaseProduct);
                        }

                    }
                    case 400 -> {

                    }
                    case 403 -> {
                        continuousBans++;
                        System.out.println("Banned Proxy (rotate)");
                        return grabReleases();
                    }
                    default -> {
                        Sentry.captureMessage("Finishline Release Task [" + getId() + "] Unknown response code! " + responseCode, SentryLevel.DEBUG);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.captureMessage("Finishline Release Task [" + getId() + "] " + e.getMessage(), SentryLevel.ERROR);
        }

        return products;
    }

    private int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
}
