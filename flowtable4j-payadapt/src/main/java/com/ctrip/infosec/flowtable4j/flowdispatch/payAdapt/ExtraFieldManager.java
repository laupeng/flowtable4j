package com.ctrip.infosec.flowtable4j.flowdispatch.payAdapt;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thyang on 2015/3/26 0026.
 * 一些特殊字段需要嵌套匹配
 */
public class ExtraFieldManager {

    private final static Map<String, String> listfieldmap = new HashMap<String, String>();
    static {
        listfieldmap.put("passengeridcode@16", "passengerlist");
        listfieldmap.put("mergepassengeridcode6@16", "passengerlist");
        listfieldmap.put("optionqty@19", "diyflowruleinfo");
        listfieldmap.put("vacationtype@19", "diyflowruleinfo");
        listfieldmap.put("passengername@1", "passengerlist");
        listfieldmap.put("passengernationality@1", "passengerlist");
        listfieldmap.put("passengercardid@1", "passengerlist");
        listfieldmap.put("passengernamecardid@1", "passengerlist");
        listfieldmap.put("passengercardid6@1", "passengerlist");
        listfieldmap.put("passengercardidlengthone@1", "passengerlist");
        listfieldmap.put("uidpassengername@1", "passengerlist");
        listfieldmap.put("uidpassengernamecardid@1", "passengerlist");
        listfieldmap.put("ccardnocodepassengernamecardid@1", "passengerlist");
        listfieldmap.put("mobilephonepassengercardid@1", "passengerlist");
        listfieldmap.put("emailpassengernamecardid@1", "passengerlist");
        listfieldmap.put("singleguestname@32", "guestnamelist");
        listfieldmap.put("singleguestname@2", "guestnamelist");
        listfieldmap.put("passengeridcode@18", "passengerlist");
    }
    public static String getParentNode(String fieldName,Integer orderType){
        String key=String.format("%s@%d",fieldName,orderType);
        if(listfieldmap.containsKey(key)){
            return listfieldmap.get(key);
        }
        return null;
    }
}