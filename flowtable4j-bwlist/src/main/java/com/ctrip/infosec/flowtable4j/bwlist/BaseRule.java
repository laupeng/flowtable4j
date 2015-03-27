package com.ctrip.infosec.flowtable4j.bwlist;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 黑名单，按 KPI NAME 分组
 * Created by thyang on 2015/3/13 0013.
 */
public abstract class BaseRule {

    final Logger logger = LoggerFactory.getLogger(BaseRule.class);
    /**
     * 字段，值，规则
     * 读写锁
     */
    private final HashMap<String, HashMap<String, List<RuleStatement>>> globalEQ = new HashMap<String, HashMap<String, List<RuleStatement>>>();
    final ReentrantReadWriteLock globalEQ_Lock = new ReentrantReadWriteLock();
    final ReentrantReadWriteLock.ReadLock globalEQ_Read = globalEQ_Lock.readLock();
    final ReentrantReadWriteLock.WriteLock globalEQ_Write = globalEQ_Lock.writeLock();

    /**
     * 字段，规则
     * 读写锁
     */
    private final HashMap<String, List<RuleStatement>> globalNEQ = new HashMap<String, List<RuleStatement>>();
    final ReentrantReadWriteLock globalNEQ_Lock = new ReentrantReadWriteLock();
    final ReentrantReadWriteLock.ReadLock globalNEQ_Read = globalNEQ_Lock.readLock();
    final ReentrantReadWriteLock.WriteLock globalNEQ_Write = globalNEQ_Lock.writeLock();

    /**
     * OrderType,字段,值,规则
     * 读写锁
     */
    private final HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>> orderTypeEQ = new HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>>();
    final ReentrantReadWriteLock orderTypeEQ_Lock = new ReentrantReadWriteLock();
    final ReentrantReadWriteLock.ReadLock orderTypeEQ_Read = orderTypeEQ_Lock.readLock();
    final ReentrantReadWriteLock.WriteLock orderTypeEQ_Write = orderTypeEQ_Lock.writeLock();

    /**
     * OrderType,字段,规则
     * 读写锁
     */
    private final HashMap<Integer, HashMap<String, List<RuleStatement>>> orderTypeNEQ = new HashMap<Integer, HashMap<String, List<RuleStatement>>>();
    final ReentrantReadWriteLock orderTypeNEQ_Lock = new ReentrantReadWriteLock();
    final ReentrantReadWriteLock.ReadLock orderTypeNEQ_Read = orderTypeNEQ_Lock.readLock();
    final ReentrantReadWriteLock.WriteLock orderTypeNEQ_Write = orderTypeNEQ_Lock.writeLock();

    /**
     * 黑白名单校验不同逻辑
     * @param fact
     * @param results
     * @return
     */
    public abstract boolean check(BWFact fact, List<RiskResult> results);

    /**
     * 校验全局的不等黑白名单
     * @param fact
     * @param results
     * @return
     */
    protected boolean checkGlobalNEQRule(BWFact fact, List<RiskResult> results) {
        try {
            globalNEQ_Read.lock();
            return checkNEQRules(fact, globalNEQ, results);
        } catch (Throwable ex) {
            logger.warn(ex.getMessage());
        } finally {
            globalNEQ_Read.unlock();
        }
        return false;
    }

    /**
     * 校验全局等于的黑白名单
     * @param fact
     * @param results
     * @return
     */
    protected boolean checkGlobalEQRule(BWFact fact, List<RiskResult> results) {
        try {
            globalEQ_Read.lock();
            return checkEQRules(fact, globalEQ, results);
        } catch (Throwable ex) {
            logger.warn(ex.getMessage());
        } finally {
            globalEQ_Read.unlock();
        }
        return false;
    }

    /**
     * 按订单类型校验不等黑白名单
     * @param fact
     * @param results
     * @return
     */
    protected boolean checkNEQRuleByOrderType(BWFact fact, List<RiskResult> results) {
        try {
            orderTypeNEQ_Read.lock();
            Integer orderType= fact.getOrderType();
            if (orderTypeNEQ.containsKey(orderType)) {
              return checkNEQRules(fact, orderTypeNEQ.get(orderType), results);
            }
        } catch (Throwable ex) {
            logger.warn(ex.getMessage());
        } finally {
            orderTypeNEQ_Read.unlock();
        }
        return false;
    }

    /**
     * 按订单类型校验等于的黑白名单
     * @param fact
     * @param results
     * @return
     */
    protected boolean checkEQRuleByOrderType(BWFact fact, List<RiskResult> results) {
        try {
            orderTypeEQ_Read.lock();
            Integer orderType= fact.getOrderType();
            if (orderTypeEQ.containsKey(orderType)) {
               return checkEQRules(fact, orderTypeEQ.get(orderType), results);
            }
        } catch (Throwable ex) {
            logger.warn(ex.getMessage());
        } finally {
            orderTypeEQ_Read.unlock();
        }
        return false;
    }

    protected abstract boolean checkEQRules(BWFact fact, HashMap<String, HashMap<String, List<RuleStatement>>> matchRules, List<RiskResult> results);

    protected abstract boolean checkNEQRules(BWFact fact, HashMap<String, List<RuleStatement>> matchRules, List<RiskResult> results);

    /**
     * 合并HashMap结果
     * @param srcMap
     * @param targetMap
     */
    private void mergeMap(HashMap srcMap, HashMap targetMap) {
        for (Object key:srcMap.keySet()){
            if(targetMap.containsKey(key)){
                Object srcV= srcMap.get(key);
                Object targetV = targetMap.get(key);
                if(srcV instanceof List){
                    mergeList((List<RuleStatement>) srcV,(List<RuleStatement>) targetV);
                } else if(srcV instanceof HashMap){
                    mergeMap((HashMap)srcV,(HashMap)targetV);
                }
            } else {
                targetMap.put(key,srcMap.get(key));
            }
        }
    }

    /**
     * 删除规则在List级别，如果原始节点在HashMap上没有，直接忽略
     * @param srcMap
     * @param targetMap
     */
    private void removeMap(HashMap srcMap, HashMap targetMap) {
        for (Object key:srcMap.keySet()){
            if(targetMap.containsKey(key)){
                Object srcV= srcMap.get(key);
                Object targetV = targetMap.get(key);
                if(srcV instanceof List){
                    ((List<RuleStatement>) targetV).removeAll((List<RuleStatement>) srcV);
                } else if(srcV instanceof HashMap){
                    removeMap((HashMap)srcV,(HashMap)targetV);
                }
            }
        }
    }

    /**
     * 两个List合并
     * 不存在则加入，否则忽略
     * @param srcList
     * @param targetList
     */
    private void mergeList(List<RuleStatement> srcList, List<RuleStatement> targetList) {
        if (srcList != null && srcList.size() > 0) {
            for (RuleStatement rule : srcList) {
                if (!targetList.contains(rule))
                    targetList.add(rule);
            }
        }
    }

    /**
     * 新增规则
     * @param ruleStatements
     * @return
     */
    public boolean addRule(List<RuleStatement> ruleStatements) {

        if(ruleStatements!=null && ruleStatements.size()>0) {
            HashMap<String, HashMap<String, List<RuleStatement>>> globalEQTemp = new HashMap<String, HashMap<String, List<RuleStatement>>>();
            HashMap<String, List<RuleStatement>> globalNEQTemp = new HashMap<String, List<RuleStatement>>();
            HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>> orderTypeEQTemp = new HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>>();
            HashMap<Integer, HashMap<String, List<RuleStatement>>> orderTypeNEQTemp = new HashMap<Integer, HashMap<String, List<RuleStatement>>>();

            buildRuleTree(ruleStatements, globalEQTemp, globalNEQTemp, orderTypeEQTemp, orderTypeNEQTemp);

            if (globalEQTemp.size() > 0) {
                try {
                    globalEQ_Write.lock();
                    mergeMap(globalEQTemp, globalEQ);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage());
                } finally {
                    globalEQ_Write.unlock();
                }
            }

            if (globalNEQTemp.size() > 0) {
                try {
                    globalNEQ_Write.lock();
                    mergeMap(globalNEQTemp, globalNEQ);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage());
                } finally {
                    globalNEQ_Write.unlock();
                }
            }

            if (orderTypeEQTemp.size() > 0) {
                try {
                    orderTypeEQ_Write.lock();
                    mergeMap(orderTypeEQTemp, orderTypeEQ);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage());
                } finally {
                    orderTypeEQ_Write.unlock();
                }
            }

            if (orderTypeNEQTemp.size() > 0) {
                try {
                    orderTypeNEQ_Write.lock();
                    mergeMap(orderTypeNEQTemp, orderTypeNEQ);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage());
                } finally {
                    orderTypeNEQ_Write.unlock();
                }
            }
        }
        return true;
    }

    /**
     * 构造黑白名单树
     * @param ruleStatements
     * @param globalEQTemp
     * @param globalNEQTemp
     * @param orderTypeEQTemp
     * @param orderTypeNEQTemp
     */
    private void buildRuleTree(List<RuleStatement> ruleStatements,
                                HashMap<String, HashMap<String, List<RuleStatement>>> globalEQTemp,
                                HashMap<String, List<RuleStatement>> globalNEQTemp,
                                HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>> orderTypeEQTemp,
                                HashMap<Integer, HashMap<String, List<RuleStatement>>> orderTypeNEQTemp) {
        RuleTerm ruleTerm = null;
        Integer orderType = 0;
        String fieldName = "";
        for (RuleStatement rule : ruleStatements) {
            orderType = rule.getOrderType();
            ruleTerm = rule.getEQRuleTerm();
            if (ruleTerm == null) { //没有等于条款的规则
                ruleTerm = rule.getFirstRuleTerm();
                if (ruleTerm == null) {
                    continue;  //没有条款的规则
                }
                fieldName = ruleTerm.getFieldName();
                if (orderType == 0) { //Global NEQ
                    AddNEQRule2Map(globalNEQTemp, fieldName, rule);
                } else {  //OrderType NEQ
                    HashMap<String, List<RuleStatement>> orderTypeRuleNEQ;
                    if (orderTypeNEQTemp.containsKey(orderType)) {
                        orderTypeRuleNEQ = orderTypeNEQTemp.get(orderType);
                    } else {
                        orderTypeRuleNEQ = new HashMap<String, List<RuleStatement>>();
                    }
                    AddNEQRule2Map(orderTypeRuleNEQ, fieldName, rule);
                    orderTypeNEQTemp.put(orderType, orderTypeRuleNEQ);
                }
            } else {  //有相等的条款
                if (orderType == 0) { //Global EQ
                    AddEQRule2Map(globalEQTemp, ruleTerm,rule);
                } else {  //OrderType EQ
                    HashMap<String, HashMap<String, List<RuleStatement>>> orderTypeRuleEQ;
                    if (orderTypeEQTemp.containsKey(orderType)) {
                        orderTypeRuleEQ = orderTypeEQTemp.get(orderType);
                    } else {
                        orderTypeRuleEQ = new HashMap<String, HashMap<String, List<RuleStatement>>>();
                    }
                    AddEQRule2Map(orderTypeRuleEQ, ruleTerm,rule);
                    orderTypeEQTemp.put(orderType, orderTypeRuleEQ);
                }
            }
        }
    }

    private void AddEQRule2Map(HashMap<String, HashMap<String, List<RuleStatement>>> parent, RuleTerm term, RuleStatement rule) {
        HashMap<String, List<RuleStatement>> fieldRules;
        String matchValue;
        List<RuleStatement> valueRules;
        String fieldName= term.getFieldName();
        if (parent.containsKey(fieldName)) {
            fieldRules = parent.get(fieldName);
        } else {
            fieldRules = new HashMap<String, List<RuleStatement>>();
        }
        matchValue = term.getMatchValue();
        if (fieldRules.containsKey(matchValue)) {
            valueRules = fieldRules.get(matchValue);
        } else {
            valueRules = new ArrayList<RuleStatement>();
        }
        valueRules.add(rule);
        fieldRules.put(matchValue, valueRules);
        parent.put(fieldName, fieldRules);
    }

    private void AddNEQRule2Map(HashMap<String, List<RuleStatement>> parent, String fieldName, RuleStatement rule) {
        List<RuleStatement> fieldRules;
        if (parent.containsKey(fieldName)) {
            fieldRules = parent.get(fieldName);
        } else {
            fieldRules = new ArrayList<RuleStatement>();
        }
        fieldRules.add(rule);
        parent.put(fieldName, fieldRules);
    }

    public boolean removeRule(List<RuleStatement> ruleStatements) {
        if(ruleStatements!=null && ruleStatements.size()>0) {
            HashMap<String, HashMap<String, List<RuleStatement>>> globalEQTemp = new HashMap<String, HashMap<String, List<RuleStatement>>>();
            HashMap<String, List<RuleStatement>> globalNEQTemp = new HashMap<String, List<RuleStatement>>();
            HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>> orderTypeEQTemp = new HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>>();
            HashMap<Integer, HashMap<String, List<RuleStatement>>> orderTypeNEQTemp = new HashMap<Integer, HashMap<String, List<RuleStatement>>>();

            buildRuleTree(ruleStatements, globalEQTemp, globalNEQTemp, orderTypeEQTemp, orderTypeNEQTemp);

            if (globalEQTemp.size() > 0) {
                try {
                    globalEQ_Write.lock();
                    removeMap(globalEQTemp, globalEQ);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage());
                } finally {
                    globalEQ_Write.unlock();
                }
            }

            if (globalNEQTemp.size() > 0) {
                try {
                    globalNEQ_Write.lock();
                    removeMap(globalNEQTemp, globalNEQ);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage());
                } finally {
                    globalNEQ_Write.unlock();
                }
            }

            if (orderTypeEQTemp.size() > 0) {
                try {
                    orderTypeEQ_Write.lock();
                    removeMap(orderTypeEQTemp, orderTypeEQ);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage());
                } finally {
                    orderTypeEQ_Write.unlock();
                }
            }

            if (orderTypeNEQTemp.size() > 0) {
                try {
                    orderTypeNEQ_Write.lock();
                    removeMap(orderTypeNEQTemp, orderTypeNEQ);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage());
                } finally {
                    orderTypeNEQ_Write.unlock();
                }
            }
        }
        return true;
    }
}
