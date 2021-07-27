package me.scrim.monitor.request;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author Brennan
 * @since 7/27/2021
 **/
public class JsonReader {

    public static JsonObject readJson(Response response) throws IOException {
        return JsonParser.parseString(response.body().string()).getAsJsonObject();
    }

}
