package org.openoa.engine.conf.mybatis.interceptor;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import net.sf.jsqlparser.util.validation.validator.OrderByValidator;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.openoa.base.constant.StringConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.CRC32;

@Component
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class LFRoutingSqlInterceptor implements Interceptor {
    @Value("${lf.main.table.count:2}")
    private  Integer mainTableCount;
    @Value("${lf.field.table.count:2}")
    private Integer fieldTableCount;
    @Value("${lf.dynamicRoutingtable:true}")
    private boolean isLFDynamicRoutingTableOn;
    private static final List<String> lfTableNames = Lists.newArrayList(StringConstants.LOWFLOW_FORM_DATA_MAIN_TABLE_NAME,StringConstants.LOWFLOW_FORM_DATA_FIELD_TABLE_NAME);
    private static final List<String> formCodesUpper=Lists.newArrayList(StringConstants.FORM_CODE.toUpperCase(),StringConstants.FORMCODE_NO_CAMAL.toUpperCase());

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
       if(isLFDynamicRoutingTableOn){
           // 获取拦截的参数
           Object[] args = invocation.getArgs();
           MappedStatement mappedStatement = (MappedStatement) args[0];
           Object parameterObject = args[1];


           BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);

           String sql = boundSql.getSql();
           List<String> tableNames = getTableNames(sql);
           if(CollectionUtils.containsAny(lfTableNames,tableNames)){
               String restoredFormCode = restoreFormCodeValueFromSql(sql, boundSql);
               CRC32 crc32=new CRC32();
               crc32.update(restoredFormCode.getBytes(StandardCharsets.UTF_8));
               long value = crc32.getValue();
               Map<String,String> original2newTblName=new HashMap<>();
               String modifiedSql="";
               for (String tableName : tableNames) {
                   if(tableName.equalsIgnoreCase(StringConstants.LOWFLOW_FORM_DATA_MAIN_TABLE_NAME)){
                       String newTblName=StringConstants.LOWFLOW_FORM_DATA_MAIN_TABLE_NAME+"_"+(value%(Math.max(mainTableCount,2)-1));
                       modifiedSql=replaceTableName(tableName,newTblName,sql);
                   }else if(tableName.equalsIgnoreCase(StringConstants.LOWFLOW_FORM_DATA_FIELD_TABLE_NAME)){
                       String newTblName=StringConstants.LOWFLOW_FORM_DATA_FIELD_TABLE_NAME+"_"+(value%(Math.max(mainTableCount,2)-1));
                       String tmpSql= StringUtils.hasText(modifiedSql)?modifiedSql:sql;
                       modifiedSql=replaceTableName(tableName,newTblName,tmpSql);
                   }
               }
               BoundSql newBoundSql = new BoundSql(
                       mappedStatement.getConfiguration(),
                       modifiedSql,
                       boundSql.getParameterMappings(),
                       boundSql.getParameterObject()
               );


               MappedStatement newMappedStatement = copyMappedStatement(mappedStatement, newBoundSql);
               args[0] = newMappedStatement;
           }
       }

        return invocation.proceed();
    }


    private MappedStatement copyMappedStatement(MappedStatement ms, BoundSql newBoundSql) {
        MappedStatement.Builder builder = new MappedStatement.Builder(
                ms.getConfiguration(),
                ms.getId(),
                parameterObject -> newBoundSql,
                ms.getSqlCommandType()
        );

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        builder.keyProperty(String.join(",", ms.getKeyProperties() == null ? new String[0] : ms.getKeyProperties()));
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }
    private String restoreFormCodeValueFromSql(String sql, BoundSql boundSql) {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();


        if (parameterMappings == null || parameterMappings.isEmpty() || parameterObject == null) {
            return "";
        }


        MetaObject metaObject = MetaObject.forObject(
                parameterObject,
                new DefaultObjectFactory(),
                new DefaultObjectWrapperFactory(),
                new DefaultReflectorFactory()
        );

        for (ParameterMapping parameterMapping : parameterMappings) {
            String propertyName = parameterMapping.getProperty();
            if(!formCodesUpper.contains(propertyName.toUpperCase())){
                continue;
            }
            Object value;


            if (boundSql.hasAdditionalParameter(propertyName)) {
                // 动态参数（如 foreach 中的参数）
                value = boundSql.getAdditionalParameter(propertyName);
            } else if (parameterObject instanceof java.util.Map) {

                value = ((java.util.Map<?, ?>) parameterObject).get(propertyName);
            } else {

                value = metaObject.getValue(propertyName);
            }
            String formattedValue = formatParameter(value);
            return formattedValue;

        }
        return "";
    }


    private String formatParameter(Object param) {
        if (param == null) {
            return "NULL";
        } else if (param instanceof String || param instanceof java.util.Date) {
            // 字符串和日期需要加单引号
            if (param instanceof java.util.Date) {
                return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(param) + "'";
            }
            return "'" + param.toString().replace("'", "''") + "'";
        } else {
            return param.toString();
        }
    }
    private List<String> getTableNames(String sql) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();

            return tablesNamesFinder.getTableList(statement);
        } catch (JSQLParserException e) {
            throw new RuntimeException("解析 SQL 出错: " + sql, e);
        }
    }

    /**
     * 替换 SQL 中的表名
     */
    private static String replaceTableName(String originalTblName,String newTblName,String originalSql) {
        try {

            Statement statement = CCJSqlParserUtil.parse(originalSql);


            if (statement instanceof Select) {
                return processSelectStatement(originalTblName,newTblName,(Select) statement);
            }


            if (statement instanceof Insert) {
                return processInsertStatement(originalTblName,newTblName,(Insert) statement);
            }


            if (statement instanceof Update) {
                return processUpdateStatement(originalTblName,newTblName,(Update) statement);
            }


            if (statement instanceof Delete) {
                return processDeleteStatement(originalTblName,newTblName,(Delete) statement);
            }

        } catch (Exception e) {
            throw new RuntimeException("解析 SQL 出错: " + originalSql, e);
        }

        // 如果无法识别语句类型，则返回原始 SQL
        return originalSql;
    }


    private static String processSelectStatement(String originalTblName,String newTblName,Select select) {
        StringBuilder modifiedSql = new StringBuilder();
        ExpressionDeParser expressionDeParser = new ExpressionDeParser();
        SelectDeParser selectDeParser = new SelectDeParser(expressionDeParser,modifiedSql) {
            @Override
            public void visit(Table table) {
                if (table.getName().equalsIgnoreCase(originalTblName)) {
                    table.setName(newTblName);
                }

                super.visit(table);
            }
            @Override
            public void visit(PlainSelect plainSelect){

                getBuffer().append("SELECT ");


                if (plainSelect.getSelectItems() != null) {
                    List<SelectItem> selectItems = plainSelect.getSelectItems();
                    for (int i = 0; i < selectItems.size(); i++) {
                        selectItems.get(i).accept(this);
                        if (i < selectItems.size() - 1) {
                            // 添加逗号和空格
                            getBuffer().append(", ");
                        }
                    }
                }

                if (plainSelect.getFromItem() != null) {
                    getBuffer().append(" FROM ");
                    plainSelect.getFromItem().accept(this);
                }


                if (plainSelect.getWhere() != null) {
                    getBuffer().append(" WHERE ");
                    plainSelect.getWhere().accept(expressionDeParser);
                }


                if (plainSelect.getGroupBy() != null) {
                    getBuffer().append(" GROUP BY ");
                    getBuffer().append(plainSelect.getGroupBy());
                }
                if (plainSelect.getOrderByElements() != null) {
                    getBuffer().append(" ORDER BY ");
                    plainSelect.getOrderByElements().forEach(orderBy -> {
                        orderBy.accept(new OrderByValidator());
                        getBuffer().append(", ");
                    });
                    getBuffer().setLength(getBuffer().length() - 2);
                }
            }
        };

        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(modifiedSql);
        select.getSelectBody().accept(selectDeParser);
        return modifiedSql.toString();
    }


    private static String processInsertStatement(String originalTblName,String newTblName,Insert insert) {
        Table table = insert.getTable();
        if (table.getName().equalsIgnoreCase(originalTblName)) {
            table.setName(newTblName);
        }
        return insert.toString();
    }


    private static String processUpdateStatement(String originalTblName,String newTblName,Update update) {
        Table table = update.getTable();
        if (table.getName().equalsIgnoreCase(originalTblName)) {
            table.setName(newTblName);
        }
        return update.toString();
    }


    private static String processDeleteStatement(String originalTblName,String newTblName,Delete delete) {
        Table table = delete.getTable();
        if (table.getName().equalsIgnoreCase(originalTblName)) {
            table.setName(newTblName);
        }
        return delete.toString();
    }
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可选：设置插件属性
    }
}
