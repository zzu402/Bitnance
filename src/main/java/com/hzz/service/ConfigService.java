package com.hzz.service;
import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.main.Api;
import com.hzz.model.Config;
import com.hzz.utils.DaoUtils;
import com.hzz.utils.JsonMapper;
import com.hzz.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/4/2
 */
public class ConfigService {

    private Logger logger = LoggerFactory.getLogger(ConfigService.class);
    private ModelDao modelDao = DaoUtils.getDao(DaoUtils.getTemplate());
    public Api api = Api.getApi();
    /*
      获取数据库配置的币种
      @param configTypePre 买或卖前缀
      @status 状态 0 表示不可用 1表示正常
     */
    public List<Config> getConfigSetList(String configTypePre, Integer status) {
        Config config = new Config();
        config.setType(configTypePre + "SetConfig");
        if (status != null && status > 0)
            config.setStatus(status);
        List<Config> list = null;
        try {
            list = modelDao.select(config);
        } catch (CommonException e) {
            logger.error("查询币种配置信息异常", e);
        }
        if (list == null || list.isEmpty())
            return null;
        return list;
    }


    /*
        对List 中config进行优先级排序
     */
    public Config[] sortConfigListByPriority(List<Config> list) {
        Config[] configs = list.toArray(new Config[list.size()]);
        Config config_i = null;
        Config config_j = null;
        Map<String, String> configInfo_i;
        Map<String, String> configInfo_j;
        for (int i = 0; i < configs.length; i++) {
            config_i = configs[i];
            configInfo_i = JsonMapper.nonDefaultMapper().fromJson(config_i.getConfigInfo(), Map.class);
            Integer priority_i = Integer.parseInt(configInfo_i.get("priority"));

            for (int j = i + 1; j < configs.length; j++) {
                config_j = configs[j];
                configInfo_j = JsonMapper.nonDefaultMapper().fromJson(config_j.getConfigInfo(), Map.class);
                Integer priority_j = Integer.parseInt(configInfo_j.get("priority"));
                if (priority_j > priority_i) {
                    configs[i] = config_j;
                    configs[j] = config_i;
                    config_i = config_j;
                    continue;
                }
            }

        }
        return configs;
    }

    /*
    * 获取手动买入/卖出币种设置的配置信息
    */
    public Map<String, Config> getConfigSets(String configTypePre, Integer status) {
        List<Config> list = getConfigSetList(configTypePre, status);
        if (list == null)
            return null;
        Map<String, Config> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++)
            map.put(list.get(i).getSymbol(), list.get(i));
        return map;
    }

    /**
     * 获取配置信息
     *
     * @param status
     * @param symbol
     * @param type
     * @return
     */
    public List<Config> getConfigs(Integer status, String symbol, String type) {

        Config config = new Config();
        config.setType(type);
        if (!StringUtil.isBlank(symbol))
            config.setSymbol(symbol);
        if (status != null && status > 0)
            config.setStatus(status);
        List<Config> list = null;
        try {
            list = modelDao.select(config);
        } catch (CommonException e) {
            logger.error("查询配置信息异常", e);
        }
        if (list == null || list.isEmpty())
            return null;
        return list;
    }

    /*
        插入或更新Config
     */
    public boolean insertOrUpdateConfigs(List<Config> list) {
        if (list == null || list.isEmpty())
            return false;
        try {
            modelDao.batchInsertOrUpdate(list, new String[]{"symbol", "type", "configInfo", "description", "status", "createTime"});
        } catch (CommonException e) {
            logger.error("插入更新配置信息异常", e);
            return  false;
        }
        return  true;

    }






}
