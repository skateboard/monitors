package me.scrim.monitor.task.impl.shopify;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * @author Brennan
 * @since 8/3/21
 **/
@Getter
@Setter
public class Url {
    private String loc;
    private ZonedDateTime lastmod;
    private String changefreq;


}
