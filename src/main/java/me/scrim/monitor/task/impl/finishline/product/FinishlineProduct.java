package me.scrim.monitor.task.impl.finishline.product;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import me.scrim.monitor.product.Product;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Brennan
 * @since 7/27/2021
 **/
public class FinishlineProduct implements Product {
    private String productID, productName, productImage, price, color, productUrl;

    @Getter
    private final List<Size> sizes = new LinkedList<>();

    public int getTotalStock() {
        int totalStock = 0;

        for(Size size : sizes) {
            totalStock += size.getStockAmount();
        }

        return totalStock;
    }

    @Override
    public JsonObject toJSON() {
        final JsonObject productObject = Product.super.toJSON();

        productObject.addProperty("product_id", getProductID());
        productObject.addProperty("total_stock", getTotalStock());

        final JsonArray jsonArray = new JsonArray();
        for(Size size : getSizes()) {
            jsonArray.add(size.toJSON());
        }
        productObject.add("sizes", jsonArray);

        return productObject;
    }

    @Override
    public String getSite() {
        return "Finishline";
    }

    public String getProductID() {
        return productID;
    }

    @Override
    public String getName() {
        return productName;
    }

    @Override
    public String getImage() {
        return productImage;
    }

    @Override
    public String getPrice() {
        return price;
    }

    @Override
    public String getColorWay() {
        return color;
    }

    @Override
    public String getUrl() {
        return productUrl;
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

    public void setPrice(String price) {
        if(price.length() == 5) {
            String startPrice = price.substring(0, 3);
            price = price.substring(3);

            price = startPrice + "." + price;
        } else if(price.length() == 4) {
            String startPrice = price.substring(0, 2);
            price = price.substring(2);

            price = startPrice + "." + price;
        } else if(price.length() == 3) {
            String startPrice = price.substring(0, 1);
            price = price.substring(1);

            price = startPrice + "." + price;
        }

        this.price = price;
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

        public JsonObject toJSON() {
            final JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("sku", getSku());
            jsonObject.addProperty("size", getSize());
            jsonObject.addProperty("stock_amount", getStockAmount());

            return jsonObject;
        }
    }
}
