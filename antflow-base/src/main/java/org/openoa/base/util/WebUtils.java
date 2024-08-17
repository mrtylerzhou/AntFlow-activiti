package org.openoa.base.util;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebUtils{
    public static HttpServletResponse toHttp(ServletResponse response) {
        return (HttpServletResponse)response;
    }
    public static HttpServletRequest toHttp(ServletRequest request) {
        return (HttpServletRequest)request;
    }
}