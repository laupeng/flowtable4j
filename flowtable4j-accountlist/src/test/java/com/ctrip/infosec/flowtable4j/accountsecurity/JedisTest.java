package com.ctrip.infosec.flowtable4j.accountsecurity;

import org.junit.Test;

import java.util.*;

/**
 * Created by zhangsx on 2015/3/18.
 */
public class JedisTest {
    @Test
    public void testMerge(){
        List<RedisStoreItem> v1=new ArrayList<RedisStoreItem>();
        RedisStoreItem i1= new RedisStoreItem();
        i1.setSceneType("s1");
        i1.setResultLevel(256);
        v1.add(i1);
        i1= new RedisStoreItem();
        i1.setSceneType("S2");
        i1.setResultLevel(10);
        v1.add(i1);
        Map<String, List<RedisStoreItem>> dic_allRules =new HashMap<String, List<RedisStoreItem>>()  ;
        dic_allRules.put("K1",v1);

        v1 = new ArrayList<RedisStoreItem>();
        i1= new RedisStoreItem();
        i1.setSceneType("S1");
        i1.setResultLevel(298);
        v1.add(i1);
        i1= new RedisStoreItem();
        i1.setSceneType("s2");
        i1.setResultLevel(295);
        v1.add(i1);
        i1= new RedisStoreItem();
        i1.setSceneType("S3");
        i1.setResultLevel(295);
        v1.add(i1);
        dic_allRules.put("K2", v1);
        Map<String, Integer> response = new HashMap<String, Integer>();
        new PaymentViaAccount().MergeRedisRules(dic_allRules,response);
        for(String key :response.keySet()){
            System.out.println(key +" " + response.get(key));
        }

    }

}
