package org.openoa.engine.utils;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;

import java.util.*;

public class BoundSqlUtils {

    /**
     * 从BoundSql中提取出 where 条件的列名 + 参数
     * @param boundSql
     * @return List<Map.Entry<String, String>> key=列名, value=对应的参数名
     */
    public static List<Map.Entry<String, String>> extractWhereColumnsAndParams(BoundSql boundSql) {
        List<Map.Entry<String, String>> result = new ArrayList<>();
        String sql = boundSql.getSql();
        List<ParameterMapping> mappings = boundSql.getParameterMappings();

        try {
            Statement stmt = CCJSqlParserUtil.parse(sql);
            final List<String> columnNames = new ArrayList<>();

            stmt.accept(new StatementVisitorAdapter() {
                @Override
                public void visit(Select select) {
                    PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
                    Expression where = plainSelect.getWhere();
                    if (where != null) {
                        parseWhere(where, columnNames);
                    }
                }

                @Override
                public void visit(Update update) {
                    Expression where = update.getWhere();
                    if (where != null) {
                        parseWhere(where, columnNames);
                    }
                }

                @Override
                public void visit(Delete delete) {
                    Expression where = delete.getWhere();
                    if (where != null) {
                        parseWhere(where, columnNames);
                    }
                }
            });

            int count = Math.min(columnNames.size(), mappings.size());
            for (int i = 0; i < count; i++) {
                String column = columnNames.get(i);
                String property = mappings.get(i).getProperty();
                result.add(new AbstractMap.SimpleEntry<>(column, property));
            }

        } catch (JSQLParserException e) {
            throw new RuntimeException("解析SQL出错", e);
        }

        return result;
    }

    private static void parseWhere(Expression where, List<String> columns) {
        ExpressionDeParser deParser = new ExpressionDeParser() {
            @Override
            public void visit(AndExpression andExpression) {
                // and 左右子节点都递归
                andExpression.getLeftExpression().accept(this);
                andExpression.getRightExpression().accept(this);
            }

            @Override
            public void visit(EqualsTo equalsTo) {
                if (equalsTo.getRightExpression() instanceof JdbcParameter) {
                    columns.add(equalsTo.getLeftExpression().toString());
                }

                equalsTo.getLeftExpression().accept(this);
                equalsTo.getRightExpression().accept(this);
            }

            @Override
            public void visit(GreaterThan greaterThan) {
                if (greaterThan.getRightExpression() instanceof JdbcParameter) {
                    columns.add(greaterThan.getLeftExpression().toString());
                }
                greaterThan.getLeftExpression().accept(this);
                greaterThan.getRightExpression().accept(this);
            }

            @Override
            public void visit(GreaterThanEquals greaterThanEquals) {
                if (greaterThanEquals.getRightExpression() instanceof JdbcParameter) {
                    columns.add(greaterThanEquals.getLeftExpression().toString());
                }
                greaterThanEquals.getLeftExpression().accept(this);
                greaterThanEquals.getRightExpression().accept(this);
            }

            @Override
            public void visit(MinorThan minorThan) {
                if (minorThan.getRightExpression() instanceof JdbcParameter) {
                    columns.add(minorThan.getLeftExpression().toString());
                }
                minorThan.getLeftExpression().accept(this);
                minorThan.getRightExpression().accept(this);
            }

            @Override
            public void visit(MinorThanEquals minorThanEquals) {
                if (minorThanEquals.getRightExpression() instanceof JdbcParameter) {
                    columns.add(minorThanEquals.getLeftExpression().toString());
                }
                minorThanEquals.getLeftExpression().accept(this);
                minorThanEquals.getRightExpression().accept(this);
            }

            @Override
            public void visit(NotEqualsTo notEqualsTo) {
                if (notEqualsTo.getRightExpression() instanceof JdbcParameter) {
                    columns.add(notEqualsTo.getLeftExpression().toString());
                }
                notEqualsTo.getLeftExpression().accept(this);
                notEqualsTo.getRightExpression().accept(this);
            }
        };
        where.accept(deParser);
    }
    private static boolean isFixedValue(Expression rightExpression) {
        return (rightExpression instanceof LongValue)
                || (rightExpression instanceof StringValue)
                || (rightExpression instanceof DoubleValue)
                || (rightExpression instanceof NullValue)
                || (rightExpression instanceof DateValue)
                || (rightExpression instanceof TimeValue)
                || (rightExpression instanceof TimestampValue);
    }


}
