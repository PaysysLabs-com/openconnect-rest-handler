package com.paysyslabs.bootstrap.rest.model;

import java.util.Map;

public class ServiceBasicResponse {
    private String code;
    private String body;
    private Map<String, Object> data;
    
    public ServiceBasicResponse(String code, String body) {
        super();
        this.code = code;
        this.body = body;
    }
    public ServiceBasicResponse(String code, String body, Map<String, Object> data) {
        super();
        this.code = code;
        this.body = body;
        this.data = data;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public Map<String, Object> getData() {
        return data;
    }
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
	@Override
	public String toString() {
		return "ServiceBasicResponse [code=" + code + ", data=" + data + "]";
	}
    
}
