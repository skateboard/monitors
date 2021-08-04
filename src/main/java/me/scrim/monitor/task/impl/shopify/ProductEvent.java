package me.scrim.monitor.task.impl.shopify;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brennan
 * @since 8/3/21
 **/
@Getter
public class ProductEvent {
    @Setter
    private String site;

    @Setter
    private ShopifyProduct product;

    private final Map<Variant, ProductStatus> updates;

    public ProductEvent(String site, ShopifyProduct product, Map<Variant, ProductStatus> updates) {
        this.site = site;
        this.product = product;
        this.updates = updates;
    }

    public ProductEvent(String site, ShopifyProduct product) {
        this(site, product, new HashMap<>());
    }
}
