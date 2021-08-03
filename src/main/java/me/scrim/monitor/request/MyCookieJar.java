package me.scrim.monitor.request;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Brennan
 * @since 7/29/21
 **/
public class MyCookieJar implements CookieJar {
    private final List<Cookie> cookies = new LinkedList<>();

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        return cookies;
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        cookies.addAll(list);
    }

    public void addCookies(Cookie cookie) {
        cookies.add(cookie);
    }

    public void clearCookies() {
        cookies.clear();
    }

    public List<Cookie> getCookies() {
        return cookies;
    }
}
