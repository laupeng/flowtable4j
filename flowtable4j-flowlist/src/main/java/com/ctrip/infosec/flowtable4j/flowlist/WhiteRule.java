package com.ctrip.infosec.flowtable4j.flowlist;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.CheckResultLog;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import java.util.List;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public class WhiteRule extends BaseRule {

    @Override
    public boolean check(FlowFact fact, RiskResult results) {
        if (checkByOrderTypeMap(fact, results) || checkAllOrderTypeMap(fact, results)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkByOrderType(OrderTypeRule rules, FlowFact fact, RiskResult results) {
        List<String> prepayType = fact.getPrepayType();
        for(String s:prepayType) {
            if (rules.byPrepay.containsKey(s)) {
                for (FlowRuleStatement rule : rules.byPrepay.get(s)) {
                    if (rule.check(fact, results)) {
                        return true;
                    }
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