package me.scrim.monitor.task.impl.shopify;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * @author Brennan
 * @since 8/3/21
 **/
@Getter
@Setter
public class Variant {

    private String id;
    private String title;
    private String sku;
    private boolean available;
    private String price;
    private int position;

    @SerializedName("inventory_quantity")
    private int inventoryQuantity;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;


}
