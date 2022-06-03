package com.viettel.vtcc.crawler.monitor.service;

import com.viettel.vtcc.crawler.monitor.model.ServiceModel;
import com.viettel.vtcc.crawler.monitor.utils.ConfigurationLoader;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ServiceAlert {
    private final int TIME_OUT = ConfigurationLoader.getInstance().getAsInteger("timeout.request", 10);
    private static String BOT_TOKEN = ConfigurationLoader.getInstance().getAsString("bot.token", "");
    private static String GROUP_ID = ConfigurationLoader.getInstance().getAsString("group.id", "");
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(180, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .build();
    private static final String URL_SEND_MSG = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage?chat_id=" + GROUP_ID + "&text=";

    public void sendAlert(ServiceModel serviceModel) {
        try {
            String data = "Service:" + serviceModel.getService_name() + " timeout is over " + TIME_OUT + "s";
            Request getRequest = new Request.Builder()
                    .url(URL_SEND_MSG + data)
                    .build();
            client.newCall(getRequest).execute();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
