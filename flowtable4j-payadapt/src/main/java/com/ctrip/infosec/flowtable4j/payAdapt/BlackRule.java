package com.ctrip.infosec.flowtable4j.payAdapt;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.PayAdaptRuleResult;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.List;

/**
 * 黑名单，按 KPI NAME 分组
 * Created by thyang on 2015/3/13 0013.
 */
public class BlackRule extends BaseRule {
    @Override
    public boolean check(FlowFact fact, List<PayAdaptRuleResult> results) {
        return checkByOrderTypeMap(fact, results);
    }

    @Override
    public boolean checkByOrderType(HashMap<Integer, List<PayAdaptStatement>> byOrderType, FlowFact fact, List<PayAdaptRuleResult> results) {
        int matched = 0;
        for(int orderType:fact.getOrderTypes()){
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
}
