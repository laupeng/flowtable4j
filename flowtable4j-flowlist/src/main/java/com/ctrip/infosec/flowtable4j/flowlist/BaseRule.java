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
    private HashMap<Integer, OrderTypeRule> byOrderType = new HashMap<Integer, OrderTypeRule>();

    /**
     * 所有订单类型，OrderType=0
     */
    private OrderTypeRule allOrderType = new OrderTypeRule();

    /**
     * 黑白名单校验不同逻辑
     *
     * @param fact
     * @param results
     * @return
     */
    public abstract boolean check(FlowFact fact, RiskResult results);

    /**
     * 按订单类型校验
     *
     * @param rules
     * @param fact
     * @param results
     * @return
     */
    public abstract boolean checkByOrderType(OrderTypeRule rules, FlowFact fact, RiskResult results);

    /**
     * 校验适用所有订单类型的规则
     *
     * @param fact
     * @param results
     * @return
     */
    protected boolean checkAllOrderTypeMap(FlowFact fact, RiskResult results) {
        try {
            return checkByOrderType(allOrderType, fact, results);
        } catch (Throwable ex) {
            logger.warn(ex.getMessage());
        }
        return false;
    }

    /**
     * 优先校验按订单类型区分的规则
     *
     * @param fact
     * @param results
     * @return
     */
    protected boolean checkByOrderTypeMap(FlowFact fact, RiskResult results) {
        try {
            Integer orderType = fact.getOrderType();
            if (byOrderType.containsKey(orderType)) {
                return checkByOrderType(byOrderType.get(orderType), fact, results);
            }
        } catch (Throwable ex) {
            logger.warn(ex.getMessage());
        }
        return false;
    }

    /**
     * 新增规则，流量规则适用全量替换
     *
     * @param rules
     * @return
     */
    public boolean addRule(List<FlowRuleStatement> rules) {

        if (rules != null && rules.size() > 0) {
            HashMap<Integer, OrderTypeRule> orderTypeMapTemp = new HashMap<Integer, OrderTypeRule>();
            OrderTypeRule globalMapTemp = new OrderTypeRule();
            buildRuleTree(rules, globalMapTemp, orderTypeMapTemp);
            byOrderType = orderTypeMapTemp;
            allOrderType = globalMapTemp;
        }
        return true;
    }

    /**
     * 构造规则树，按订单类型、支付类型
     */
    private void buildRuleTree(List<FlowRuleStatement> rules, OrderTypeRule allOrderTypeTemp, HashMap<Integer, OrderTypeRule> byOrderTypeTemp) {
        Integer orderType = 0;
        String prepayType = "";
        OrderTypeRule orderTypeRule;
        for (FlowRuleStatement rule : rules) {
            orderType = rule.getOrderType();
            prepayType = rule.getPrepayType();
            if (orderType.equals(0)) {
                addRuleByPrepayType(allOrderTypeTemp, prepayType, rule);
            } else {
                if (byOrderTypeTemp.containsKey(orderType)) {
                    orderTypeRule = byOrderTypeTemp.get(orderType);
                } else {
                    orderTypeRule = new OrderTypeRule();
                }
                addRuleByPrepayType(orderTypeRule, prepayType, rule);
                byOrderTypeTemp.put(orderType, orderTypeRule);
            }
        }
    }

    private void addRuleByPrepayType(OrderTypeRule orderTypeRule, String prepayType, FlowRuleStatement rule) {
        List<FlowRuleStatement> prepayRules;
        if ("ALL".equalsIgnoreCase(prepayType)) {
            if (!orderTypeRule.allPrepay.contains(rule)) {
                orderTypeRule.allPrepay.add(rule);
            }
        } else {
            if (orderTypeRule.byPrepay.containsKey(prepayType)) {
                prepayRules = orderTypeRule.byPrepay.get(prepayType);
            } else {
                prepayRules = new ArrayList<FlowRuleStatement>();
            }
            if (!prepayRules.contains(rule)) {
                prepayRules.add(rule);
            }
            orderTypeRule.byPrepay.put(prepayType, prepayRules);
        }
    }
}