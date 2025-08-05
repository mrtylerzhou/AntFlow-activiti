package org.openoa.engine.bpmnconf.actservice;

import org.activiti.engine.*;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *@Author JimuOffice
 * @Description a handy bean to get the processEngine and its core service
 * @Date 2022-05-17 14:12
 * @Param
 * @return
 * @Version 1.0
 */
@Configuration
public class ActivitiServiceBean {

    @Autowired
    private ProcessEngineFactoryBean processEngineFactoryBean;
    @Autowired
    private ProcessEngine processEngine;


    @Bean
    public RepositoryService repositoryService(){
        return processEngine.getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService(){
        return processEngine.getRuntimeService();
    }

    @Bean
    public TaskService taskService(){
        return processEngine.getTaskService();
    }

    @Bean
    public IdentityService identityService(){
        return processEngine.getIdentityService();
    }

    @Bean
    public ManagementService managementService(){
        return processEngine.getManagementService();
    }
}
