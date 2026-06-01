package org.openoa;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Test configuration for embedded MariaDB4j.
 * <p>
 * MariaDB4j and SQL initialization are static singletons — they survive Spring context
 * restarts. The JVM keeps running between test classes (surefire/reuseForks=true, IDEA default),
 * so the database only starts once.
 */
@Configuration
public class MariaDB4jTestConfig {

    private static final int PORT = 3308;
    private static final String DB_NAME = "antflow_test";

    private static volatile DB db;
    private static volatile boolean sqlInitialized = false;

    private static synchronized void ensureStarted() throws Exception {
        if (db == null) {
            DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
            config.setPort(PORT);
            config.setSecurityDisabled(true);
            config.addArg("--character-set-server=utf8mb4");
            config.addArg("--collation-server=utf8mb4_unicode_ci");
            db = DB.newEmbeddedDB(config.build());
            db.start();
            db.createDB(DB_NAME);
        }
    }

    @Bean(destroyMethod = "")
    public DB mariaDB4j() throws Exception {
        ensureStarted();
        return db;
    }

    @Bean
    public DataSource dataSource(ResourcePatternResolver resourceResolver) throws Exception {
        ensureStarted();

        DataSource ds = DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:" + PORT + "/" + DB_NAME + "?useUnicode=true&characterEncoding=UTF-8")
                .username("root")
                .password("")
                .build();

        if (!sqlInitialized) {
            synchronized (MariaDB4jTestConfig.class) {
                if (!sqlInitialized) {
                    Resource schema1 = resourceResolver.getResource("classpath:act_init_db.sql");
                    Resource schema2 = resourceResolver.getResource("classpath:bpm_init_db.sql");
                    Resource data = resourceResolver.getResource("classpath:bpm_init_db_data.sql");

                    DatabasePopulator populator = new ResourceDatabasePopulator(
                            true, true, "UTF-8", schema1, schema2, data);
                    DatabasePopulatorUtils.execute(populator, ds);
                    sqlInitialized = true;
                }
            }
        }

        return ds;
    }
}
