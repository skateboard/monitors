package me.scrim.monitor.task.impl.walmart.product;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.scrim.monitor.product.Product;

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
