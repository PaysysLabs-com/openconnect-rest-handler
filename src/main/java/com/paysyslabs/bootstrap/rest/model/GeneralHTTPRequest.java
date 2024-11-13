package com.paysyslabs.bootstrap.rest.model;

import java.util.List;
import java.util.Map;

public class GeneralHTTPRequest {
    private String stan;
    private String type;
    private String url;
    private String method;
    private List<String> headers;
    private String body;
    private Map<String, String> tags;
    private List<GeneralHTTPRequest> performOnSuccess;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public List<GeneralHTTPRequest> getPerformOnSuccess() {
        return performOnSuccess;
    }

    public void setPerformOnSuccess(List<GeneralHTTPRequest> performOnSuccess) {
        this.performOnSuccess = performOnSuccess;
    }

}
