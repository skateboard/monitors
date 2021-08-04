package me.scrim.monitor.task.impl.stockx;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.scrim.monitor.discord.utils.DiscordEmbeds;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.AbstractTask;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Map;

/**
 * @author Brennan
 * @since 8/2/21
 **/
public class StockXTask extends AbstractTask {
    private StockXProduct cachedProduct;
    private StockXProduct reloadedProduct;

    private boolean notSet = false;

    public StockXTask(String productName) {
        super(String.format("https://stockx.com/api/products/%s?includes=market&currency=usd", productName));

        this.cachedProduct = getProduct();
        if(cachedProduct == null) {
            setStarted(false);
            System.out.println("Failed");
            return;
        }
        setStarted(true);
    }

    @Override
    public void run() {
        while (isStarted()) {
            reloadedProduct = getProduct();

            if(reloadedProduct != null) {
                if(reloadedProduct.isSingleItem()) {
                    if(!notSet) {
                        reloadedProduct.setPrice(300);

                        notSet = true;
                    }

                    if(!reloadedProduct.getPrice().equals(cachedProduct.getPrice())) {
                        DiscordEmbeds.sendStockX(reloadedProduct, cachedProduct, "https://discord.com/api/webhooks/869519299998531585/IVsinhLZEsBK2BcIqWPF4eGiJmKtKaMNPGxxnW5t8-nH0h-Vuj7UFjaKYQCyn-fp3Aa3");
                    }
                } else {
                    for (int i = 0; i < cachedProduct.getSizes().size(); i++) {
                        final var initialPrice = cachedProduct.getSizes().get(i);
                        final var comparePrice = reloadedProduct.getSizes().get(i);

                        if(!notSet) {
                            comparePrice.setPrice(150);
                            notSet = true;
                        }

                        if(comparePrice.getPrice() != 0) {
                            if(comparePrice.getPrice() < initialPrice.getPrice())
                                DiscordEmbeds.sendStockXSize(reloadedProduct, comparePrice, initialPrice, "https://discord.com/api/webhooks/869519299998531585/IVsinhLZEsBK2BcIqWPF4eGiJmKtKaMNPGxxnW5t8-nH0h-Vuj7UFjaKYQCyn-fp3Aa3");
                        }
                    }
                }
            }
        }
    }

    private StockXProduct getProduct() {
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

                        final JsonObject productObject = jsonObject.getAsJsonObject("Product");

                        final StockXProduct stockXProduct = new StockXProduct();
                        stockXProduct.setName(productObject.get("name").getAsString());
                        stockXProduct.setStockXName(productObject.get("urlKey").getAsString());
                        stockXProduct.setImage(productObject.getAsJsonObject("media").get("imageUrl").getAsString());
                        stockXProduct.setPrice(productObject.getAsJsonObject("market").get("lowestAsk").getAsInt());

                        stockXProduct.setSingleItem(productObject.getAsJsonObject("children").entrySet().size() == 1);
                        stockXProduct.setRetailPrice(productObject.get("retailPrice").getAsInt());

                        if(!stockXProduct.isSingleItem()) {
                            final JsonObject childrenObject = productObject.getAsJsonObject("children");

                            for(Map.Entry<String, JsonElement> childrenEntry : childrenObject.entrySet()) {
                                final JsonObject child = childrenEntry.getValue().getAsJsonObject();;
                                stockXProduct.getSizes().add(new StockXProduct.Size(
                                        child.getAsJsonObject("market").get("lastSaleSize").getAsString(),child.getAsJsonObject("market").get("lowestAsk").getAsInt()));
                            }
                        }

                        return stockXProduct;
                    }
                    case 400 -> {

                    }
                    case 403 -> {

                    }
                    default -> {

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
