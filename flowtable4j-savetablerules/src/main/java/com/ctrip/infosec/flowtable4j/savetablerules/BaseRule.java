package com.ctrip.infosec.flowtable4j.savetablerules;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
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
    protected HashMap<Integer, List<FlowRuleStatement>> byOrderType = new HashMap<Integer, List<FlowRuleStatement>>();

    abstract void checkAndSave(FlowFact checkEntity);
    /**
     * 新增规则，流量规则适用全量替换
     *
     * @param rules
     * @return
     */
    public boolean addRule(List<FlowRuleStatement> rules) {

        if (rules != null && rules.size() > 0) {
            HashMap<Integer, List<FlowRuleStatement>> orderTypeMapTemp = new HashMap<Integer, List<FlowRuleStatement>>();
            buildRuleTree(rules, orderTypeMapTemp);
            byOrderType = orderTypeMapTemp;
        }
        return true;
    }

    /**
     * 构造规则树，按订单类型、支付类型
     */
    private void buildRuleTree(List<FlowRuleStatement> rules, HashMap<Integer, List<FlowRuleStatement>> orderTypeMapTemp) {
        Integer orderType = 0;
        for (FlowRuleStatement rule : rules) {
            orderType = rule.getOrderType();
            if (!orderTypeMapTemp.containsKey(orderType)) {
                orderTypeMapTemp.put(orderType,new ArrayList<FlowRuleStatement>());
            }
            orderTypeMapTemp.get(orderType).add(rule);
        }
    }
}