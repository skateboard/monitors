package me.scrim.monitor.task.impl.shopify;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.scrim.monitor.product.Product;

import java.time.ZonedDateTime;

/**
 * @author Brennan
 * @since 8/2/21
 **/
@Getter
@Setter
public class ShopifyProduct implements Product {
    private String name, image, price, colorWay, url, site;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public String getPrice() {
        return price;
    }

    @Override
    public String getColorWay() {
        return colorWay;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getSite() {
        return site;
    }

    @Override
    public JsonObject toJSON() {
        return Product.super.toJSON();
    }

    @Getter
    @Setter
    public static class Variant {
        private String id;
        private String title;
        private String sku;
        private boolean available;
        private String price;
        private int position;
        private int inventoryQuantity;
        private String productId;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
    }
}
