package me.scrim.monitor.task.impl.finishline.product;

import lombok.Getter;
import lombok.Setter;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Brennan
 * @since 7/27/2021
 **/
@Getter
public class FinishlineProduct {
    private String productID, productName, productImage, price, color, productUrl;

    private final List<Size> sizes = new LinkedList<>();

    public String getStockX() {
        return String.format("https://stockx.com/search?s=%s", URLEncoder.encode(getProductName() + " " + getColor(), Charset.defaultCharset()));
    }

    public void setProductUrl(String styleID, String colorID) {
        this.productUrl = String.format("https://www.finishline.com/store/product/glitch-monitors/%s?styleId=%s&colorId=%s",
                getProductID(), styleID, colorID);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setImage(String image) {
        this.productImage = image;
    }

    public void setBaseInformation(String id, String name) {
        this.productID = id;
        this.productName = name;
    }

    public void setPrice(int price) {
        this.price = NumberFormat.getCurrencyInstance().format(price);

    }

    public void addSize(Size size) {
        this.sizes.add(size);
    }

    @Getter
    @Setter
    public static class Size {
        private String sku, size;
        private int stockAmount;

        public Size(String sku, String size, int stockAmount) {
            this.sku = sku;
            this.size = size;
            this.stockAmount = stockAmount;
        }
    }
}
