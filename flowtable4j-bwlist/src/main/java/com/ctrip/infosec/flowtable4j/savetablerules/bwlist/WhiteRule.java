package com.ctrip.infosec.flowtable4j.savetablerules.bwlist;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.List;


/**
 * Created by thyang on 2015/3/13 0013.
 */
public class WhiteRule extends BaseRule {
     @Override
    public boolean check(BWFact fact,RiskResult results) {
       return  (checkEQRuleByOrderType(fact, results)||checkNEQRuleByOrderType(fact, results));
    }

    @Override
    protected boolean checkEQRules(BWFact fact, HashMap<Integer,HashMap<String, HashMap<String, List<RuleStatement>>>> orderTypeEQ, RiskResult results) {
         for (Integer orderType : fact.getOrderTypes()) {
            if (orderTypeEQ.containsKey(orderType)) {     //遍历所有订单类型
                HashMap<String, HashMap<String, List<RuleStatement>>> matchRules = orderTypeEQ.get(orderType);
                for (String key : matchRules.keySet()) {  //遍历所有字段
                    String val = Strings.nullToEmpty(fact.getString(key)).toUpperCase();
                    if (!Strings.isNullOrEmpty(val)) {
                        HashMap<String, List<RuleStatement>> fieldRules = matchRules.get(key);// 遍历所有字段
                        if (fieldRules.containsKey(val)) {
                            List<RuleStatement> valRules = fieldRules.get(val);// 遍历 该值 相关规则
                            try {
                                for (RuleStatement ruleStatement : valRules) {
                                    if(ruleStatement.check(fact, results))
                                    {
                                        return true;
                                    }
                                }
                            } catch (Throwable ex) {
                                logger.warn(ex.getMessage(), ex);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected boolean checkNEQRules(BWFact fact,HashMap<Integer,HashMap<String, List<RuleStatement>>> orderTypeNEQ, RiskResult results) {
        for (Integer orderType : fact.getOrderTypes()) {
            if (orderTypeNEQ.containsKey(orderType)) {
                HashMap<String, List<RuleStatement>> matchRules= orderTypeNEQ.get(orderType);
                for (String key : matchRules.keySet()) { //遍历所有字段
                    String val = fact.getString(key);
                    if (!Strings.isNullOrEmpty(val)) {
                        List<RuleStatement> keyRules = matchRules.get(key); //该字段的相关规则
                        try {
                            for (RuleStatement ruleStatement : keyRules) {
                                  if(ruleStatement.check(fact, results)) {
                                     return true;
                                }
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
        return false;
    }
}
