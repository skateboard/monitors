package me.scrim.monitor.task.impl.shopify.newtask;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Brennan
 * @since 8/3/21
 **/
@Getter
@Setter
public class ShopifyProduct {

    private String id, title, handle;

    @SerializedName("published_at")
    private String publishedAt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    private List<Variant> variants;
    private List<Image> images;
}
