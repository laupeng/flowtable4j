package com.ctrip.infosec.flowtable4j.payAdapt;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.MapX;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015/3/24 0024.
 */
public class FieldMatchRuleTerm extends PayAdaptRuleTerm {
    public FieldMatchRuleTerm(String fieldName, String operator, String matchField) {
        super(fieldName, operator, matchField);
    }

    @Override
    public boolean check(FlowFact fact) {
        if(executor == null){
            return false;
        }
        boolean matched = false;
        if (prefix == null) {
            String fn = fact.getString(fieldName);
            String mf = fact.getString(matchField);
            matched = executor.match(fn, mf);
        } else {
            List<Map<String, Object>> rows = (List<Map<String,Object>>) fact.getList(prefix);
            if (rows != null && rows.size() > 0) {
                for (Map<String, Object> row : rows) {
                    String fn = getString(row,fieldName);
                    String mf = getString(row,matchField);
                    matched =executor.match(fn, mf);
                    if(matched){
                        break;
                    }
                }
            }
        }
        return matched;
    }
}
