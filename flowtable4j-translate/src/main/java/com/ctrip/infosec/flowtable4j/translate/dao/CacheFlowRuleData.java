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

    //private static List<Map<String,Object>> flowRules = null;
    private static List<Map<String,Object>> flowFilters = null;

    public static Map<String,Object> flowRules = new HashMap<String, Object>();
    public static Map<String,Object> originalRisklevel = new HashMap<String, Object>();

    public static List<Map<String, Object>> getFlowFilters()
    {
        return flowFilters;
    }

    public static void setFlowFilters(List<Map<String, Object>> flowFilters)
    {
        CacheFlowRuleData.flowFilters = flowFilters;
    }

    //缓存机场和城市的编码
    public static Map<String,Object> airPortCache = new HashMap();
}
