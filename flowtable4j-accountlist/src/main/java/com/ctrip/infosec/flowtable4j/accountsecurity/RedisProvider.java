package com.ctrip.infosec.flowtable4j.accountsecurity;

import com.ctrip.infosec.sars.util.GlobalConfig;
import com.ctrip.infosec.sars.util.mapper.JsonMapper;
import credis.java.client.CacheProvider;
import credis.java.client.setting.RAppSetting;
import credis.java.client.util.CacheFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by zhangsx on 2015/3/17.
 */
@Component
public class RedisProvider {
    private final String redisCluster = "AccountRiskControl";
    private CacheProvider cache;
    JsonMapper mapper = new JsonMapper();
    public RedisProvider() {
        RAppSetting.setAppID(GlobalConfig.getString("APPID"));
        RAppSetting.setLoggingServerIP(GlobalConfig.getString("CLogging.serverIp"));//"collector.logging.uat.qa.nt.ctripcorp.com"
        RAppSetting.setLoggingServerPort(GlobalConfig.getString("CLogging.serverPort"));
        RAppSetting.setLogging(true);
        RAppSetting.setCRedisServiceUrl(GlobalConfig.getString("CRedis.serviceUrl"));//"http://ws.config.framework.fws.qa.nt.ctripcorp.com/configws/"
        cache = CacheFactory.GetProvider(redisCluster);
    }

    public<T> List<T> getBWGValue(String key,Class<T> classType) {
        List<T> listOfResult = new ArrayList<T>();
        if (cache != null) {
            Set<String> rules = cache.zrange(key, 0, -1);
            if (rules != null) {
                for (String str : rules) {
                    T item = mapper.fromJson(str, classType);
                    listOfResult.add(item);
                }
            }
        }
        return listOfResult;
    }

    public CacheProvider getCache() {
        return this.cache;
    }
}