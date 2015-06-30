package com.ctrip.infosec.flowtable4j.bwrule;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.google.common.base.Strings;

import java.math.BigDecimal;

/**
 * Created by thyang on 2015/3/13 0013.
 */
public class RuleTerm {
    private final static EQComparer eqOper = new EQComparer();
    private final static GEComparer geOper = new GEComparer();
    private final static INComparer inOper = new INComparer();
    private final static LEComparer leOper = new LEComparer();
    private final static LLIKEComparer llOper = new LLIKEComparer();

    private String fieldName;
    private String matchValue;
    private String operator;

    private ConditionComparer executor;

    public RuleTerm(String fieldName, String operator, String matchValue) {
        this.setFieldName(fieldName.toLowerCase());
        //兼容性考虑，用matchValue作为字典的Key，需要不区分大小写
        this.setMatchValue(Strings.nullToEmpty(matchValue).toLowerCase());
        this.operator = Strings.nullToEmpty(operator).toLowerCase();

        if ("eq".equals(operator)) {
            executor = eqOper;
        } else if ("le".equals(operator)) {
            executor = leOper;
        } else if ("in".equals(operator)) {
            executor = inOper;
        } else if ("ge".equals(operator)) {
            executor = geOper;
        } else if ("llike".equals(operator)) {
            executor = llOper;
        }
    }

    public boolean check(BWFact fact) {
        if (executor == null) {
            return false;
        }
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

abstract class ConditionComparer {
    public abstract boolean match(String fieldValue, String matchValue);
}

class EQComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if (!Strings.isNullOrEmpty(fieldValue)) {
            fieldValue = fieldValue.trim();
            matchValue = Strings.nullToEmpty(matchValue).trim();
            return fieldValue.equalsIgnoreCase(matchValue);
        }
        return false;
    }
}

class GEComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if (!Strings.isNullOrEmpty(fieldValue)) {
            BigDecimal fv = new BigDecimal(fieldValue);
            BigDecimal mv = new BigDecimal(matchValue);
            return fv.compareTo(mv) >= 0;
        }
        return false;
    }
}

class INComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if (!Strings.isNullOrEmpty(fieldValue)) {
              fieldValue= fieldValue.toLowerCase().trim();
              matchValue =Strings.nullToEmpty(matchValue).toLowerCase().trim();
              return fieldValue.contains(matchValue);
              //黑白名单中，Match Value小
        }
        return false;
    }
}

class LEComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if (!Strings.isNullOrEmpty(fieldValue)) {
            BigDecimal fv = new BigDecimal(fieldValue);
            BigDecimal mv = new BigDecimal(matchValue);
            return fv.compareTo(mv) <= 0;
        }
        return false;
    }
}

class LLIKEComparer extends ConditionComparer {
    @Override
    public boolean match(String fieldValue, String matchValue) {
        if (!Strings.isNullOrEmpty(fieldValue)) {
            fieldValue = fieldValue.toLowerCase().trim();
            matchValue = Strings.nullToEmpty(matchValue).toLowerCase().trim();
            return fieldValue.startsWith(matchValue);
        }
        return false;
    }
}
