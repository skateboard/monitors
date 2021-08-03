package me.scrim.monitor.redis;

import com.google.gson.JsonObject;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import io.sentry.Sentry;
import lombok.Getter;
import me.scrim.monitor.product.Product;
import me.scrim.monitor.task.impl.footsites.Footsites;

/**
 * @author Brennan
 * @since 7/27/21
 **/
@Getter
public class Redis {
    private final StatefulRedisConnection<String, String> sender;
    private final RedisCommands<String, String> information;

    public Redis() {
        final RedisClient redisClient = RedisClient.create("redis://AuthPanelRedis1234@45.63.56.247:6379/0");
        final StatefulRedisPubSubConnection<String, String> subConnection = redisClient.connectPubSub();

        final StatefulRedisConnection<String, String> informationConnection = redisClient.connect();
        this.information = informationConnection.sync();

        final RedisPubSubListener<String, String> listener = new RedisPubSubAdapter<>() {
            @Override
            public void message(String channel, String message) {
                System.out.println(message);
            }
        };
        subConnection.addListener(listener);

        final RedisPubSubCommands<String, String> subscriber = subConnection.sync();
        subscriber.subscribe("monitors");

        this.sender = redisClient.connect();
    }

    public void sendFootsitesUpdate(Footsites foosite, boolean update) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("site", foosite.getSite());
        jsonObject.addProperty("url", "https://" + foosite.getUrl());
        jsonObject.addProperty("update", update);

        this.sender.sync().publish("footsites_information", jsonObject.toString());
    }

    public void sendShopifyCheckpoint(String websiteUrl, boolean goneUp) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "shopify_checkpoint_update");
        jsonObject.addProperty("website_url", websiteUrl);
        jsonObject.addProperty("update", goneUp);

        sendShopifyInformation(jsonObject);
    }

    public void sendShopifyPasswordUpdate(String websiteUrl, boolean goneUp) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "shopify_password_update");
        jsonObject.addProperty("website_url", websiteUrl);
        jsonObject.addProperty("update", goneUp);

        sendShopifyInformation(jsonObject);
    }

    public void sendShopifyDomainUpdate(String newDomain, String oldDomain) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "shopify_domain_update");
        jsonObject.addProperty("new_domain", newDomain);
        jsonObject.addProperty("old_domain", oldDomain);

        sendShopifyInformation(jsonObject);
    }

    private void sendShopifyInformation(JsonObject jsonObject) {
        this.sender.sync().publish("shopify_information", jsonObject.toString());
    }

    public void sendReleases(Product product) {
        this.sender.sync().publish("releases_service", product.toJSON().toString());
    }

    public void sendWebhooks(Product product) {
        this.sender.sync().publish("webhooks_service", product.toJSON().toString());
    }

}
