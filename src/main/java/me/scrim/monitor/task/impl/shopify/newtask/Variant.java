package me.scrim.monitor.task.impl.shopify.newtask;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Brennan
 * @since 8/3/21
 **/
@Getter
@Setter
public class Variant {

    private String id, title, price;
    private boolean available;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("inventory_quantity")
    private int inventoryQuantity;
}
