package me.scrim.monitor.task.impl.shopify;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.scrim.monitor.product.Product;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Brennan
 * @since 8/2/21
 **/
@Getter
@Setter
public class ShopifyProduct {
    private String id;
    private String title;
    private String handle;

    @SerializedName("published_at")
    private String publishedAt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;
    private String vendor;

    @SerializedName("variants")
    private List<Variant> variants;

    @SerializedName("images")
    private List<Image> images;

}
