package me.scrim.monitor.product;

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

    default String getGoat() {
        return String.format("https://www.goat.com/search?query=%s", URLEncoder.encode(getName() + " " + getColorWay(),
                Charset.defaultCharset()));
    }

    default String getStockX() {
        return String.format("https://stockx.com/search?s=%s", URLEncoder.encode(getName() + " " + getColorWay(),
                Charset.defaultCharset()));
    }



}
