package org.openoa;

import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("AntFlow Integration Test")
public abstract class IntegrationBaseTest extends BaseTest {
}
