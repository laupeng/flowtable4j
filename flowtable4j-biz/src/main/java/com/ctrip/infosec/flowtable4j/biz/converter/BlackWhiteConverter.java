package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.baseconverter.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.RequestBody;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 转换PO为黑白名单，支付风控黑白名单
 * Created by thyang on 2015-06-10.
 */
@Component
public class BlackWhiteConverter extends ConverterBase {
    @Autowired
    BlackWhiteConverterEx blackWhiteConverterEx;

    public List<String> getModule(int orderType) {
        if (CtripOrderType.HotelGroup.getCode() == orderType) {
            return Arrays.asList(new String[]{"hotelgroupinfolist","paymentinfolist"});
        }
        if (CtripOrderType.Flights.getCode() == orderType) {
            return Arrays.asList(new String[]{"flightinfolist","paymentinfolist"});
        }
        if (CtripOrderType.CRH.getCode() == orderType) {
            return Arrays.asList(new String[]{"railinfolist","vacationinfolist","paymentinfolist"});
        }
        return new ArrayList<String>();
    }

    public BWFact convert(PO po) {

        BWFact fact = new BWFact();
        Map<String, Object> content = new HashMap<String, Object>();
        fact.setContent(content);
        fact.setOrderType(po.getOrdertype());
        fact.setOrderTypes(new ArrayList<Integer>());
        fact.getOrderTypes().add(0);
        fact.getOrderTypes().add(po.getOrdertype());
        Map<String, Object> productInfo = po.getProductinfo();
        Map<String, Object> paymentInfo = po.getPaymentinfo();
        List<String> modules = getModule(po.getOrdertype());

        copyMapIfNotNull(getMap(productInfo, "ipinfo"), content,
                ImmutableMap.of("ipcity", "ipcity", "ipcountry", "ipcountry", "useripadd", "userip"));

        copyMapIfNotNull(getMap(productInfo, "otherinfo"), content, new String[]{"ordertosignupdate", "takeofftoorderdate"});

        if (modules.contains("paymentinfolist")) {
            blackWhiteConverterEx.fillPaymentInfoToMap(paymentInfo, content);
        }

        copyMapIfNotNull(getMap(productInfo, "contactinfo"), content, new String[]{
                    "contactemail", "contactfax","contactname", "contacttel",
                    "mobilephone", "telcall", "sendticketaddr", "forignmobilephone"});


        copyMapIfNotNull(getMap(productInfo, "userinfo"), content, new String[]{"istempuser", "uid", "userpassword", "totalpenalty"});

        copyMapIfNotNull(getMap(productInfo, "maininfo"), content, new String[]{"isonline", "serverfrom", "clientid"});

        if (modules.contains("hotelgroupinfolist")) {
            List<Map<String,Object>> hotelGroups = getList(productInfo,"hotelgroupinfolist");
            if(hotelGroups!=null && hotelGroups.size()>0) {
                for(Map<String,Object> hotelGroup:hotelGroups) {
                    if("T".equals(getString(hotelGroup,"ismainproduct"))) {
                        copyMapIfNotNull(hotelGroup,content, ImmutableMap.of("productid", "productid", "productname", "productnamed", "city", "city"));
                        break;
                    }
                }
            }
        }

        if (modules.contains("flightinfolist")) {
            blackWhiteConverterEx.fillFlightInfoList(productInfo, content);
        }

        if(modules.contains("railinfolist")){
            blackWhiteConverterEx.fillRailInfoList(productInfo,content);
        }

        if(modules.contains("vacationinfolist")){
            blackWhiteConverterEx.fillVacationInfoList(productInfo,content);
        }

        setValue(content, "deviceid", "");               //	DeviceID
        setValue(content, "fuzzydeviceid", "");         //	FuzzyID
        setValue(content, "trueip", "");                 //	真实IP地址
        setValue(content, "trueipgeo", "");              //	真实IP国籍
        setValue(content, "proxyip", "");                //	代理IP地址
        setValue(content, "proxyipgeo", "");             //	代理IP国籍

        copyValueIfNotNull(getMap(productInfo,"didinfo"),"did",content,"did");

        return fact;
    }

    public BWFact convert(RequestBody requestBody) {
        BWFact fact = new BWFact();
        Map<String, Object> content = new HashMap<String, Object>();
        Map<String, Object> eventBody= requestBody.getEventBody();
        fact.setContent(content);
        fact.setOrderType(Integer.parseInt(getString(eventBody,"ordertype","0")));
        fact.setOrderTypes(new ArrayList<Integer>());
        fact.getOrderTypes().add(0);
        fact.getOrderTypes().add(fact.getOrderType());
        Map<String,Object> bwlist=getMap(eventBody,"blacklist");
        if(bwlist!=null && bwlist.size()>0) {
            content.putAll(bwlist);
        }
        return fact;
    }

}
