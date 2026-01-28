/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openoa.engine.conf.engineconfig;

import org.activiti.engine.*;
import org.activiti.engine.impl.cmd.ProcessNodeJump;
import org.activiti.spring.*;
import org.openoa.base.listener.StartEngineEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides sane definitions for the various beans required to be productive with Activiti in Spring.
 *
 * @author Josh Long
 * @author Vedran Pavic
 */
public abstract class AbstractProcessEngineAutoConfiguration
        extends AbstractProcessEngineConfiguration {

  protected ActivitiProperties activitiProperties;

  @Autowired
  private ResourcePatternResolver resourceLoader;
  
  @Autowired
  private ProcessEngineConfigurationConfigurer processEngineConfigurationConfigurer;


  @Bean
  public SpringAsyncExecutor springAsyncExecutor(TaskExecutor taskExecutor) {
    return new SpringAsyncExecutor(taskExecutor, springRejectedJobsHandler());
  }
  @Bean
  public SpringRejectedJobsHandler springRejectedJobsHandler() {
    return new SpringCallerRunsRejectedJobsHandler();
  }
  protected SpringProcessEngineConfiguration baseSpringProcessEngineConfiguration(List<StartEngineEventListener> startEngineEventListeners,
                                                                                  DataSource dataSource, PlatformTransactionManager platformTransactionManager,
                                                                                  SpringAsyncExecutor springAsyncExecutor) throws IOException {

    List<Resource> procDefResources = this.discoverProcessDefinitionResources(
        this.resourceLoader, this.activitiProperties.getProcessDefinitionLocationPrefix(),
        this.activitiProperties.getProcessDefinitionLocationSuffixes(),
        this.activitiProperties.isCheckProcessDefinitions());

    SpringProcessEngineConfiguration conf = super.processEngineConfigurationBean(startEngineEventListeners,
        procDefResources.toArray(new Resource[procDefResources.size()]), dataSource, 
        platformTransactionManager, springAsyncExecutor);

    conf.setDeploymentName(defaultText(activitiProperties.getDeploymentName(), conf.getDeploymentName()));
    conf.setDatabaseSchema(defaultText(activitiProperties.getDatabaseSchema(), conf.getDatabaseSchema()));
    conf.setDatabaseSchemaUpdate(defaultText(activitiProperties.getDatabaseSchemaUpdate(), conf.getDatabaseSchemaUpdate()));
    conf.setDbIdentityUsed(activitiProperties.isDbIdentityUsed());
    conf.setDbHistoryUsed(activitiProperties.isDbHistoryUsed());

    conf.setJobExecutorActivate(activitiProperties.isJobExecutorActivate());
    conf.setAsyncExecutorEnabled(activitiProperties.isAsyncExecutorEnabled());
    conf.setAsyncExecutorActivate(false);
    
    conf.setMailServerHost(activitiProperties.getMailServerHost());
    conf.setMailServerPort(activitiProperties.getMailServerPort());
    conf.setMailServerUsername(activitiProperties.getMailServerUserName());
    conf.setMailServerPassword(activitiProperties.getMailServerPassword());
    conf.setMailServerDefaultFrom(activitiProperties.getMailServerDefaultFrom());
    conf.setMailServerUseSSL(activitiProperties.isMailServerUseSsl());
    conf.setMailServerUseTLS(activitiProperties.isMailServerUseTls());

    conf.setHistoryLevel(activitiProperties.getHistoryLevel());

    if (activitiProperties.getCustomMybatisMappers() != null) {
      conf.setCustomMybatisMappers(getCustomMybatisMapperClasses(activitiProperties.getCustomMybatisMappers()));
    }

    if (activitiProperties.getCustomMybatisXMLMappers() != null) {
      conf.setCustomMybatisXMLMappers(new HashSet<String>(activitiProperties.getCustomMybatisXMLMappers()));
    }
    
    if (processEngineConfigurationConfigurer != null) {
    	processEngineConfigurationConfigurer.configure(conf);
    }

    return conf;
  }

  private Set<Class<?>> getCustomMybatisMapperClasses(List<String> customMyBatisMappers) {
    Set<Class<?>> mybatisMappers = new HashSet<Class<?>>();
    for (String customMybatisMapperClassName : customMyBatisMappers) {
      try {
        Class customMybatisClass = Class.forName(customMybatisMapperClassName);
        mybatisMappers.add(customMybatisClass);
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException("Class " + customMybatisMapperClassName + " has not been found.", e);
      }
    }
    return mybatisMappers;
  }


  private String defaultText(String deploymentName, String deploymentName1) {
    if (StringUtils.hasText(deploymentName))
      return deploymentName;
    return deploymentName1;
  }

  @Autowired
  protected void setActivitiProperties(ActivitiProperties activitiProperties) {
    this.activitiProperties = activitiProperties;
  }

  protected ActivitiProperties getActivitiProperties() {
    return this.activitiProperties;
  }


  @Bean
  @ConditionalOnMissingBean
  @Override
  public RuntimeService runtimeServiceBean(ProcessEngine processEngine) {
    return super.runtimeServiceBean(processEngine);
  }


  @Bean
  @ConditionalOnMissingBean
  @Override
  public RepositoryService repositoryServiceBean(ProcessEngine processEngine) {
    return super.repositoryServiceBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  @Override
  public ProcessNodeJump getProcessNodeJumpServiceBean(ProcessEngine processEngine){
    return super.getProcessNodeJumpServiceBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  @Override
  public TaskService taskServiceBean(ProcessEngine processEngine) {
    return super.taskServiceBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  @Override
  public HistoryService historyServiceBean(ProcessEngine processEngine) {
    return super.historyServiceBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  @Override
  public ManagementService managementServiceBeanBean(ProcessEngine processEngine) {
    return super.managementServiceBeanBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  @Override
  public FormService formServiceBean(ProcessEngine processEngine) {
    return super.formServiceBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  @Override
  public IdentityService identityServiceBean(ProcessEngine processEngine) {
    return super.identityServiceBean(processEngine);
  }

  @Bean
  @ConditionalOnMissingBean
  public TaskExecutor taskExecutor() {
    return new SimpleAsyncTaskExecutor();
  }
}
