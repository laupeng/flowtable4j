package com.ctrip.infosec.flowtable4j.biz.subprocessor;

import com.ctrip.infosec.flowtable4j.biz.ConverterBase;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-07-04.
 */
@Component
public class BlackWhiteConverterEx extends ConverterBase {

    public void fillFlightInfo(Map<String, Object> productinfo,Map<String,Object> content) {
        List<Map<String,Object>> flightinfolistMap = getList(productinfo,"flightinfolist");
        if(flightinfolistMap!=null && flightinfolistMap.size()>0){
            for(Map<String,Object> flightInfo :flightinfolistMap){
                //复制 FlightOrder信息
                copyMapIfNotNull(getMap(flightInfo,"order"),content, new String[]{"aairport", "eairport", "dairport", "acity", "dcity"});

                //合并PassengerInfo信息
                List<Map<String,Object>> passengers = getList(flightInfo,"passengerlist");
                if(passengers!=null && passengers.size()>0){
                    StringBuilder names=new StringBuilder("|");
                    StringBuilder nations = new StringBuilder("|");
                    StringBuilder cards = new StringBuilder("|");
                    for (Map<String,Object> p:passengers){
                        if(getString(p,"passengername")!=null){
                            names.append(getString(p,"passengername")).append("|");
                        }
                        if(getString(p,"passengernationality")!=null){
                            names.append(getString(p,"passengernationality")).append("|");
                        }
                        if(getString(p,"passengercardid")!=null){
                            names.append(getString(p,"passengercardid")).append("|");
                        }
                    }
                    setValue(content,"passengername",names.toString());
                    setValue(content,"passengernationality",nations.toString());
                    setValue(content,"passengercardid",cards.toString());
                }
            }
        }
    }
}
