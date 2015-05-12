package com.ctrip.infosec.flowtable4j.accountsecurity;

import com.ctrip.infosec.flowtable4j.core.utils.SimpleStaticThreadPool;
import com.ctrip.infosec.flowtable4j.model.AccountFact;
import com.ctrip.infosec.flowtable4j.model.AccountItem;
import com.google.common.base.Strings;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    final int ACCOUNT_EXPIRE = 2000;
    @Autowired
    private ParameterDeamon parameterDeamon;
    @Autowired
    private RedisProvider redisProvider;
    private FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");
    private Logger logger = LoggerFactory.getLogger(PaymentViaAccount.class);

    public int SetBWGRule(List<RuleContent> rules)
    {
        return  0;
    }
    /**
     * 验证黑白灰名单
     *
     * @param fact
     * @return
     */
    public void CheckBWGRule(AccountFact fact, Map<String, Integer> result) {
        if (fact == null || fact.getCheckItems() == null || fact.getCheckItems().size() == 0) {
            throw new RuntimeException("数据格式错误，请求内容为空");
        }
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        final String currentDate = format.format(System.currentTimeMillis());

        //多线程取Redis规则
        final ConcurrentHashMap<String, List<RedisStoreItem>> dic_allRules = new ConcurrentHashMap<String, List<RedisStoreItem>>();

        //按Key取SortedSet Rules并发取规则
        for (final AccountItem item : fact.getCheckItems()) {
            //参数为空，略过
            if (Strings.isNullOrEmpty(item.getCheckType().trim()) || Strings.isNullOrEmpty(item.getSceneType().trim()) ||
                    Strings.isNullOrEmpty(item.getCheckValue().trim())) {
                continue;
            }

            result.put(item.getSceneType().toUpperCase(), 0);

            final int chkType = parameterDeamon.getCheckType(item.getCheckType());
            final int sceneType = parameterDeamon.getSceneType(item.getSceneType());

            if (chkType > 0 && sceneType > 0) {
                tasks.add(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        KeyValue keyValue = new KeyValue();
                        keyValue.setSceneType(item.getSceneType().toUpperCase());
                        keyValue.setRuleKey(String.format("CheckType:%s|SceneType:%s|CheckValue:%s", chkType, sceneType, item.getCheckValue()).toUpperCase());
                        getRuleByKey(dic_allRules, currentDate, keyValue);
                        return null;
                    }
                });
            }
        }
        try {
            SimpleStaticThreadPool.getInstance().invokeAll(tasks, ACCOUNT_EXPIRE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("InterruptedException.", e);
        }
        MergeRedisRules(dic_allRules, result);
    }

    /**
     * 合并结果
     *
     * @param dic_allRules
     * @param response
     */
    protected void MergeRedisRules(Map<String, List<RedisStoreItem>> dic_allRules, Map<String, Integer> response) {
        /**
         * 所有有效黑白名单
         */
        List<RedisStoreItem> allRules = new ArrayList<RedisStoreItem>();
        for (List<RedisStoreItem> items : dic_allRules.values()) {
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

        String currentSceneType = "";
        int currentResultLevel = 0;
        /**
         * 按SceneType遍历规则，如果有<99的取最小，否则取最大
         */
        for (Iterator<RedisStoreItem> it = allRules.iterator(); it.hasNext(); ) {
            RedisStoreItem item = it.next();
            if (item.getSceneType().compareToIgnoreCase(currentSceneType) == 0) {
                if (currentResultLevel > 99) {
                    currentResultLevel = item.getResultLevel();
                }
            } else {
                if (!currentSceneType.equals("")) {
                    response.put(currentSceneType, currentResultLevel);
                }
                currentSceneType = item.getSceneType().toUpperCase();
                currentResultLevel = item.getResultLevel();
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
     * @param ruleInfo
     */
    protected void getRuleByKey(Map<String, List<RedisStoreItem>> dic_allRules, String currentDate, Object ruleInfo) {
        if (ruleInfo instanceof KeyValue) {
            KeyValue val = (KeyValue) ruleInfo;
            List<RedisStoreItem> redisStoreItems = redisProvider.GetBWGFromRedis(val.getRuleKey());
            if (redisStoreItems != null && redisStoreItems.size() > 0) {
                for (int i = redisStoreItems.size() - 1; i >= 0; i--) {
                    RedisStoreItem item = redisStoreItems.get(i);
                    item.setSceneType(val.getSceneType().toUpperCase());
                    if (item.getEffectDate().compareTo(currentDate) > 0 || currentDate.compareTo(item.getExpiryDate()) > 0) {
                        redisStoreItems.remove(i);
                    }
                }
                if (redisStoreItems.size() > 0){
                    dic_allRules.put(val.getRuleKey(), redisStoreItems);
                }
            }
        }
    }
}
