package org.openoa.engine.conf.engineconfig;

import javax.sql.DataSource;

public interface DataSourceFactory {
    DataSource createDataSource(String url, String username, String password);
}
