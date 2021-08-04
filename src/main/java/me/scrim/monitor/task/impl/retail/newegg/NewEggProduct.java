package me.scrim.monitor.task.impl.retail.newegg;

import lombok.Getter;
import lombok.Setter;
import me.scrim.monitor.product.Product;

/**
 * @author Brennan
 * @since 8/4/21
 **/
@Getter
public class NewEggProduct implements Product {
    private String name, sku, image, price, url;

    @Setter
    private boolean available;

    public NewEggProduct(String name, String sku, String image, String price, String url, boolean available) {
        this.name = name;
        this.sku = sku;
        this.image = image;
        this.price = price;
        this.url = url;
        this.available = available;
    }

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
        return "";
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getSite() {
        return "NewEgg";
    }
}
