package com.viettel.vtcc.crawler.monitor.service;

import com.viettel.vtcc.crawler.monitor.model.ServiceModel;
import com.viettel.vtcc.crawler.monitor.repository.ServiceRepo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ServiceCheckpoint {
    @Autowired
    ServiceAlert serviceAlert;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    public ServiceCheckpoint() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            try {
                ServiceRepo.listService.keySet().parallelStream().forEach(service -> {
                    ServiceModel serviceModel = ServiceRepo.listService.get(service);
                    boolean is_checkpoint = isCheckpoint(serviceModel);
                    if (is_checkpoint) {
                        // send alert telegram
                        serviceAlert.sendAlert(serviceModel);
                    } else {
                        log.info("service is normal {}", service);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    private boolean isCheckpoint(ServiceModel serviceModel) {
        boolean isCheckpoint = false;
        try {
            Request getRequest;
            Headers headers = Headers.of(serviceModel.getService_headers());
            if (serviceModel.getService_method().equalsIgnoreCase("POST")) {
                RequestBody body = RequestBody.create(MediaType.get("application/x-www-form-urlencoded"), serviceModel.getService_body());
                getRequest = new Request.Builder()
                        .url(serviceModel.getService_api())
                        .headers(headers)
                        .post(body)
                        .build();
            } else {
                getRequest = new Request.Builder()
                        .url(serviceModel.getService_api())
                        .headers(headers)
                        .build();
            }
            isCheckpoint = client.newCall(getRequest).execute().code() != 200;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return isCheckpoint;
    }
}
