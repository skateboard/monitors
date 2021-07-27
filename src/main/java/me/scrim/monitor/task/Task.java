package me.scrim.monitor.task;

import lombok.Getter;
import okhttp3.Headers;
import okhttp3.OkHttpClient;

import java.util.UUID;

/**
 * @author Brennan
 * @since 7/26/2021
 **/
@Getter
public abstract class Task {
    private final String id, websiteURL;
    private final OkHttpClient client;

    private Headers.Builder headers = new Headers.Builder();

    public Task(String websiteURL) {
        this.id = UUID.randomUUID().toString();
        this.websiteURL = websiteURL;

        this.client = new OkHttpClient.Builder().build();
    }

    public abstract void run();

    public void addHeader(String key, String value) {
        headers.add(key, value);
    }

    public Headers getHeaders() {
        return headers.build();
    }
}
