package com.ctrip.infosec.flowtable4j.payAdapt;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.PayAdaptResultItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 流量规则按订单类型区分
 * Created by thyang on 2015/3/13 0013.
 */
public class PayAdaptRule {

    final Logger logger = LoggerFactory.getLogger(PayAdaptRule.class);

    /**
     * 订单类型区分
     */
    private HashMap<Integer, List<PayAdaptStatement>> byOrderType = new HashMap<Integer, List<PayAdaptStatement>>();

    /**
     * 规则校验
     * @param fact
     * @param results
     * @return
     */
    public boolean check(FlowFact fact, List<PayAdaptResultItem> results) {
        return checkByOrderTypeMap(fact, results);
    }

    /**
     * 按订单类型校验
     *
     * @param byOrderType
     * @param fact
     * @param results
     * @return
     */
    public boolean checkByOrderType(HashMap<Integer, List<PayAdaptStatement>> byOrderType, FlowFact fact, List<PayAdaptResultItem> results) {
        int matched = 0;
        for(Integer orderType:fact.getOrderTypes()){
            if (byOrderType.containsKey(orderType)) {
                List<PayAdaptStatement> orderTypeRules = byOrderType.get(orderType);
                for (PayAdaptStatement rule : orderTypeRules) {
                    if (rule.check(fact, results)) {
                        matched++;
                    }
                }
            }
        }
        return matched > 0;
    } 

    /**
     * 优先校验按订单类型区分的规则
     * @param fact
     * @param results
     * @return
     */
    protected boolean checkByOrderTypeMap(FlowFact fact, List<PayAdaptResultItem> results) {
        try {
            checkByOrderType(byOrderType, fact, results);
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 新增规则，流量规则适用全量替换
     *
     * @param rules
     * @return
     */
    public boolean addRule(List<PayAdaptStatement> rules) {
        if (rules != null) {
            HashMap<Integer, List<PayAdaptStatement>> orderTypeMapTemp = new HashMap<Integer, List<PayAdaptStatement>>();
            if (rules.size() > 0) {
                buildRuleTree(rules, orderTypeMapTemp);
            }
            byOrderType = orderTypeMapTemp;
        }
        return true;
    }

    /**
     * 构造规则树，按订单类型、支付类型
     */
    private void buildRuleTree(List<PayAdaptStatement> rules, HashMap<Integer, List<PayAdaptStatement>> orderTypeMapTemp) {
        Integer orderType = 0;
        for (PayAdaptStatement rule : rules) {
            orderType = rule.getOrderType();
            List<PayAdaptStatement> orderTypeRule = null;
            if (orderTypeMapTemp.containsKey(orderType)) {
                orderTypeRule = orderTypeMapTemp.get(orderType);
            } else {
                orderTypeRule = new ArrayList<PayAdaptStatement>();
            }
            orderTypeRule.add(rule);
            orderTypeMapTemp.put(orderType, orderTypeRule);
        }
    }
}