package org.openoa.common.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * @author xuhao
 * @date 2020/5/22
 * @description xss的request包装类
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public String _body;
    private static final String POST_METHOD = "POST";

    public XssHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        StringBuffer sBuffer = new StringBuffer();
        boolean multipartContent = ServletFileUpload.isMultipartContent(request);
        if (!multipartContent) {

            BufferedReader bufferedReader = request.getReader();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sBuffer.append(line);
            }
            _body = sBuffer.toString();
        } else {
            _body = "";
        }


    }


    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(_body.getBytes());
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public String getHeader(String name) {
        return clearXss(super.getHeader(name));
    }

    @Override
    public String getParameter(String name) {
        return clearXss(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (Objects.nonNull(values)) {
            String[] newValues = new String[values.length];

            for (int i = 0; i < values.length; i++) {
                newValues[i] = clearXss(values[i]);
            }
            return newValues;
        }
        return super.getParameterValues(name);
    }


    /**
     * 功能描述: 处理字符转义
     *
     * @param value
     * @return java.lang.String
     * @author xuhao
     */
    private String clearXss(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        return HtmlUtils.htmlEscape(value);
    }
}
