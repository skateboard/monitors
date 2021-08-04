package me.scrim.monitor.task.impl.amazon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.scrim.monitor.discord.utils.DiscordEmbeds;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.AbstractTask;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Random;

/**
 * @author Brennan
 * @since 8/3/21
 **/
public class AmazonTask extends AbstractTask {
    private final String asin, orderID;

    private final AmazonProduct amazonProduct = new AmazonProduct();

    private boolean cachedATC, reloadedATC, set;

    public AmazonTask(String asin, String orderID) {
        super("https://www.amazon.com/~/dp/" + asin);
        this.asin = asin;
        this.orderID = orderID;

        amazonProduct.setOfferID(orderID);

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

        final String sid = getSID();

        this.cachedATC = addToCart(sid);
        setStarted(true);
    }

    @Override
    public void run() {
        while (isStarted()) {
            final String sid = getSID();

            reloadedATC = addToCart(sid);

            if(reloadedATC) {

                if(!set) {
                    cachedATC = false;
                    set = true;
                }

                if(reloadedATC != cachedATC) {
                    continuousNoUpdates = 0;
                    setProductInformation();

                    System.out.println(amazonProduct.toJSON());
                    DiscordEmbeds.sendAmazon(amazonProduct, "https://discord.com/api/webhooks/869519299998531585/IVsinhLZEsBK2BcIqWPF4eGiJmKtKaMNPGxxnW5t8-nH0h-Vuj7UFjaKYQCyn-fp3Aa3");
                    System.out.println("different");
                } else {
                    continuousNoUpdates++;
                    System.out.println("same");
                }
            }
            cachedATC = reloadedATC;
        }
    }

    private void setProductInformation() {
        try {
            final Request request = new Request.Builder()
                    .url(getWebsiteUrl())
                    .headers(getHeaders())
                    .get()
                    .build();

            try(Response response = getClient().newCall(request).execute()) {
                final Document document = Jsoup.parse(response.body().string());

                amazonProduct.setName(document.getElementById("productTitle").text());
                amazonProduct.setImage(document.getElementById("landingImage").attr("data-old-hires"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean addToCart(String sid) {
        try {
            final FormBody formBody = new FormBody.Builder()
                    .add("marketplaceId", "ATVPDKIKX0DER")
                    .add("asin", "B005QIYL7E")
                    .add("customerId", "")
                    .add("sessionId", sid)
                    .add("accessoryItemAsin", "B002M40VJM")
                    .add("accessoryItemOfferingId", orderID)
                    .add("languageOfPreference", "en_US")
                    .add("accessoryItemQuantity", "1")
                    .add("accessoryItemPrice", String.valueOf(randomNumber()))
                    .add("accessoryMerchantId", "ATVPDKIKX0DER")
                    .add("accessoryProductGroupId", "")
                    .build();
            final Request request = new Request.Builder()
                    .url("https://smile.amazon.com/gp/product/features/aloha-ppd/udp-ajax-handler/attach-add-to-cart.html")
                    .headers(getHeaders())
                    .post(formBody)
                    .build();

            try(Response response = getClient().newCall(request).execute()) {
                final int responseCode = response.code();

                if(responseCode == 400) {
                    createSession();
                    createSession();

                    return addToCart(sid);
                }

                final String body = response.body().string().trim();
                final JsonObject jsonObject = JsonReader.readJson(body);

                amazonProduct.setPrice(jsonObject.get("formattedTotalPrice").getAsString());
                final JsonArray includedAsins = jsonObject.getAsJsonArray("includedAsins");

                if(!includedAsins.isEmpty()) {
                    for(JsonElement jsonElement : includedAsins) {
                        if(jsonElement.getAsString().equals(asin)) {
                            amazonProduct.setAsin(asin);
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getSID() {
        createSession();
        createSession();

        for(Cookie cookie : getCookieJar().getCookies()) {
            if(cookie.name().equalsIgnoreCase("session-id"))
                return cookie.value();
        }

        return null;
    }

    private void createSession() {
        try {
            final Request request = new Request.Builder()
                    .url("https://smile.amazon.com/gp/mobile/udp/ajax-handlers/reftag.html?ref_=dp_atch_abb_i%22")
                    .headers(getHeaders())
                    .get()
                    .build();

            getClient().newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int randomNumber() {
        return new Random().nextInt(100);
    }
}
