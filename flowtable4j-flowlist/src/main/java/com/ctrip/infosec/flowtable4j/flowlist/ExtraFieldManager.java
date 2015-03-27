package com.ctrip.infosec.flowtable4j.flowlist;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thyang on 2015/3/26 0026.
 * 一些特殊字段需要嵌套匹配
 */
public class ExtraFieldManager {

    private final static Map<String, String> listFieldMap = new HashMap<String, String>();
    static {
        listFieldMap.put("PassengerIDCode@16", "PassengerList");
        listFieldMap.put("MergePassengerIDCode6@16", "PassengerList");
        listFieldMap.put("OptionQty@19", "DIYFlowRuleInfo");
        listFieldMap.put("VacationType@19", "DIYFlowRuleInfo");
        listFieldMap.put("PassengerName@1", "PassengerList");
        listFieldMap.put("PassengerNationality@1", "PassengerList");
        listFieldMap.put("PassengerCardID@1", "PassengerList");
        listFieldMap.put("PassengerNameCardID@1", "PassengerList");
        listFieldMap.put("PassengerCardID6@1", "PassengerList");
        listFieldMap.put("PassengerCardIDLengthOne@1", "PassengerList");
        listFieldMap.put("UidPassengerName@1", "PassengerList");
        listFieldMap.put("UidPassengerNameCardID@1", "PassengerList");
        listFieldMap.put("CCardNoCodePassengerNameCardID@1", "PassengerList");
        listFieldMap.put("MobilePhonePassengerCardID@1", "PassengerList");
        listFieldMap.put("EMailPassengerNameCardID@1", "PassengerList");
        listFieldMap.put("SingleGuestName@32", "GuestNameList");
        listFieldMap.put("SingleGuestName@2", "GuestNameList");
        listFieldMap.put("PassengerIDCode@18", "PassengerList");
    }
    public static String getParentNode(String fieldName,Integer orderType){
        String key=String.format("%s@%d",fieldName,orderType);
        if(listFieldMap.containsKey(key)){
            return listFieldMap.get(key);
        }
        return null;
    }
}