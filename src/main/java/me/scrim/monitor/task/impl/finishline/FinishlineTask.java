package me.scrim.monitor.task.impl.finishline;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import me.scrim.monitor.discord.utils.DiscordEmbeds;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.Task;
import me.scrim.monitor.task.impl.finishline.product.FinishlineProduct;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Brennan
 * @since 7/26/2021
 **/
public class FinishlineTask extends Task {
    private final String color;

    public FinishlineTask(String sku, String color) {
        super(String.format("https://prodmobloy2.finishline.com/api/products/%s", sku));
        this.color = color;

        addHeader("Host", "prodmobloy2.finishline.com");
        addHeader("welove", "maltliquor");
        addHeader("X-Api-Version", "3.0");
        addHeader("Content-Type", "application/json; charset=utf-8");
        addHeader("x-Banner", "FINL");
        addHeader("User-Agent", "Finish Line/2.7.3  (Android 2.7.3; Build/2.7.3)");
    }

    @Override
    public void run() {
        try {
            final Request request = new Request.Builder()
                    .url(getWebsiteURL())
                    .headers(getHeaders())
                    .get()
                    .build();

            try(Response response = getClient().newCall(request).execute()) {
                int responseCode = response.code();

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
                                product.setPrice(colorObject.get("salePriceCents").getAsInt());
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

                                DiscordEmbeds.sendEmbeds(product,
                                        "https://discord.com/api/webhooks/869519299998531585/IVsinhLZEsBK2BcIqWPF4eGiJmKtKaMNPGxxnW5t8-nH0h-Vuj7UFjaKYQCyn-fp3Aa3");
                            }
                        }
                    }
                    case 400 -> {

                    }
                    default -> {
                        Sentry.captureMessage("Finishline Task [" + getId() + "] Unknown response code! " + responseCode, SentryLevel.DEBUG);
                    }
                }

            }
        } catch (Exception e) {
            Sentry.captureMessage("Finishline Task [" + getId() + "] " + e.getMessage(), SentryLevel.ERROR);
        }
    }
}
