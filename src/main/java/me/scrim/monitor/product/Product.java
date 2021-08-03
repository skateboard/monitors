package me.scrim.monitor.product;

import com.google.gson.JsonObject;

import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * @author Brennan
 * @since 7/27/21
 **/
public interface Product {

    String getName();

    String getImage();

    String getPrice();

    String getColorWay();

    String getUrl();

    String getSite();

    default JsonObject toJSON() {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("site", getSite());
        jsonObject.addProperty("name", getName());
        jsonObject.addProperty("image", getImage());
        jsonObject.addProperty("price", getPrice());
        jsonObject.addProperty("color_way", getColorWay());
        jsonObject.addProperty("url", getUrl());

        jsonObject.addProperty("stockx_link", getStockX());
        jsonObject.addProperty("goat_link", getGoat());

        return jsonObject;
    }

    default String getGoat() {
        return String.format("https://www.goat.com/search?query=%s", URLEncoder.encode(getName() + " " + getColorWay(),
                Charset.defaultCharset()));
    }

    default String getStockX() {
        return String.format("https://stockx.com/search?s=%s", URLEncoder.encode(getName() + " " + getColorWay(),
                Charset.defaultCharset()));
    }



}
