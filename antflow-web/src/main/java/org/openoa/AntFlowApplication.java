package org.openoa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class AntFlowApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AntFlowApplication.class, args);
    }


}
