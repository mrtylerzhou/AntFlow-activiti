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

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.activiti.engine.impl.cfg.multitenant.MultiSchemaMultiTenantProcessEngineConfiguration;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.openoa.base.listener.StartEngineEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Joram Barrez
 * @author Josh Long
 */
//@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class DataSourceProcessEngineAutoConfiguration {

    @Configuration
    //@ConditionalOnMissingClass("javax.persistence.EntityManagerFactory")
    @EnableConfigurationProperties(ActivitiProperties.class)
    public static class DataSourceProcessEngineConfiguration extends AbstractProcessEngineAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        @ConditionalOnMissingBean
        public SpringProcessEngineConfiguration springProcessEngineConfiguration(
                List<StartEngineEventListener> startEngineEventListeners,
                DataSource dataSource,
                PlatformTransactionManager transactionManager,
                SpringAsyncExecutor springAsyncExecutor) throws IOException {

            return this.baseSpringProcessEngineConfiguration(startEngineEventListeners,dataSource, transactionManager, springAsyncExecutor);
        }
        @Bean
        @ConditionalOnMissingBean
        public ProcessEngineFactoryBean processEngineFactoryBean(SpringProcessEngineConfiguration springProcessEngineConfiguration){
            return super.springProcessEngineBean(springProcessEngineConfiguration);
        }
    }
}
