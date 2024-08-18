package org.openoa.engine.conf.confval;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class BizDataSourceConfVal extends DataSourceConfVal {
    @Value("${jimubiz.datasource.url}")
    private String url;

    @Value("${jimubiz.datasource.username}")
    private String username;

    @Value("${jimubiz.datasource.password}")
    private String password;

}
