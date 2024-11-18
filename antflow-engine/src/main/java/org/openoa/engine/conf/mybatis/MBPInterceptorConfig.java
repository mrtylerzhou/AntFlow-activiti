package org.openoa.engine.conf.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.util.ThreadLocalContainer;
import org.openoa.engine.conf.mybatis.interceptor.SqlInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.zip.CRC32;

import static org.openoa.base.constant.StringConstants.LOWFLOW_FORM_DATA_FIELD_TABLE_NAME;
import static org.openoa.base.constant.StringConstants.LOWFLOW_FORM_DATA_MAIN_TABLE_NAME;

@Configuration
public class MBPInterceptorConfig {


    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor configInterceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //prevent delete All
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());//防止改、删全表
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }

}
