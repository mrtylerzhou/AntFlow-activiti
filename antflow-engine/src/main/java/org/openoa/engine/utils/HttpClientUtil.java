package org.openoa.engine.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * 请不要在应用未启动之前使用此此中静态方法
 * @className: HttpClientUtil
 * @author: Tyler Zhou
 **/
@Component
@Slf4j
public class HttpClientUtil implements ApplicationContextAware {
    private  static RestTemplate restTemplate;

    public static String doGet(String url) {
        return doGet(url,null,null,String.class);
    }
    public static String doGet(String url, HttpHeaders headers) {
        return doGet(url,headers, null,String.class);
    }
    public  static <T> T doGet(String url,HttpHeaders headers, Map<String, String> param,Class<T> cls){

        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();
            HttpEntity<Void> requestEntity = new HttpEntity<>(null,headers);

            ResponseEntity<T> exchange = restTemplate.exchange(uri,HttpMethod.GET,requestEntity,cls);
            T body = exchange.getBody();
            return body;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doPostForm(String url, MultiValueMap<String,String>params){
        return doPostForm(url,params,null,String.class);
    }
    public static String doPostForm(String url, MultiValueMap<String,String> params, HttpHeaders headers){
        return doPostForm(url,params,headers,String.class);
    }
    public static <T> T doPostForm(String url, MultiValueMap<String, String> params, HttpHeaders headers, Class<T> cls){
        if(headers==null){
            headers=new HttpHeaders();
        }
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<T> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, cls);
        T body = exchange.getBody();
        return body;
    }
    public static String doPostJson(String url, Object body){
        return doPostJson(url,body,null,String.class);
    }
    public static String doPostJson(String url, Object body, HttpHeaders headers){
        return doPostJson(url,body,headers,String.class);
    }
    public static String doPostXml(String url,Object body){
        return doPostContent(url,body,null,MediaType.APPLICATION_XML,String.class);
    }
    public static <T> T doPostJson(String url, Object body, HttpHeaders headers, Class<T> tClass){
       return doPostContent(url,body,headers,MediaType.APPLICATION_JSON,tClass);
    }

    public static <T> T doPostContent(String url,Object body,HttpHeaders headers,MediaType mediaType,Class<T> tClass){
        if(headers==null){
            headers=new HttpHeaders();
        }
        headers.setContentType(mediaType);
        HttpEntity<Object> requestEntity= new HttpEntity<>(body, headers);
        ResponseEntity<T> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, tClass);
        T result = exchange.getBody();
        return result;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        restTemplate=applicationContext.getBean(RestTemplate.class);
    }
}
