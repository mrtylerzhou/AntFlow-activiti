package org.openoa.engine.bpmnconf.es;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for Elasticsearch.
 * Creates RestHighLevelClient bean when ES is enabled and the client class is on the classpath.
 *
 * @Author tylerzhou
 */
@Slf4j
@Configuration
@ConditionalOnClass(name = "org.elasticsearch.client.RestHighLevelClient")
@ConditionalOnProperty(prefix = "antflow.es", name = "enabled", havingValue = "true")
public class EsAutoConfiguration {

    @Autowired
    private EsProperties esProperties;

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() {
        String hosts = esProperties.getHosts();
        String[] hostArray = hosts.split(",");
        HttpHost[] httpHosts = new HttpHost[hostArray.length];
        for (int i = 0; i < hostArray.length; i++) {
            String host = hostArray[i].trim();
            if (!host.startsWith("http")) {
                host = "http://" + host;
            }
            httpHosts[i] = HttpHost.create(host);
        }
        log.info("Initializing Elasticsearch RestHighLevelClient with hosts: {}", hosts);
        return new RestHighLevelClient(RestClient.builder(httpHosts));
    }
}
