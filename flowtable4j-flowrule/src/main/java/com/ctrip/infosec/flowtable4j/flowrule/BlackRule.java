package com.ctrip.infosec.flowtable4j.flowrule;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.List;

/**
 * 黑名单，按 KPI NAME 分组
 * Created by thyang on 2015/3/13 0013.
 */
public class BlackRule extends BaseRule  {
    @Override
    public boolean check(FlowFact fact, RiskResult results) {
         return checkByOrderTypeMap(fact, results);
    }

    @Override
    public boolean checkByOrderType(HashMap<Integer, HashMap<String, List<FlowRuleStatement>>> byOrderType, FlowFact fact, RiskResult results) {
        int matched =0;
        for(Integer orderType:fact.getOrderTypes()) {
            if(byOrderType.containsKey(orderType)){
                HashMap<String, List<FlowRuleStatement>> orderTypeRules = byOrderType.get(orderType);
                for(String s:fact.getPrepayType()) {
                    s= Strings.nullToEmpty(s).toUpperCase(); //PrepayType不区分大小写
                    if (orderTypeRules.containsKey(s)) {
                        for (FlowRuleStatement rule : orderTypeRules.get(s)) {
                             if(rule.check(fact, results)) {
                                 matched++;
                             }
                        }
                    }
                }
            }
        }
        return matched > 0;
    }
}
