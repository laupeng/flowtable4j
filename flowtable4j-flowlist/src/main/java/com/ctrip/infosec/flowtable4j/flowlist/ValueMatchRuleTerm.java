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
          if(prefix==null) {
              return executor.match(fact.getString(fieldName), matchValue);
          }
          else
          {
              boolean matched=false;
              List<Map<String,Object>> subs = (List<Map<String,Object>>) fact.getList(prefix);
              if(subs!=null){
                  for (Map<String,Object> row:subs){
                     if(executor.match(getString(row,fieldName),matchValue)){
                         matched=true;
                         break;
                     }
                  }
              }
              return  matched;
          }
    }
}
