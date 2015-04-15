package com.ctrip.infosec.flowtable4j.flowlist;

import com.ctrip.infosec.flowtable4j.dal.Counter;
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
public class CounterMatchRuleTerm extends FlowRuleTerm {

    private String countType;
    private String countField;
    private Integer startOffset;
    private Integer endOffset;
    private String sqlStatement;
    private static Logger logger = LoggerFactory.getLogger(CounterMatchRuleTerm.class);
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
        if(executor == null){
            return false;
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        boolean matched = false;
        if (prefix == null) {
            Object obj = fact.getObject(fieldName);
            if (obj != null) {
                String key = String.format("%s|(%d,%d)|%s", sqlStatement, startOffset, endOffset, obj);
                if (fact.requestCache.containsKey(key)) {
                    logger.debug("[whereField:"+fieldName+",sqlStatement:"+sqlStatement+"][op:"+executor.toString()+"][matchValue:"+matchValue+"]");
                    matched = executor.match(fact.requestCache.get(key), matchValue);
                } else {
                    String count = Counter.getCounter(countType, sqlStatement, fieldName, startOffset,
                            endOffset, fact.getObject(countField), obj);
                    fact.requestCache.put(key, count);
                    logger.debug( "[whereField:" + fieldName + ",sqlStatement:" + sqlStatement + "][op:" + executor.toString() + "][matchFieldValue:" +
                            fact.getObject(countField)+"][whereFieldValue:"+obj+"]");
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
        stopwatch.stop();
        logger.info("CounterMatch costs:"+stopwatch.elapsed(TimeUnit.MILLISECONDS)+"ms");
        return matched;
    }
}
