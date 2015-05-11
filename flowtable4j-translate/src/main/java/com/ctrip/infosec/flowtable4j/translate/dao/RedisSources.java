package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.flowtable4j.translate.common.GlobalConfig;
import credis.java.client.CacheProvider;
import credis.java.client.setting.RAppSetting;
import credis.java.client.util.CacheFactory;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Created by lpxie on 15-3-31.
 */
@Repository
public class RedisSources
{
    private static Logger logger = LoggerFactory.getLogger(RedisSources.class);

    private static CacheProvider cacheProvider;
    private static int expireTime = 3600 * 24;//一天
    /*redis相关属性检查*/
    static final String serviceUrl = GlobalConfig.getString("CRedis.serviceUrl");
    static final String appId = GlobalConfig.getString("appId");
    static final String provider = GlobalConfig.getString("CRedis.provider");

    static void check() {
        Validate.notEmpty(serviceUrl, "在GlobalConfig.properties里没有找到\"CRedis.serviceUrl\"配置项.");
        Validate.notEmpty(appId, "在GlobalConfig.properties里没有找到\"appId\"配置项.");
        Validate.notEmpty(provider, "在GlobalConfig.properties里没有找到\"CRedis.provider\"配置项.");
    }

    public void init(){
        check();
        logger.info("Start to connect redis");
        RAppSetting.setAppID(appId);
        RAppSetting.setCRedisServiceUrl(serviceUrl);
        RAppSetting.setLogging(false);
        try {
            cacheProvider = CacheFactory.GetProvider(provider);
        } catch (RuntimeException exp) {
            logger.error("Connect to redis failed by " + exp.getMessage());
        }
    }

    public static String getValue(String key)
    {
        return cacheProvider.get(key);
    }

    public static void setKeyValue(String key,String value)
    {
        cacheProvider.set(key,value);
        cacheProvider.expire(key,expireTime);
    }
}
