package org.openoa.starter.config;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScans(
        { @MapperScan("org.openoa.base.mapper"),
                @MapperScan("org.openoa.common.mapper"),
                @MapperScan("org.openoa.engine.bpmnconf.mapper")
        }
)
@ComponentScan({"org.openoa"}) // 指定你的模块的包路径
public class AntFlowAutoConfiguration {

}
