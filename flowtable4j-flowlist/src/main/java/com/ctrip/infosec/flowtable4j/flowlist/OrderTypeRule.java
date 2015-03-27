package com.ctrip.infosec.flowtable4j.flowlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 为BaseRule里面操作方便，封装
 * Created by thyang on 2015/3/25 0025.
 */
public class OrderTypeRule {
    Map<String,List<FlowRuleStatement>> byPrepay = new HashMap<String, List<FlowRuleStatement>>();
    List<FlowRuleStatement> allPrepay = new ArrayList<FlowRuleStatement>();
    public Integer size(){
        return byPrepay.size() + allPrepay.size();
    }
}
