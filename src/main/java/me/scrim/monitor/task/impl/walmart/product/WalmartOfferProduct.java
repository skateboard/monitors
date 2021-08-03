package me.scrim.monitor.task.impl.walmart.product;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.scrim.monitor.product.Product;

/**
 * @author Brennan
 * @since 8/1/21
 **/
@Getter
public class WalmartOfferProduct implements Product {
    private WalmartProduct parent;
    private String price;

    public WalmartOfferProduct(WalmartProduct parent, String price) {
        this.parent = parent;
        this.price = price;
    }

    @Override
    public String getName() {
        return parent.getProductName();
    }

    @Override
    public String getImage() {
        return parent.getImage();
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
        return String.format("https://www.walmart.com/ip/~/%s", parent.getProductID());
    }

    @Override
    public String getSite() {
        return "Walmart";
    }

    @Override
    public JsonObject toJSON() {
        return Product.super.toJSON();
    }
}
