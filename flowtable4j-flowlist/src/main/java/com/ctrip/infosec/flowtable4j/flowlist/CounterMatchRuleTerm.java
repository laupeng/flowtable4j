package com.ctrip.infosec.flowtable4j.flowlist;

import com.ctrip.infosec.flowtable4j.model.FlowFact;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015/3/24 0024.
 */
public class CounterMatchRuleTerm extends FlowRuleTerm {

    private String countType;
    private String countField;
    private Integer startOffset;
    private Integer endOffset;
    private String sqlStatement;

    public CounterMatchRuleTerm(String fieldName, String operator, String matchValue) {
        super(fieldName, operator, matchValue);
    }

    public void setCountType(String countType, String countField, String sqlStatement) {
        this.countType = countType;
        this.countField = countField;
        this.sqlStatement = sqlStatement;
    }

    public void setTimeOffset(Integer startOffset, Integer endOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public boolean check(FlowFact fact) {
        boolean matched = false;
        if (prefix == null) {
            Object obj = fact.getObject(fieldName);
            if (obj != null) {
                String key = String.format("%s|(%d,%d)|%s", sqlStatement, startOffset, endOffset, obj);
                if (fact.requestCache.containsKey(key)) {
                    matched = executor.match(fact.requestCache.get(key), matchValue);
                } else {
                    String count = Counter.getCounter(countType, sqlStatement, fieldName, startOffset,
                            endOffset, fact.getObject(countField), obj);
                    fact.requestCache.put(key, count);
                    matched = executor.match(count, matchValue);
                }
            }
        } else {
            /**
             * C#原型为List；数组 + List在Java中反序列化为ArrayList
             * 比较乘客时需要遍历所有乘客
             */
            List<Map<String, Object>> rows = (List<Map<String, Object>>) fact.getList(prefix);
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Object obj = getObject(row, fieldName);
                    if (obj != null) {
                        String key = String.format("%s|(%d,%d)|%s", sqlStatement, startOffset, endOffset,obj);
                        if (fact.requestCache.containsKey(key)) {
                            matched = executor.match(fact.requestCache.get(key), matchValue);
                        } else {
                            String count = Counter.getCounter(countType, sqlStatement, fieldName, startOffset,
                                    endOffset, getObject(row, countField), obj);
                            fact.requestCache.put(key, count);
                            if (executor.match(count,matchValue)) {
                                matched = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return matched;
    }
}
