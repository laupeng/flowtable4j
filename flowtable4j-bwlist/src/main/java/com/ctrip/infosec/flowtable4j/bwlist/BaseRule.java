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
     * OrderType,字段,值,规则
     * 读写锁
     * Global的OrderType 为 0
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
     *
     * @param fact
     * @param results
     * @return
     */
    public abstract boolean check(BWFact fact, RiskResult results);

    /**
     * 按订单类型校验不等黑白名单
     *
     * @param fact
     * @param results
     * @return
     */
    protected boolean checkNEQRuleByOrderType(BWFact fact, RiskResult results) {
        try {
            orderTypeNEQ_Read.lock();
            checkNEQRules(fact,orderTypeNEQ,results);
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            orderTypeNEQ_Read.unlock();
        }
        return false;
    }

    /**
     * 按订单类型校验等于的黑白名单
     *
     * @param fact
     * @param results
     * @return
     */
    protected boolean checkEQRuleByOrderType(BWFact fact, RiskResult results) {
        try {
            orderTypeEQ_Read.lock();
            return checkEQRules(fact,orderTypeEQ, results);
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            orderTypeEQ_Read.unlock();
        }
        return false;
    }

    protected abstract boolean checkEQRules(BWFact fact,HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>> orderTypeEQ, RiskResult results);

    protected abstract boolean checkNEQRules(BWFact fact,HashMap<Integer,HashMap<String, List<RuleStatement>>> orderTypeNEQ, RiskResult results);

    /**
     * 合并HashMap结果
     *
     * @param srcMap
     * @param targetMap
     */
    private void mergeMap(HashMap srcMap, HashMap targetMap) {
        for (Object key : srcMap.keySet()) {
            if (targetMap.containsKey(key)) {
                Object srcV = srcMap.get(key);
                Object targetV = targetMap.get(key);
                if ((srcV instanceof List)&&(targetV instanceof  List)) {
                    mergeList((List<RuleStatement>) srcV, (List<RuleStatement>) targetV);
                } else if ((srcV instanceof HashMap) && (targetV instanceof HashMap)) {
                    mergeMap((HashMap) srcV, (HashMap) targetV);
                }
            } else {
                targetMap.put(key, srcMap.get(key));
            }
        }
    }

    /**
     * 删除规则在List级别，如果原始节点在HashMap上没有，直接忽略
     *
     * @param srcMap
     * @param targetMap
     */
    private void removeMap(HashMap srcMap, HashMap targetMap) {
        for (Object key : srcMap.keySet()) {
            if (targetMap.containsKey(key)) {
                Object srcV = srcMap.get(key);
                Object targetV = targetMap.get(key);
                if ((srcV instanceof List) && (targetV instanceof List)) {
                    ((List<RuleStatement>) targetV).removeAll((List<RuleStatement>) srcV);
                } else if ((srcV instanceof HashMap)&&(targetV instanceof HashMap)) {
                    removeMap((HashMap) srcV, (HashMap) targetV);
                }
            }
        }
    }

    /**
     * 两个List合并
     * 不存在则加入，否则忽略
     *
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
     *
     * @param ruleStatements
     * @return
     */
    public boolean addRule(List<RuleStatement> ruleStatements) {

        if (ruleStatements != null && ruleStatements.size() > 0) {
            HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>> orderTypeEQTemp = new HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>>();
            HashMap<Integer, HashMap<String, List<RuleStatement>>> orderTypeNEQTemp = new HashMap<Integer, HashMap<String, List<RuleStatement>>>();

            buildRuleTree(ruleStatements, orderTypeEQTemp, orderTypeNEQTemp);

            if (orderTypeEQTemp.size() > 0) {
                try {
                    orderTypeEQ_Write.lock();
                    mergeMap(orderTypeEQTemp, orderTypeEQ);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage(), ex);
                } finally {
                    orderTypeEQ_Write.unlock();
                }
            }

            if (orderTypeNEQTemp.size() > 0) {
                try {
                    orderTypeNEQ_Write.lock();
                    mergeMap(orderTypeNEQTemp, orderTypeNEQ);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage(), ex);
                } finally {
                    orderTypeNEQ_Write.unlock();
                }
            }
        }
        return true;
    }

    /**
     * 构造黑白名单树
     *
     * @param ruleStatements
     * @param orderTypeEQTemp
     * @param orderTypeNEQTemp
     */
    private void buildRuleTree(List<RuleStatement> ruleStatements,
                               HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>> orderTypeEQTemp,
                               HashMap<Integer, HashMap<String, List<RuleStatement>>> orderTypeNEQTemp) {
        RuleTerm EQruleTerm = null;
        Integer orderType = 0;
        String fieldName = "";
        for (RuleStatement rule : ruleStatements) {
            orderType = rule.getOrderType();
            EQruleTerm = rule.getEQRuleTerm();
            if (EQruleTerm == null) { //没有等于条款的规则
                EQruleTerm = rule.getFirstRuleTerm();
                if (EQruleTerm == null) {
                    continue;  //没有条款的规则
                }
                fieldName = EQruleTerm.getFieldName();
                //OrderType NEQ
                HashMap<String, List<RuleStatement>> orderTypeRuleNEQ;
                if (orderTypeNEQTemp.containsKey(orderType)) {
                    orderTypeRuleNEQ = orderTypeNEQTemp.get(orderType);
                } else {
                    orderTypeRuleNEQ = new HashMap<String, List<RuleStatement>>();
                }
                AddNEQRule2Map(orderTypeRuleNEQ, fieldName, rule);
                orderTypeNEQTemp.put(orderType, orderTypeRuleNEQ);
            } else {  //有相等的条款
                //OrderType EQ
                HashMap<String, HashMap<String, List<RuleStatement>>> orderTypeRuleEQ;
                if (orderTypeEQTemp.containsKey(orderType)) {
                    orderTypeRuleEQ = orderTypeEQTemp.get(orderType);
                } else {
                    orderTypeRuleEQ = new HashMap<String, HashMap<String, List<RuleStatement>>>();
                }
                AddEQRule2Map(orderTypeRuleEQ, EQruleTerm, rule);
                orderTypeEQTemp.put(orderType, orderTypeRuleEQ);
            }
        }
    }

    private void AddEQRule2Map(HashMap<String, HashMap<String, List<RuleStatement>>> parent, RuleTerm term, RuleStatement rule) {
        HashMap<String, List<RuleStatement>> fieldRules;
        String matchValue;
        List<RuleStatement> valueRules;
        String fieldName = term.getFieldName();
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
        if (ruleStatements != null && ruleStatements.size() > 0) {
            HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>> orderTypeEQTemp = new HashMap<Integer, HashMap<String, HashMap<String, List<RuleStatement>>>>();
            HashMap<Integer, HashMap<String, List<RuleStatement>>> orderTypeNEQTemp = new HashMap<Integer, HashMap<String, List<RuleStatement>>>();

            buildRuleTree(ruleStatements, orderTypeEQTemp, orderTypeNEQTemp);

            if (orderTypeEQTemp.size() > 0) {
                try {
                    orderTypeEQ_Write.lock();
                    removeMap(orderTypeEQTemp, orderTypeEQ);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage(), ex);
                } finally {
                    orderTypeEQ_Write.unlock();
                }
            }

            if (orderTypeNEQTemp.size() > 0) {
                try {
                    orderTypeNEQ_Write.lock();
                    removeMap(orderTypeNEQTemp, orderTypeNEQ);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage(), ex);
                } finally {
                    orderTypeNEQ_Write.unlock();
                }
            }
        }
        return true;
    }
}
