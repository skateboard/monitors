package me.scrim.monitor.task.impl.hibbett;

import com.google.gson.JsonObject;
import me.scrim.monitor.request.JsonReader;
import me.scrim.monitor.task.AbstractTask;
import okhttp3.Cookie;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Brennan
 * @since 7/28/21
 **/
public class HibbettTask extends AbstractTask {

    private JsonObject holdCapData;
    private String deviceID;

    public HibbettTask(String sku) {
        super("https://hibbett-mobileapi.prolific.io/ecommerce/products/" + sku);

        addHeader("User-Agent", "Hibbett/3.9.0 (com.hibbett.hibbett-sports; build:4558; iOS 12.2.0) Alamofire/4.5.1");
        addHeader("accept-language", "en-US;q=1.0");
        addHeader("accept-encoding", "gzip;q=1.0, compress;q=0.5");
        addHeader("content-type", "application/json; charset=utf-8");
        addHeader("accept", "*/*");
        addHeader("version", "3.9.0");
        addHeader("platform", "IOS");
        addHeader("x-api-key", "0PutYAUfHz8ozEeqTFlF014LMJji6Rsc8bpRBGB0");

    }

    @Override
    public void run() {
        resolveProduct();
    }

    private void resolveProduct() {
        try {
             final Request request = new Request.Builder()
                     .url(getWebsiteUrl())
                     .headers(getHeaders())
                     .get()
                     .build();

             try(Response response = getClient().newCall(request).execute()) {
                 //final JsonObject jsonObject = JsonReader.readJson(response);

                 System.out.println(response.code());
                 System.out.println(response.body().string());
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
