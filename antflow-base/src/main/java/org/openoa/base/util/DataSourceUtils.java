package org.openoa.base.util;

import org.openoa.base.constant.enums.SupporttedDatabaseEnum;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceUtils {
    private static volatile SupporttedDatabaseEnum cachedDbType;

    public static SupporttedDatabaseEnum getCurrentDb() {
        if (cachedDbType == null) {
            synchronized (DataSourceUtils.class) {
                if (cachedDbType == null) {
                    DataSource dataSource = SpringBeanUtils.getBean(DataSource.class);
                    try (Connection conn = dataSource.getConnection()) {
                        String productName = conn.getMetaData().getDatabaseProductName();
                        cachedDbType = SupporttedDatabaseEnum.getByDatabaseName(productName);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return cachedDbType;
    }
}