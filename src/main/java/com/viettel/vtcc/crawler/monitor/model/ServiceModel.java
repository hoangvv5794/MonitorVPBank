package com.viettel.vtcc.crawler.monitor.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
public class ServiceModel {
    private String service_id;
    private String service_name;
    private String service_api;
    private String service_method;
    private String service_body;
    private HashMap<String, String> service_headers;
}
