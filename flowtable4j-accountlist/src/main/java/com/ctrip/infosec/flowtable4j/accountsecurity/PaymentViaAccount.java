package com.ctrip.infosec.flowtable4j.accountsecurity;

import com.ctrip.infosec.flowtable4j.core.utils.SimpleStaticThreadPool;
import com.ctrip.infosec.flowtable4j.model.AccountFact;
import com.ctrip.infosec.flowtable4j.model.AccountItem;
import com.ctrip.infosec.sars.monitor.util.Utils;
import com.google.common.base.Strings;
import org.apache.commons.digester.RulesBase;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.text.StyledEditorKit;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangsx on 2015/3/17.
 */
@Component
public class PaymentViaAccount {
    //超时 ms
    final int TIMEOUT = 200;
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
                    if(!(Strings.isNullOrEmpty(checkType)||Strings.isNullOrEmpty(checkValue))){
                        RuleStore ruleStore = new RuleStore();
                        ruleStore.setE(item.getExpiryDate());
                        ruleStore.setS( item.getSceneType());
                        ruleStore.setR(item.getResultLevel());

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
            logger.error("be interrupted", e);
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
                    if(!(Strings.isNullOrEmpty(checkType)||Strings.isNullOrEmpty(checkValue))){
                        RuleStore ruleStore = new RuleStore();
                        ruleStore.setE(item.getExpiryDate());
                        ruleStore.setS( item.getSceneType());
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
            logger.error("be interrupted", e);
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

        final Set<String> keys=new HashSet<String>();
        final Set<String> sceneTypes = new HashSet<String>();

        for(AccountItem item:fact.getCheckItems()){
            keys.add(String.format("BW|%s|%s",item.getCheckType(),item.getCheckValue()));
            sceneTypes.add(item.getSceneType());
        }

        final String date = format.format(System.currentTimeMillis());
        final Map<String, List<RuleStore>> dic_allrules = new ConcurrentHashMap<String, List<RuleStore>>();
        for (final Iterator<String> it= keys.iterator();it.hasNext();) {
            tasks.add(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    getRuleByKey(dic_allrules, date, it.next().toUpperCase(),sceneTypes);
                    return null;
                }
            });
        }
        try {
            SimpleStaticThreadPool.getInstance().invokeAll(tasks, TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("be interrupted", e);
        }
        mergeRedisRule(dic_allrules, result);
    }


    protected void mergeRedisRule(Map<String, List<RuleStore>> map, Map<String, Integer> response) {
        /**
         * 所有有效黑白名单
         */
        List<RuleStore> allRules = new ArrayList<RuleStore>();
        for (List<RuleStore> items : map.values()) {
            allRules.addAll(items);
        }
        /**
         * 按SceneType + ResultLevel 排序
         */
        Collections.sort(allRules, new Comparator<RuleStore>() {
            @Override
            public int compare(RuleStore o1, RuleStore o2) {
                int cmp = o1.getS().compareToIgnoreCase(o2.getS());
                if (cmp == 0) {
                    cmp = o1.getR() - o2.getR();
                }
                return cmp;
            }
        });

        String currentSceneType = "";
        int currentResultLevel = 0;
        /**
         * 按SceneType遍历规则，如果有<99的取最小，否则取最大
         */
        for (Iterator<RuleStore> it = allRules.iterator(); it.hasNext(); ) {
            RuleStore item = it.next();
            if (item.getS().compareToIgnoreCase(currentSceneType) == 0) {
                if (currentResultLevel > 99) {
                    currentResultLevel = item.getR();
                }
            } else {
                if (!currentSceneType.equals("")) {
                    response.put(currentSceneType, currentResultLevel);
                }
                currentSceneType = item.getS().toUpperCase();
                currentResultLevel = item.getR();
            }
        }
        if (!currentSceneType.equals("")) {
            response.put(currentSceneType, currentResultLevel);
        }
    }

    /**
     * 线程方法，增加异常处理
     *
     * @param dic_allRules
     * @param currentDate
     * @param key
     */
    protected void getRuleByKey(Map<String, List<RuleStore>> dic_allRules, String currentDate, String key,Set<String> sceneTypes) {
        List<RuleStore> redisStoreItems = redisProvider.getBWGValue(key, RuleStore.class);
        if (redisStoreItems != null && redisStoreItems.size() > 0) {
            for (int i = redisStoreItems.size() - 1; i >= 0; i--) {
                RuleStore item = redisStoreItems.get(i);
                String exp = item.getE();
                if (exp.compareTo(currentDate) > 0 || currentDate.compareTo(exp) > 0 || !sceneTypes.contains(item.getS())) {
                    redisStoreItems.remove(i);
                } else {
                    item.setS(item.getS().toUpperCase());
                }
            }
            if (redisStoreItems.size() > 0) {
                dic_allRules.put(key, redisStoreItems);
            }
        }
    }
}
