package org.openoa.engine.conf;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.openoa.engine.conf.mvc.ProcessEngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer {
    /**
     * 解決工作流生成图片乱码问题
     *
     * @param processEngineConfiguration processEngineConfiguration
     */
    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");

    }
}
