package com.ctrip.infosec.flowtable4j.flowlist;

import com.ctrip.infosec.flowtable4j.model.FlowFact;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015/3/24 0024.
 */
public class ValueMatchRuleTerm extends FlowRuleTerm {

    public ValueMatchRuleTerm(String fieldName,String operator,String matchValue){
        super(fieldName,operator,matchValue);
    }
    @Override
    public boolean check(FlowFact fact) {
          boolean matched=false;
          if(prefix==null) {
              matched = executor.match(fact.getString(fieldName), matchValue);
          }
          else
          {
              List<Map<String,Object>> rows = (List<Map<String,Object>>) fact.getList(prefix);
              if(rows!=null){
                  for (Map<String,Object> row:rows){
                     if(executor.match(getString(row,fieldName),matchValue)){
                         matched=true;
                         break;
                     }
                  }
              }
          }
        return  matched;
    }
}
