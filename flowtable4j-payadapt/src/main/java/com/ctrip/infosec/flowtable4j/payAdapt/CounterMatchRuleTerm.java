package com.ctrip.infosec.flowtable4j.payAdapt;

import com.ctrip.infosec.flowtable4j.dal.Counter;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.MapX;
import com.ctrip.infosec.sars.util.SpringContextHolder;
import com.google.common.base.Strings;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015/3/24 0024.
 */
public class CounterMatchRuleTerm extends PayAdaptRuleTerm {
    private String countType;
    private String countField;
    private Integer startOffset;
    private Integer endOffset;
    private String sqlStatement;
    private String keyFieldName;

    private static Counter counter;
    static {
        counter = SpringContextHolder.getBean("counter");
    }

    public CounterMatchRuleTerm(String fieldName, String operator, String matchValue) {
        super(fieldName, operator, matchValue);
        keyFieldName = fieldName.toUpperCase();
    }

    public void setCountType(String countType, String countField, String sqlStatement) {
        this.countType = countType;
        this.countField = countField;
        this.sqlStatement = sqlStatement.replace('@', ':').toUpperCase();
    }

    public void setTimeOffset(Integer startOffset, Integer endOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public String toString() {
        return String.format("%s|(%d,%d)|%s", sqlStatement, startOffset, endOffset, fieldName);
    }

    @Override
    public boolean check(FlowFact fact) {
        if (executor == null) {
            return false;
        }
        boolean matched = false;
        if (prefix == null) {
            String keyFieldValue = fact.getString(fieldName);
            if (!Strings.isNullOrEmpty(keyFieldValue)) {
                String key = String.format("%s|(%d,%d)|%s", sqlStatement, startOffset, endOffset, keyFieldValue).toUpperCase();
                if (fact.requestCache.containsKey(key)) {
                    matched = executor.match(fact.requestCache.get(key), matchValue);
                } else {
                    String count = counter.getCounter(countType, sqlStatement, keyFieldName, startOffset,
                            endOffset, fact.getString(countField), keyFieldValue);
                    fact.requestCache.put(key, count);
                    matched = executor.match(count, matchValue);
                }
            }
        } else {
            /**
             * C#原型为List；数组 + List在Java中反序列化为ArrayList
             * 比较乘客时需要遍历所有乘客
             */
            List<Map<String, Object>> rows =(List<Map<String,Object>>) fact.getList(prefix);
            if (rows != null && rows.size() > 0) {
                for (Map<String, Object> row : rows) {
                    String keyFieldValue = getString(row, fieldName);
                    if (!Strings.isNullOrEmpty(keyFieldValue)) {
                        String key = String.format("%s|(%d,%d)|%s", sqlStatement, startOffset, endOffset, keyFieldValue).toUpperCase();
                        if (fact.requestCache.containsKey(key)) {
                            matched = executor.match(fact.requestCache.get(key), matchValue);
                        } else {
                            String count = counter.getCounter(countType, sqlStatement, keyFieldName, startOffset,
                                    endOffset, getString(row, countField), keyFieldValue);
                            fact.requestCache.put(key, count);
                            matched = executor.match(count, matchValue);
                        }
                        if (matched) { //多位乘客，只要命中一个既退出
                            break;
                        }
                    }
                }
            }
        }
        return matched;
    }
}
