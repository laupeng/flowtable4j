package com.ctrip.infosec.flowtable4j.translate.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lpxie on 15-5-18.
 */
public class CacheFlowRuleData
{
    public static final String FlowRuleAllStatisticTable = "20001";
    public static final String StatisticTableFilter = "30001";

    private static List<Map<String,Object>> flowRules = null;
    private static List<Map<String,Object>> flowFilters = null;

    public static Map<String,Object> originalRisklevel = new HashMap<String, Object>();

    public static List<Map<String, Object>> getFlowRules()
    {
        return flowRules;
    }

    public static void setFlowRules(List<Map<String, Object>> flowRules)
    {
        CacheFlowRuleData.flowRules = flowRules;
    }

    public static List<Map<String, Object>> getFlowFilters()
    {
        return flowFilters;
    }

    public static void setFlowFilters(List<Map<String, Object>> flowFilters)
    {
        CacheFlowRuleData.flowFilters = flowFilters;
    }
}
