package com.ctrip.infosec.flowtable4j.dal;

import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.ctrip.infosec.sars.util.GlobalConfig;
import com.ctrip.infosec.sars.util.mapper.JsonMapper;
import com.google.common.base.Strings;
import credis.java.client.CacheProvider;
import credis.java.client.setting.RAppSetting;
import credis.java.client.util.CacheFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhangsx on 2015/3/17.
 */
@Component
public class RedisProvider {
    private final String redisCluster = "CounterServer_03";
    JsonMapper mapper = new JsonMapper();
    public RedisProvider() {
        RAppSetting.setAppID(GlobalConfig.getString("APPID"));
        RAppSetting.setLoggingServerIP(GlobalConfig.getString("CLogging.serverIp"));//"collector.logging.uat.qa.nt.ctripcorp.com"
        RAppSetting.setLoggingServerPort(GlobalConfig.getString("CLogging.serverPort"));
        RAppSetting.setLogging(true);
        RAppSetting.setCRedisServiceUrl(GlobalConfig.getString("CRedis.serviceUrl"));//"http://ws.config.framework.fws.qa.nt.ctripcorp.com/configws/"
     }

    /**
     * 从Set中获取所有成员，反序列化
     * @param key
     * @param classType
     * @param <T>
     * @return
     */
    public <T> List<T> mgetBySet(String key, Class<T> classType) {
        List<T> listOfResult = new ArrayList<T>();
        CacheProvider cache = getCache();
        if (cache != null) {
            Set<String> rules = cache.smembers(key);
            if (rules != null) {
                for (String str : rules) {
                    T item = mapper.fromJson(str, classType);
                    listOfResult.add(item);
                }
            }
        }
        return listOfResult;
    }

    /**
     * 从Set中获取所有成员
     * @param key
     * @return
     */
    public Set<String> mgetBySet(String key) {
        CacheProvider cache = getCache();
        if (cache != null) {
            return cache.smembers(key);
        }
        return null;
    }

    /**
     * 从Set中移除一个元素
     * @param key
     * @param value
     * @return
     */
    public boolean rmSetValue(String key,String value) {
        CacheProvider cache = getCache();
        if (cache != null) {
            return cache.srem(key, value)>0;
        }
        return false;
    }

    /**
     * Set中增加一个元素
     * @param key
     * @param value
     * @return
     */
    public boolean set2Set(String key,String value) {
        CacheProvider cache = getCache();
        if (cache != null) {
            return cache.sadd(key, value)>0;
        }
        return false;
    }

    /**
     * 从HashSet中获取某属性值
     * @param key
     * @param field
     * @return
     */
    public String getByHash(String key,String field){
        CacheProvider cache = getCache();
        if (cache != null) {
           return cache.hget(key, field);
        }
        return null;
    }

    /**
     * 从Hash中获取某属性，并反序列化
     * @param key
     * @param field
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getByHash(String key,String field,Class<T> clazz){
        CacheProvider cache = getCache();
        if (cache != null) {
            String value= cache.hget(key, field);
            if(!Strings.isNullOrEmpty(value)){
                return mapper.fromJson(value,clazz);
            }
        }
        return null;
    }

    /**
     * Hash中存入某属性
     * @param key
     * @param field
     * @param value
     * @return
     */
    public boolean set2Hash(String key, String field, String value){
        CacheProvider cache = getCache();
        if (cache != null) {
            return cache.hset(key, field,value);
        }
        return false;
    }

    /**
     * 同时存入多个属性
     * @param key
     * @param hash
     * @return
     */
    public boolean mset2Hash(String key, Map<String, String> hash){
        CacheProvider cache = getCache();
        if (cache != null) {
            return cache.hmset(key, hash);
        }
        return false;
    }

    /**
     * 设置Key的TTL，单位为秒
     * @param key
     * @param offset
     * @return
     */
    public boolean expire(String key,int offset){
        CacheProvider cache = getCache();
        if (cache != null) {
            return cache.expire(key, offset);
        }
        return false;
    }

    /**
     * 从Hash中获取多个元素
     * @param hashKey
     * @param field
     * @return
     */
    public List<String> mgetByHash(String hashKey,String... field){
        CacheProvider cache = getCache();
        if (cache != null) {
            return cache.hmget(hashKey, field);
        }
        return null;
    }

    private CacheProvider getCache() {
        return CacheFactory.GetProvider(redisCluster);
    }

    public void cacheProductInfo(String orderId,String orderType,PO po){

    }

    public PO getCachedProductInfo(String orderId,String orderType,PO po){
       return new PO();
    }
}