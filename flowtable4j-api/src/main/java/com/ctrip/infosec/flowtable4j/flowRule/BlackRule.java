package com.ctrip.infosec.flowtable4j.flowRule;

import java.util.HashMap;
import java.util.List;

/**
 * 黑名单，按 KPI NAME 分组
 * Created by thyang on 2015/3/13 0013.
 */
public class BlackRule extends BaseRule  {

    @Override
    public boolean check(FlowFact fact, List<BWResult> results) {
        checkEQRuleByOrderType(fact, results);
        checkGlobalEQRule(fact, results);
        checkNEQRuleByOrderType(fact, results);
        checkGlobalNEQRule(fact, results);
        return true;
    }

    @Override
    protected boolean checkEQRules(FlowFact fact, HashMap<String, HashMap<String, List<FlowRuleStatement>>> matchRules, List<BWResult> results) {
        boolean matched=false;
        for (String key : matchRules.keySet()) {
            String val = fact.getString(key);
            if (val != null && val != "") {
                HashMap<String, List<FlowRuleStatement>> fieldRules = matchRules.get(key);
                if (fieldRules.containsKey(val)) {
                    List<FlowRuleStatement> valRules = fieldRules.get(val);
                    for (FlowRuleStatement flowRuleStatement : valRules) {
                       matched = flowRuleStatement.check(fact, results) | matched;
                    }
                }
            }
        }
        return matched;
    }

    @Override
    protected boolean checkNEQRules(FlowFact fact, HashMap<String, List<FlowRuleStatement>> matchRules, List<BWResult> results) {
        boolean matched=false;
        for (String key : matchRules.keySet()) {
            String val = fact.getString(key);
            if (val != null && val != "") {
                List<FlowRuleStatement> keyRules = matchRules.get(key);
                for (FlowRuleStatement flowRuleStatement : keyRules) {
                   matched = flowRuleStatement.check(fact, results) | matched;
                }
            }
        }
        return matched;
    }

}
