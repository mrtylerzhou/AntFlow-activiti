package org.openoa.engine.conf.engineconfig;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.util.Map;

public interface IAntflowDataSourceConfigScanner {
    Map<String, DataSourceProperties> getAntflowDataSourceProperties();
}
