package com.ctrip.infosec.flowtable4j.biz.processor;

import com.ctrip.infosec.flowtable4j.biz.ConverterBase;
import com.ctrip.infosec.flowtable4j.biz.subprocessor.BlackWhiteConverterEx;
import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.MapX;
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
            return Arrays.asList(new String[]{"hotelgroupinfo"});
        }
        if (CtripOrderType.Flights.getCode() == orderType) {
            return Arrays.asList(new String[]{"flightinfolist"});
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

        copyMapIfNotNull(getMap(productInfo, "ipinfo"), content, ImmutableMap.of("ipcity", "ipcity", "ipcountry", "ipcountry", "useripadd", "userip"));

        copyMapIfNotNull(getMap(productInfo, "otherinfo"), content, new String[]{"ordertosignupdate", "takeofftoorderdate"});

        if (modules.contains("paymentinfolist")) {
            fillPaymentInfoToMap(paymentInfo, content);
        }

        copyMapIfNotNull(getMap(productInfo, "contactinfo"), content, new String[]{
                    "contactemail", "contactfax","contactname", "contacttel",
                    "mobilephone", "telcall", "sendticketaddr", "forignmobilephone"});


        copyMapIfNotNull(getMap(productInfo, "userinfo"), content, new String[]{"istempuser", "uid", "userpassword", "totalpenalty"});

        copyMapIfNotNull(getMap(productInfo, "maininfo"), content, new String[]{"isonline", "serverfrom", "clientid"});

        if (modules.contains("hotelgroupinfo")) {
            copyMapIfNotNull(getMap(productInfo, "hotelgroupinfo"), content, ImmutableMap.of("productid", "productid", "productname", "productnamed", "city", "city"));
        }

        if (modules.contains("flightinfolist")) {
            blackWhiteConverterEx.fillFlightInfo(productInfo, content);
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


    /**
     * 实际应用中，paymentinfolist 只有一条记录
     * 同样 cardInfoList 也只有一条记录
     *
     * @param paymentInfo
     * @param target
     */
    protected void fillPaymentInfoToMap(Map<String, Object> paymentInfo, Map<String, Object> target) {
        List<Map<String, Object>> paymentinfolist = getList(paymentInfo, "paymentinfolist");
        if (paymentinfolist != null && paymentinfolist.size() > 0) {
            for (Map<String, Object> paymentItem : paymentinfolist) {
                Map<String, Object> payment = getMap(paymentItem, "payment");
                if (payment != null) {
                    String prepayType = MapX.getStringEmpty(payment, "prepaytype").toUpperCase();
                    if ("CASH".equals(prepayType)) {
                        setValue(target, "prepaytypedetails", "X");
                    } else if ("PAYPL".equals(prepayType)) {
                        setValue(target, "prepaytypedetails", "P");
                    } else if ("DCARD".equals(prepayType)) {
                        setValue(target, "prepaytypedetails", "D");
                    } else if ("DQPAY".equals(prepayType)) {
                        setValue(target, "prepaytypedetails", "D");
                    }

                    if (prepayType.equals("CCARD") || prepayType.equals("DCARD") || prepayType.equals("DQPAY")) {
                        List<Map<String, Object>> cardInfoList = getList(paymentItem, "cardinfolist");
                        if (cardInfoList != null && cardInfoList.size() > 0) {
                            Map<String, Object> cardInfo0 = cardInfoList.get(0);
                            copyMapIfNotNull(cardInfo0, target, new String[]{
                                    "bankofcardissue", "cardbin", "cardbinissue", "cardholder",
                                    "ccardnocode", "cardnorefid", "nationality", "nationalityofisuue",
                                    "ccardprenocode"
                            });

                            if (prepayType.equals("CCARD")) {
                                if ("T".equals(getString(cardInfo0, "isforigencard", "").toUpperCase())) {
                                    setValue(target, "prepaytypedetails", "W");
                                } else {
                                    setValue(target, "prepaytypedetails", "N");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
