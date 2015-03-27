package com.ctrip.infosec.flowtable4j.flowlist;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.RiskResult;

import java.util.List;

/**
 * 黑名单，按 KPI NAME 分组
 * Created by thyang on 2015/3/13 0013.
 */
public class BlackRule extends BaseRule  {
    @Override
    public boolean check(FlowFact fact, List<RiskResult> results) {
        checkByOrderTypeMap(fact, results);
        checkAllOrderTypeMap(fact, results);
        return  true;
    }

    @Override
    public boolean checkByOrderType(OrderTypeRule rules, FlowFact fact, List<RiskResult> results) {
        String prepayType = fact.getPrepayType();
        if(rules.byPrepay.containsKey(prepayType)) {
            for(FlowRuleStatement rule:rules.byPrepay.get(prepayType)){
                rule.check(fact,results);
            }
        }
        for(FlowRuleStatement rule:rules.allPrepay){
            rule.check(fact,results);
        }
        return true;
    }
}
