package me.scrim.monitor.task.impl.stockx;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.scrim.monitor.product.Product;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Brennan
 * @since 8/2/21
 **/
@Setter
public class StockXProduct implements Product {
    private String name, stockXName, image, colorWay;
    private int price;

    @Getter
    private final List<Size> sizes = new LinkedList<>();

    @Getter
    private boolean singleItem;

    private int retailPrice;

    public String getRetailPrice() {
        return "$" + retailPrice;
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
        return "$" + price;
    }

    @Override
    public String getColorWay() {
        return "none";
    }

    @Override
    public String getUrl() {
        return "https://stockx.com/" + stockXName;
    }

    @Override
    public String getSite() {
        return "StockX";
    }

    @Override
    public JsonObject toJSON() {
        return Product.super.toJSON();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Size {
        private String size;
        private int price;
    }
}
