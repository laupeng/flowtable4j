package com.ctrip.infosec.flowtable4j.bwlist;

import com.ctrip.infosec.flowtable4j.model.BWFact;
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
    public boolean check(BWFact fact,RiskResult results) {
        return checkEQRuleByOrderType(fact, results) | checkNEQRuleByOrderType(fact, results);
    }

    @Override
    protected boolean checkEQRules(BWFact fact, HashMap<Integer,HashMap<String, HashMap<String, List<RuleStatement>>>> orderTypeEQ, RiskResult results) {
        int matched = 0;
        for (Integer orderType : fact.getOrderTypes()) {
            if (orderTypeEQ.containsKey(orderType)) {
                HashMap<String, HashMap<String, List<RuleStatement>>> matchRules = orderTypeEQ.get(orderType);
                for (String key : matchRules.keySet()) { //遍历所有字段
                    String val = fact.getString(key);
                    if (!Strings.isNullOrEmpty(val)) {
                        HashMap<String, List<RuleStatement>> fieldRules = matchRules.get(key);
                        if (fieldRules.containsKey(val)) { //如果值的 Key 存在
                            List<RuleStatement> valRules = fieldRules.get(val);
                            try {
                                for (RuleStatement ruleStatement : valRules) {
                                    if(ruleStatement.check(fact, results))  matched++;
                                }
                            } catch (Throwable ex) {
                                logger.warn(ex.getMessage(), ex);
                            }
                        }
                    }
                }
            }
        }
        return matched > 0;
    }

    @Override
    protected boolean checkNEQRules(BWFact fact,HashMap<Integer,HashMap<String, List<RuleStatement>>> orderTypeNEQ, RiskResult results) {
        int matched = 0;
        for (Integer orderType : fact.getOrderTypes()) {
            if (orderTypeNEQ.containsKey(orderType)) {
                HashMap<String, List<RuleStatement>> matchRules= orderTypeNEQ.get(orderType);
                for (String key : matchRules.keySet()) { //遍历所有字段
                     String val = fact.getString(key);
                     if (!Strings.isNullOrEmpty(val)) {
                        List<RuleStatement> keyRules = matchRules.get(key);
                        try {
                            for (RuleStatement ruleStatement : keyRules) {
                                if(ruleStatement.check(fact, results)) matched++;
                            }
                        }
                        catch (Throwable ex)
                        {
                            logger.warn(ex.getMessage(),ex);
                        }
                    }
                }
            }
        }
        return matched > 0;
    }

}
