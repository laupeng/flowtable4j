package com.ctrip.infosec.flowtable4j.flowlist;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by thyang on 2015/3/24 0024.
 */
public class ValueMatchRuleTerm extends FlowRuleTerm {
    private static Logger logger = LoggerFactory.getLogger(ValueMatchRuleTerm.class);

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
            logger.debug("[fieldName:" + fieldName + ",fieldValue:" + fn + "][op:" + executor.toString() + "][matchValue:" + matchValue + "]");
            matched = executor.match(fn, matchValue);
        } else {
            List<Map<String, Object>> rows = (List<Map<String, Object>>) fact.getList(prefix);
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    String fn = getString(row, fieldName);
                    logger.debug("[fieldName:"+fieldName+",fieldValue:"+fn+"][op:"+executor.toString()+"][matchValue:"+matchValue+"]");
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
