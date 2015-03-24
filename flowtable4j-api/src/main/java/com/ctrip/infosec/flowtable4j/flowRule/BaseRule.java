package com.ctrip.infosec.flowtable4j.flowRule;

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
    private final HashMap<String, HashMap<String, List<FlowRuleStatement>>> globalEQ = new HashMap<String, HashMap<String, List<FlowRuleStatement>>>();
    final ReentrantReadWriteLock globalEQ_Lock = new ReentrantReadWriteLock();
    final ReentrantReadWriteLock.ReadLock globalEQ_Read = globalEQ_Lock.readLock();
    final ReentrantReadWriteLock.WriteLock globalEQ_Write = globalEQ_Lock.writeLock();

    /**
     * 字段，规则
     * 读写锁
     */
    private final HashMap<String, List<FlowRuleStatement>> globalNEQ = new HashMap<String, List<FlowRuleStatement>>();
    final ReentrantReadWriteLock globalNEQ_Lock = new ReentrantReadWriteLock();
    final ReentrantReadWriteLock.ReadLock globalNEQ_Read = globalNEQ_Lock.readLock();
    final ReentrantReadWriteLock.WriteLock globalNEQ_Write = globalNEQ_Lock.writeLock();

    /**
     * OrderType,字段,值,规则
     * 读写锁
     */
    private final HashMap<Integer, HashMap<String, HashMap<String, List<FlowRuleStatement>>>> orderTypeEQ = new HashMap<Integer, HashMap<String, HashMap<String, List<FlowRuleStatement>>>>();
    final ReentrantReadWriteLock orderTypeEQ_Lock = new ReentrantReadWriteLock();
    final ReentrantReadWriteLock.ReadLock orderTypeEQ_Read = orderTypeEQ_Lock.readLock();
    final ReentrantReadWriteLock.WriteLock orderTypeEQ_Write = orderTypeEQ_Lock.writeLock();

    /**
     * OrderType,字段,规则
     * 读写锁
     */
    private final HashMap<Integer, HashMap<String, List<FlowRuleStatement>>> orderTypeNEQ = new HashMap<Integer, HashMap<String, List<FlowRuleStatement>>>();
    final ReentrantReadWriteLock orderTypeNEQ_Lock = new ReentrantReadWriteLock();
    final ReentrantReadWriteLock.ReadLock orderTypeNEQ_Read = orderTypeNEQ_Lock.readLock();
    final ReentrantReadWriteLock.WriteLock orderTypeNEQ_Write = orderTypeNEQ_Lock.writeLock();

    /**
     * 黑白名单校验不同逻辑
     * @param fact
     * @param results
     * @return
     */
    public abstract boolean check(FlowFact fact, List<BWResult> results);

    /**
     * 校验全局的不等黑白名单
     * @param fact
     * @param results
     * @return
     */
    protected boolean checkGlobalNEQRule(FlowFact fact, List<BWResult> results) {
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
    protected boolean checkGlobalEQRule(FlowFact fact, List<BWResult> results) {
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
    protected boolean checkNEQRuleByOrderType(FlowFact fact, List<BWResult> results) {
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
    protected boolean checkEQRuleByOrderType(FlowFact fact, List<BWResult> results) {
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

    protected abstract boolean checkEQRules(FlowFact fact, HashMap<String, HashMap<String, List<FlowRuleStatement>>> matchRules, List<BWResult> results);

    protected abstract boolean checkNEQRules(FlowFact fact, HashMap<String, List<FlowRuleStatement>> matchRules, List<BWResult> results);

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
                    mergeList((List<FlowRuleStatement>) srcV,(List<FlowRuleStatement>) targetV);
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
                    ((List<FlowRuleStatement>) targetV).removeAll((List<FlowRuleStatement>) srcV);
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
    private void mergeList(List<FlowRuleStatement> srcList, List<FlowRuleStatement> targetList) {
        if (srcList != null && srcList.size() > 0) {
            for (FlowRuleStatement rule : srcList) {
                if (!targetList.contains(rule))
                    targetList.add(rule);
            }
        }
    }

    /**
     * 新增规则
     * @param flowRuleStatements
     * @return
     */
    public boolean addRule(List<FlowRuleStatement> flowRuleStatements) {

        if(flowRuleStatements !=null && flowRuleStatements.size()>0) {
            HashMap<String, HashMap<String, List<FlowRuleStatement>>> globalEQTemp = new HashMap<String, HashMap<String, List<FlowRuleStatement>>>();
            HashMap<String, List<FlowRuleStatement>> globalNEQTemp = new HashMap<String, List<FlowRuleStatement>>();
            HashMap<Integer, HashMap<String, HashMap<String, List<FlowRuleStatement>>>> orderTypeEQTemp = new HashMap<Integer, HashMap<String, HashMap<String, List<FlowRuleStatement>>>>();
            HashMap<Integer, HashMap<String, List<FlowRuleStatement>>> orderTypeNEQTemp = new HashMap<Integer, HashMap<String, List<FlowRuleStatement>>>();

            buildRuleTree(flowRuleStatements, globalEQTemp, globalNEQTemp, orderTypeEQTemp, orderTypeNEQTemp);

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
     * @param flowRuleStatements
     * @param globalEQTemp
     * @param globalNEQTemp
     * @param orderTypeEQTemp
     * @param orderTypeNEQTemp
     */
    private void buildRuleTree(List<FlowRuleStatement> flowRuleStatements,
                                HashMap<String, HashMap<String, List<FlowRuleStatement>>> globalEQTemp,
                                HashMap<String, List<FlowRuleStatement>> globalNEQTemp,
                                HashMap<Integer, HashMap<String, HashMap<String, List<FlowRuleStatement>>>> orderTypeEQTemp,
                                HashMap<Integer, HashMap<String, List<FlowRuleStatement>>> orderTypeNEQTemp) {
        FlowRuleTerm flowRuleTerm = null;
        Integer orderType = 0;
        String fieldName = "";
        for (FlowRuleStatement rule : flowRuleStatements) {
            orderType = rule.getOrderType();
            flowRuleTerm = rule.getEQRuleTerm();
            if (flowRuleTerm == null) { //没有等于条款的规则
                flowRuleTerm = rule.getFirstRuleTerm();
                if (flowRuleTerm == null) {
                    continue;  //没有条款的规则
                }
                fieldName = flowRuleTerm.getFieldName();
                if (orderType == 0) { //Global NEQ
                    AddNEQRule2Map(globalNEQTemp, fieldName, rule);
                } else {  //OrderType NEQ
                    HashMap<String, List<FlowRuleStatement>> orderTypeRuleNEQ;
                    if (orderTypeNEQTemp.containsKey(orderType)) {
                        orderTypeRuleNEQ = orderTypeNEQTemp.get(orderType);
                    } else {
                        orderTypeRuleNEQ = new HashMap<String, List<FlowRuleStatement>>();
                    }
                    AddNEQRule2Map(orderTypeRuleNEQ, fieldName, rule);
                    orderTypeNEQTemp.put(orderType, orderTypeRuleNEQ);
                }
            } else {  //有相等的条款
                if (orderType == 0) { //Global EQ
                    AddEQRule2Map(globalEQTemp, flowRuleTerm,rule);
                } else {  //OrderType EQ
                    HashMap<String, HashMap<String, List<FlowRuleStatement>>> orderTypeRuleEQ;
                    if (orderTypeEQTemp.containsKey(orderType)) {
                        orderTypeRuleEQ = orderTypeEQTemp.get(orderType);
                    } else {
                        orderTypeRuleEQ = new HashMap<String, HashMap<String, List<FlowRuleStatement>>>();
                    }
                    AddEQRule2Map(orderTypeRuleEQ, flowRuleTerm,rule);
                    orderTypeEQTemp.put(orderType, orderTypeRuleEQ);
                }
            }
        }
    }

    private void AddEQRule2Map(HashMap<String, HashMap<String, List<FlowRuleStatement>>> parent, FlowRuleTerm term, FlowRuleStatement rule) {
        HashMap<String, List<FlowRuleStatement>> fieldRules;
        String matchValue;
        List<FlowRuleStatement> valueRules;
        String fieldName= term.getFieldName();
        if (parent.containsKey(fieldName)) {
            fieldRules = parent.get(fieldName);
        } else {
            fieldRules = new HashMap<String, List<FlowRuleStatement>>();
        }
        matchValue = term.getMatchValue();
        if (fieldRules.containsKey(matchValue)) {
            valueRules = fieldRules.get(matchValue);
        } else {
            valueRules = new ArrayList<FlowRuleStatement>();
        }
        valueRules.add(rule);
        fieldRules.put(matchValue, valueRules);
        parent.put(fieldName, fieldRules);
    }

    private void AddNEQRule2Map(HashMap<String, List<FlowRuleStatement>> parent, String fieldName, FlowRuleStatement rule) {
        List<FlowRuleStatement> fieldRules;
        if (parent.containsKey(fieldName)) {
            fieldRules = parent.get(fieldName);
        } else {
            fieldRules = new ArrayList<FlowRuleStatement>();
        }
        fieldRules.add(rule);
        parent.put(fieldName, fieldRules);
    }

    public boolean removeRule(List<FlowRuleStatement> flowRuleStatements) {
        if(flowRuleStatements !=null && flowRuleStatements.size()>0) {
            HashMap<String, HashMap<String, List<FlowRuleStatement>>> globalEQTemp = new HashMap<String, HashMap<String, List<FlowRuleStatement>>>();
            HashMap<String, List<FlowRuleStatement>> globalNEQTemp = new HashMap<String, List<FlowRuleStatement>>();
            HashMap<Integer, HashMap<String, HashMap<String, List<FlowRuleStatement>>>> orderTypeEQTemp = new HashMap<Integer, HashMap<String, HashMap<String, List<FlowRuleStatement>>>>();
            HashMap<Integer, HashMap<String, List<FlowRuleStatement>>> orderTypeNEQTemp = new HashMap<Integer, HashMap<String, List<FlowRuleStatement>>>();

            buildRuleTree(flowRuleStatements, globalEQTemp, globalNEQTemp, orderTypeEQTemp, orderTypeNEQTemp);

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
