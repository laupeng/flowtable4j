package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.baseconverter.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.MapX;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by zhangsx on 2015/5/6.
 */
@Component
public class PayAdaptConverter extends ConverterBase {

    public static boolean isPayAdapatFlowRuleOpen() {
        return true;
    }

    /**
     * 获取支付适配黑白名单校验实体
     *
     * @param orderType
     * @param orderId
     * @return
     */

    public Map<String, Object> getLastProductInfo(long orderId,int orderType) {
        Map<String, Object> tmpProduct = checkRiskDAO.getLastProductInfo(String.valueOf(orderId), String.valueOf(orderType), "");
        if (tmpProduct != null) {
            return mapper.fromJson(MapX.getString(tmpProduct, "content"), HashMap.class);
        }
        return null;
    }

    public Map<String, Object> fillBWGCheckEntity(Map<String, Object> productInfo) {
        Map<String, Object> bwEntity = new HashMap<String, Object>();
        setValueIfNotEmpty(bwEntity, "contactemail", getString(productInfo, new String[]{"contactinfo", "contactemail"}));
        setValueIfNotEmpty(bwEntity, "mobilephone", getString(productInfo, new String[]{"contactinfo", "mobilephone"}));
        setValueIfNotEmpty(bwEntity, "did", getString(productInfo, new String[]{"didinfo", "did"}));
        setValueIfNotEmpty(bwEntity, "uid", getString(productInfo, new String[]{"userinfo", "uid"}));
        return bwEntity;
    }

    private void fillCRH(Map<String, Object> productInfo, Map<String, Object> target) {
        copyMapIfNotNull(getMap(productInfo,"maininfo"),target,new String[]{"ordertype","amount","serverfrom"});
        copyMapIfNotNull(getMap(productInfo,"userinfo"),target,
                         new String[]{"vipgrade","bindedmobilephone","relatedmobilephone","relatedemail","cuscharacter"});
        copyMapIfNotNull(getMap(productInfo,"contactinfo"),target,
                new String[]{"mobilephone","mobilephonecity","contactemail"});
        copyValueIfNotNull(getMap(productInfo, "otherinfo"),"ordertosignupdate",target,"ordertosignupdate");
        List<Map<String,Object>> railInfoList = MapX.getList(productInfo,"railinfolist");
        if(railInfoList!=null && railInfoList.size()>0) {
            Map<String,Object> railInfo = MapX.getMap(railInfoList.get(0), "rail");
            if(railInfo!=null && railInfo.size()>0) {
                setValueIfNotEmpty(target, "acity", getString(railInfo,"acity"));
                setValueIfNotEmpty(target, "dcity", getString(railInfo,"dcity"));
            }
        }
    }

    private void fillHotelGroup(Map<String, Object> productInfo, Map<String, Object> target) {
        copyMapIfNotNull(getMap(productInfo, "maininfo"), target, new String[]{"ordertype", "amount", "serverfrom"});

        copyMapIfNotNull(getMap(productInfo,"userinfo"),target,
                new String[]{"vipgrade","bindedmobilephone","relatedmobilephone","relatedemail","cuscharacter","bindedemail"});

        copyMapIfNotNull(getMap(productInfo, "contactinfo"), target,
                new String[]{"mobilephone", "mobilephonecity", "contactemail"});

        copyValueIfNotNull(getMap(productInfo, "otherinfo"),"ordertosignupdate",target,"ordertosignupdate");

        copyValueIfNotNull(getMap(productInfo, "ipinfo"),"useripvalue",target,"useripvalue");

        List<Map<String,Object>> hotelGroups = getList(productInfo,"hotelgroupinfolist");
        if(hotelGroups!=null && hotelGroups.size()>0) {
            for(Map<String,Object> hotelGroup:hotelGroups) {
                if("T".equals(getString(hotelGroup,"ismainproduct"))) {
                    copyValueIfNotNull(hotelGroup, "city", target, "city");
                    break;
                }
            }
        }
    }

    private void fillTTD(Map<String, Object> productInfo, Map<String, Object> target) {
        copyMapIfNotNull(getMap(productInfo, "maininfo"), target, new String[]{"ordertype", "amount", "serverfrom"});
        //POConvertBase fillMobileProvince
        copyMapIfNotNull(getMap(productInfo,"userinfo"),target,
                new String[]{"vipgrade","bindedmobilephone","relatedmobilephone","cuscharacter","bindedmobilephonecity","relatedmobilephonecity"});

        copyMapIfNotNull(getMap(productInfo, "contactinfo"), target,
                new String[]{"mobilephone", "mobilephonecity"});

        copyValueIfNotNull(getMap(productInfo, "otherinfo"), "ordertosignupdate", target, "ordertosignupdate");

        List<Map<String,Object>> vacationinfoList = MapX.getList(productInfo,"vacationinfolist");

        if(vacationinfoList!=null && vacationinfoList.size()>0) {
            Map<String,Object> vac= vacationinfoList.get(0);
            setValueIfNotEmpty(target,"productname",getString(vac,new String[]{"order","productname"}));
            StringBuilder sb = new StringBuilder("|");
            List<Map<String, Object>> vactionOptions = MapX.getList(vac, "optionlist");
            for (Map<String, Object> va : vactionOptions) {
                sb.append(getString(va, "optionname")).append("|");
            }
            setValueIfNotEmpty(target,"vacationoptionname", sb.toString());
        }
    }

    private void fillFlight(Map<String, Object> productInfo, Map<String, Object> target) {

        copyMapIfNotNull(getMap(productInfo, "maininfo"), target, new String[]{"ordertype", "amount", "serverfrom"});

        copyMapIfNotNull(getMap(productInfo,"userinfo"),target,
                new String[]{"vipgrade","bindedmobilephone","relatedmobilephone","relatedemail","cuscharacter","bindedemail"});

        copyMapIfNotNull(getMap(productInfo, "contactinfo"), target,
                new String[]{"mobilephone", "mobilephonecity","mobilephoneprovince","contactemail","sendtickeraddr"});

        copyMapIfNotNull(getMap(productInfo, "otherinfo"), target,
                new String[]{"ordertosignupdate", "takeofftoorderdate"});

        copyMapIfNotNull(getMap(productInfo, "ipinfo"), target,
                new String[]{"useripvalue", "continent"});

        List<Map<String,Object>> flightsinfoList = MapX.getList(productInfo,"flightinfolist");
        if(flightsinfoList!=null && flightsinfoList.size()>0) {
            Map<String,Object> flight= MapX.getMap(flightsinfoList.get(0),"order");

            //POConvertEx.fillADCityNameProvince已经处理
            setValueIfNotEmpty(target, "dcity", getString(flight, "dcityname"));
            setValueIfNotEmpty(target, "dcityprovince", getString(flight, "dcityprovince"));
            setValueIfNotEmpty(target, "acity", getString(flight,"acityname"));
            setValueIfNotEmpty(target, "acityprovince", getString(flight,"acityprovince"));
            setValueIfNotEmpty(target, "flightclass", getString(flight, "flightclass"));
        }
    }

    private void fillIPCityProvince(Map<String,Object> target){
        String ip = getString(target,"useripvalue");
        if(StringUtils.isNumeric(ip)){
            Map<String,Object> ipInfo = checkRiskDAO.getIPInfo(Long.parseLong(ip));
            copyMapIfNotNull(ipInfo,target,ImmutableMap.of("province","province","city","ipcity"));
        }
    }

    public Map<String, Object> fillPayAdaptCheckEntity(Map<String, Object> productInfo,int orderType) {
        Map<String, Object> payAdaptEntity = new HashMap<String, Object>();
        if(productInfo!=null && productInfo.size()>0) {
            if (CtripOrderType.CRH.getCode() == orderType) {
                fillCRH(productInfo, payAdaptEntity);
            } else if (CtripOrderType.TTD.getCode() == orderType) {
                fillTTD(productInfo, payAdaptEntity);
            } else if (CtripOrderType.Flights.getCode() == orderType) {
                fillFlight(productInfo, payAdaptEntity);
            } else if (CtripOrderType.HotelGroup.getCode() == orderType) {
                fillHotelGroup(productInfo, payAdaptEntity);
            }
        }
        fillIPCityProvince(payAdaptEntity);
        return payAdaptEntity;
    }
}
