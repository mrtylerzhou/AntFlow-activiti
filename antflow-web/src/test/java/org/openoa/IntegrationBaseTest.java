package org.openoa;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {AntFlowApplication.class, MariaDB4jTestConfig.class})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ActiveProfiles("test")
@DisplayName("AntFlow Integration Test")
public abstract class IntegrationBaseTest extends BaseTest {
}
