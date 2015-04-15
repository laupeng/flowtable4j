package com.ctrip.infosec.flowtable4j.accountsecurity;

import org.junit.Test;

import java.util.*;

/**
 * Created by zhangsx on 2015/3/18.
 */
public class JedisTest {
    private void MergeRedisRules(Map<String, List<RedisStoreItem>> dic_allRules, Map<String, Integer> response) {
        /**
         * 所有有效黑白名单
         */
        List<RedisStoreItem> allRules = new ArrayList<RedisStoreItem>();
        for(List<RedisStoreItem> items : dic_allRules.values()){
            allRules.addAll(items);
        }
        /**
         * 按SceneType + ResultLevel 排序
         */
        Collections.sort(allRules, new Comparator<RedisStoreItem>() {
            @Override
            public int compare(RedisStoreItem o1, RedisStoreItem o2) {
                int cmp = o1.getSceneType().compareToIgnoreCase(o2.getSceneType());
                if (cmp == 0) {
                    cmp = o1.getResultLevel() - o2.getResultLevel();
                }
                return cmp;
            }
        });

        String currentSceneType="";
        int currentResultLevel=0;
        /**
         * 按SceneType遍历规则，如果有<99的取最小，否则取最大
         */
        for(Iterator<RedisStoreItem> it=allRules.iterator();it.hasNext();){
            RedisStoreItem item = it.next();
            if(item.getSceneType().compareToIgnoreCase(currentSceneType)==0){
                if(currentResultLevel > 99){
                    currentResultLevel = item.getResultLevel();
                }
            }
            else
            {
                if(!currentSceneType.equals("")){
                    response.put(currentSceneType,currentResultLevel);
                }
                currentSceneType = item.getSceneType().toUpperCase();
                currentResultLevel = item.getResultLevel();
            }
        }
        if(!currentSceneType.equals("")){
            response.put(currentSceneType,currentResultLevel);
        }
    }

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
        dic_allRules.put("K2",v1);
        Map<String, Integer> response = new HashMap<String, Integer>();
        MergeRedisRules(dic_allRules,response);
        for(String key :response.keySet()){
            System.out.println(key +" " + response.get(key));
        }

    }

}
