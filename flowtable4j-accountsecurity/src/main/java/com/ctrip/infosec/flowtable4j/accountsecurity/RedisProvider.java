package com.ctrip.infosec.flowtable4j.accountsecurity;

import com.ctrip.flowtable4j.core.utils.JsonMapper;
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
        //TODO 设置变量

        RAppSetting.setAppID("100000807");
        RAppSetting.setLoggingServerIP("collector.logging.uat.qa.nt.ctripcorp.com");//"collector.logging.uat.qa.nt.ctripcorp.com"
        RAppSetting.setLoggingServerPort("63100");
        RAppSetting.setLogging(true);
        RAppSetting.setCRedisServiceUrl("http://ws.config.framework.fws.qa.nt.ctripcorp.com/configws/");//"http://ws.config.framework.fws.qa.nt.ctripcorp.com/configws/"
        cache = CacheFactory.GetProvider(redisCluster);
    }

    public List<RedisStoreItem> GetBWGFromRedis(String key) {
        List<RedisStoreItem> listOfResult = new ArrayList<RedisStoreItem>();
        if (cache != null) {
            Set<String> set = cache.zrange(key, 0, -1);
            if (set != null) {
                for (String str : set) {
                    RedisStoreItem item = mapper.fromJson(str, RedisStoreItem.class);
                    listOfResult.add(item);
                }
            }
        }
        return listOfResult;
    }
}
