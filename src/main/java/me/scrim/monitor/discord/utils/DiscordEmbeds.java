package me.scrim.monitor.discord.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.scrim.monitor.task.impl.finishline.product.FinishlineProduct;
import me.scrim.monitor.task.impl.finishline.product.FinishlineReleaseProduct;
import me.scrim.monitor.task.impl.footsites.FootsiteProduct;
import me.scrim.monitor.task.impl.footsites.Footsites;
import me.scrim.monitor.task.impl.stockx.StockXProduct;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.TimeZone;

/**
 * @author Brennan
 * @since 7/27/2021
 **/
public class DiscordEmbeds {
    private final static SimpleDateFormat format = new SimpleDateFormat("EEEE dd, MMMM, yyyy");

    static {
        format.setTimeZone(TimeZone.getTimeZone("GMT-4"));
    }

    public static void sendFootsiteProduct(FootsiteProduct product, String webHookUrl) {

    }

    public static void sendStockXSize(StockXProduct product, StockXProduct.Size size, StockXProduct.Size oldSize, String webHookUrl) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("StockX Price Alert", ""));
        embedBuilder.setColor(new Color(0x348092).getRGB());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(new WebhookEmbed.EmbedFooter("StockX Price Monitor Powered By Glitch", ""));
        embedBuilder.setThumbnailUrl(product.getImage());

        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Product", product.getName()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Size", size.getSize()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false, "Retail Price", product.getRetailPrice()));

        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "New Lowest Ask", "$" + size.getPrice()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Old Lowest Ask", "$" + oldSize.getPrice()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false, "Link", String.format("[%s](%s)",
                "Link", product.getUrl())));

        final WebhookMessageBuilder webhookMessageBuilder = new WebhookMessageBuilder();
        webhookMessageBuilder.setUsername("GlitchMonitors: StockX");
        webhookMessageBuilder.addEmbeds(embedBuilder.build());

        WebhookClient.withUrl(webHookUrl).send(webhookMessageBuilder.build());
    }

    public static void sendStockX(StockXProduct newProduct, StockXProduct oldProduct, String webHookUrl) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("StockX Price Alert", ""));
        embedBuilder.setColor(new Color(0x348092).getRGB());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(new WebhookEmbed.EmbedFooter("StockX Price Monitor Powered By Glitch", ""));
        embedBuilder.setThumbnailUrl(newProduct.getImage());

        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Product", newProduct.getName()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Retail Price", newProduct.getRetailPrice()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false, "New Lowest Ask", newProduct.getPrice()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false, "Old Lowest Ask", oldProduct.getPrice()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false, "Link", String.format("[%s](%s)",
                "Link", newProduct.getUrl())));

        final WebhookMessageBuilder webhookMessageBuilder = new WebhookMessageBuilder();
        webhookMessageBuilder.setUsername("GlitchMonitors: StockX");
        webhookMessageBuilder.addEmbeds(embedBuilder.build());

        WebhookClient.withUrl(webHookUrl).send(webhookMessageBuilder.build());
    }

    public static void sendFootsitesQueueUp(Footsites footsites, boolean goneOnline, String webHookUrl) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("Footsites Queue", ""));
        embedBuilder.setColor(new Color(0x348092).getRGB());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(new WebhookEmbed.EmbedFooter("Footsites Queue Monitor Powered By Glitch", ""));
        embedBuilder.setDescription(String.format("[%s](%s) %s", footsites.getSite(), "https://" + footsites.getUrl(),
                goneOnline ? "queue has gone online" : "queue has gone offline"));

        final WebhookMessageBuilder webhookMessageBuilder = new WebhookMessageBuilder();
        webhookMessageBuilder.setUsername("GlitchMonitors: Footsites Queue");
        webhookMessageBuilder.addEmbeds(embedBuilder.build());

        WebhookClient.withUrl(webHookUrl).send(webhookMessageBuilder.build());

    }

    public static void sendShopifyInfo(String newUrl, String oldUrl, String webHookUrl) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("Shopify Info Change", ""));
        embedBuilder.setColor(new Color(0x348092).getRGB());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(new WebhookEmbed.EmbedFooter("Shopify Info Monitor Powered By Glitch", ""));

        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "New Domain", newUrl));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false, "Old Domain", oldUrl));

        final WebhookMessageBuilder webhookMessageBuilder = new WebhookMessageBuilder();
        webhookMessageBuilder.setUsername("GlitchMonitors: Shopify Info");
        webhookMessageBuilder.addEmbeds(embedBuilder.build());

        WebhookClient.withUrl(webHookUrl).send(webhookMessageBuilder.build());
    }

    public static void sendShopifyCheckpoint(String url, boolean goneOnline, String webHookUrl) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("Shopify Password", ""));
        embedBuilder.setColor(new Color(0x348092).getRGB());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(new WebhookEmbed.EmbedFooter("Shopify Password Monitor Powered By Glitch", ""));
        embedBuilder.setDescription(String.format("[%s](%s) %s", url, url,
                goneOnline ? "recapthca checkpoint has gone online" : "recapthca checkpoint has gone offline"));

        final WebhookMessageBuilder webhookMessageBuilder = new WebhookMessageBuilder();
        webhookMessageBuilder.setUsername("GlitchMonitors: Shopify Passwords");
        webhookMessageBuilder.addEmbeds(embedBuilder.build());

        WebhookClient.withUrl(webHookUrl).send(webhookMessageBuilder.build());
    }

    public static void sendShopifyPassword(String url, boolean goneOnline, String webHookUrl) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("Shopify Password", ""));
        embedBuilder.setColor(new Color(0x348092).getRGB());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(new WebhookEmbed.EmbedFooter("Shopify Password Monitor Powered By Glitch", ""));
        embedBuilder.setDescription(String.format("[%s](%s) %s", url, url,
                goneOnline ? "password page has gone online" : "password page has gone offline"));

        final WebhookMessageBuilder webhookMessageBuilder = new WebhookMessageBuilder();
        webhookMessageBuilder.setUsername("GlitchMonitors: Shopify Passwords");
        webhookMessageBuilder.addEmbeds(embedBuilder.build());

        WebhookClient.withUrl(webHookUrl).send(webhookMessageBuilder.build());
    }

    public static void sendRelease(FinishlineReleaseProduct product, String url) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("Product Release Coming up", ""));
        embedBuilder.setColor(new Color(0x348092).getRGB());
        embedBuilder.setThumbnailUrl(product.getImage());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(new WebhookEmbed.EmbedFooter("Finishline Releases Monitor Powered By Glitch", ""));

        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Product", String.format("[%s](%s)", product.getName(), product.getUrl())));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Product ID", product.getProductID()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Color Way", product.getColorWay()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Brand", product.getBrand()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Price", "$" + product.getPrice()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false, "Release Date", format.format(product.getReleaseDate())));

        embedBuilder.addField(new WebhookEmbed.EmbedField(false, "Useful Links",
                "[StockX](" +product.getStockX() + ") | [Goat](" + product.getGoat() + ")"));

        final WebhookMessageBuilder webhookMessageBuilder = new WebhookMessageBuilder();
        webhookMessageBuilder.setUsername("GlitchMonitors: Finishline Releases");
        webhookMessageBuilder.addEmbeds(embedBuilder.build());

        WebhookClient.withUrl(url).send(webhookMessageBuilder.build());
    }

    public static void sendEmbeds(FinishlineProduct product, String url) {
        final WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();

        embedBuilder.setTitle(new WebhookEmbed.EmbedTitle(product.getName(), product.getUrl()));
        embedBuilder.setColor(new Color(0x348092).getRGB());
        embedBuilder.setThumbnailUrl(product.getImage());
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setFooter(new WebhookEmbed.EmbedFooter("Finishline Monitor Powered By Glitch", ""));

        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Product ID", product.getProductID()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Price", "$" + product.getPrice()));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Color", product.getColorWay()));

        embedBuilder.addField(new WebhookEmbed.EmbedField(false, "Total Loaded Stock", String.valueOf(product.getTotalStock())));
        for(FinishlineProduct.Size size : product.getSizes()) {
            embedBuilder.addField(new WebhookEmbed.EmbedField(true, "Size [" + size.getSize() + "]",
                    size.getSku() + " | " + size.getStockAmount()));
        }

        embedBuilder.addField(new WebhookEmbed.EmbedField(false, "Useful Links",
                "[StockX](" +product.getStockX() + ") | [Goat](" + product.getGoat() + ")"));

        final WebhookMessageBuilder webhookMessageBuilder = new WebhookMessageBuilder();
        webhookMessageBuilder.setUsername("GlitchMonitors: Finishline");
        webhookMessageBuilder.addEmbeds(embedBuilder.build());

        WebhookClient.withUrl(url).send(webhookMessageBuilder.build());
    }

}
