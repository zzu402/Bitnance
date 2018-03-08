package com.hzz.common.dao;


import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 用于生成数据库表对应的模型代码
 * Created by hongshuiqiao on 2017/6/20.
 */
public class ModelGenerator {
    private String pkg="com.jibb.model.dao";
    private String catalog="jibaobao";
    private String schema=null;
    private String tablePrefix="jibb_";
    private File outputDir = new File("./jibb-model/target/modelDir");
    private List<String> importList = new ArrayList<>();

    public void generate(JdbcTemplate jdbcTemplate){
        try {
            Connection connection = jdbcTemplate.getDataSource().getConnection();

            List<String> tableList = new ArrayList<>();
            ResultSet tables = connection.getMetaData().getTables(catalog, schema, "", new String[]{"TABLE"});
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableList.add(tableName);
            }

            tables.close();
            connection.close();

            if(!outputDir.exists()){
                outputDir.mkdir();
            }

            for (String table : tableList) {
                String result = generateTable(jdbcTemplate, table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getPkColumns(JdbcTemplate jdbcTemplate, String table){
        List<String> pkList = new ArrayList<>();
        try {
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            ResultSet primaryKeys = connection.getMetaData().getPrimaryKeys(catalog, schema, table);
            while (primaryKeys.next()){
                pkList.add(primaryKeys.getString("COLUMN_NAME"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkList;
    }

    private String firstUpcase(String value) {
        return Character.toUpperCase(value.charAt(0))+value.substring(1);
    }

    private String propertyName(String value) {
        String[] segments = value.split("[_-]");
        String result="";
        for (String segment : segments) {
            if(result.length()==0){
                result=result+segment;
            }else{
                result=result+firstUpcase(segment);
            }
        }
        return result;
    }

    private String generateTable(JdbcTemplate jdbcTemplate, String table) {
        String className = table;
        if(className.startsWith(tablePrefix)){
            className = className.substring(tablePrefix.length());
        }
        String[] segments = className.split("[_-]");
        className="";
        for (String segment : segments) {
            className=className+firstUpcase(segment);
        }

        List<String> pkList = getPkColumns(jdbcTemplate, table);

        StringBuilder builder = new StringBuilder();
        builder.append("package ");
        builder.append(pkg);
        builder.append(";\r\n\r\n");
        for (String importItem : importList) {
            builder.append(String.format("import %s;\r\n",importItem));
        }
        builder.append("\r\n");
        builder.append("/**\r\n");
//        builder.append(" * Created by hongshuiqiao on "+ DateUtil.format("yyyy/MM/dd", new Date())+".\r\n");
        builder.append(" * Created by hongshuiqiao.\r\n");
        builder.append(" */\r\n");
        builder.append("@Table(\""+table+"\")\r\n");
        builder.append("public class "+className+"  extends AbstractModel<"+className+"> {\r\n");
        StringBuilder methodBuilder = new StringBuilder();
        Map<String, SqlParameter> tableMap = DaoHelper.parseTable(jdbcTemplate, table);
        Iterator<Map.Entry<String, SqlParameter>> iterator = tableMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, SqlParameter> next = iterator.next();
            String key = next.getKey();
            SqlParameter value = next.getValue();
            String propertyName = propertyName(key);
            if("version".equalsIgnoreCase(propertyName)){
                builder.append("    @Version");
            }else{
                builder.append("    @Column");
            }

            if (pkList.contains(key)){
                builder.append("(");
                //主键
                if(!key.equals(propertyName)){
                    builder.append("value=\""+key+"\", ");
                }
                builder.append("pk=true");
                builder.append(")");
            }else{
                if(!key.equals(propertyName)){
                    builder.append("(\""+key+"\")");
                }
            }
            String dataType = getDataType(value);
            buildField(builder, propertyName, dataType);

            buildMethod(methodBuilder, propertyName, dataType);
        }

        builder.append("\r\n");
        builder.append(methodBuilder.toString());
        builder.append("}\r\n");

        String result = builder.toString();
        try {
            FileUtils.write(new File(outputDir, className+".java"), result, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void buildField(StringBuilder builder, String fieldName, String fieldType) {
        builder.append("\r\n");
        builder.append("    private ");
        builder.append(fieldType);
        builder.append(" "+ fieldName +";\r\n");
    }

    private void buildMethod(StringBuilder methodBuilder, String fieldName, String fieldType) {
        methodBuilder.append("    public "+ fieldType +" get"+firstUpcase(fieldName)+"() {\r\n");
        methodBuilder.append("        return "+ fieldName +";\r\n");
        methodBuilder.append("    }\r\n\r\n");

        methodBuilder.append("    public void set"+firstUpcase(fieldName)+"("+ fieldType +" "+ fieldName +") {\r\n");
        methodBuilder.append("        this."+ fieldName +" = "+ fieldName +";\r\n");
        methodBuilder.append("    }\r\n\r\n");
    }

    private String getDataType(SqlParameter parameter) {
        String typeName = parameter.getTypeName().toUpperCase();
        if(typeName.contains("VARCHAR") || typeName.contains("CHAR") || typeName.contains("BLOB") || typeName.contains("TEXT"))
            return "String";
        if(typeName.contains("DECIMAL") || typeName.contains("DOUBLE"))
            return "Double";
        if(typeName.contains("FLOAT"))
            return "Float";
        if(typeName.contains("BIGINT") || typeName.contains("MEDIUMINT"))
            return "Long";
        if(typeName.contains("INT"))
            return "Integer";
        if(typeName.contains("DATETIME") || typeName.contains("TIMESTAMP") || typeName.contains("TIME") || typeName.contains("YEAR") || typeName.contains("DATE"))
            return "Time";
        return "String";
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public List<String> getImportList() {
        return importList;
    }

    public static void main(String[] args) {
//        ModelGenerator generator = new ModelGenerator();
//        generator.setCatalog("jibaobao");
//        generator.setOutputDir(new File("D:/test/"));
//        generator.setPkg("com.jibb.model");
//        generator.setTablePrefix("jibb_");
//        generator.getImportList().add("com.jibb.dao.annotation.*");
//        generator.getImportList().add("com.jibb.dao.AbstractModel");
//        generator.generate(jdbcTemplate);
    }
}
