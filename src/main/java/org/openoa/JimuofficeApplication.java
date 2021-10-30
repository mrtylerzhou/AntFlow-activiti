package org.openoa;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class JimuofficeApplication {
    public static final String SCAN_BASE_PACKAGES = "org.openoa";
    public static void main(String[] args) {
        SpringApplication.run(JimuofficeApplication.class, args);
    }

}
