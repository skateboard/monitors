package me.scrim.monitor.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author Brennan
 * @since 7/27/2021
 **/
public class JsonReader {
    public final static Gson GSON = new GsonBuilder().create();

    public static JsonObject readJson(String json) throws IOException {
        return JsonParser.parseString(json).getAsJsonObject();
    }

    public static JsonObject readJson(Response response) throws IOException {
        return readJson(response.body().string());
    }

}
