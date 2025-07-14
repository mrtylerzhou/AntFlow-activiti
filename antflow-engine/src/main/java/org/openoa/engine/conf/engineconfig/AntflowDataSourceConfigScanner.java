package org.openoa.engine.conf.engineconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.*;

@Configuration
public class AntflowDataSourceConfigScanner implements IAntflowDataSourceConfigScanner{

    @Autowired
    private Environment environment;

    @Override
    public   Map<String, DataSourceProperties> getAntflowDataSourceProperties() {
        String prefix = "spring.antflow";
        Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(environment);
        Binder binder = new Binder(sources);
        Map<String, DataSourceProperties> stringObjectMap = binder.bind(prefix, Bindable.mapOf(String.class, DataSourceProperties.class))
                .orElse(Collections.emptyMap());

        return stringObjectMap;
    }
}
