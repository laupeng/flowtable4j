package com.ctrip.infosec.flowtable4j.bwlist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 黑名单，按 KPI NAME 分组
 * Created by thyang on 2015/3/13 0013.
 */
public class BlackRule extends BaseRule  {

    @Override
    public boolean check(BWFact fact, List<BWResult> results) {
        checkEQRuleByOrderType(fact, results);
        checkGlobalEQRule(fact, results);
        checkNEQRuleByOrderType(fact, results);
        checkGlobalNEQRule(fact, results);
        return true;
    }

    @Override
    protected boolean checkEQRules(BWFact fact, HashMap<String, HashMap<String, List<RuleStatement>>> matchRules, List<BWResult> results) {
        boolean matched=false;
        for (String key : matchRules.keySet()) {
            String val = fact.getString(key);
            if (val != null && val != "") {
                HashMap<String, List<RuleStatement>> fieldRules = matchRules.get(key);
                if (fieldRules.containsKey(val)) {
                    List<RuleStatement> valRules = fieldRules.get(val);
                    for (RuleStatement ruleStatement : valRules) {
                       matched = ruleStatement.check(fact, results) | matched;
                    }
                }
            }
        }
        return matched;
    }

    @Override
    protected boolean checkNEQRules(BWFact fact, HashMap<String, List<RuleStatement>> matchRules, List<BWResult> results) {
        boolean matched=false;
        for (String key : matchRules.keySet()) {
            String val = fact.getString(key);
            if (val != null && val != "") {
                List<RuleStatement> keyRules = matchRules.get(key);
                for (RuleStatement ruleStatement : keyRules) {
                   matched = ruleStatement.check(fact, results) | matched;
                }
            }
        }
        return matched;
    }

}
