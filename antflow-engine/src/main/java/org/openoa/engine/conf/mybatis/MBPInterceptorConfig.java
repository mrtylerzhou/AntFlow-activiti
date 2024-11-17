package org.openoa.engine.conf.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.openoa.base.constant.StringConstants;
import org.openoa.base.util.ThreadLocalContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.zip.CRC32;

import static org.openoa.base.constant.StringConstants.LOWFLOW_FORM_DATA_FIELD_TABLE_NAME;
import static org.openoa.base.constant.StringConstants.LOWFLOW_FORM_DATA_MAIN_TABLE_NAME;

@Configuration
public class MBPInterceptorConfig {
    @Value("${lf.main.table.count:2}")
    private  Integer mainTableCount;
    @Value("${lf.field.table.count:10}")
    private Integer fieldTableCount;
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //prevent delete All
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());//防止改、删全表
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
            if(LOWFLOW_FORM_DATA_MAIN_TABLE_NAME.equals(tableName)||LOWFLOW_FORM_DATA_FIELD_TABLE_NAME.equals(tableName)){
                String sum= (String) ThreadLocalContainer.get(StringConstants.FORM_CODE);
                CRC32 crc32=new CRC32();
                crc32.update(sum.getBytes(StandardCharsets.UTF_8));
                long value = crc32.getValue();
                if(LOWFLOW_FORM_DATA_MAIN_TABLE_NAME.equals(tableName)){
                    tableName+="_"+value%mainTableCount;
                }else{
                    tableName+="_"+value%fieldTableCount;
                }
            }
            return tableName ;
        });
        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        return interceptor;
    }
}
