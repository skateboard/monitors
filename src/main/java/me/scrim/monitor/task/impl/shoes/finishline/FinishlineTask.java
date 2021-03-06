package me.scrim.monitor.task.impl.shoes.finishline;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import me.scrim.monitor.ScrimMonitors;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.AbstractTask;
import me.scrim.monitor.task.impl.shoes.finishline.product.FinishlineProduct;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Brennan
 * @since 7/26/2021
 **/
public class FinishlineTask extends AbstractTask {
    private final String color;

    private FinishlineProduct cachedProduct;
    private FinishlineProduct reloadProduct;

    private int lastUpdateIndex = 0;

    public FinishlineTask(String sku, String color) {
        super(String.format("https://prodmobloy2.finishline.com/api/products/%s", sku));
        this.color = color;

        addHeader("Host", "prodmobloy2.finishline.com");
        addHeader("welove", "maltliquor");
        addHeader("X-Api-Version", "3.0");
        addHeader("Content-Type", "application/json; charset=utf-8");
        addHeader("x-Banner", "FINL");
        addHeader("User-Agent", "Finish Line/2.7.3  (Android 2.7.3; Build/2.7.3)");

        this.cachedProduct = findProduct();

        if(cachedProduct == null) {
            Sentry.captureMessage("Finishline Task [" + getId() + "] Failed to grab initial product!", SentryLevel.DEBUG);

            setStarted(false);
            return;
        }
        this.lastUpdateIndex = cachedProduct.getSizes().size();
        setStarted(true);
    }

    @Override
    public void run() {
        while (isStarted()) {
            if(cachedProduct != null) {
                reloadProduct = findProduct();

                if(reloadProduct != null) {
                    reloadProduct.addSize(new FinishlineProduct.Size("s", "s", 4));
                    if(lastUpdateIndex != reloadProduct.getSizes().size()) {
                        continuousNoUpdates = 0;
                        ScrimMonitors
                                .INSTANCE
                                .getRedis()
                                .sendWebhooks(reloadProduct);

                        Sentry.setTag("type", "successful_webhooks");
                        Sentry
                                .captureMessage("Finishline Task [" + getId() + "] Successfully sent webhooks!",
                                        SentryLevel.INFO);
                    } else {
                        continuousNoUpdates++;
                    }

                    updateCache(reloadProduct);
                    reloadProduct = null;

                    sleep(continuousNoUpdates > 7 ? 3000 : 500);
                }
            }
        }
    }

    private void updateCache(FinishlineProduct product) {
        this.cachedProduct = product;
        this.lastUpdateIndex = product.getSizes().size();
    }

    private FinishlineProduct findProduct() {
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
                        final FinishlineProduct product = new FinishlineProduct();
                        final JsonObject responseObject = JsonReader.readJson(response);

                        product.setBaseInformation(responseObject.get("productId").getAsString(), responseObject.get("displayName").getAsString());

                        final JsonArray colorWaysArray = responseObject.getAsJsonArray("colorWays");

                        for(int i = 0; i < colorWaysArray.size(); i++) {
                            final JsonObject colorObject = colorWaysArray.get(i).getAsJsonObject();

                            if(colorObject.get("colorDescription").getAsString().contains(color)) {
                                final String image = colorObject
                                        .getAsJsonArray("images")
                                        .get(0)
                                        .getAsJsonObject().get("thumbnailUrl")
                                        .getAsString();

                                final String color = colorObject.get("colorDescription").getAsString();

                                product.setImage(image);
                                product.setPrice(colorObject.get("salePriceCents").getAsString());
                                product.setColor(color);
                                product.setProductUrl(colorObject.get("styleId").getAsString(), colorObject.get("colorId").getAsString());

                                final JsonArray skuArray = colorObject.getAsJsonArray("skus");

                                for (int j = 0; j < skuArray.size(); j++) {
                                    final JsonObject skuObject = skuArray.get(j).getAsJsonObject();

                                    if(!skuObject.get("outOfStock").getAsBoolean()) {
                                        product.addSize(new FinishlineProduct.Size(skuObject.get("skuId").getAsString(),
                                                skuObject.get("size").getAsString(),
                                                skuObject.get("quantityAvailable").getAsInt()));
                                    }
                                }


                                return product;
                            }
                        }
                    }
                    case 400 -> {

                    }
                    case 499 -> {
                        System.out.println("Product not loaded.");

                        return findProduct();
                    }
                    case 403 -> {
                        continuousBans++;
                        System.out.println("Banned Proxy (rotate)");
                        return findProduct();
                    }
                    default -> {
                        Sentry.captureMessage("Finishline Task [" + getId() + "] Unknown response code! " + responseCode, SentryLevel.DEBUG);
                    }
                }

            }
        } catch (Exception e) {
            Sentry.captureMessage("Finishline Task [" + getId() + "] " + e.getMessage(), SentryLevel.ERROR);
        }

        return null;
    }
}
