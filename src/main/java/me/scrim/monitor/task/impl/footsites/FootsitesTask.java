package me.scrim.monitor.task.impl.footsites;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.AbstractTask;
import me.scrim.monitor.task.impl.footsites.FootsiteProduct;
import me.scrim.monitor.task.impl.footsites.Footsites;
import okhttp3.Request;
import okhttp3.Response;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @author Brennan
 * @since 8/2/21
 **/
public class FootsitesTask extends AbstractTask {
    private final Footsites footsite;
    private final String pdpLink, sku;

    private FootsiteProduct cachedProduct, reloadProduct;
    private int lastUpdateIndex = 0;

    private Queue<FootsiteProduct> cachedProductQueue, reloadProductQueue;

    public FootsitesTask(Footsites footsite, String sku) {
        super("https://www." + footsite.getUrl());
        this.footsite = footsite;
        this.pdpLink = String.format("%s/apigate/products/pdp/%s", getWebsiteUrl(), sku);
        this.sku = sku;

//        this.cachedProduct = getProduct();
//        if(cachedProduct == null) {
//            setStarted(false);
//            System.out.println("Failed");
//            return;
//        }
//        this.lastUpdateIndex = cachedProduct.getSizes().size();
//        setStarted(true);
        this.cachedProductQueue = getProductQueue();
        setStarted(true);
    }

    @Override
    public void run() {
        while (isStarted()) {
            reloadProductQueue = getProductQueue();

            if (!reloadProductQueue.isEmpty()) {

            }

            cachedProductQueue = reloadProductQueue;
            reloadProductQueue = null;
        }
    }

    private void updateCache(FootsiteProduct product) {
        this.cachedProduct = product;
        this.lastUpdateIndex = product.getSizes().size();
    }

    private Queue<FootsiteProduct> getProductQueue() {
        final Queue<FootsiteProduct> footsiteProducts = new ArrayDeque<>();
        try {
            final Request request = new Request.Builder()
                    .url(pdpLink)
                    .get()
                    .build();

            try (Response response = getClient().newCall(request).execute()) {
                final int responseCode = response.code();

                switch (responseCode) {
                    case 200 -> {
                        final JsonObject jsonObject = JsonReader.readJson(response);

                        final String name = jsonObject.get("name").getAsString();

                        final JsonArray sellableUnits = jsonObject.getAsJsonArray("sellableUnits");
                        for (int i = 0; i < sellableUnits.size(); i++) {
                            final JsonObject sellableUnit = sellableUnits.get(i).getAsJsonObject();

                            final FootsiteProduct footsiteProduct = new FootsiteProduct(footsite, name, sku);

                            if (sellableUnit.get("stockLevelStatus").getAsString().equalsIgnoreCase("inStock")) {
                                if (footsiteProduct.getPrice() == null)
                                    footsiteProduct.setPrice(sellableUnit.getAsJsonObject("price").get("formattedValue").getAsString());

                                final JsonArray attributes = sellableUnit.getAsJsonArray("attributes");
                                for (int j = 0; j < attributes.size(); j++) {
                                    final JsonObject attribute = attributes.get(j).getAsJsonObject();

                                    final JsonArray images = jsonObject.getAsJsonArray("images");
                                    for (int k = 0; k < images.size(); k++) {
                                        final String image = getImage(images.get(k).getAsJsonObject(), sellableUnit.get("code").getAsString());
                                        if (image != null) {
                                            footsiteProduct.setImage(image);
                                            break;
                                        }
                                    }

                                    switch (attribute.get("type").getAsString()) {
                                        case "size" -> footsiteProduct.setSize(attribute.get("value").getAsString());
                                        case "style" -> footsiteProduct.setStyle(attribute.get("value").getAsString());
                                    }

                                    footsiteProducts.add(footsiteProduct);
                                }
                            }
                        }
                    }
                    case 400 -> {
                        System.out.println("Product not loaded");
                        return getProductQueue();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return footsiteProducts;
    }

    private FootsiteProduct getProduct() {
        try {
            final Request request = new Request.Builder()
                    .url(pdpLink)
                    .get()
                    .build();

            try (Response response = getClient().newCall(request).execute()) {
                final int responseCode = response.code();

                switch (responseCode) {
                    case 200 -> {
                        final JsonObject jsonObject = JsonReader.readJson(response);

                        final String name = jsonObject.get("name").getAsString();

                        final FootsiteProduct footsiteProduct = new FootsiteProduct(footsite, name, sku);
                        final JsonArray sellableUnits = jsonObject.getAsJsonArray("sellableUnits");
                        for (int i = 0; i < sellableUnits.size(); i++) {
                            final JsonObject sellableUnit = sellableUnits.get(i).getAsJsonObject();

                            if (sellableUnit.get("stockLevelStatus").getAsString().equalsIgnoreCase("inStock")) {
                                if (footsiteProduct.getPrice() == null) {
                                    footsiteProduct.setPrice(sellableUnit.getAsJsonObject("price").get("formattedValue").getAsString());
                                }

                                final JsonArray attributes = sellableUnit.getAsJsonArray("attributes");
                                for (int j = 0; j < attributes.size(); j++) {
                                    final JsonObject attribute = attributes.get(j).getAsJsonObject();
                                    final FootsiteProduct.Size size = new FootsiteProduct.Size();

                                    final JsonArray images = jsonObject.getAsJsonArray("images");
                                    for (int k = 0; k < images.size(); k++) {
                                        final String image = getImage(images.get(k).getAsJsonObject(), sellableUnit.get("code").getAsString());
                                        if (image != null) {
                                            size.setImage(image);
                                            break;
                                        }
                                    }

                                    switch (attribute.get("type").getAsString()) {
                                        case "size" -> size.setSize(attribute.get("value").getAsString());
                                        case "style" -> size.setStyle(attribute.get("value").getAsString());
                                    }

                                    footsiteProduct.getSizes().add(size);
                                }

                            }
                        }

                        return footsiteProduct;
                    }
                    case 400 -> {
                        System.out.println("Product not loaded");
                        return getProduct();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @return What was updated since the last cache.
     */
    private List<FootsiteProduct> getUpdated() {
        Queue<FootsiteProduct> productQueue = getProductQueue();
        List<FootsiteProduct> cachedProducts;
        List<FootsiteProduct> newProducts = null;

        if (cachedProductQueue.size() != productQueue.size()) {
            cachedProducts = new ArrayList<>(cachedProductQueue);

            newProducts = productQueue.stream().filter(product -> !cachedProducts.contains(product)).collect(Collectors.toList());
        }

        if (newProducts != null) {
            return newProducts;
        }

        return new ArrayList<>();
    }

    private String getImage(JsonObject jsonObject, String code) {

        if (jsonObject.get("code").getAsString().equals(code)) {
            return jsonObject.getAsJsonArray("variations").get(1).getAsJsonObject().get("url").getAsString();
        }

        return null;
    }

    private boolean isProductLoaded() {
        try {
            final Request request = new Request.Builder()
                    .url(String.format("%s/product/~/%s.html", getWebsiteUrl(), sku))
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36")
                    .build();

            try (Response response = getClient().newCall(request).execute()) {
                final String title = response.body().string().split("<title>")[1].split("</title>")[0];

                return !title.contains(".html");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
