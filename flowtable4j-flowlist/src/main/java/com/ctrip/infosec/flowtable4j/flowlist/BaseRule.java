package com.ctrip.infosec.flowtable4j.flowlist;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 流量规则按 订单类型、支付类型区分
 * Created by thyang on 2015/3/13 0013.
 */
public abstract class BaseRule {

    final Logger logger = LoggerFactory.getLogger(BaseRule.class);
    /**
     * 订单类型区分
     */
    private  HashMap<Integer,OrderTypeRule>  orderTypeMap= new HashMap<Integer, OrderTypeRule>();

    /**
     *所有订单类型，OrderType=0
     */
    private  OrderTypeRule globalMap = new OrderTypeRule();
    /**
     * 黑白名单校验不同逻辑
     * @param fact
     * @param results
     * @return
     */
    public abstract boolean check(FlowFact fact, List<RiskResult> results);

    /**
     * 按订单类型校验
     * @param rules
     * @param fact
     * @param results
     * @return
     */
    public abstract boolean checkByOrderType(OrderTypeRule rules,FlowFact fact,List<RiskResult> results);

    /**
     * 校验适用所有订单类型的规则
     * @param fact
     * @param results
     * @return
     */
    protected boolean checkGlobalMap(FlowFact fact, List<RiskResult> results) {
        try {
            return checkByOrderType(globalMap, fact, results);
        } catch (Throwable ex) {
            logger.warn(ex.getMessage());
        }
        return false;
    }

    /**
     * 优先校验按订单类型区分的规则
     * @param fact
     * @param results
     * @return
     */
    protected boolean checkOrderTypeMap(FlowFact fact, List<RiskResult> results) {
        try {
            Integer orderType= fact.getOrderType();
            if(orderTypeMap.containsKey(orderType)) {
                return checkByOrderType(orderTypeMap.get(orderType),fact,results);
            }
        } catch (Throwable ex) {
            logger.warn(ex.getMessage());
        }
        return false;
    }

    /**
     * 新增规则，流量规则适用全量替换
     * @param flowRuleStatements
     * @return
     */
    public boolean addRule(List<FlowRuleStatement> flowRuleStatements) {

        if(flowRuleStatements !=null && flowRuleStatements.size()>0) {
            HashMap<Integer, OrderTypeRule> orderTypeMapTemp = new HashMap<Integer, OrderTypeRule>();
            OrderTypeRule globalMapTemp = new OrderTypeRule();
            buildRuleTree(flowRuleStatements, globalMapTemp, orderTypeMapTemp);
            orderTypeMap = orderTypeMapTemp;
            globalMap = globalMapTemp;
        }
        return true;
    }

    /**
     * 构造规则树，按订单类型、支付类型
     */
    private void buildRuleTree(List<FlowRuleStatement> flowRuleStatements,OrderTypeRule globalMapTemp,HashMap<Integer,OrderTypeRule>  orderTypeMapTemp) {
        Integer orderType = 0;
        String prepayType = "";
        OrderTypeRule orderTypeRule;
        for (FlowRuleStatement rule : flowRuleStatements) {
             orderType  = rule.getOrderType();
             prepayType = rule.getPrepayType();
             if(orderType.equals(0)){
                 addRuleByPrepayType(globalMapTemp, prepayType, rule);
             }
             else
             {
                 if(orderTypeMapTemp.containsKey(orderType)){
                     orderTypeRule = orderTypeMapTemp.get(orderType);
                 }
                 else {
                     orderTypeRule = new OrderTypeRule();
                 }
                 addRuleByPrepayType(orderTypeRule,prepayType,rule);
                 orderTypeMapTemp.put(orderType,orderTypeRule);
             }
        }
    }

    private void addRuleByPrepayType(OrderTypeRule orderTypeRule, String prepayType, FlowRuleStatement rule) {
        List<FlowRuleStatement> prepayRules;
        if("ALL".equalsIgnoreCase(prepayType)){
            if(!orderTypeRule.allPrepay.contains(rule))
            {
                orderTypeRule.allPrepay.add(rule);
            }
        }
        else {
           if(orderTypeRule.byPrepay.containsKey(prepayType)){
              prepayRules = orderTypeRule.byPrepay.get(prepayType);
           }
           else
           {
              prepayRules = new ArrayList<FlowRuleStatement>();
           }
            if(!prepayRules.contains(rule)){
              prepayRules.add(rule);
            }
            orderTypeRule.byPrepay.put(prepayType,prepayRules);
        }
    }
}