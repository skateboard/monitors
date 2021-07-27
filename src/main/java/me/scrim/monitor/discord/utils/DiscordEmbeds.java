package me.scrim.monitor.discord.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.scrim.monitor.task.impl.finishline.product.FinishlineProduct;

import java.awt.*;
import java.time.Instant;

/**
 * @author Brennan
 * @since 7/27/2021
 **/
public class DiscordEmbeds {

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
        webhookMessageBuilder.setUsername("GlitchMonitor");
        webhookMessageBuilder.addEmbeds(embedBuilder.build());

        WebhookClient.withUrl(url).send(webhookMessageBuilder.build());
    }

}
