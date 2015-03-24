package com.ctrip.infosec.flowtable4j.flowRule;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public class WhiteRule extends BaseRule {
     @Override
    public boolean check(FlowFact fact,List<BWResult> results) {
       return  (checkEQRuleByOrderType(fact, results)||checkGlobalEQRule(fact, results)||checkNEQRuleByOrderType(fact, results)||checkGlobalNEQRule(fact, results));
    }

    @Override
    protected boolean checkEQRules(FlowFact fact, HashMap<String, HashMap<String, List<FlowRuleStatement>>> matchRules,List<BWResult> results) {
        for (String key : matchRules.keySet()) {
            String val = fact.getString(key);
            if (val != null && val != "") {
                HashMap<String, List<FlowRuleStatement>> fieldRules = matchRules.get(key);
                if (fieldRules.containsKey(val)) {
                    List<FlowRuleStatement> valRules= fieldRules.get(val);
                    for (FlowRuleStatement flowRuleStatement : valRules) {
                        if (flowRuleStatement.check(fact, results)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected boolean checkNEQRules(FlowFact fact, HashMap<String, List<FlowRuleStatement>> matchRules, List<BWResult> results) {
        for (String key : matchRules.keySet()) {
            String val = fact.getString(key);
            if (val != null && val != "") {
                List<FlowRuleStatement> keyRules= matchRules.get(key);
                for (FlowRuleStatement flowRuleStatement : keyRules) {
                    if (flowRuleStatement.check(fact, results)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
