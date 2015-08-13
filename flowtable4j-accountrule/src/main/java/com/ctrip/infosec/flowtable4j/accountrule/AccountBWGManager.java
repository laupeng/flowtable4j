package com.ctrip.infosec.flowtable4j.accountrule;

import com.ctrip.infosec.flowtable4j.dal.RedisProvider;
import com.ctrip.infosec.flowtable4j.model.AccountFact;
import com.ctrip.infosec.flowtable4j.model.AccountItem;
import com.ctrip.infosec.flowtable4j.model.RuleContent;
import com.ctrip.infosec.flowtable4j.model.SimpleStaticThreadPool;
import com.ctrip.infosec.sars.monitor.util.Utils;
import com.google.common.base.Strings;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 按SceneType、CheckType组装的黑白名单
 * 注意作为Redis的Key，在程序中对 CheckType，CheckValue，SceneType全部大写处理
 * Created by zhangsx on 2015/3/17.
 */
@Component
public class AccountBWGManager {
    //超时 ms
    final int TIMEOUT = 2000;

    @Autowired
    private RedisProvider redisProvider;

    private FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");

    private Logger logger = LoggerFactory.getLogger(AccountBWGManager.class);

    /**
     * 保存黑白名单到Redis
     * @param rules
     */
    public String setBWGRule(List<RuleContent> rules) {
        String result="OK";
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        for (final RuleContent item : rules) {
            tasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    String checkType = item.getCheckType();
                    String checkValue = item.getCheckValue();
                    if (!(Strings.isNullOrEmpty(checkType) || Strings.isNullOrEmpty(checkValue))) {
                        RuleStore ruleStore = new RuleStore();
                        ruleStore.setE(item.getExpiryDate());
                        ruleStore.setS(item.getSceneType().toUpperCase()); //Upper
                        ruleStore.setR(item.getResultLevel());
                        //CheckValue,CheckType,SceneType should be uppercase
                        String key = String.format("BW|%s|%s", checkType, checkValue).toUpperCase();
                        String value = Utils.JSON.toJSONString(ruleStore);
                        redisProvider.set2Set(key, value);
                    }
                    return null;
                }
            });
        }
        try {
            List<Future<Object>> futures = SimpleStaticThreadPool.getInstance().invokeAll(tasks, TIMEOUT, TimeUnit.MILLISECONDS);
            for(Future<Object> future:futures){
                try {
                    future.get();
                } catch(Exception ex) {
                    result="FAIL";
                }
            }
        } catch (InterruptedException ex) {
            logger.warn("save bwg rule timeout", ex);
            result="FAIL";
        }
        return  result;
    }

    /**
     * 从Redis中移除黑白名单
     * @param rules
     */
    public String removeBWGRule(List<RuleContent> rules) {
        String result ="OK";
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        for (final RuleContent item : rules) {
            tasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    String checkType = item.getCheckType();
                    String checkValue = item.getCheckValue();
                    if (!(Strings.isNullOrEmpty(checkType) || Strings.isNullOrEmpty(checkValue))) {
                        RuleStore ruleStore = new RuleStore();
                        ruleStore.setE(item.getExpiryDate());
                        ruleStore.setS(item.getSceneType().toUpperCase());
                        ruleStore.setR(item.getResultLevel());
                        //CheckValue,CheckType,SceneType should be uppercase
                        String key = String.format("BW|%s|%s", checkType, checkValue).toUpperCase();
                        String value = Utils.JSON.toJSONString(ruleStore);
                        redisProvider.rmSetValue(key, value);
                    }
                    return null;
                }
            });
        }
        try {
            List<Future<Object>> futures = SimpleStaticThreadPool.getInstance().invokeAll(tasks, TIMEOUT, TimeUnit.MILLISECONDS);
            for(Future<Object> future:futures){
                try {
                    future.get();
                } catch(Exception ex) {
                    result="FAIL";
                }
            }
        } catch (InterruptedException e) {
            logger.warn("remove bwg rule timeout", e);
            result = "FAIL";
        }
        return  result;
    }


    /**
     * 验证黑白灰名单
     *
     * @param fact
     * @return
     */
    public void checkBWGRule(AccountFact fact, Map<String, Integer> result) {
        if (fact == null || fact.getCheckItems() == null || fact.getCheckItems().size() == 0) {
            logger.warn("check data is null or empty");
            return;
        }
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        final Set<String> redisKeys = new HashSet<String>();
        final Set<String> sceneTypes = new HashSet<String>();
        //找出需要校验的SceneType
        for (AccountItem item : fact.getCheckItems()) {
            redisKeys.add(String.format("BW|%s|%s", item.getCheckType(),item.getCheckValue()).toUpperCase());
            sceneTypes.add(item.getSceneType().toUpperCase());
        }

        final String currentDate = format.format(System.currentTimeMillis());
        final Map<String, List<RuleStore>> dic_allrules = new ConcurrentHashMap<String, List<RuleStore>>();
        for (final String key : redisKeys) {
            tasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    getRuleByKey(dic_allrules, currentDate, key, sceneTypes);
                    return null;
                }
            });
        }
        //在支付适配场景中，应该只有2~3个Key，可以并发
        try {
            List<Future<Object>> futures = SimpleStaticThreadPool.getInstance().invokeAll(tasks, TIMEOUT, TimeUnit.MILLISECONDS);
            for(Future<Object> future:futures){
                try {
                    future.get();
                } catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        } catch (Exception e) {
            logger.warn("check bwg rule timeout", e);
        }
        mergeRedisRule(dic_allrules, result);
    }

    /**
     * 合并黑白名单
     * 白者最白，黑者最黑
     *
     * @param dicAllRules
     * @param results
     */
    private void mergeRedisRule(Map<String, List<RuleStore>> dicAllRules, Map<String, Integer> results) {
        /**
         * 所有有效黑白名单
         */
        List<RuleStore> allRules = new ArrayList<RuleStore>();
        for (List<RuleStore> items : dicAllRules.values()) {
            allRules.addAll(items);
        }

        for (RuleStore item : allRules) {
            String sceneType = item.getS();
            int value = item.getR();
            if (results.containsKey(sceneType)) {
                int oldValue = results.get(sceneType);
                if (oldValue < 99) {//原值是白名单 取最小值
                    value = Math.min(oldValue, value);
                } else {//原值>=100
                    if (value > 99) {
                        value = Math.max(value, oldValue);
                    }
                }
            }
            results.put(sceneType, value);
        }
    }

    /**
     * 根据Key从Redis获取黑白名单
     * @param dic_allRules
     * @param currentDate
     * @param searchKey
     */
    private void getRuleByKey(Map<String, List<RuleStore>> dic_allRules, String currentDate, String searchKey, Set<String> sceneTypes) {
        long now= System.nanoTime();
        int count=0;
        List<RuleStore> redisStoreItems = redisProvider.mgetBySet(searchKey, RuleStore.class);
        count = redisStoreItems.size();
        if (redisStoreItems != null && redisStoreItems.size() > 0) {
            for (int i = redisStoreItems.size() - 1; i >= 0; i--) {
                RuleStore item = redisStoreItems.get(i);
                String exp = item.getE();
                //如果已经过期或不再指定的SceneType中，废弃
                if (exp.compareTo(currentDate) < 0 || !sceneTypes.contains(item.getS().toUpperCase())) {
                    redisStoreItems.remove(i);
                } else {
                    item.setS(item.getS().toUpperCase());
                }
            }
            if (redisStoreItems.size() > 0) {
                dic_allRules.put(searchKey, redisStoreItems);
            }
        }
        long eps = (System.nanoTime() - now)/1000000L;

        logger.info("Read redis& merge elapse " + eps+", total records:"+count);

    }
}
