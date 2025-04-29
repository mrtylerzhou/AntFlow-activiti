package org.openoa.engine.conf.mybatis.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
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
import org.openoa.base.entity.BpmBusinessProcess;
import org.openoa.base.exception.JiMuBizException;
import org.openoa.base.interf.BpmBusinessProcessService;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.engine.bpmnconf.service.biz.BpmBusinessProcessServiceImpl;
import org.openoa.engine.utils.BoundSqlUtils;
import org.openoa.engine.utils.ConsistentHashingAlg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class LFConsistentHashingRoutingSqlInterceptor implements Interceptor, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(LFConsistentHashingRoutingSqlInterceptor.class);
    @Value("${lf.main.table.count:1}")
    private  Integer mainTableCount;
    @Value("${lf.field.table.count:1}")
    private Integer fieldTableCount;

    private ConsistentHashingAlg mainTableHashing;
    private ConsistentHashingAlg fieldTableHashing;

    private static final List<String> LF_TABLE_NAMES = Lists.newArrayList(StringConstants.LOWFLOW_FORM_DATA_MAIN_TABLE_NAME,StringConstants.LOWFLOW_FORM_DATA_FIELD_TABLE_NAME);
    private static final List<String> FORM_CODES_UPPER =Lists.newArrayList(StringConstants.FORM_CODE.toUpperCase(),StringConstants.FORMCODE_NO_CAMAL.toUpperCase());

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

           // 获取拦截的参数
           Object[] args = invocation.getArgs();
           MappedStatement mappedStatement = (MappedStatement) args[0];
           Object parameterObject = args[1];


           BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);

           String sql = boundSql.getSql();
           List<String> tableNames = getTableNames(sql);
           if(CollectionUtils.containsAny(LF_TABLE_NAMES,tableNames)){
               String restoredFormCode = restoreFormCodeValueFromSql(sql, boundSql);
               String modifiedSql="";
               if(mainTableCount>=2){
                   for (String tableName : tableNames) {
                       if(tableName.equalsIgnoreCase(StringConstants.LOWFLOW_FORM_DATA_MAIN_TABLE_NAME)){

                               String newTblName= mainTableHashing.getServer(restoredFormCode);
                               modifiedSql=replaceTableName(tableName,newTblName,sql);
                       }else if(tableName.equalsIgnoreCase(StringConstants.LOWFLOW_FORM_DATA_FIELD_TABLE_NAME)){

                               String newTblName= fieldTableHashing.getServer(restoredFormCode);
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

        /*try {
            List<Map.Entry<String, String>> entries = BoundSqlUtils.extractWhereColumnsAndParams(boundSql);
            String idValue="";
            if(!CollectionUtils.isEmpty(entries)){
                for (Map.Entry<String, String> entry : entries) {
                    if(FORM_CODES_UPPER.contains(entry.getKey().toUpperCase())){
                        Object value = metaObject.getValue(entry.getValue());
                        if(value!=null){
                            return formatParameter(value);
                        }
                    }else if ("ID".equalsIgnoreCase(entry.getKey())) {
                        Object value = metaObject.getValue(entry.getValue());
                        if(value!=null){
                            idValue= formatParameter(value);
                        }
                    }
                }
                if(!StringUtils.hasText(idValue)){
                    //目前无法支持模糊查找,必须要拿到formcode先确定路由表,其它场景太复杂了,暂不支持
                    throw new JiMuBizException("不支持的sql语句,sql语句中没有formCode,并且没有id条件!");
                }
                BpmBusinessProcessServiceImpl businessProcessService = SpringBeanUtils.getBean(BpmBusinessProcessServiceImpl.class);
                LambdaQueryWrapper<BpmBusinessProcess> queryWrapper = Wrappers.<BpmBusinessProcess>lambdaQuery()
                        .eq(BpmBusinessProcess::getBusinessId, idValue)
                        .eq(BpmBusinessProcess::getIsLowCodeFlow, 1);
                BpmBusinessProcess bpmBusinessProcess = businessProcessService.getOne(queryWrapper);
                if(bpmBusinessProcess!=null){
                    return bpmBusinessProcess.getProcessinessKey();
                }else{
                    throw new JiMuBizException("无法根据低代码流程指定Id:"+idValue+"找到流程信息,对应的流程不存在!");
                }

            }else {
                log.warn("不支持的sql语句,sql语句中没有where子句"+sql);

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        for (ParameterMapping parameterMapping : parameterMappings) {
            String propertyName = parameterMapping.getProperty();
            if("ID".equalsIgnoreCase(propertyName)){
                //虽然理论上也也存在where id=xxx and a=x and b=x这种sql,但是默认用户不会这么写,所以暂时不支持
                    BpmBusinessProcess bpmBusinessProcess = SpringBeanUtils.getBean(BpmBusinessProcessServiceImpl.class).getOne(Wrappers.<BpmBusinessProcess>lambdaQuery()
                            .eq(BpmBusinessProcess::getBusinessId, parameterObject)
                            .eq(BpmBusinessProcess::getIsLowCodeFlow, 1));
                    if(bpmBusinessProcess!=null){
                        return bpmBusinessProcess.getProcessinessKey();
                    }else{
                        throw new JiMuBizException("无法根据低代码流程指定Id:"+parameterObject+"找到流程信息,对应的流程不存在!");
                    }
            }
            if(!FORM_CODES_UPPER.contains(propertyName.toUpperCase())){
                continue;
            }
            Object value=null;


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
    /**
     * 从 SQL 查询中提取列名
     * @param sql 输入的 SQL 查询
     * @return 返回 SQL 查询中的列名
     * @throws Exception 解析错误
     */
    public static List<String> getColumnNamesFromSelectSql(String sql)  {
        List<String> columnNames = new ArrayList<>();

        // 创建 SQL 解析器
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        // 解析 SQL 语句
        Statement statement = null;
        try {
            statement = parserManager.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }

        if (statement instanceof Select) {
            Select selectStatement = (Select) statement;

            // 获取 SELECT 子句中的列名
            SelectBody selectBody = selectStatement.getSelectBody();
            if (selectBody instanceof PlainSelect) {
                PlainSelect plainSelect = (PlainSelect) selectBody;

                // 获取 SELECT 子句中的所有列名
                for (SelectItem item : plainSelect.getSelectItems()) {
                    if (item instanceof SelectExpressionItem) {
                        Expression expression = ((SelectExpressionItem) item).getExpression();
                        columnNames.addAll(getColumnNames(expression));
                    }
                }

                // 获取 WHERE 子句中的列名
                Expression whereClause = plainSelect.getWhere();
                if (whereClause != null) {
                    columnNames.addAll(getColumnNames(whereClause));
                }
            }
        }

        return columnNames;
    }
    /**
     * 从 SQL 查询中提取 WHERE 子句中的列名
     * @param sql 输入的 SQL 查询
     * @return 返回 SQL 查询中的 WHERE 子句涉及的列名
     * @throws Exception 解析错误
     */
    public static List<String> getWhereColumnNamesFromSql(String sql) {
        List<String> columnNames = new ArrayList<>();

        // 创建 SQL 解析器
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        // 解析 SQL 语句
        Statement statement = null;
        try {
            statement = parserManager.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }

        // 处理不同类型的 SQL 语句
        if (statement instanceof Select) {
            Select selectStatement = (Select) statement;
            processWhereClause(selectStatement, columnNames);
        } else if (statement instanceof Update) {
            Update updateStatement = (Update) statement;
            processWhereClause(updateStatement, columnNames);
        } else if (statement instanceof Delete) {
            Delete deleteStatement = (Delete) statement;
            processWhereClause(deleteStatement, columnNames);
        }

        return columnNames;
    }

    /**
     * 提取 SQL 语句中 WHERE 子句中的列名
     * @param statement SQL 语句
     * @param columnNames 存储列名的列表
     */
    private static void processWhereClause(Statement statement, List<String> columnNames) {
        Expression whereClause = null;

        // 对不同类型的 SQL 语句进行处理
        if (statement instanceof Select) {
            Select selectStatement = (Select) statement;
            PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
            whereClause = plainSelect.getWhere();
        } else if (statement instanceof Update) {
            Update updateStatement = (Update) statement;
            whereClause = updateStatement.getWhere();
        } else if (statement instanceof Delete) {
            Delete deleteStatement = (Delete) statement;
            whereClause = deleteStatement.getWhere();
        }


        if (whereClause != null) {
            columnNames.addAll(getColumnNames(whereClause));
        }
    }
    /**
     * 递归解析表达式并提取列名
     * @param expression SQL 中的表达式
     * @return 返回列名
     */
    public static List<String> getColumnNames(Expression expression) {
        List<String> columnNames = new ArrayList<>();

        if (expression instanceof Column) {
            // 如果是列名，则加入到结果列表中
            columnNames.add(((Column) expression).getColumnName());
        } else if (expression instanceof BinaryExpression) {
            // 如果是二元表达式（如 AND、OR 等），则递归解析左侧和右侧表达式
            BinaryExpression binaryExpression = (BinaryExpression) expression;
            columnNames.addAll(getColumnNames(binaryExpression.getLeftExpression()));
            columnNames.addAll(getColumnNames(binaryExpression.getRightExpression()));
        } else if (expression instanceof Parenthesis) {
            // 如果是括号表达式，继续递归解析括号内的表达式
            Parenthesis parenthesis = (Parenthesis) expression;
            columnNames.addAll(getColumnNames(parenthesis.getExpression()));
        }

        return columnNames;
    }
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可选：设置插件属性
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        mainTableHashing=new ConsistentHashingAlg(StringConstants.LOWFLOW_FORM_DATA_MAIN_TABLE_NAME,mainTableCount);
        fieldTableHashing=new ConsistentHashingAlg(StringConstants.LOWFLOW_FORM_DATA_FIELD_TABLE_NAME,fieldTableCount);
    }
}
