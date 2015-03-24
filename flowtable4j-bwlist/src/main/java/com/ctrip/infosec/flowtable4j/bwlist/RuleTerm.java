package com.ctrip.infosec.flowtable4j.bwlist;

import com.ctrip.infosec.flowtable4j.model.bw.BWFact;

import java.math.BigDecimal;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public class RuleTerm {
    private final static EQExecutor eqOper = new EQExecutor();
    private final static GEExecutor geOper = new GEExecutor();
    private final static INExecutor inOper = new INExecutor();
    private final static LEExecutor leOper = new LEExecutor();
    private final static LLIKEExecutor llOper = new LLIKEExecutor();

    private String fieldName;
    private String matchValue;
    private String operator;

    private ConditionExecutor executor;

    public RuleTerm(String fieldName,String operator,String matchValue){
        this.setFieldName(fieldName);
        this.setMatchValue(matchValue);
        this.operator = operator;

        if("EQ".equals(operator)) {
            executor = eqOper;
        } else if("LE".equals(operator)){
            executor = leOper;
        } else if("IN".equals(operator)){
            executor = inOper;
        } else if("GE".equals(operator)){
            executor = geOper;
        }
        else if("LLIKE".equals(operator)){
            executor = llOper;
        }
    }

    public boolean check(BWFact fact){
        return executor.match(fact.getString(getFieldName()), getMatchValue());
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMatchValue() {
        return matchValue;
    }

    public void setMatchValue(String matchValue) {
        this.matchValue = matchValue;
    }
}

abstract class ConditionExecutor{
    public abstract boolean match(String fieldValue,String matchValue);
}

class EQExecutor extends ConditionExecutor {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            return  fieldValue.equalsIgnoreCase(matchValue);
        }
        return false;
    }
}

class GEExecutor extends ConditionExecutor{
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            BigDecimal fv = new BigDecimal(fieldValue);
            BigDecimal mv = new BigDecimal(matchValue);
            return fv.compareTo(mv) >= 0;
        }
        return false;
    }
}

class INExecutor extends ConditionExecutor {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            return  fieldValue.indexOf(matchValue)>=0;
        }
        return false;
    }
}
class LEExecutor extends ConditionExecutor{
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            BigDecimal fv = new BigDecimal(fieldValue);
            BigDecimal mv = new BigDecimal(matchValue);
            return fv.compareTo(mv) <= 0;
        }
        return false;
    }
}

class LLIKEExecutor extends ConditionExecutor{
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            return  fieldValue.indexOf(matchValue)==0;
        }
        return false;
    }
}





