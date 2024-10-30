package com.mercadolibre.sectest.suitetab;

import java.util.Date;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;

public class Issue {

    private Date date;
    private String type;
    private String url;
    private String body;
    private String message;
    private HttpRequest request;
    private HttpResponse response;
    
    public Issue(Date date, String type, String url, String body, String message, HttpRequest request, HttpResponse response) {
        this.date = date;
        this.type = type;
        this.url = url;
        this.body = body;
        this.message = message;
        this.request = request;
        this.response = response;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
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
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
 
    public HttpRequest getRequest() {
        return request;
    }
    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public HttpResponse getResponse() {
        return response;
    }
    public void setResponse(HttpResponse response) {
        this.response = response;
    }
}
