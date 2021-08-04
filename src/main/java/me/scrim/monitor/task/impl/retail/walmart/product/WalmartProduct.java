package me.scrim.monitor.task.impl.retail.walmart.product;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Brennan
 * @since 8/1/21
 **/
@Getter
@Setter
public class WalmartProduct {
    private String productID, productName, image;

    private final List<Offer> offers = new LinkedList<>();

    @Getter
    public static class Offer {
        private String price;

        public Offer(String price) {
            this.price = price;
        }
    }

}
