package org.openoa.engine.utils;

import com.google.common.collect.Sets;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    //粗略判断是否为curd语句
    public static boolean isCurdSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false; // 空语句视为非 CRUD
        }

        String lowerSql = sql.toLowerCase(Locale.ROOT);

        // 使用正则匹配 insert / update / delete / select 为独立单词（\b 是单词边界）
        Pattern pattern = Pattern.compile("\\b(insert|update|delete|select)\\b");
        Matcher matcher = pattern.matcher(lowerSql);

        return matcher.find();
    }

    private static String removeLeadingCommentsAndWhitespace(String sql) {
        String trimmed = sql.trim();

        // 去除单行注释和多行注释开头
        while (trimmed.startsWith("--") || trimmed.startsWith("/*")) {
            if (trimmed.startsWith("--")) {
                int newline = trimmed.indexOf('\n');
                trimmed = newline >= 0 ? trimmed.substring(newline + 1).trim() : "";
            } else if (trimmed.startsWith("/*")) {
                int endComment = trimmed.indexOf("*/");
                trimmed = endComment >= 0 ? trimmed.substring(endComment + 2).trim() : "";
            }
        }

        return trimmed;
    }

    private static String getFirstKeyword(String sql) {
        if (sql.isEmpty()) return "";

        // 获取前缀单词
        String[] parts = sql.split("\\s+", 2);
        return parts[0];
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
