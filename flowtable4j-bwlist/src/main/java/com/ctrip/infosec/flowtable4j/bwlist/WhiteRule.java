package com.ctrip.infosec.flowtable4j.bwlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public class WhiteRule extends BaseRule {
     @Override
    public boolean check(BWFact fact,List<BWResult> results) {
       return  (checkEQRuleByOrderType(fact, results)||checkGlobalEQRule(fact, results)||checkNEQRuleByOrderType(fact, results)||checkGlobalNEQRule(fact, results));
    }

    @Override
    protected boolean checkEQRules(BWFact fact, HashMap<String, HashMap<String, List<RuleStatement>>> matchRules,List<BWResult> results) {
        for (String key : matchRules.keySet()) {
            String val = fact.getString(key);
            if (val != null && val != "") {
                HashMap<String, List<RuleStatement>> fieldRules = matchRules.get(key);
                if (fieldRules.containsKey(val)) {
                    List<RuleStatement> valRules= fieldRules.get(val);
                    for (RuleStatement ruleStatement : valRules) {
                        if (ruleStatement.check(fact, results)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected boolean checkNEQRules(BWFact fact, HashMap<String, List<RuleStatement>> matchRules, List<BWResult> results) {
        for (String key : matchRules.keySet()) {
            String val = fact.getString(key);
            if (val != null && val != "") {
                List<RuleStatement> keyRules= matchRules.get(key);
                for (RuleStatement ruleStatement : keyRules) {
                    if (ruleStatement.check(fact, results)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
