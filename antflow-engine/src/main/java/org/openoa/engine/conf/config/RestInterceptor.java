package org.openoa.engine.conf.config;

import lombok.extern.slf4j.Slf4j;
import org.openoa.base.exception.AFBizException;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 *
 * @className: RestInterceptor
 * @author: Tyler Zhou
 **/
@Slf4j
@Component
public class RestInterceptor implements ClientHttpRequestInterceptor {



    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        StopWatch watch = new StopWatch();
        watch.start();
        ClientHttpResponse execute = clientHttpRequestExecution.execute(httpRequest, bytes);
        watch.stop();
        long totalTimeMillis = watch.getTotalTimeMillis();
        intermediateProcessRequest(httpRequest, bytes);
        intermediateProcessResponse(execute, totalTimeMillis);
        return execute;
    }

    private String getRequestBody(byte[] body) throws UnsupportedEncodingException {
        if (body != null && body.length > 0) {
            return (new String(body, StandardCharsets.UTF_8));
        } else {
            return null;
        }
    }

    private void intermediateProcessRequest(HttpRequest request, byte[] body) throws IOException {
        log.info("request method : " + request.getMethod() + " request body : " + getRequestBody(body));
    }

    private void intermediateProcessResponse(ClientHttpResponse response, long totalMils) throws IOException {
        String body = getBodyString(response);
        HttpStatus statusCode = response.getStatusCode();
        double totalSeconds = totalMils / 1000d;
        log.info("response status code:{} response status text:{},请求耗时:{}, response body :{}",
                response.getStatusCode(), response.getStatusText(),totalSeconds, body);

        if(HttpStatus.TOO_MANY_REQUESTS==statusCode){
            throw new AFBizException("请求限流");
        }
    }

    private String getBodyString(ClientHttpResponse response) throws IOException {
        try {
            if (response != null && response.getBody() != null) // &&
            {
                StringBuilder inputStringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
                String line = bufferedReader.readLine();
                while (line != null) {
                    inputStringBuilder.append(line);
                    inputStringBuilder.append('\n');
                    line = bufferedReader.readLine();
                }
                return inputStringBuilder.toString();
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
