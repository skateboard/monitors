package me.scrim.monitor.task.impl.retail.newegg;

import com.google.gson.JsonObject;
import me.scrim.monitor.discord.utils.DiscordEmbeds;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.AbstractTask;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Brennan
 * @since 8/4/21
 **/
public class NewEggTask extends AbstractTask {
    private final String sku;

    private NewEggProduct cachedProduct, reloadProduct;

    private boolean set;

    public NewEggTask(String sku) {
        super(String.format("https://www.newegg.com/product/api/ProductRealtime?ItemNumber=%s", sku));
        this.sku = sku;

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
                if(!set) {
                    cachedProduct.setAvailable(false);

                    set = true;
                }
                if(reloadProduct.isAvailable() != cachedProduct.isAvailable()) {
                    System.out.println("different");
                    System.out.println(reloadProduct.toJSON());
                    DiscordEmbeds.sendNewEgg(reloadProduct, "https://discord.com/api/webhooks/869519299998531585/IVsinhLZEsBK2BcIqWPF4eGiJmKtKaMNPGxxnW5t8-nH0h-Vuj7UFjaKYQCyn-fp3Aa3");
                } else {
                    System.out.println("same");
                }
            }

            cachedProduct = reloadProduct;
        }
    }

    private NewEggProduct getProduct() {
        try {
            final Request request = new Request.Builder()
                    .url(getWebsiteUrl())
                    .get()
                    .build();

            try(Response response = getClient().newCall(request).execute()) {
                final JsonObject jsonObject = JsonReader.readJson(response);

                final JsonObject productObject = jsonObject.get("MainItem").getAsJsonObject();
                final JsonObject descriptionObject = productObject.getAsJsonObject("Description");
                final JsonObject imageObject = productObject.getAsJsonObject("Image");

                final String image = imageObject.getAsJsonObject("Normal").get("ImageName").getAsString();

                final String imageUrl = String.format("https://c1.neweggimages.com/ProductImageCompressAll1280/%s", image);

                final String urlKeyWords = descriptionObject.get("UrlKeywords").getAsString();

                final String url = String.format("https://www.newegg.com/%s/p/%s", urlKeyWords, "N82E16819113567");

                final String cost = "$" + productObject.get("FinalPrice").getAsString();

                return new NewEggProduct(descriptionObject.get("Title").getAsString(), sku,
                        imageUrl, cost, url, productObject.get("Instock").getAsBoolean());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
