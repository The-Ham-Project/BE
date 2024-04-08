package com.hanghae.theham.global.dto;

import jakarta.servlet.http.HttpServletRequest;

public class RequestInfo {

    private final HttpServletRequest request;

    public RequestInfo(HttpServletRequest request) {
        this.request = request;
    }

    public String requestURL() {
        return request.getRequestURL().toString();
    }

    public String method() {
        return request.getMethod();
    }

    public String remoteAddress() {
        return request.getRemoteAddr();
    }
}
