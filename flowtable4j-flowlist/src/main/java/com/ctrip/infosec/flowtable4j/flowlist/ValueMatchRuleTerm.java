package com.ctrip.infosec.flowtable4j.flowlist;

import com.ctrip.infosec.flowtable4j.model.FlowFact;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015/3/24 0024.
 */
public class ValueMatchRuleTerm extends FlowRuleTerm {

    public ValueMatchRuleTerm(String fieldName, String operator, String matchValue) {
        super(fieldName, operator, matchValue);
    }

    @Override
    public boolean check(FlowFact fact) {
        if(executor == null){
            return false;
        }
        boolean matched = false;
        if (prefix == null) {
            String fn = fact.getString(fieldName);
            matched = executor.match(fn, matchValue);
        } else {
            List<Map<String, Object>> rows = (List<Map<String, Object>>) fact.getList(prefix);
            if (rows != null && rows.size()>0) {
                for (Map<String, Object> row : rows) {
                    String fn = getString(row, fieldName);
                    if (executor.match(fn, matchValue)) {
                        matched = true;
                        break;
                    }
                }
            }
        }
        return matched;
    }
}
