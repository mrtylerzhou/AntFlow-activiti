package org.openoa.engine.conf.engineconfig;

import org.activiti.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.openoa.base.exception.JiMuBizException;
import org.springframework.boot.jdbc.DatabaseDriver;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class DefaultDataBaseTypeDetector {

    public static String detectDataSourceDbType(DataSource dataSource){
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            DatabaseDriver databaseDriver = DatabaseDriver.fromProductName(databaseProductName);
            String dbName = mapDatabaseType(databaseDriver);
            return dbName;
        } catch (SQLException e) {
            throw new JiMuBizException("can not get database produce name"+e.getMessage());
        }
    }


    // 自定义方法将 Spring 的 DatabaseDriver 映射为 Activiti 的 DatabaseType
    private static String mapDatabaseType(DatabaseDriver databaseDriver) {
        switch (databaseDriver) {
            case MYSQL:
            case MARIADB:
                return MultiSchemaMultiTenantProcessEngineConfiguration.DATABASE_TYPE_MYSQL;
            case POSTGRESQL:
                return MultiSchemaMultiTenantProcessEngineConfiguration.DATABASE_TYPE_POSTGRES;
            case ORACLE:
                return MultiSchemaMultiTenantProcessEngineConfiguration.DATABASE_TYPE_ORACLE;
            case H2:
                return MultiSchemaMultiTenantProcessEngineConfiguration.DATABASE_TYPE_H2;
            case DB2:
                return MultiSchemaMultiTenantProcessEngineConfiguration.DATABASE_TYPE_DB2;
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseDriver);
        }
    }
}
