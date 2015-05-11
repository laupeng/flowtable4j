package com.ctrip.infosec.flowtable4j.payAdapt;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.PayAdaptRuleResult;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public class WhiteRule extends BaseRule {

    @Override
    public boolean check(FlowFact fact, List<PayAdaptRuleResult> results) {
        if (checkByOrderTypeMap(fact, results)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkByOrderType(HashMap<Integer, List<PayAdaptStatement>> byOrderType, FlowFact fact, List<PayAdaptRuleResult> results) {
        for(int orderType:fact.getOrderTypes()){
            if (byOrderType.containsKey(orderType)) {
                List<PayAdaptStatement> orderTypeRules = byOrderType.get(orderType);
                for (PayAdaptStatement rule : orderTypeRules) {
                    if (rule.check(fact, results)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}