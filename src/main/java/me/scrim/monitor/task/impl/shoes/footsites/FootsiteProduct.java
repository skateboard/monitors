package me.scrim.monitor.task.impl.shoes.footsites;

import lombok.Getter;
import lombok.Setter;
import me.scrim.monitor.product.Product;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Brennan
 * @since 8/2/21
 **/
public class FootsiteProduct implements Product {
    @Getter
    private Footsites footsite;

    @Setter
    private String name, price;

    @Getter
    private String productID;

    @Setter
    private String size, image, style;

    @Getter
    private final List<Size> sizes = new LinkedList<>();


    public FootsiteProduct(Footsites footsite, String name, String productID) {
        this.footsite = footsite;
        this.name = name;
        this.productID = productID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImage() {
        return String.format("https://images.%s/pi/%s/large/%s.jpeg", footsite.getSite(), productID, productID);
    }

    @Override
    public String getPrice() {
        return price;
    }

    @Override
    public String getColorWay() {
        return null;
    }

    @Override
    public String getUrl() {
        return String.format("https://%s/product/~/%s.html", footsite.getSite(), productID);
    }

    @Override
    public String getSite() {
        return footsite.getSite();
    }

    @Getter
    @Setter
    public static class Size {
        private String size, image, style;


    }
}
