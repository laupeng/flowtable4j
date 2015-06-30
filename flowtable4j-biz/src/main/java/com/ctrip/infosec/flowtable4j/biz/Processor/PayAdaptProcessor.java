package com.ctrip.infosec.flowtable4j.biz.processor;

import com.ctrip.infosec.flowtable4j.dal.CheckRiskDAO;
import com.ctrip.infosec.flowtable4j.dal.Counter;
import com.ctrip.infosec.flowtable4j.dal.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.MapX;
import com.ctrip.infosec.sars.util.mapper.JsonMapper;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by zhangsx on 2015/5/6.
 */
@Component
public class PayAdaptProcessor {
    @Autowired
    CheckRiskDAO checkRiskDAO;
    protected static JsonMapper mapper = new JsonMapper();
    private static Logger logger = LoggerFactory.getLogger(Counter.class);

    public boolean setValueIfNotEmpty(Map<String, Object> target, String key, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            return MapX.setValue(target, key, value);
        }
        return false;
    }

    public String getString(Map<String, Object> data, String key) {
        return MapX.getString(data, key);
    }

    public String getString(Map<String, Object> data, String[] key) {
        return MapX.getString(data, key);
    }


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
        setValueIfNotEmpty(target, "ordertype", getString(productInfo, new String[]{"maininfo", "ordertype"}));
        setValueIfNotEmpty(target, "amount", getString(productInfo, new String[]{"maininfo", "amount"}));
        setValueIfNotEmpty(target, "serverfrom", getString(productInfo, new String[]{"maininfo", "serverfrom"}));

        setValueIfNotEmpty(target, "vipgrade", getString(productInfo, new String[]{"userinfo", "vipgrade"}));
        setValueIfNotEmpty(target, "bindedmobilephone", getString(productInfo, new String[]{"userinfo", "bindedmobilephone"}));
        setValueIfNotEmpty(target, "relatedmobilephone", getString(productInfo, new String[]{"userinfo", "relatedmobilephone"}));
        setValueIfNotEmpty(target, "relatedemail", getString(productInfo, new String[]{"userinfo", "relatedemail"}));
        setValueIfNotEmpty(target, "cuscharacter", getString(productInfo, new String[]{"userinfo", "cuscharacter"}));

        setValueIfNotEmpty(target, "mobilephone", getString(productInfo, new String[]{"contactinfo", "mobilephone"}));
        setValueIfNotEmpty(target, "mobilephonecity", getString(productInfo, new String[]{"contactinfo", "mobilephonecity"}));
        setValueIfNotEmpty(target, "contactemail", getString(productInfo, new String[]{"contactinfo", "contactemail"}));

        setValueIfNotEmpty(target, "ordertosignupdate", getString(productInfo, new String[]{"otherinfo", "ordertosignupdate"}));

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
        setValueIfNotEmpty(target, "ordertype", getString(productInfo, new String[]{"maininfo", "ordertype"}));
        setValueIfNotEmpty(target, "amount", getString(productInfo, new String[]{"maininfo", "amount"}));
        setValueIfNotEmpty(target, "serverfrom", getString(productInfo, new String[]{"maininfo", "serverfrom"}));

        setValueIfNotEmpty(target, "vipgrade", getString(productInfo, new String[]{"userinfo", "vipgrade"}));
        setValueIfNotEmpty(target, "bindedmobilephone", getString(productInfo, new String[]{"userinfo", "bindedmobilephone"}));
        setValueIfNotEmpty(target, "relatedmobilephone", getString(productInfo, new String[]{"userinfo", "relatedmobilephone"}));
        setValueIfNotEmpty(target, "relatedemail", getString(productInfo, new String[]{"userinfo", "relatedemail"}));
        setValueIfNotEmpty(target, "bindedemail", getString(productInfo, new String[]{"userinfo", "relatedemail"}));
        setValueIfNotEmpty(target, "cuscharacter", getString(productInfo, new String[]{"userinfo", "cuscharacter"}));

        setValueIfNotEmpty(target, "mobilephone", getString(productInfo, new String[]{"contactinfo", "mobilephone"}));
        setValueIfNotEmpty(target, "mobilephonecity", getString(productInfo, new String[]{"contactinfo", "mobilephonecity"}));
        setValueIfNotEmpty(target, "contactemail", getString(productInfo, new String[]{"contactinfo", "contactemail"}));

        setValueIfNotEmpty(target, "ordertosignupdate", getString(productInfo, new String[]{"otherinfo", "ordertosignupdate"}));
        setValueIfNotEmpty(target, "useripvalue", getString(productInfo, new String[]{"ipinfo", "useripvalue"}));
        setValueIfNotEmpty(target, "province", getString(productInfo, new String[]{"ipinfo", "province"}));
        setValueIfNotEmpty(target, "ipcity", getString(productInfo, new String[]{"ipinfo", "ipcity"}));
        setValueIfNotEmpty(target, "city", getString(productInfo, new String[]{"hotelgroupinfo", "city"}));
    }

    private void fillTTD(Map<String, Object> productInfo, Map<String, Object> target) {
        setValueIfNotEmpty(target, "ordertype", getString(productInfo, new String[]{"maininfo", "ordertype"}));
        setValueIfNotEmpty(target, "amount", getString(productInfo, new String[]{"maininfo", "amount"}));
        setValueIfNotEmpty(target, "serverfrom", getString(productInfo, new String[]{"maininfo", "serverfrom"}));

        setValueIfNotEmpty(target, "vipgrade", getString(productInfo, new String[]{"userinfo", "vipgrade"}));
        setValueIfNotEmpty(target, "bindedmobilephone", getString(productInfo, new String[]{"userinfo", "bindedmobilephone"}));
        setValueIfNotEmpty(target, "relatedmobilephone", getString(productInfo, new String[]{"userinfo", "relatedmobilephone"}));

        setValueIfNotEmpty(target, "cuscharacter", getString(productInfo, new String[]{"userinfo", "cuscharacter"}));
        setValueIfNotEmpty(target, "bindedmobilephonecity", getString(productInfo, new String[]{"userinfo", "bindedmobilephonecity"}));
        setValueIfNotEmpty(target, "relatedmobilephonecity", getString(productInfo, new String[]{"userinfo", "relatedmobilephonecity"}));

        setValueIfNotEmpty(target, "mobilephone", getString(productInfo, new String[]{"contactinfo", "mobilephone"}));
        setValueIfNotEmpty(target, "mobilephonecity", getString(productInfo, new String[]{"contactinfo", "mobilephonecity"}));
        setValueIfNotEmpty(target, "ordertosignupdate", getString(productInfo, new String[]{"otherinfo", "ordertosignupdate"}));

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

        setValueIfNotEmpty(target, "ordertype", getString(productInfo, new String[]{"maininfo", "ordertype"}));
        setValueIfNotEmpty(target, "amount", getString(productInfo, new String[]{"maininfo", "amount"}));
        setValueIfNotEmpty(target, "serverfrom", getString(productInfo, new String[]{"maininfo", "serverfrom"}));

        setValueIfNotEmpty(target, "vipgrade", getString(productInfo, new String[]{"userinfo", "vipgrade"}));
        setValueIfNotEmpty(target, "bindedmobilephone", getString(productInfo, new String[]{"userinfo", "bindedmobilephone"}));
        setValueIfNotEmpty(target, "relatedmobilephone", getString(productInfo, new String[]{"userinfo", "relatedmobilephone"}));
        setValueIfNotEmpty(target, "relatedemail", getString(productInfo, new String[]{"userinfo", "relatedemail"}));
        setValueIfNotEmpty(target, "bindedemail", getString(productInfo, new String[]{"userinfo", "bindedemail"}));
        setValueIfNotEmpty(target, "cuscharacter", getString(productInfo, new String[]{"userinfo", "cuscharacter"}));
        //需要特别处理

        setValueIfNotEmpty(target, "sendtickeraddr", getString(productInfo, new String[]{"contactinfo", "sendtickeraddr"}));
        setValueIfNotEmpty(target, "contactemail", getString(productInfo, new String[]{"contactinfo", "contactemail"}));
        setValueIfNotEmpty(target, "mobilephone", getString(productInfo, new String[]{"contactinfo", "mobilephone"}));
        setValueIfNotEmpty(target, "mobilephonecity", getString(productInfo, new String[]{"contactinfo", "mobilephonecity"}));
        setValueIfNotEmpty(target, "mobilephoneprovince", getString(productInfo, new String[]{"contactinfo", "mobilephoneprovince"}));

        setValueIfNotEmpty(target, "ordertosignupdate", getString(productInfo, new String[]{"otherinfo", "ordertosignupdate"}));
        setValueIfNotEmpty(target, "takeofftoorderdate", getString(productInfo, new String[]{"otherinfo", "takeofftoorderdate"}));

        setValueIfNotEmpty(target, "useripvalue", getString(productInfo, new String[]{"ipinfo", "useripvalue"}));
        setValueIfNotEmpty(target, "province", getString(productInfo, new String[]{"ipinfo", "province"}));
        setValueIfNotEmpty(target, "ipcity", getString(productInfo, new String[]{"ipinfo", "ipcity"}));

        setValueIfNotEmpty(target, "continent", getString(productInfo, new String[]{"ipinfo", "continent"}));

        List<Map<String,Object>> flightsinfoList = MapX.getList(productInfo,"flightinfolist");
        if(flightsinfoList!=null && flightsinfoList.size()>0) {
            Map<String,Object> flight= MapX.getMap(flightsinfoList.get(0),"order");
            setValueIfNotEmpty(target, "dcity", getString(flight, "dcityname"));
            setValueIfNotEmpty(target, "dcityprovince", getString(flight, "dcityprovince"));
            setValueIfNotEmpty(target, "acity", getString(flight,"acityname"));
            setValueIfNotEmpty(target, "acityprovince", getString(flight,"acityprovince"));
            setValueIfNotEmpty(target, "flightclass", getString(flight, "flightclass"));
        }
    }

    public Map<String, Object> fillPayAdaptCheckEntity(Map<String, Object> productInfo,int orderType) {
        Map<String, Object> payAdaptEntity = new HashMap<String, Object>();
        if(CtripOrderType.CRH.getCode() == orderType){
            fillCRH(productInfo,payAdaptEntity);
        } else if(CtripOrderType.TTD.getCode()==orderType){
            fillTTD(productInfo, payAdaptEntity);
        } else if(CtripOrderType.Flights.getCode() == orderType){
            fillFlight(productInfo,payAdaptEntity);
        } else if (CtripOrderType.HotelGroup.getCode()== orderType){
            fillHotelGroup(productInfo,payAdaptEntity);
        }
        return payAdaptEntity;
    }
}
