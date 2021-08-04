package me.scrim.monitor.task.impl.shoes.finishline.product;

import me.scrim.monitor.product.Product;

import java.util.Date;

/**
 * @author Brennan
 * @since 7/27/21
 **/
public class FinishlineReleaseProduct implements Product {
    private String productID, name, image, price, colorWay, url, brand;
    private Date releaseDate;

    public void setInformation(String productID, String name, String image, String colorWay, String brand, long releaseDate) {
        this.productID = productID;
        this.name = name;
        this.image = image;
        this.colorWay = colorWay;
        this.brand = brand;

        this.releaseDate = new Date(releaseDate * 1000L);
    }

    public String getBrand() {
        return brand;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setProductUrl(String styleID, String colorID) {
        this.url = String
                .format("https://www.finishline.com/store/product/glitch-monitors/%s?styleId=%s&colorId=%s",
                getProductID(), styleID, colorID);
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
        return name;
    }

    @Override
    public String getImage() {
        return image;
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
}
