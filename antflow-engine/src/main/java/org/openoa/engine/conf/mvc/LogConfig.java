package org.openoa.engine.conf.mvc;

import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@Configuration
public class LogConfig {

    @Bean
    public CommandLineRunner configureLogLevels() {
        return args -> {
            Logger handlerLogger = (Logger) LoggerFactory.getLogger("org.springframework.web.servlet.handler");
            handlerLogger.setLevel(Level.DEBUG);
        };
    }
}
