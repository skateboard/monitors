package me.scrim.monitor.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import io.sentry.Sentry;
import lombok.Getter;

/**
 * @author Brennan
 * @since 7/27/21
 **/
@Getter
public class Redis {
    private StatefulRedisConnection<String, String> sender;
    private RedisCommands<String, String> information;

    public Redis() {
        final RedisClient redisClient = RedisClient.create("redis://AuthPanelRedis1234@45.63.56.247:6379/0");
        final StatefulRedisPubSubConnection<String, String> subConnection = redisClient.connectPubSub();

        final StatefulRedisConnection<String, String> informationConnection = redisClient.connect();
        this.information = informationConnection.sync();

        final RedisPubSubListener<String, String> listener = new RedisPubSubAdapter<>() {
            @Override
            public void message(String channel, String message) {
                System.out.println("message");
            }
        };
        subConnection.addListener(listener);

        final RedisPubSubCommands<String, String> subscriber = subConnection.sync();
        subscriber.subscribe("monitor_service");

        this.sender = redisClient.connect();
    }

    public void sendWebhooks(String message) {
        this.sender.sync().publish("webhooks_service", message);
    }
}
