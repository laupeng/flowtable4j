package com.ctrip.infosec.flowtable4j.accountsecurity;

import com.ctrip.infosec.flowtable4j.core.utils.SimpleStaticThreadPool;
import com.ctrip.infosec.flowtable4j.model.AccountFact;
import com.ctrip.infosec.flowtable4j.model.AccountItem;
import com.ctrip.infosec.flowtable4j.model.RuleContent;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangsx on 2015/3/17.
 */
@Component
public class PaymentViaAccount {
    //超时 ms
    final int TIMEOUT = 2000;
    @Autowired
    private RedisProvider redisProvider;
    private FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");
    private Logger logger = LoggerFactory.getLogger(PaymentViaAccount.class);

    public void setBWGRule(List<RuleContent> rules) {
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

                        redisProvider.getCache().sadd(key, value);
                    }
                    return null;
                }
            });
        }
        try {
            SimpleStaticThreadPool.getInstance().invokeAll(tasks, TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("保存黑白名单异常", e);
        }
    }

    public void removeBWGRule(List<RuleContent> rules) {
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

                        String key = String.format("BW|%s|%s", checkType, checkValue).toUpperCase();
                        String value = Utils.JSON.toJSONString(ruleStore);
                        redisProvider.getCache().srem(key, value);
                    }
                    return null;
                }
            });
        }
        try {
            SimpleStaticThreadPool.getInstance().invokeAll(tasks, TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("删除黑白名单异常", e);
        }
    }


    /**
     * 验证黑白灰名单
     *
     * @param fact
     * @return
     */
    public void checkBWGRule(AccountFact fact, Map<String, Integer> result) {
        if (fact == null || fact.getCheckItems() == null || fact.getCheckItems().size() == 0) {
            throw new RuntimeException("数据格式错误，请求内容为空");
        }
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

        final Set<String> redisKeys = new HashSet<String>();
        final Set<String> sceneTypes = new HashSet<String>();

        for (AccountItem item : fact.getCheckItems()) {
            redisKeys.add(String.format("BW|%s|%s", item.getCheckType(), item.getCheckValue()).toUpperCase());
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
        try {
            SimpleStaticThreadPool.getInstance().invokeAll(tasks, TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("黑白名单校验失败", e);
        }
        mergeRedisRule(dic_allrules, result);
    }

    /**
     * 合并黑白名单
     * 白者最白，黑者最黑
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
                    value = Math.min(oldValue,value);
                } else //原值>=100
                {
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
        List<RuleStore> redisStoreItems = redisProvider.getBWGValue(searchKey, RuleStore.class);
        if (redisStoreItems != null && redisStoreItems.size() > 0) {
            for (int i = redisStoreItems.size() - 1; i >= 0; i--) {
                RuleStore item = redisStoreItems.get(i);
                String exp = item.getE();
                if (exp.compareTo(currentDate) < 0 || !sceneTypes.contains(item.getS())) {
                    redisStoreItems.remove(i);
                } else {
                    item.setS(item.getS().toUpperCase());
                }
            }
            if (redisStoreItems.size() > 0) {
                dic_allRules.put(searchKey, redisStoreItems);
            }
        }
    }
}
