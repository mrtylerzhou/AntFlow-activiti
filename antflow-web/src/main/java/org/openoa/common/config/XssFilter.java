package org.openoa.common.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: jwz
 * @Date: 2024/9/9 9:13
 * @Description:
 * @Version: 1.0.0
 */
public class XssFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;

        filterChain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), res);
    }
}
