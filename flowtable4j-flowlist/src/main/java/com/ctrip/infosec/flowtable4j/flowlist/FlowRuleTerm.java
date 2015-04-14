package com.ctrip.infosec.flowtable4j.flowlist;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public abstract class FlowRuleTerm {
    private final static EQComparer eqOper = new EQComparer();
    private final static NEComparer neOper = new NEComparer();
    private final static GEComparer geOper = new GEComparer();
    private final static GTComparer gtOper = new GTComparer();
    private final static INComparer inOper = new INComparer();
    private final static NAComparer naOper = new NAComparer();
    private final static LEComparer leOper = new LEComparer();
    private final static LTComparer ltOper = new LTComparer();
    private final static LLIKEComparer llOper = new LLIKEComparer();
    private final static RLIKEComparer rlOper = new RLIKEComparer();
    private final static RegXComparer rgOper = new RegXComparer();
    private final static Logger logger = LoggerFactory.getLogger(FlowRuleTerm.class);
    protected ConditionComparer executor;
    protected String fieldName;
    private   String operator;
    protected String prefix=null;
    /**
     * 两个字段一样的值，只是子类中不会那么别扭
     */
    protected String matchValue;
    protected String matchField;


    public FlowRuleTerm(String fieldName,String operator,String matchValue){
        this.fieldName = fieldName;
        this.operator =operator;
        this.matchValue = matchValue;
        this.matchField = matchValue;
        setComparer();
    }

    public abstract boolean check(FlowFact fact);

    //缺少FLESS,SCORE
    private void setComparer(){
        if("EQ".equals(operator) || "FEQ".equals(operator)){
            executor = eqOper;
        }
        else if("NE".equals(operator)||"FNE".equals(operator)){
            executor = neOper;
        }
        else if("GE".equals(operator)||"FGE".equals(operator)){
            executor =geOper;
        }
        else if("IN".equals(operator)||"FIN".equals(operator)){
            executor =inOper;
        }
        else if("LE".equals(operator)||"FLE".equals(operator)){
            executor = leOper;
        }
        else if("GT".equals(operator)||"GREAT".equals(operator)||"FGT".equals(operator)){
            executor = gtOper;
        }
        else if("NA".equals(operator)|| "FNA".equals(operator)){
            executor = naOper;
        }
        else if("LT".equals(operator)||"LESS".equals(operator)||"FLT".equals(operator)||"FLESS".equals(operator)){
            executor = ltOper;
        }
        else if("LLIKE".equals(operator)){
            executor = llOper;
        }
        else if("RLIKE".equals(operator)){
            executor = rlOper;
        }
        else if("REGEX".equals(operator)){
            executor = rgOper;
        }else{
            logger.error("has unbind op:"+operator);
        }
    }

    protected String getString(Map<String,Object> row,String fieldName){
        if(row.containsKey(fieldName)){
          Object obj=row.get(fieldName);
            if(obj instanceof String){
                return (String)obj;
            }
            else
            {
                return obj.toString();
            }
        }
        return null;
    }

    protected Object getObject(Map<String,Object> row,String fieldName){
        if(row.containsKey(fieldName)){
           return row.get(fieldName);
        }
        return null;
    }

}

/**
 * 一系列比较器
 */
abstract class ConditionComparer {
    public abstract boolean match(String fieldValue,String matchValue);

}

class EQComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {

        if(fieldValue!=null && matchValue!=null){
            return  fieldValue.equalsIgnoreCase(matchValue);
        }
        return false;
    }

    @Override
    public String toString() {
        return "EQComparer{}";
    }
}

class NEComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            return  !fieldValue.equalsIgnoreCase(matchValue);
        }
        return false;
    }

    @Override
    public String toString() {
        return "NEComparer{}";
    }
}

class GEComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            BigDecimal fv = new BigDecimal(fieldValue);
            BigDecimal mv = new BigDecimal(matchValue);
            return fv.compareTo(mv) >= 0;
        }
        return false;
    }

    @Override
    public String toString() {
        return "GEComparer{}";
    }
}

class INComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            return  fieldValue.contains(matchValue);
        }
        return false;
    }

    @Override
    public String toString() {
        return "INComparer{}";
    }
}

class LEComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            BigDecimal fv = new BigDecimal(fieldValue);
            BigDecimal mv = new BigDecimal(matchValue);
            return fv.compareTo(mv) <= 0;
        }
        return false;
    }

    @Override
    public String toString() {
        return "LEComparer{}";
    }
}

class LLIKEComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            return  fieldValue.startsWith(matchValue);
        }
        return false;
    }

    @Override
    public String toString() {
        return "LLIKEComparer{}";
    }
}

class RLIKEComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            return  fieldValue.endsWith(matchValue);
        }
        return false;
    }

    @Override
    public String toString() {
        return "RLIKEComparer{}";
    }
}

class GTComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            BigDecimal fv = new BigDecimal(fieldValue);
            BigDecimal mv = new BigDecimal(matchValue);
            return fv.compareTo(mv) > 0;
        }
        return false;
    }

    @Override
    public String toString() {
        return "GTComparer{}";
    }
}

class LTComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            BigDecimal fv = new BigDecimal(fieldValue);
            BigDecimal mv = new BigDecimal(matchValue);
            return fv.compareTo(mv) < 0;
        }
        return false;
    }

    @Override
    public String toString() {
        return "LTComparer{}";
    }
}

class NAComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            return  !fieldValue.contains(matchValue);
        }
        return false;
    }

    @Override
    public String toString() {
        return "NAComparer{}";
    }
}

class RegXComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if(fieldValue!=null && matchValue!=null){
            return  fieldValue.matches(matchValue);
        }
        return false;
    }

    @Override
    public String toString() {
        return "RegXComparer{}";
    }
}
