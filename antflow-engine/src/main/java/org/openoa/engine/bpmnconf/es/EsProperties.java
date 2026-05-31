package org.openoa.engine.bpmnconf.es;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Elasticsearch configuration properties.
 *
 * @Author tylerzhou
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "antflow.es")
public class EsProperties {
    /**
     * Whether ES integration is enabled. Default false.
     */
    private boolean enabled = false;
    /**
     * ES server hosts, comma-separated (e.g. "localhost:9200").
     */
    private String hosts = "localhost:9200";
    /**
     * Index name for process data.
     */
    private String indexName = "antflow-process-data";
}
