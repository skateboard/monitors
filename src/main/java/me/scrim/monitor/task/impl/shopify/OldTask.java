package me.scrim.monitor.task.impl.shopify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.scrim.monitor.product.Product;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.AbstractTask;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @author Brennan
 * @since 8/2/21
 **/
public class OldTask extends AbstractTask {
    private String site;

    private final long cycleTime;
    private final long stockCycleTime;

    private ZonedDateTime lastUpdate;
    private ZonedDateTime lastStockUpdate;
    private Map<ShopifyProduct, ShopifyProduct> productMap;
    private Deque<ProductEvent> queue;


    private long lastCycle;
    private long lastStockCycle;
    private boolean error;

    public OldTask(String site) {
        super(site);
        this.site = site;
        lastUpdate = null;
        lastStockUpdate = ZonedDateTime.now();
        productMap = new HashMap<>();
        queue = new ArrayDeque<>();

        lastCycle = 0L;
        lastStockCycle = 0L;

        this.cycleTime = 1500; //30000
        this.stockCycleTime = 3600000;
        error = false;

        try {
            initalizeCookies();

        } catch (Exception e) {
            e.printStackTrace();
        }

        setStarted(true);
    }

    @Override
    public void run() {
        while (isStarted()) {
            try {
                long currentCycle = System.currentTimeMillis();

                if (currentCycle - lastCycle >= cycleTime) {
                    refresh(currentCycle);
                    checkQueue();

                    lastCycle = currentCycle;

                    long sleepTime = cycleTime - (System.currentTimeMillis() - currentCycle);
                    System.out.println(sleepTime);
                    sleep((sleepTime > 0) ? sleepTime : 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkQueue() {
        if (!queue.isEmpty()) {
            while (!queue.isEmpty()) {
                notifyListeners(queue.pop());
            }

            // notify listeners we are done
            notifyListeners(null);
        }
    }


    private void refresh(long currentCycle) throws Exception {
        final String siteMap = getProductsSiteMap();
        final Queue<Url> urlQueue = getSiteQueue(siteMap);

        final Url latest = urlQueue.poll();
        ZonedDateTime thisUpdate = latest.getLastmod();

        if (lastUpdate == null || thisUpdate.isAfter(lastUpdate)) {
            try {

                System.out.println("refreshing?");
                refreshProducts(currentCycle);

                lastUpdate = thisUpdate;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (error) {
            error = false;

          //  notifyResolved();
        }
    }

    private void refreshProducts(long currentCycle) throws Exception {
        boolean stockUpdated = false;
        ZonedDateTime latestUpdatedAt = null;

        List<ShopifyProduct> newProducts = new ArrayList<>();
        int page = 1;

        List<ShopifyProduct> next;
        while (!(next = getProductsByPage(page)).isEmpty()) {
            newProducts.addAll(next);
            page++;
        }

        for (ShopifyProduct newProduct : newProducts) {
            ShopifyProduct oldProduct = productMap.get(newProduct);
            Map<Variant, ProductStatus> updates = new HashMap<>();

            if (oldProduct == null) {
                productMap.put(newProduct, newProduct);

                for (Variant newVariant : newProduct.getVariants()) {
                    updates.put(newVariant, ProductStatus.IN_STOCK);
                }
            } else {
                List<Variant> newVariants = newProduct.getVariants();
                List<Variant> oldVariants = oldProduct.getVariants();

                for (Variant newVariant : newVariants) {
                    // compare new variants with variants found last update
                    boolean found = false;
                    for (Variant oldVariant : oldVariants) {
                        if (newVariant.equals(oldVariant)) {
                            ZonedDateTime updatedAt = OffsetDateTime.parse(newVariant.getUpdatedAt()).toZonedDateTime();

                            if (newVariant.isAvailable() && oldVariant.getInventoryQuantity() <= 0) {
                                // back in stock
                                updates.put(newVariant, ProductStatus.BACK_IN_STOCK);
                            } else if (!newVariant.isAvailable() && oldVariant.getInventoryQuantity() > 0) {
                                // out of stock
                                updates.put(newVariant, ProductStatus.OUT_OF_STOCK);
                            } else if (currentCycle - lastStockCycle >= stockCycleTime) {
                                // check if product was been updated
                                if (updatedAt.isAfter(lastStockUpdate)) {
                                    if (newVariant.isAvailable() && oldVariant.getInventoryQuantity() > 0) {
                                        // stock change
                                        updates.put(newVariant, ProductStatus.STOCK_CHANGE);

                                        stockUpdated = true;

                                        if (latestUpdatedAt == null || updatedAt.isAfter(latestUpdatedAt))
                                            latestUpdatedAt = updatedAt;
                                    }
                                }
                            }

                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        // new variant
                        oldVariants.add(newVariant);
                        updates.put(newVariant, ProductStatus.IN_STOCK);
                    }
                }

                Set<Variant> _oldVariants = new HashSet<>(oldVariants);
                if (_oldVariants.removeAll(newVariants)) {
                    for (Variant _oldVariant : _oldVariants) {
                        // remove old variants
                        updates.put(_oldVariant, ProductStatus.REMOVED);
                    }
                }
            }

            if (!updates.isEmpty()) {
                // update the product with inventory quantities
                ShopifyProduct updatedProduct = getProductWithInventory(newProduct);
                productMap.put(updatedProduct, updatedProduct);

                // send event to queue
                queue.offer(new ProductEvent(site, updatedProduct, updates));
            }
        }
    }

    private void initalizeCookies() throws Exception {
        final Request request = new Request.Builder()
                .url("https://testmonitors.myshopify.com/password?password=gothat")
                .get()
                .build();

        try(Response response = getClient().newCall(request).execute()) {
            System.out.println(response.code());
        }
    }

    private ProductList getProductsByPage(int page) throws Exception {
        final Request request = new Request.Builder()
                .url("https://" + site + "/products.json?page=" + page)
                .get()
                .build();

        try(Response response = getClient().newCall(request).execute()) {
            final JsonObject jsonObject = JsonReader.readJson(response);

            return JsonReader.GSON.fromJson(jsonObject.getAsJsonArray("products"), ProductList.class);
        }
    }

    private ShopifyProduct getProductWithInventory(ShopifyProduct product) throws Exception {
        final Request request = new Request.Builder()
                .url("https://" + site + "/products/" + product.getHandle() + ".json")
                .get()
                .build();

        try(Response response = getClient().newCall(request).execute()) {
            final JsonObject jsonObject = JsonReader.readJson(response);


            return JsonReader.GSON.fromJson(jsonObject.getAsJsonObject("product"), ShopifyProduct.class);
        }
    }

    private Queue<Url> getSiteQueue(String siteMap) throws Exception {
        final Queue<Url> urlQueue = new ArrayDeque<>();
        final Request request = new Request.Builder()
                .url(siteMap)
                .get()
                .build();

        try(Response response = getClient().newCall(request).execute()) {
            final JSONObject xml = XML.toJSONObject(response.body().string());

            final JSONObject urlset = xml.getJSONObject("urlset");

            final JSONArray urls = urlset.getJSONArray("url");
            for (int i = 0; i < urls.length(); i++) {
                final JSONObject jsonObject = urls.getJSONObject(i);
                final Url url = new Url();

                url.setLoc(jsonObject.getString("loc"));
                if(jsonObject.has("lastmod")) {
                    url.setLastmod(OffsetDateTime.parse(jsonObject.getString("lastmod")).toZonedDateTime());
                }
                url.setChangefreq(jsonObject.getString("changefreq"));
                urlQueue.add(url);
            }
        }

        return urlQueue;
    }

    private String getProductsSiteMap() throws Exception {
        final Request request = new Request.Builder()
                .url(String.format("https://www.%s/sitemap.xml", site))
                .get()
                .build();

        try(Response response = getClient().newCall(request).execute()) {
            final JSONObject xml = XML.toJSONObject(response.body().string());
            final JSONObject siteMaps = xml.getJSONObject("sitemapindex");

            return siteMaps.getJSONArray("sitemap").getJSONObject(0).getString("loc");
        }
    }

    private void notifyListeners(ProductEvent productEvent) {
        if(productEvent == null) {
            System.out.println("finished");
            return;
        }
        System.out.println(productEvent.getSite());
        System.out.println(productEvent.getProduct().getVariants().size());
        System.out.println(productEvent.getProduct().getHandle());
    }
}
