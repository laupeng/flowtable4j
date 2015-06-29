package com.ctrip.infosec.flowtable4j.flowdispatch.payAdapt;

import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public abstract class PayAdaptRuleTerm {
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
    private final static Logger logger = LoggerFactory.getLogger(PayAdaptRuleTerm.class);
    protected ConditionComparer executor;
    protected String fieldName;
    private   String operator;
    protected String prefix=null;
    /**
     * 两个字段一样的值，只是子类中不会那么别扭
     */
    protected String matchValue;
    protected String matchField;


    public PayAdaptRuleTerm(String fieldName, String operator, String matchValue){
        this.fieldName = fieldName.toLowerCase();
        this.operator =operator.toLowerCase();
        this.matchValue = Strings.nullToEmpty(matchValue).toLowerCase();
        this.matchField = Strings.nullToEmpty(matchValue).toLowerCase();
        setComparer();
    }

    public abstract boolean check(FlowFact fact);

    //缺少FLESS,SCORE
    private void setComparer(){
        if("eq".equals(operator) || "feq".equals(operator)){
            executor = eqOper;
        }
        else if("ne".equals(operator)||"fne".equals(operator)){
            executor = neOper;
        }
        else if("ge".equals(operator)||"fge".equals(operator)){
            executor = geOper;
        }
        else if("in".equals(operator)||"fin".equals(operator)){
            executor = inOper;
        }
        else if("le".equals(operator)||"fle".equals(operator)){
            executor = leOper;
        }
        else if("gt".equals(operator)||"great".equals(operator)||"fgt".equals(operator)){
            executor = gtOper;
        }
        else if("na".equals(operator)|| "fna".equals(operator)){
            executor = naOper;
        }
        else if("lt".equals(operator)||"less".equals(operator)||"flt".equals(operator)||"fless".equals(operator)){
            executor = ltOper;
        }
        else if("llike".equals(operator)){
            executor = llOper;
        }
        else if("rlike".equals(operator)){
            executor = rlOper;
        }
        else if("regex".equals(operator)){
            executor = rgOper;
        }
    }

    protected String getString(Map<String,Object> row,String fieldName){
        if(row.containsKey(fieldName)){
          Object obj=row.get(fieldName);
          if(obj != null)
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
        if(!Strings.isNullOrEmpty(fieldValue) && !Strings.isNullOrEmpty(matchValue)){
            fieldValue = fieldValue.trim();
            matchValue = matchValue.trim();
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
        if(!Strings.isNullOrEmpty(fieldValue) && !Strings.isNullOrEmpty(matchValue)){
            fieldValue = fieldValue.trim();
            matchValue=  matchValue.trim();
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
        if(!Strings.isNullOrEmpty(fieldValue)  && !Strings.isNullOrEmpty(matchValue)){
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
        //等同正则表达式
        if(!Strings.isNullOrEmpty(fieldValue) && !Strings.isNullOrEmpty(matchValue)){
            fieldValue = fieldValue.trim();
            matchValue = matchValue.trim();
            Pattern p = Pattern.compile(matchValue,Pattern.CASE_INSENSITIVE);
            return  p.matcher(fieldValue).find();
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
        if(!Strings.isNullOrEmpty(fieldValue) && !Strings.isNullOrEmpty(matchValue)){
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
        if(!Strings.isNullOrEmpty(fieldValue) && !Strings.isNullOrEmpty(matchValue)){
            fieldValue = fieldValue.trim();
            matchValue = matchValue.trim();
            Pattern p = Pattern.compile(matchValue,Pattern.CASE_INSENSITIVE);
            return  p.matcher(fieldValue).find();
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
        if(!Strings.isNullOrEmpty(fieldValue) && !Strings.isNullOrEmpty(matchValue)){
            fieldValue = fieldValue.trim();
            matchValue = matchValue.trim();
            Pattern p = Pattern.compile(matchValue,Pattern.CASE_INSENSITIVE);
            return  p.matcher(fieldValue).find();
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
        if(!Strings.isNullOrEmpty(fieldValue) && !Strings.isNullOrEmpty(matchValue)){
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
        if(!Strings.isNullOrEmpty(fieldValue) && !Strings.isNullOrEmpty(matchValue)){
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
        if(!Strings.isNullOrEmpty(fieldValue) && !Strings.isNullOrEmpty(matchValue)){
            fieldValue = fieldValue.trim();
            matchValue = matchValue.trim();
            Pattern p = Pattern.compile(matchValue,Pattern.CASE_INSENSITIVE);
            return !p.matcher(fieldValue).find();
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
        if (matchValue!=null){
            fieldValue = Strings.nullToEmpty(fieldValue).trim();
            matchValue = matchValue.trim();
            Pattern p = Pattern.compile(matchValue,Pattern.CASE_INSENSITIVE);
            return  p.matcher(fieldValue).find();
        }
        return false;
    }

    @Override
    public String toString() {
        return "RegXComparer{}";
    }
}
