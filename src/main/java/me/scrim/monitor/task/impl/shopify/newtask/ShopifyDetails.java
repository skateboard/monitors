package me.scrim.monitor.task.impl.shopify.newtask;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Brennan
 * @since 8/3/21
 **/
@Getter
@Setter
public class ShopifyDetails {
    private String site;
    private String type;
    private ShopifyProduct product;
    private List<Variant> variants;

    public ShopifyDetails(String site, ShopifyProduct product, List<Variant> variants) {
        this.site = site;
        this.type = "new_product";
        this.product = product;
        this.variants = variants;
    }

    public ShopifyDetails(String site, ShopifyProduct product) {
        this.site = site;
        this.type = "restock";
        this.product = product;
        this.variants = new ArrayList<>();
    }
}
