package me.scrim.monitor.task.impl.shopify;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Brennan
 * @since 8/3/21
 **/
@Getter
@Setter
public class Image {
    private String id;

    @SerializedName("created_at")
    private String createdAt;
    private int position;

    @SerializedName("updated_at")
    private String updatedAt;
    private String src;
    private int width;
    private int height;

}
