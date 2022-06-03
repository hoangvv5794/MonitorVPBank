package com.viettel.vtcc.crawler.monitor.repository;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.viettel.vtcc.crawler.monitor.model.ServiceModel;
import com.viettel.vtcc.crawler.monitor.utils.ConfigurationLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Component
@Slf4j
public class ServiceRepo {
    public static HashMap<String, ServiceModel> listService = new LinkedHashMap<>();
    private String FILE_CHECKPOINT = ConfigurationLoader.getInstance().getAsString("file.check", "conf/data_check.json");

    public ServiceRepo() {
        try {
            String json_payload = FileUtils.readFileToString(new File(FILE_CHECKPOINT), "UTF-8");
            JsonArray jsonArray = JsonParser.parseString(json_payload).getAsJsonArray();
            jsonArray.forEach(element -> {
                JsonObject jsonObject = element.getAsJsonObject();
                // get service_id, service_name, service_api , service_method
                String service_id = jsonObject.get("service_id").getAsString();
                String service_name = jsonObject.get("service_name").getAsString();
                String service_api = jsonObject.get("service_api").getAsString();
                String service_method = jsonObject.get("service_method").getAsString();
                HashMap<String, String> list_header = new HashMap<>();
                JsonArray array_header = jsonObject.getAsJsonArray("header_request");
                array_header.forEach(jsonElement -> {
                    JsonObject element_header = jsonElement.getAsJsonObject();
                    String key_header = element_header.get("header_key").getAsString();
                    String value_header = element_header.get("header_value").getAsString();
                    list_header.put(key_header, value_header);
                });
                ServiceModel serviceModel = ServiceModel
                        .builder()
                        .service_id(service_id)
                        .service_name(service_name)
                        .service_api(service_api)
                        .service_method(service_method)
                        .service_headers(list_header)
                        .build();
                ServiceRepo.listService.put(service_id, serviceModel);
            });
            log.info("add {} service to monitor", ServiceRepo.listService.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
