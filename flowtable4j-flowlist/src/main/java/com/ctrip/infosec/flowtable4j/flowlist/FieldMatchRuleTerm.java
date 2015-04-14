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
public class FieldMatchRuleTerm extends FlowRuleTerm {
    private static Logger logger= LoggerFactory.getLogger(FieldMatchRuleTerm.class);
    public FieldMatchRuleTerm(String fieldName, String operator, String matchField) {
        super(fieldName, operator, matchField);
    }

    @Override
    public boolean check(FlowFact fact) {
        boolean matched = false;
        if (prefix == null) {
//            matched = executor.match(fact.getString(fieldName), fact.getString(matchField));
            String fn = fact.getString(fieldName);
            String mf = fact.getString(matchField);
            logger.debug("[fieldName:"+fieldName+",fieldValue:"+fn+"][op:"+executor.toString()+"][matchField:"+matchField+",matchValue:"+mf+"]");
            matched = executor.match(fn, mf);
        } else {
            List<Map<String, Object>> rows = (List<Map<String, Object>>) fact.getList(prefix);
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    String fn = getString(row, fieldName);
                    String mf = getString(row, matchField);
                    logger.debug("[fieldName:"+fieldName+",fieldValue"+fn+"][op:"+executor.toString()+"][matchField:"+matchField+",matchValue"+mf+"]");
                    if (executor.match(fn, mf)) {
                        matched = true;
                        break;
                    }
                }
            }
        }
        return matched;
    }
}
