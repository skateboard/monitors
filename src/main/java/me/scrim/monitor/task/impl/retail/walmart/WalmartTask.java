package me.scrim.monitor.task.impl.retail.walmart;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.AbstractTask;
import me.scrim.monitor.task.impl.retail.walmart.product.WalmartOfferProduct;
import me.scrim.monitor.task.impl.retail.walmart.product.WalmartProduct;
import okhttp3.Request;
import okhttp3.Response;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Brennan
 * @since 8/1/21
 **/
public class WalmartTask extends AbstractTask {
    private WalmartProduct cachedProduct;

    public WalmartTask(String productID) {
        super(String.format("https://www.walmart.com/terra-firma/item/%s", productID));

        this.cachedProduct = getProduct();

        if(cachedProduct != null) {
            setStarted(false);
            return;
        }
        setStarted(true);
    }

    @Override
    public void run() {
        final WalmartProduct product = getProduct();

        if(product != null) {
            System.out.println(product.getOffers().size());
        }
    }

    private WalmartOfferProduct getOfferProduct(String productID, String offerID) {
        try {
            final Request request = new Request.Builder()
                    .url(String.format("https://www.walmart.com/terra-firma/item/%s", productID))
                    .get()
                    .build();

            try(Response response = getClient().newCall(request).execute()) {
                final int responseCode = response.code();

                switch (responseCode) {
                    case 200 -> {
                        final JsonObject jsonObject = JsonReader.readJson(response);

                        System.out.println(jsonObject);
                        final JsonObject offersObject = jsonObject.getAsJsonObject("payload").getAsJsonObject("offers");

                        System.out.println(offersObject);
                        for(Map.Entry<String, JsonElement> offerEntry : offersObject.entrySet()) {
                            System.out.println(offerEntry.getKey());
                            if(offerEntry.getKey().equalsIgnoreCase(offerID)) {
                                final JsonObject offerObject = offerEntry.getValue().getAsJsonObject();

                                if(offerObject.get("productAvailability")
                                        .getAsJsonObject().get("availabilityStatus")
                                        .getAsString().equalsIgnoreCase("IN_STOCK")) {
                                    final String price = offerObject.getAsJsonObject("priceMap").getAsJsonObject("CURRENT").get("price").getAsString();

                                    System.out.println("?");
                                    return new WalmartOfferProduct(cachedProduct, price);
                                }
                            }
                        }
                    }
                    case 400 -> {

                    }
                    case 403 -> {
                        System.out.println("Rotating Proxy");
                    }
                    default -> {
                        Sentry.captureMessage("Walmart Task [" + getId() + "] Unknown response code! " + responseCode, SentryLevel.DEBUG);
                    }
                }
            }
        } catch (Exception e) {
            Sentry.captureMessage("Walmart Task [" + getId() + "] " + e.getMessage(), SentryLevel.ERROR);
        }

        return null;
    }

    private WalmartProduct getProduct() {
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

                        final JsonObject products = jsonObject.getAsJsonObject("payload").getAsJsonObject("products");

                        final WalmartProduct walmartProduct = new WalmartProduct();
                        String imageID = "";

                        final List<String> offerIds = new LinkedList<>();
                        for(Map.Entry<String, JsonElement> productsEntry : products.entrySet()) {
                            final JsonObject productObject = productsEntry.getValue().getAsJsonObject();

                            if(productObject.get("status").getAsString().equalsIgnoreCase("FETCHED")) {
                                walmartProduct.setProductName(productObject.getAsJsonObject("productAttributes").get("productName").getAsString());

                                imageID = productObject.getAsJsonArray("images").get(0).getAsString();
                                for(JsonElement element : productObject.getAsJsonArray("offers")) {
                                    offerIds.add(element.getAsString());
                                }
                            }
                        }

                        walmartProduct.setImage(jsonObject
                                .getAsJsonObject("payload").
                                        getAsJsonObject("images").
                                        getAsJsonObject(imageID).
                                        getAsJsonObject("assetSizeUrls").
                                        get("IMAGE_SIZE_60").getAsString());

                        final JsonObject offers = jsonObject
                                .getAsJsonObject("payload").getAsJsonObject("offers");
                        for(Map.Entry<String, JsonElement> offerEntry : offers.entrySet()) {
                            if(offerIds.contains(offerEntry.getKey())) {
                                final JsonObject offerObject = offerEntry.getValue().getAsJsonObject();

                                if(offerObject.get("productAvailability")
                                        .getAsJsonObject().get("availabilityStatus")
                                        .getAsString().equalsIgnoreCase("IN_STOCK")) {
                                    final String price = offerObject
                                            .getAsJsonObject("pricesInfo").getAsJsonObject("priceMap").getAsJsonObject("CURRENT").get("price").getAsString();
                                    walmartProduct.getOffers().add(new WalmartProduct.Offer(price));
                                }
                            }
                        }

                        return walmartProduct;
                    }
                    case 400 -> {

                    }
                    case 403 -> {
                        System.out.println("Rotate Proxies");
                        return getProduct();
                    }
                    default -> {
                        Sentry.captureMessage("Walmart Task [" + getId() + "] Unknown response code! " + responseCode, SentryLevel.DEBUG);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.captureMessage("Walmart Task [" + getId() + "] " + e.getMessage(), SentryLevel.ERROR);
        }

        return null;
    }
}
