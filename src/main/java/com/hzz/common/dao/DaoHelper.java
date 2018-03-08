package com.hzz.common.dao;

import com.hzz.common.dao.annotation.Column;
import com.hzz.common.dao.annotation.Table;
import com.hzz.common.dao.annotation.Version;
import com.hzz.exception.CommonException;
import com.hzz.exception.CommonExceptionHelper;

import com.hzz.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hongshuiqiao on 2017/6/8.
 */
public class DaoHelper {
    private static Logger logger = LoggerFactory.getLogger(DaoHelper.class);
    /**
     * table,(column, SqlParameter)
     */
    private static Map<String, Map<String, SqlParameter>> paramMap = new HashMap<>();
    private static Map<Class, TableModel> tableModelMap = new ConcurrentHashMap<Class, TableModel>();

    /**
     * 将名称加上``
     * 如：abc => `abc`
     * a.bc => `a`.`bc`
     *
     * @param value
     * @return
     */
    public static String quote(String value) {
        String[] segments = value.split("[.]");
        StringBuilder builder = new StringBuilder();
        for (String segment : segments) {
            if (builder.length() > 0) {
                builder.append(".");
            }
            builder.append(String.format("`%s`", segment.trim()));
        }
        return builder.toString();
    }

    public static boolean isBlank(String value) {
        return null == value || "".equals(value.trim());
    }

    public static String[] getPkColums(Class type) throws CommonException {
        List<String> list = new ArrayList<>();
        TableModel tableModel = tableModelMap.get(type);
        if (null != tableModel) {
            List<ColumnModel> columnModels = tableModel.columnModels;
            for (ColumnModel columnModel : columnModels) {
                if(columnModel.pkColumn)
                    list.add(columnModel.column);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public static TableModel getTableModel(Class type) throws CommonException {
        TableModel tableModel = tableModelMap.get(type);
        if (null != tableModel)
            return tableModel;

        synchronized (DaoHelper.class){
            tableModel = tableModelMap.get(type);
            if (null != tableModel)
                return tableModel;

            //有Model直接返回
            if (!type.isAnnotationPresent(Table.class)) {//如果指定类型的注释存在于此元素上
                throw CommonExceptionHelper.daoException(String.format("%s没有Table注解", type.getName()), null);
            }
            //没有TABLE注解抛出异常
            Table table = (Table) type.getAnnotation(Table.class);
            String tableName = table.value();//获取表名
            if (DaoHelper.isBlank(tableName)) {
                tableName = type.getSimpleName();
            }
            tableModel = new TableModel();
            tableModel.table = tableName;
            tableModel.type = type;

            tableModelMap.put(type, tableModel);
            Field[] fields = type.getDeclaredFields();//获取该类声明的字段
            for (Field field : fields) {
                field.setAccessible(true);//获取字段的权限
                if (field.isAnnotationPresent(Column.class)) {
                    tableModel.columnModels.add(parseColumnModel(field));
                } else if (field.isAnnotationPresent(Version.class)) {
                    Version version = field.getAnnotation(Version.class);
                    ColumnModel columnModel = new ColumnModel();
                    columnModel.column = getColumnName(field, version.value());
                    columnModel.versionColumn = true;
                    columnModel.field = field;
                    tableModel.columnModels.add(columnModel);
                    tableModel.version = columnModel;
                }
            }
            tableModelMap.put(type, tableModel);
            return tableModel;
        }
    }

    private static ColumnModel parseColumnModel(Field field) {
        Column column = field.getAnnotation(Column.class);
        ColumnModel columnModel = new ColumnModel();
        columnModel.column = getColumnName(field, column.value());
        columnModel.pkColumn = column.pk();
        columnModel.field = field;
        return columnModel;
    }

    private static String getColumnName(Field field, String columnName) {
        if (DaoHelper.isBlank(columnName))
            return field.getName();
        return columnName;
    }

    public static void addValueToArgs(JdbcTemplate jdbcTemplate, List<SqlParameter> argList, String table, String column, Object columnValue) throws CommonException {
        argList.add(getSqlValueParameter(jdbcTemplate, table, column, columnValue));
    }

    public static <T extends AbstractModel> String handleSelect(JdbcTemplate jdbcTemplate, T condition, List<SqlParameter> args, boolean forUpdate) throws CommonException {
        StringBuilder sqlBuilder = new StringBuilder();
        TableModel tableModel = getTableModel(condition.getClass());//分析类的注解并封装在TableModel里面
        sqlBuilder.append("SELECT ");

        if(condition.columns().isEmpty()){
            if(!tableModel.columnModels.isEmpty()){
                buildSelectElementSql(sqlBuilder, null, tableModel.columnModels,false);
            }else{
                sqlBuilder.append(" * ");
            }
        }else{
            sqlBuilder.append(StringUtils.join(condition.columns(), ","));
        }

        sqlBuilder.append(" FROM ");
        sqlBuilder.append(quote(tableModel.table));

        String where = condition.where().parseSql(jdbcTemplate, tableModel.table, args);
        if (!DaoHelper.isBlank(where)) {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(where);
        }
        buildSelectPost(tableModel.table, jdbcTemplate, condition, sqlBuilder, args);
        String having = condition.having().parseSql(jdbcTemplate, tableModel.table, args);
        if (!DaoHelper.isBlank(having)) {
            sqlBuilder.append(" HAVING ");
            sqlBuilder.append(having);
        }

        if (forUpdate) {
            sqlBuilder.append(" FOR UPDATE");
        }
        return sqlBuilder.toString();
    }

    private static boolean buildSelectElementSql(StringBuilder sqlBuilder, String tableAliasName, List<ColumnModel> columnModels, boolean flag) {
        boolean newFlag = flag;
        for (ColumnModel columnModel: columnModels) {
            String columnName = columnModel.column;
            String fieldName = columnModel.field.getName();
            if(newFlag)    sqlBuilder.append(",");
            if(!StringUtil.isBlank(tableAliasName)) {
                sqlBuilder.append(tableAliasName);
                sqlBuilder.append(".");
            }
            sqlBuilder.append(quote(columnName));

            if(!StringUtil.isBlank(tableAliasName)){
                sqlBuilder.append(" AS ");
                sqlBuilder.append(tableAliasName);
                sqlBuilder.append("___");
                sqlBuilder.append(fieldName);
            }else{
                if(!columnName.equalsIgnoreCase(fieldName)) {
                    sqlBuilder.append(" AS ");
                    sqlBuilder.append(fieldName);
                }
            }
            newFlag=true;
        }
        return newFlag;
    }

    public static <T extends AbstractModel> String handleSelectCount(JdbcTemplate jdbcTemplate, T condition, List<SqlParameter> args) throws CommonException {
        StringBuilder sqlBuilder = new StringBuilder();
        TableModel tableModel = getTableModel(condition.getClass());
        sqlBuilder.append(String.format("SELECT COUNT(*) FROM %s", DaoHelper.quote(tableModel.table)));
        String where = condition.where().parseSql(jdbcTemplate, tableModel.table, args);
        if (!DaoHelper.isBlank(where)) {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(where);
        }
        String having = condition.having().parseSql(jdbcTemplate, tableModel.table, args);
        if (!DaoHelper.isBlank(having)) {
            sqlBuilder.append(" HAVING ");
            sqlBuilder.append(having);
        }
        return sqlBuilder.toString();
    }

    public static String handleSelect(JdbcTemplate jdbcTemplate, Map<JoinModel, JoinType> models, ConditionModel condition, List<SqlParameter> args) throws CommonException {
        StringBuilder sqlBuilder = new StringBuilder();
        Iterator<Map.Entry<JoinModel, JoinType>> iterator = models.entrySet().iterator();
        StringBuilder joinBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ");
        List<String> selectColumns = condition.columns();
        if(!selectColumns.isEmpty()){
            sqlBuilder.append(StringUtils.join(selectColumns, ","));
        }
        boolean flag = false;
        while (iterator.hasNext()){
            Map.Entry<JoinModel, JoinType> next = iterator.next();
            JoinModel model = next.getKey();
            JoinType joinType = next.getValue();

            TableModel tableModel = getTableModel(model.getJoinModel());
            if(selectColumns.isEmpty()){
                flag = buildSelectElementSql(sqlBuilder, model.getAliasName(),tableModel.columnModels, flag);
            }

            if(joinBuilder.length()>0) {
                appendJoinTable(jdbcTemplate, args, joinBuilder, model, joinType, tableModel);
            }else{
                joinMainTable(joinBuilder, model, tableModel);
            }
        }
        sqlBuilder.append(joinBuilder.toString());

        String where = condition.where().parseSql(jdbcTemplate, null, args);
        if (!DaoHelper.isBlank(where)) {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(where);
        }
        buildSelectPost(null, jdbcTemplate, condition, sqlBuilder, args);
        String having = condition.having().parseSql(jdbcTemplate, null, args);
        if (!DaoHelper.isBlank(having)) {
            sqlBuilder.append(" HAVING ");
            sqlBuilder.append(having);
        }
        return sqlBuilder.toString();
    }

    private static void joinMainTable(StringBuilder joinBuilder, JoinModel model, TableModel tableModel) {
        joinBuilder.append(" FROM ");
        joinBuilder.append(tableModel.table);
        if(!StringUtil.isBlank(model.getAliasName())) {
            joinBuilder.append(" AS ");
            joinBuilder.append(model.getAliasName());
        }
    }

    private static void appendJoinTable(JdbcTemplate jdbcTemplate, List<SqlParameter> args, StringBuilder joinBuilder, JoinModel model, JoinType joinType, TableModel tableModel) throws CommonException {
        joinBuilder.append(" ");
        joinBuilder.append(joinType.getType());
        joinBuilder.append(" ");
        joinBuilder.append(tableModel.table);
        if(!StringUtil.isBlank(model.getAliasName())) {
            joinBuilder.append(" AS ");
            joinBuilder.append(model.getAliasName());
        }
        joinBuilder.append(" ON ");
        joinBuilder.append(model.on().parseSql(jdbcTemplate, tableModel.table, args));
    }

    public static String handleSelectCount(JdbcTemplate jdbcTemplate, Map<JoinModel, JoinType> models, ConditionModel condition, List<SqlParameter> args) throws CommonException {
        StringBuilder sqlBuilder = new StringBuilder();
        Iterator<Map.Entry<JoinModel, JoinType>> iterator = models.entrySet().iterator();
        sqlBuilder.append("SELECT COUNT(*) ");
        boolean flag=false;
        while (iterator.hasNext()){
            Map.Entry<JoinModel, JoinType> next = iterator.next();
            JoinModel model = next.getKey();
            JoinType joinType = next.getValue();

            TableModel tableModel = getTableModel(model.getJoinModel());

            if(flag) {
                appendJoinTable(jdbcTemplate, args, sqlBuilder, model, joinType, tableModel);
            }else{
                flag=true;
                joinMainTable(sqlBuilder, model, tableModel);
            }
        }
        String where = condition.where().parseSql(jdbcTemplate, null, args);
        if (!DaoHelper.isBlank(where)) {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(where);
        }

        String having = condition.having().parseSql(jdbcTemplate, null, args);
        if (!DaoHelper.isBlank(having)) {
            sqlBuilder.append(" HAVING ");
            sqlBuilder.append(having);
        }
        return sqlBuilder.toString();
    }

    private static void buildSelectPost(String table, JdbcTemplate jdbcTemplate, AbstractModel condition, StringBuilder sqlBuilder, List<SqlParameter> args) {
        String groupBy = condition.groupBy();
        if(!StringUtil.isBlank(groupBy)) {
            sqlBuilder.append(" GROUP BY ");
            sqlBuilder.append(groupBy);
        }
        String orderBy = condition.orderBy();
        if(!StringUtil.isBlank(orderBy)) {
            sqlBuilder.append(" ORDER BY ");
            sqlBuilder.append(orderBy);
        }
        Integer limitCount = condition.limitCount();
        Integer limitOffset = condition.limitOffset();
        if (null != limitCount && limitCount>0) {
            sqlBuilder.append(" LIMIT ");
            if (null != limitOffset && limitOffset>=0) {
                sqlBuilder.append(limitOffset);
                sqlBuilder.append(",");
            }
            sqlBuilder.append(limitCount);
        }
    }

    public static <T extends AbstractModel> String handleUpdate(JdbcTemplate jdbcTemplate, T updateModel, List<SqlParameter> args, List<String> incrementColumns) throws CommonException {
        StringBuilder sqlBuilder = new StringBuilder();
        TableModel tableModel = getTableModel(updateModel.getClass());
        sqlBuilder.append(String.format("UPDATE %s SET ", DaoHelper.quote(tableModel.table)));
        boolean flag = false;
        for (ColumnModel columnModel : tableModel.columnModels) {
            Field field = columnModel.field;
            Object columnValue = ModelHelper.getFieldValue(updateModel, field);
            if (null != columnValue) {
                if (flag) {
                    sqlBuilder.append(",");
                }
                sqlBuilder.append(quote(columnModel.column));
                if(null!=incrementColumns && incrementColumns.contains(field.getName())){
                    //使用增量更新
                    sqlBuilder.append(" = (");
                    sqlBuilder.append(quote(columnModel.column));
                    sqlBuilder.append(" + ");
                    sqlBuilder.append(columnValue);
                    sqlBuilder.append(")");
                }else{
                    sqlBuilder.append(" = ?");
                    addValueToArgs(jdbcTemplate, args, tableModel.table, columnModel.column, columnValue);
                }
                flag = true;
            }
        }

        String where = updateModel.where().parseSql(jdbcTemplate, tableModel.table, args);
        if (!DaoHelper.isBlank(where)) {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(where);
        }else{
            throw CommonExceptionHelper.daoException("缺少更新条件",null);
        }
        return sqlBuilder.toString();
    }

    public static <T extends AbstractModel> String handleDelete(JdbcTemplate jdbcTemplate, T condition, List<SqlParameter> args) throws CommonException {
        StringBuilder sqlBuilder = new StringBuilder();
        TableModel tableModel = getTableModel(condition.getClass());
        sqlBuilder.append(String.format("DELETE FROM %s", DaoHelper.quote(tableModel.table)));
        String where = condition.where().parseSql(jdbcTemplate, tableModel.table, args);
        if (!DaoHelper.isBlank(where)) {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(where);
        }else{
            throw CommonExceptionHelper.daoException("缺少删除条件",null);
        }
        return sqlBuilder.toString();
    }

    public static <T extends AbstractModel> String handleInsert(JdbcTemplate jdbcTemplate, T model, List<SqlParameter> args) throws CommonException {
        List<T> models = new ArrayList<>();
        models.add(model);
        return handleInsert(jdbcTemplate,models, null, args);
    }

    public static <T extends AbstractModel> String handleInsert(JdbcTemplate jdbcTemplate, List<T> models, String[] updateColumns, List<SqlParameter> args) throws CommonException {
        StringBuilder sqlBuilder = new StringBuilder();
        if(models.isEmpty())
            throw CommonExceptionHelper.daoException("没有数据需要插入",null);
        TableModel tableModel = getTableModel(models.get(0).getClass());
        sqlBuilder.append(String.format("INSERT INTO %s (", DaoHelper.quote(tableModel.table)));
        StringBuilder valueBuilder = new StringBuilder();

        for (int i = 0; i < models.size(); i++) {
            T model = models.get(i);
            boolean flag = false;
            if(i>0)
                valueBuilder.append(",");
            valueBuilder.append("(");
            for (ColumnModel columnModel : tableModel.columnModels) {
                Field field = columnModel.field;
                Object columnValue = ModelHelper.getFieldValue(model, field);
                if (null != columnValue) {
                    if (flag) {
                        if(i==0) sqlBuilder.append(",");
                        valueBuilder.append(",");
                    }
                    if(i==0) sqlBuilder.append(quote(columnModel.column));
                    valueBuilder.append("?");
                    addValueToArgs(jdbcTemplate, args, tableModel.table, columnModel.column, columnValue);
                    flag = true;
                }
            }
            valueBuilder.append(")");
        }

        sqlBuilder.append(") VALUES ");
        sqlBuilder.append(valueBuilder.toString());
        if(null != updateColumns && updateColumns.length>0){
            boolean flag = false;
            sqlBuilder.append(" ON DUPLICATE KEY UPDATE ");
            for (String updateColumn: updateColumns) {
                if(flag)
                    sqlBuilder.append(",");
                sqlBuilder.append(quote(updateColumn));
                sqlBuilder.append("=VALUES(");
                sqlBuilder.append(quote(updateColumn));
                sqlBuilder.append(")");
                flag=true;
            }
        }
        return sqlBuilder.toString();
    }

    private static SqlParameterValue getSqlValueParameter(JdbcTemplate jdbcTemplate, String table, String column, Object columnValue) throws CommonException {
        Map<String, SqlParameter> columnMap = paramMap.get(table);
        if (null == columnMap) {
            columnMap = parseTable(jdbcTemplate, table);
            paramMap.put(table, columnMap);
        }

        SqlParameter param = columnMap.get(column);
        if(null == param){
            throw CommonExceptionHelper.daoException(String.format("没有找到[%s]",column),null);
        }
        return new SqlParameterValue(param, columnValue);
    }

    public static Map<String, SqlParameter> parseTable(JdbcTemplate jdbcTemplate, String table) {
        Map<String, SqlParameter> map = new LinkedHashMap<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet columnResultSet = null;
        Field typeNameField = null;
        try {
            typeNameField = SqlParameter.class.getDeclaredField("typeName");
            typeNameField.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            statement = connection.createStatement();
            columnResultSet = statement.executeQuery(String.format("SELECT * FROM %s LIMIT 1", table));
            ResultSetMetaData metaData = columnResultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                int columnType = metaData.getColumnType(i);
                String columnTypeName = metaData.getColumnTypeName(i);
                int scale = metaData.getScale(i);
                SqlParameter parameter = new SqlParameter(columnName, columnType, scale);
                if(null != typeNameField){
                    try {
                        typeNameField.set(parameter, columnTypeName);
                    } catch (IllegalAccessException e) {
                    }
                }
                map.put(columnName, parameter);
            }
        } catch (SQLException e) {
            throw CommonExceptionHelper.daoRuntimeException(String.format("解析表%s失败", table), e);
        } finally {
            try {
                if (null != columnResultSet)
                    columnResultSet.close();
            } catch (SQLException e) {
                logger.error("关闭结果集时出错.", e);
            }
            try {
                if (null != statement)
                    statement.close();
            } catch (SQLException e) {
                logger.error("关闭statement出错.", e);
            }
            try {
                if (null != connection)
                    connection.close();
            } catch (SQLException e) {
                logger.error("关闭连接时出错.", e);
            }
        }
        return map;
    }
}

class TableModel {
    String table;
    Class type;
    List<ColumnModel> columnModels = new ArrayList<ColumnModel>();
    ColumnModel version;
}

class ColumnModel {
    String column;
    Field field;
    boolean pkColumn;
    boolean versionColumn;
}