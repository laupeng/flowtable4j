package com.ctrip.infosec.flowtable4j.payAdapt;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.RiskResult;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public class WhiteRule extends BaseRule {

    @Override
    public boolean check(FlowFact fact, RiskResult results) {
        if (checkByOrderTypeMap(fact, results)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkByOrderType(HashMap<Integer, HashMap<String, List<PayAdaptStatement>>> byOrderType, FlowFact fact, RiskResult results) {
        for(Integer orderType:fact.getOrderTypes()) {
            if(byOrderType.containsKey(orderType)){
                HashMap<String, List<PayAdaptStatement>> orderTypeRules = byOrderType.get(orderType);
                for(String s:fact.getPrepayType()) {
                    s= Strings.nullToEmpty(s).toUpperCase(); //PrepayType不区分大小写
                    if (orderTypeRules.containsKey(s)) {
                        for (PayAdaptStatement rule : orderTypeRules.get(s)) {
                            if (rule.check(fact, results)){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}