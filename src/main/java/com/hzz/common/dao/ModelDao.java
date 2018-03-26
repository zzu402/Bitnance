package com.hzz.common.dao;

import com.hzz.exception.CommonException;
import com.hzz.exception.CommonExceptionHelper;
import com.hzz.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 模型各属性的类型使用对象，不允许使用基本类型，如果值为null，在具体的操作中将被忽略
 * Created by hongshuiqiao on 2017/6/7.
 */
@Component
public class ModelDao {
    private Logger logger = LoggerFactory.getLogger(ModelDao.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T extends AbstractModel> List<T> select(T condition) throws CommonException {
        return select(condition, false);
    }

    private <T extends AbstractModel> void handleConditionWhere(T model, T condition) throws CommonException {
        TableModel tableModel = DaoHelper.getTableModel(condition.getClass());
        List<ColumnModel> columnModels = tableModel.columnModels;
        for (ColumnModel columnModel : columnModels) {
            String column = columnModel.column;
            Object columnValue = ModelHelper.getFieldValue(condition, columnModel.field);
            if(null == columnValue)
                continue;
            model.where().add(new ConditionOp(column, OpType.EQ, columnValue));
        }
    }

    public <T extends AbstractModel> List<T> select(T condition, boolean forUpdate) throws CommonException {
        handleConditionWhere(condition, condition);

        List<SqlParameter> args = new ArrayList<SqlParameter>();
        String sql = DaoHelper.handleSelect(jdbcTemplate, condition, args, forUpdate);
        Class<T> type = (Class<T>) condition.getClass();
        return select(sql, args, type);
    }

    public <T> List<T> select(String sql, List<SqlParameter> args, Class<T> resultType){
        String uuid = null;
        if(logger.isDebugEnabled()){
            uuid= StringUtil.uuid();
            logger.debug(String.format("[%s] select : %s", uuid, sql));
        }
        List<T> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<T>(resultType), args.toArray());
        if(logger.isDebugEnabled())
            logger.debug(String.format("[%s] select success, return count %d", uuid, result.size()));
        return result;
    }

    public List<Map<String,Object>> select(String sql, List<SqlParameter> args){
        String uuid = null;
        if(logger.isDebugEnabled()){
            uuid= StringUtil.uuid();
            logger.debug(String.format("[%s] select : %s", uuid, sql));
        }
        List<Map<String,Object>> result = jdbcTemplate.query(sql, new ColumnMapRowMapper(), args.toArray());
        if(logger.isDebugEnabled())
            logger.debug(String.format("[%s] select success, return count %d", uuid, result.size()));
        return result;
    }

    public <T extends AbstractModel> int selectCount(String sql, List<SqlParameter> args) throws CommonException {
        String uuid = null;
        if(logger.isDebugEnabled()){
            uuid= StringUtil.uuid();
            logger.debug(String.format("[%s] selectCount : %s", uuid, sql));
        }
        Integer result = jdbcTemplate.queryForObject(sql, new SingleColumnRowMapper<Integer>(Integer.class), args.toArray());
        if(logger.isDebugEnabled())
            logger.debug(String.format("[%s] selectCount success, return %d", uuid, result));
        return result;
    }

    public <T extends AbstractModel> int selectCount(T condition) throws CommonException {
        handleConditionWhere(condition, condition);

        List<SqlParameter> args = new ArrayList<SqlParameter>();
        String sql = DaoHelper.handleSelectCount(jdbcTemplate, condition, args);
        return selectCount(sql,args);
    }

    public <T> List<T> select(Map<JoinModel, JoinType> models, ConditionModel condition, Class<T> resultType) throws CommonException {
        return doSelect(models, condition, new BeanPropertyRowMapper<>(resultType));
    }

    public List<Map<String,Object>> select(Map<JoinModel, JoinType> models, ConditionModel condition) throws CommonException {
        return doSelect(models,condition, new ColumnMapRowMapper());
    }

    public List<Map<String,AbstractModel>> selectModels(Map<JoinModel, JoinType> models, ConditionModel condition) throws CommonException {
        return doSelect(models,condition, new MultiBeanRowMapper(models.keySet().toArray(new JoinModel[models.size()])));
    }

    private <T> List<T> doSelect(Map<JoinModel, JoinType> models, ConditionModel condition, RowMapper rowMapper) throws CommonException {
        List<SqlParameter> args = new ArrayList<SqlParameter>();
        String sql = DaoHelper.handleSelect(jdbcTemplate, models, condition, args);
        String uuid = null;
        if(logger.isDebugEnabled()){
            uuid= StringUtil.uuid();
            logger.debug(String.format("[%s] select : %s", uuid, sql));
        }
        List<T> result = jdbcTemplate.query(sql, rowMapper, args.toArray());
        if(logger.isDebugEnabled())
            logger.debug(String.format("[%s] select success, return count %d", uuid, result.size()));
        return result;
    }

    public <T extends AbstractModel> int selectCount(Map<JoinModel, JoinType> models, ConditionModel condition) throws CommonException {
        List<SqlParameter> args = new ArrayList<SqlParameter>();
        String sql = DaoHelper.handleSelectCount(jdbcTemplate, models, condition, args);
        String uuid = null;
        if(logger.isDebugEnabled()){
            uuid= StringUtil.uuid();
            logger.debug(String.format("[%s] selectCount : %s", uuid, sql));
        }
        Integer result = jdbcTemplate.queryForObject(sql, new SingleColumnRowMapper<Integer>(Integer.class), args.toArray());
        if(logger.isDebugEnabled())
            logger.debug(String.format("[%s] selectCount success, return %d", uuid, result));
        return result;
    }

    public <T extends AbstractModel> int update(T updateModel) throws CommonException {
        return update(updateModel,new ArrayList<String>(), false);
    }

    public <T extends AbstractModel> int update(T updateModel, T condition) throws CommonException {
        return update(updateModel, condition, false);
    }

    public <T extends AbstractModel> int update(T updateModel, T condition, boolean withVersion) throws CommonException {
        handleConditionWhere(updateModel, condition);
        return update(updateModel,new ArrayList<String>(),withVersion);
    }

    public <T extends AbstractModel> int incrementUpdate(T updateModel, T condition, List<String> incrementColumns) throws CommonException {
        handleConditionWhere(updateModel, condition);
        return update(updateModel,incrementColumns,false);
    }

    public <T extends AbstractModel> int incrementUpdate(T updateModel, List<String> incrementColumns) throws CommonException {
        return update(updateModel,incrementColumns,false);
    }

    public <T extends AbstractModel> int update(T updateModel, List<String> incrementColumns, boolean withVersion) throws CommonException {
        TableModel tableModel = DaoHelper.getTableModel(updateModel.getClass());
        List<String> incrementColumnList = new ArrayList<>();
        if(null!=incrementColumns)
            incrementColumnList.addAll(incrementColumns);
        if(withVersion && null != tableModel.version){
            incrementColumnList.add(tableModel.version.column);
            try {
                tableModel.version.field.set(updateModel, 1);//版本加1
            } catch (IllegalAccessException e) {
                throw CommonExceptionHelper.daoException("设置版本号时出错",e);
            }
        }
        List<SqlParameter> args = new ArrayList<SqlParameter>();
        String sql = DaoHelper.handleUpdate(jdbcTemplate, updateModel, args, incrementColumnList);
        return update(sql,args);
    }

    public int update(String sql, List<SqlParameter> args){
        String uuid = null;
        if(logger.isDebugEnabled()){
            uuid= StringUtil.uuid();
            logger.debug(String.format("[%s] update : %s", uuid, sql));
        }
        Integer result = jdbcTemplate.update(sql, new ArgumentPreparedStatementSetter(args.toArray()));
        if(logger.isDebugEnabled())
            logger.debug(String.format("[%s] update success, return %d", uuid, result));
        return result;
    }

    public <T extends AbstractModel> int delete(T condition) throws CommonException {
        handleConditionWhere(condition, condition);
        List<SqlParameter> args = new ArrayList<SqlParameter>();
        String sql = DaoHelper.handleDelete(jdbcTemplate, condition, args);
        return update(sql,args);
    }

    public <T extends AbstractModel> int insert(T insertModel) throws CommonException {
        List<T> insertModels = new ArrayList<>();
        insertModels.add(insertModel);
        return batchInsert(insertModels);
    }
    public <T extends AbstractModel> int insertOrUpdate(T model, String[] updateColumns) throws CommonException {
        List<T> list = new ArrayList();
        list.add(model);
        return this.batchInsertOrUpdate(list, updateColumns);
    }

    public <T extends AbstractModel> int batchInsert(List<T> insertModels) throws CommonException {
        return batchInsertOrUpdate(insertModels, null);
    }

    public <T extends AbstractModel> int batchInsertOrUpdate(List<T> insertModels, String[] updateColumns) throws CommonException {
        int count = 0;
        int batchSize=1000;
        int begin = 0;
        int end = 0;
        while (true){
            end=Math.min((begin+batchSize),(insertModels.size()));
            List<T> models = insertModels.subList(begin,end);
            count=count+doBatchInsertOrUpdate(models,updateColumns);
            begin=begin+models.size();
            if(insertModels.size()<=begin)
                break;
        }
        return count;
    }

    private <T extends AbstractModel> int doBatchInsertOrUpdate(List<T> insertModels, String[] updateColumns) throws CommonException {
        List<SqlParameter> args = new ArrayList<SqlParameter>();
        String sql = DaoHelper.handleInsert(jdbcTemplate, insertModels, updateColumns, args);
        return update(sql,args);
    }

    public <T extends AbstractModel> T insertAndReturn(T condition) throws CommonException {
        List<SqlParameter> args = new ArrayList<SqlParameter>();
        String sql = DaoHelper.handleInsert(jdbcTemplate, condition, args);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String[] pkColums = DaoHelper.getPkColums(condition.getClass());
        String uuid = null;
        if(logger.isDebugEnabled()){
            uuid= StringUtil.uuid();
            logger.debug(String.format("[%s] insertAndReturn : %s", uuid, sql));
        }
        PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory(sql, args);
        factory.setReturnGeneratedKeys(true);
        factory.setGeneratedKeysColumnNames(pkColums);
        PreparedStatementCreator creator = factory.newPreparedStatementCreator(sql, args.toArray());
        jdbcTemplate.update(creator, keyHolder);
        List<Object> keys = new ArrayList<>(keyHolder.getKeys().values());
        Map<String, Object> pkObj = new LinkedHashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            pkObj.put(pkColums[0], keys.get(0));
        }

        try {
            T newCon = (T)condition.getClass().newInstance();
            ModelHelper.mergeModels(newCon, pkObj);
            List<T> list = select(newCon);
            if(logger.isDebugEnabled())
                logger.debug(String.format("[%s] insertAndReturn success", uuid));
            if(list.size()>0)
                return list.get(0);
            return null;
        } catch (InstantiationException e) {
            throw CommonExceptionHelper.daoException("插入后查询结果失败", e);
        } catch (IllegalAccessException e) {
            throw CommonExceptionHelper.daoException("插入后查询结果失败", e);
        }
    }

}
