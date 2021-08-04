package me.scrim.monitor.task.impl.retail.amazon;

import lombok.Getter;
import lombok.Setter;
import me.scrim.monitor.product.Product;

/**
 * @author Brennan
 * @since 8/4/21
 **/
@Getter
@Setter
public class AmazonProduct implements Product {
    private String asin, name, image, price, offerID;

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
        return String.format("https://www.amazon.com/~/dp/%s", asin);
    }

    @Override
    public String getSite() {
        return "Amazon";
    }
}
