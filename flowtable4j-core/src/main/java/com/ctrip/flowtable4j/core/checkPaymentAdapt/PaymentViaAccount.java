package com.ctrip.flowtable4j.core.checkPaymentAdapt;

import com.google.common.base.Strings;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangsx on 2015/3/17.
 */
@Component
public class PaymentViaAccount {
    //超时 ms
    final int ACCOUNT_EXPIRE = 50;
    @Autowired
    private ParameterDeamon parameterDeamon;
    @Autowired
    private RedisProvider redisProvider;
    private FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-ddTHH:mm:ss");
    private Logger logger = LoggerFactory.getLogger(PaymentViaAccount.class);

    public PaymentViaAccount() {
        parameterDeamon.startWatch();
    }

    /**
     * 验证黑白灰名单
     *
     * @param checkItems
     * @return
     */
    public Map<String, Integer> CheckBWGRule(List<AccountCheckItem> checkItems) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        ExecutorService executorService = Executors.newCachedThreadPool();;
        if (checkItems == null || checkItems.size() == 0) {
            throw new RuntimeException("数据格式错误，请求内容为空");
        }

        final String currentDate = format.format(System.currentTimeMillis());

        //多线程取Redis规则
        final ConcurrentHashMap<String,List<RedisStoreItem>> dic_allRules = new ConcurrentHashMap<String, List<RedisStoreItem>>();
        //按Key取SortedSet Rules并发取规则
        for (final AccountCheckItem item : checkItems) {

            if (Strings.isNullOrEmpty(item.getCheckType().trim()) || Strings.isNullOrEmpty(item.getSceneType().trim()) ||
                    Strings.isNullOrEmpty(item.getCheckValue().trim())) {
                continue;
            }
            result.put(item.getSceneType().toUpperCase(),0);
            final int chkType = parameterDeamon.getCheckType(item.getCheckType());
            final int scntype = parameterDeamon.getSceneType(item.getSceneType());
            if (chkType > 0 && scntype > 0) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        KeyValue keyValue = new KeyValue();
                        keyValue.setSceneType(item.getSceneType().toUpperCase());
                        keyValue.setRuleKey(String.format("CheckType:{%s}|SceneType:{%s}|CheckValue:{%s}", chkType, scntype, item.getCheckValue()).toUpperCase());

                        getRuleByKey(dic_allRules,currentDate,keyValue);
                    }
                });
            }
        }

        try {
            executorService.awaitTermination(ACCOUNT_EXPIRE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("",e);
        }

        MergeRedisRules(dic_allRules, result);

        return result;

    }

    /**
     * 合并结果
     * @param dic_allRules
     * @param response
     */
    private void MergeRedisRules(Map<String, List<RedisStoreItem>> dic_allRules, Map<String, Integer> response) {
        List<RedisStoreItem> allRules = new ArrayList<RedisStoreItem>();
        for(List<RedisStoreItem> items : dic_allRules.values()){
            allRules.addAll(items);
        }

        HashMap<String,List<RedisStoreItem>> group = new HashMap<String, List<RedisStoreItem>>();
        for(RedisStoreItem entry:allRules){
            if(group.containsKey(entry.getSceneType())){
                group.get(entry.getSceneType()).add(entry);
            }else{
                List<RedisStoreItem> group_list = new ArrayList<RedisStoreItem>();
                group_list.add(entry);
                group.put(entry.getSceneType(),group_list);
            }
        }

        for(Iterator<String> it=group.keySet().<String>iterator();it.hasNext();){
            String key= it.next();
            List<RedisStoreItem> list = group.get(key);
            Collections.sort(list, new Comparator<RedisStoreItem>() {
                @Override
                public int compare(RedisStoreItem o1, RedisStoreItem o2) {
                    if(o1.getResultLevel()==o2.getResultLevel()){
                        return 0;
                    }else if(o1.getResultLevel()>o2.getResultLevel()){
                        return 1;
                    }else{
                        return -1;
                    }
                }
            });
            //白名单取第一笔，其它取最大值
            RedisStoreItem item = list.get(0);
            if (item.getResultLevel() > 99)
            {
                item = list.get(list.size() - 1);
            }
            response.put(item.getSceneType(),item.getResultLevel());
        }
    }


    /**
     * 线程方法，增加异常处理
     * @param dic_allRules
     * @param currentDate
     * @param ruleInfo
     */
    private void getRuleByKey(Map<String, List<RedisStoreItem>> dic_allRules, String currentDate, Object ruleInfo) {
        if(ruleInfo instanceof KeyValue){
            KeyValue val = (KeyValue)ruleInfo;
            List<RedisStoreItem> redisStoreItems = redisProvider.GetBWGFromRedis(val.getRuleKey());
            List<RedisStoreItem> redisRules = new ArrayList<RedisStoreItem>();
            if(redisStoreItems!=null){
                for(RedisStoreItem item : redisStoreItems){
                    if(item.getEffectDate().compareTo(currentDate)<=0&&currentDate.compareTo(item.getExpiryDate())<=0){
                        redisRules.add(item);
                    }
                }
            }

            for(RedisStoreItem item : redisRules){
                if(item.getSceneType().equals(val.getSceneType())){
                    dic_allRules.put(val.getRuleKey(),redisRules);
                }
            }
        }
    }
}
