package me.scrim.monitor.task.impl.retail.bestbuy;

import lombok.Getter;
import me.scrim.monitor.product.Product;

/**
 * @author Brennan
 * @since 8/4/21
 **/
@Getter
public class BestBuyProduct implements Product {
    private final String name, sku, image, price, url;
    private boolean available;

    public BestBuyProduct(String name, String sku, String image, String price, String url, boolean available) {
        this.name = name;
        this.available = available;
        this.sku = sku;
        this.image = image;
        this.price = price;
        this.url = url;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getImage() {
        return null;
    }

    @Override
    public String getPrice() {
        return null;
    }

    @Override
    public String getColorWay() {
        return "";
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getSite() {
        return "BestBuy";
    }
}
