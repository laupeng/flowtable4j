package com.ctrip.infosec.flowtable4j.flowlist;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.RiskResult;

import java.util.List;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public class WhiteRule extends BaseRule {

    @Override
    public boolean check(FlowFact fact, List<RiskResult> results) {
        if (checkByOrderTypeMap(fact, results) || checkAllOrderTypeMap(fact, results)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkByOrderType(OrderTypeRule rules, FlowFact fact, List<RiskResult> results) {
        String prepayType = fact.getPrepayType();
        if (rules.byPrepay.containsKey(prepayType)) {
            for (FlowRuleStatement rule : rules.byPrepay.get(prepayType)) {
                if (rule.check(fact, results)) {
                    return true;
                }
            }
        }
        for (FlowRuleStatement rule : rules.allPrepay) {
            if (rule.check(fact, results)) {
                return true;
            }
        }
        return false;
    }
}