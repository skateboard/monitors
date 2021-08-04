package me.scrim.monitor.task.impl.shopify.newtask;

import com.google.gson.JsonObject;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.AbstractTask;
import me.scrim.monitor.task.impl.shopify.ProductList;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Iterator;

/**
 * @author Brennan
 * @since 8/3/21
 **/
public class ShopifyTask extends AbstractTask {
    private ShopifyProductList previousProducts, currentProducts;

    public ShopifyTask(String websiteURL) {
        super(websiteURL);

        try {
            initalizeCookies();

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.previousProducts = getProductList();
        if(this.previousProducts == null) {
            setStarted(false);
            System.out.println("Failed");
            return;
        }
        setStarted(true);
    }

    @Override
    public void run() {
        while (isStarted()) {
            this.currentProducts = getProductList();

            var _currentProducts = currentProducts;

            ShopifyProduct matchedProduct;

            for (int i = 0; i < previousProducts.size(); i++) {
                final ShopifyProduct previousProduct = previousProducts.get(i);
                matchedProduct = this.getProduct(previousProduct.getId(), currentProducts);

                if(!previousProduct.getUpdatedAt().equals(matchedProduct.getUpdatedAt()))
                    checkForRestocks(matchedProduct, previousProduct);
            }

            for (int i = 0; i < previousProducts.size(); i++) {
                final ShopifyProduct previousProduct = previousProducts.get(i);

                _currentProducts.removeIf(product -> product.getId().equals(previousProduct.getId()));
            }

            System.out.println("Current Prods: "+ _currentProducts.size());
            if(_currentProducts.size() > 0) {
                for(ShopifyProduct shopifyProduct : _currentProducts) {
                    final ShopifyProduct withInventory = getProduct(shopifyProduct);
                    final ShopifyDetails shopifyDetails = new ShopifyDetails(getWebsiteUrl(),
                            withInventory,
                            withInventory.getVariants());

                    notifyListener(shopifyDetails);
                }
            }

            this.previousProducts = currentProducts;
            this.currentProducts = null;
        }
    }

    private void checkForRestocks(ShopifyProduct newProduct, ShopifyProduct oldProduct) {
        System.out.println("restocked");
        final ShopifyProduct product = getProduct(newProduct);

        if(product == null) {
            System.out.println("FAILED");
            return;
        }
        final ShopifyDetails restockDetails = new ShopifyDetails(getWebsiteUrl(), product);

        for(Variant variant : product.getVariants()) {
            if(variant.isAvailable()) {
                for(Variant oldVariant : oldProduct.getVariants()) {
                    if(!oldVariant.getId().equals(variant.getId()))
                        restockDetails.getVariants().add(variant);
                }
            }
        }

        if(restockDetails.getVariants().size() > 0) {
            notifyListener(restockDetails);
        }
    }

    private void notifyListener(ShopifyDetails details) {
        System.out.println(details.getSite());
        System.out.println(details.getType());
        System.out.println(details.getProduct().getHandle());
    }

    private ShopifyProduct getProduct(ShopifyProduct product) {
        try {
            final Request request = new Request.Builder()
                    .url(String.format("%s/products/%s.json", getWebsiteUrl(), product.getHandle()))
                    .build();

            try(Response response = getClient().newCall(request).execute()) {
                final JsonObject jsonObject = JsonReader.readJson(response);

                return JsonReader.GSON.fromJson(jsonObject.getAsJsonObject("product"), ShopifyProduct.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private ShopifyProduct getProduct(String currentId, ShopifyProductList products) {
        for (final ShopifyProduct shopifyProduct : products) {
            if (shopifyProduct.getId().equalsIgnoreCase(currentId))
                return shopifyProduct;
        }

        return null;
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


    private ShopifyProductList getProductList() {
        try {
            final Request request = new Request.Builder()
                    .url(String.format("%s/products.json", getWebsiteUrl()))
                    .get()
                    .build();

            try(Response response = getClient().newCall(request).execute()) {
                final JsonObject jsonObject = JsonReader.readJson(response);

                return JsonReader.GSON.fromJson(jsonObject.getAsJsonArray("products"), ShopifyProductList.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
