package com.ctrip.infosec.flowtable4j.flowlist;

import com.ctrip.infosec.flowtable4j.model.FlowFact;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015/3/24 0024.
 */
public class CounterMatchRuleTerm extends FlowRuleTerm {

    private String  countType;
    private String  countField;
    private Integer startOffset;
    private Integer endOffset;
    private String  sqlStatement;

    public CounterMatchRuleTerm(String fieldName,String operator,String matchValue)
    {
        super(fieldName,operator,matchValue);
    }

    public void setCountType(String countType,String countField,String sqlStatement){
        this.countType = countType;
        this.countField = countField;
        this.sqlStatement = sqlStatement;
    }

    public void setTimeOffset(Integer startOffset,Integer endOffset){
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public boolean check(FlowFact fact) {
        if(prefix==null) {
            String key = String.format("%s|(%d,%d)|%s", sqlStatement,startOffset, endOffset,fact.getObject(fieldName));
            if (fact.requestCache.containsKey(key)) {
                return executor.match(fact.requestCache.get(key), matchValue);
            } else {
                String count = Counter.getCounter(countType,sqlStatement,fieldName,startOffset,
                                                  endOffset,fact.getObject(countField), fact.getObject(fieldName));
                fact.requestCache.put(key, count);
                return executor.match(count, matchValue);
            }
        }
        else
        {
            boolean matched=false;
            List<Map<String,Object>> subs = (List<Map<String,Object>>) fact.getList(prefix);
            if(subs!=null){
                for (Map<String,Object> row:subs){
                        String key = String.format("%s|(%d,%d)|%s", sqlStatement,startOffset, endOffset,fact.getObject(fieldName));
                       if (fact.requestCache.containsKey(key)) {
                           return executor.match(fact.requestCache.get(key), matchValue);
                        } else {
                        String count = Counter.getCounter(countType,sqlStatement ,fieldName, startOffset,
                                endOffset, getObject(row,countField), getObject(row, fieldName));
                        fact.requestCache.put(key, count);
                        if(executor.match(count, matchValue))
                        {
                            matched =true;
                            break;
                        }
                    }
                }
            }
            return  matched;
        }
    }
}
