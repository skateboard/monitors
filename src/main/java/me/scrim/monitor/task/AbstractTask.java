package me.scrim.monitor.task;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Headers;
import okhttp3.OkHttpClient;

import java.util.UUID;

/**
 * @author Brennan
 * @since 7/27/21
 **/
public abstract class AbstractTask implements Task {
    private final String id, websiteURL;
    private final OkHttpClient client;

    @Getter
    public int continuousNoRestocks, continuousBans;

    @Setter
    private boolean started;

    private Headers.Builder headers = new Headers.Builder();

    public AbstractTask(String websiteURL) {
        this.id = UUID.randomUUID().toString();
        this.websiteURL = websiteURL;

        this.client = new OkHttpClient.Builder().build();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getWebsiteUrl() {
        return websiteURL;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    public void addHeader(String key, String value) {
        headers.add(key, value);
    }

    public Headers getHeaders() {
        return headers.build();
    }

    public OkHttpClient getClient() {
        return client;
    }
}
