package org.openoa.engine.conf.config;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.openoa.engine.vo.HttpClientProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestTemplateConfig {

    @Bean
    RestTemplate restTemplate(@Qualifier("clientHttpRequestFactory") ClientHttpRequestFactory requestFactory,RestInterceptor restInterceptor) {
        requestFactory = requestFactory instanceof BufferingClientHttpRequestFactory ? requestFactory : new BufferingClientHttpRequestFactory(requestFactory);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> c : messageConverters) {
            if (c instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) c).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
        restTemplate.getInterceptors().add(restInterceptor);
        restTemplate.getMessageConverters().add(0,this.getFastJsonConverter());
        return restTemplate;

    }

    @Bean
    @ConfigurationProperties(prefix = "spring.resttemplate")
    HttpClientProperties httpClientProperties() {
        return new HttpClientProperties();
    }

    @Bean(name = "clientHttpRequestFactory")
    ClientHttpRequestFactory clientHttpRequestFactory(HttpClientProperties httpClientProperties) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        //如果不使用HttpClient的连接池，则使用restTemplate默认的SimpleClientHttpRequestFactory,底层基于HttpURLConnection

        if (!httpClientProperties.isUseHttpClientPool()) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(httpClientProperties.getConnectTimeout());
            factory.setReadTimeout(httpClientProperties.getReadTimeout());
            return factory;
        }

        TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

        ConnectionKeepAliveStrategy myStrategy = (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator
                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase
                        ("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return 5 * 1000;
        };
        //HttpClient4.3及以上版本不手动设置HttpClientConnectionManager,默认就会使用连接池PoolingHttpClientConnectionManager
        HttpClient httpClient = HttpClientBuilder
                .create()
                .setMaxConnTotal(httpClientProperties.getMaxTotalConnect())
                .setMaxConnPerRoute(httpClientProperties.getMaxConnectPerRoute())
                .evictExpiredConnections()
                .evictIdleConnections(5000, TimeUnit.MILLISECONDS)
                .setSSLSocketFactory(csf)
                .setKeepAliveStrategy(myStrategy)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(httpClientProperties.getConnectTimeout());
        factory.setReadTimeout(httpClientProperties.getReadTimeout());
        factory.setConnectionRequestTimeout(httpClientProperties.getConnectionRequestTimeout());
        return factory;
    }
    //自定义报文转换器
    FastJsonHttpMessageConverter getFastJsonConverter(){

        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 设置feature
        fastJsonConfig.setFeatures(Feature.AllowISO8601DateFormat);
        // 设置日期格式、关闭循环引用检测
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.DisableCircularReferenceDetect);
        //指定全局日期格式
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

        //设定MediaType
        fastJsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON_UTF8));
        return fastJsonHttpMessageConverter;
    }

}