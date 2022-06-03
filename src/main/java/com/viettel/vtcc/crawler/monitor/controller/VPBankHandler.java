package com.viettel.vtcc.crawler.monitor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.viettel.vtcc.crawler.monitor.model.ResponseModel;
import com.viettel.vtcc.crawler.monitor.model.ServiceModel;
import com.viettel.vtcc.crawler.monitor.repository.ServiceRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
public class VPBankHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(path = "service/add", consumes = "application/json")
    public ResponseModel addService(@RequestBody String json_payload) {
        // add service to monitor
        log.info("add service with info {}", json_payload);
        JsonObject jsonObject = JsonParser.parseString(json_payload).getAsJsonObject();
        try {
            // get service_id, service_name, service_api , service_method
            String service_id = jsonObject.get("service_id").getAsString();
            String service_name = jsonObject.get("service_name").getAsString();
            String service_api = jsonObject.get("service_api").getAsString();
            String service_method = jsonObject.get("service_method").getAsString();
            HashMap<String, String> list_header = new HashMap<>();
            JsonArray array_header = jsonObject.getAsJsonArray("header_request");
            array_header.forEach(jsonElement -> {
                JsonObject element_header = jsonElement.getAsJsonObject();
                String key_header = element_header.get("key").getAsString();
                String value_header = element_header.get("value").getAsString();
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
            return ResponseModel.success_message("create new service " + service_id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseModel.error_message();
    }

    @PostMapping(path = "service/remove", consumes = "application/json")
    public ResponseModel removeService(@RequestBody String json_payload) {
        // remove service to monitor
        log.info("remove service with info {}", json_payload);
        JsonObject jsonObject = JsonParser.parseString(json_payload).getAsJsonObject();
        try {
            String service_id = jsonObject.get("service_id").getAsString();
            if (ServiceRepo.listService.containsKey(service_id)) {
                ServiceRepo.listService.remove(service_id);
                log.info("remove service_id {}", service_id);
                return ResponseModel.success_message("remove service_id " + service_id);
            } else {
                log.info("service_id no exist {}", service_id);
                return ResponseModel.success_message("service_id no exist " + service_id);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseModel.error_message();
    }

    @PostMapping(path = "service/edit", consumes = "application/json")
    public void editService(@RequestBody String json_payload) {
        // edit service to monitor

    }

    @GetMapping(path = "service/list")
    public ResponseModel listAllService() {
        // get all service => return id + name
        log.info("get all service");
        try {
            JsonArray jsonArray = new JsonArray();
            ServiceRepo.listService.forEach((key, value) -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("service_id", key);
                jsonObject.addProperty("service_name", value.getService_name());
                jsonArray.add(jsonObject);
            });
            return ResponseModel.success_message(jsonArray);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseModel.error_message();
    }

    @PostMapping(path = "service/get_info", consumes = "application/json")
    public ResponseModel getInfoService(@RequestBody String json_payload) {
        // get info of service with id
        log.info("get service with info {}", json_payload);
        JsonObject jsonObject = JsonParser.parseString(json_payload).getAsJsonObject();
        try {
            String service_id = jsonObject.get("service_id").getAsString();
            ServiceModel serviceModel = ServiceRepo.listService.get(service_id);
            return ResponseModel.success_message(objectMapper.writeValueAsString(serviceModel));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseModel.error_message();
    }
}
