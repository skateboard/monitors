package me.scrim.monitor.task.impl.retail.bestbuy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.AbstractTask;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Brennan
 * @since 8/4/21
 **/
public class BestBuyTask extends AbstractTask {
    private final String sku;

    private BestBuyProduct cachedProduct, reloadProduct;

    public BestBuyTask(String sku) {
        super(String.format("https://www.bestbuy.com/api/3.0/priceBlocks?skus=%s", sku));
        this.sku = sku;

        addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        addHeader("accept-language", "en-US,en;q=0.9");
        addHeader("cache-control", "no-cache");
        addHeader("pragma", "no-cache");
        addHeader("sec-ch-ua", "\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"91\", \"Chromium\";v=\"91\"");
        addHeader("sec-ch-ua-mobile", "?0");
        addHeader("sec-fetch-dest", "document");
        addHeader("sec-fetch-mode", "navigate");
        addHeader("sec-fetch-site", "none");
        addHeader("sec-fetch-user", "?1");
        addHeader("upgrade-insecure-requests", "1");
        addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36");

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
            reloadProduct = getProduct();

            if(reloadProduct != null) {
                if(reloadProduct.isAvailable() != cachedProduct.isAvailable()) {
                    System.out.println("different");
                } else {
                    System.out.println("same");
                }
            }
        }
    }

    private BestBuyProduct getProduct() {
        try {
            final Request request = new Request.Builder()
                    .url(getWebsiteUrl())
                    .headers(getHeaders())
                    .get()
                    .build();

            try(Response response = getClient().newCall(request).execute()) {
                final JsonArray jsonArray = JsonReader.readJsonArray(response);

                final JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                final JsonObject productObject = jsonObject.get("sku").getAsJsonObject();

                final boolean purchasable = productObject.getAsJsonObject("buttonState").has("purchasable");

                final String name = productObject.get("names").getAsJsonObject().get("short").getAsString();

                final String price = productObject.get("price").getAsJsonObject().get("currentPrice").getAsString();

                final String skuID = productObject.get("skuId").getAsString();

                final String identifier = skuID.substring(0, 4);

                final String image = String.format("https://pisces.bbystatic.com/image2/BestBuy_US/images/products/%s/%s_sd.jpg", identifier, skuID);

                final String url = "https://www.bestbuy.com" + productObject.get("url").getAsString();

                return new BestBuyProduct(name, sku, image, price, url, purchasable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
