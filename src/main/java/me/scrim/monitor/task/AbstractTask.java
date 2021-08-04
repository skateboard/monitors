package me.scrim.monitor.task;

import lombok.Getter;
import lombok.Setter;
import me.scrim.monitor.request.MyCookieJar;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.OkHttpClient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.UUID;

/**
 * @author Brennan
 * @since 7/27/21
 **/
public abstract class AbstractTask implements Task {
    private final String id, websiteURL;
    private OkHttpClient client;

    @Getter
    public int continuousBans, continuousNoUpdates;

    @Setter
    private boolean started;

    private Headers.Builder headers = new Headers.Builder();

    public AbstractTask(String websiteURL) {
        this.id = UUID.randomUUID().toString();
        this.websiteURL = websiteURL;

        this.client = new OkHttpClient.Builder()
                .cookieJar(new MyCookieJar())
                .hostnameVerifier((s, sslSession) -> true)
                .build();
       // rotateProxy();
    }

    public MyCookieJar getCookieJar() {
        return ((MyCookieJar) client.cookieJar());
    }

    public void rotateProxy() {
        //45.86.67.131:52754:exdpcu:1oJXiHYtjEgj
        final Authenticator proxyAuthenticator = (route, response) -> {
            final String credential = Credentials.basic("exdpcu", "1oJXiHYtjEgj");

            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        final MyCookieJar beforeJar = getCookieJar();
        this.client = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("45.86.67.131", 52754)))
                .proxyAuthenticator(proxyAuthenticator)
                .cookieJar(beforeJar)
                .build();

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
