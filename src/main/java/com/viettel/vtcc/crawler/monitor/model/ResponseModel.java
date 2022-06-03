package com.viettel.vtcc.crawler.monitor.model;

import lombok.Data;

@Data
public class ResponseModel {
    private int response_code;
    private String response_message;
    private Object data;

    public static ResponseModel error_message() {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setResponse_code(-1);
        responseModel.setResponse_message("Error message");
        return responseModel;
    }

    public static ResponseModel success_message(Object data) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setResponse_code(1);
        responseModel.setResponse_message("Request success");
        responseModel.setData(data);
        return responseModel;
    }
}
