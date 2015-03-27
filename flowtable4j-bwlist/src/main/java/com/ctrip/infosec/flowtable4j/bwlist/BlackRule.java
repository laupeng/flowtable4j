package com.ctrip.infosec.flowtable4j.bwlist;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.RiskResult;

import java.util.HashMap;
import java.util.List;

/**
 * 黑名单，按 KPI NAME 分组
 * Created by thyang on 2015/3/13 0013.
 */
public class BlackRule extends BaseRule {

    @Override
    public boolean check(BWFact fact, List<RiskResult> results) {
        checkEQRuleByOrderType(fact, results);
        checkGlobalEQRule(fact, results);
        checkNEQRuleByOrderType(fact, results);
        checkGlobalNEQRule(fact, results);
        return true;
    }

    @Override
    protected boolean checkEQRules(BWFact fact, HashMap<String, HashMap<String, List<RuleStatement>>> matchRules, List<RiskResult> results) {
        boolean matched = false;
        for (String key : matchRules.keySet()) {
            String val = fact.getString(key);
            if (val != null && val != "") {
                HashMap<String, List<RuleStatement>> fieldRules = matchRules.get(key);
                if (fieldRules.containsKey(val)) {
                    List<RuleStatement> valRules = fieldRules.get(val);
                    try {
                        for (RuleStatement ruleStatement : valRules) {
                            matched = ruleStatement.check(fact, results) | matched;
                        }
                    }
                    catch (Throwable ex)
                    {
                        logger.warn(ex.getMessage());
                    }
                }
            }
        }
        return matched;
    }

    @Override
    protected boolean checkNEQRules(BWFact fact, HashMap<String, List<RuleStatement>> matchRules, List<RiskResult> results) {
        boolean matched = false;
        for (String key : matchRules.keySet()) {
            String val = fact.getString(key);
            if (val != null && val != "") {
                List<RuleStatement> keyRules = matchRules.get(key);
                try {
                    for (RuleStatement ruleStatement : keyRules) {
                        matched = ruleStatement.check(fact, results) | matched;
                    }
                }
                catch (Throwable ex)
                {
                    logger.warn(ex.getMessage());
                }
            }
        }
        return matched;
    }

}
