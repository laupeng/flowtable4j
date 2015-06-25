package com.ctrip.infosec.flowtable4j.v2m.converter;

import com.ctrip.infosec.flowtable4j.model.BWFact;
import com.ctrip.infosec.flowtable4j.model.MapX;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 转换PO为黑白名单，支付风控黑白名单
 * Created by thyang on 2015-06-10.
 */
@Component
public class BlackWhiteConverter extends ConverterBase {

    public BWFact convert(PO po) {

        BWFact fact = new BWFact();
        Map<String, Object> content = new HashMap<String, Object>();
        fact.setContent(content);
        fact.setOrderType(po.getOrdertype());
        fact.setOrderTypes(new ArrayList<Integer>());
        fact.getOrderTypes().add(0);
        fact.getOrderTypes().add(po.getOrdertype());

        copyMap(po.getProductinfo(), "ipinfo", content, ImmutableMap.of("ipcity", "ipcity", "ipcountry", "ipcountry", "useripadd", "userip"));

        copyMap(po.getProductinfo(), "otherinfo", content, new String[]{"ordertosignupdate"});

        fillPaymentInfoToMap(po,content);

        copyMap(po.getProductinfo(), "contactinfo", content, new String[]{"contactemail", "contactfax", "contactname", "contacttel",
                "mobilephone", "telcall", "sendticketaddr", "forignmobilephone"});

        copyMap(po.getProductinfo(), "userinfo", content, new String[]{"istempuser","uid","userpassword","totalpenalty"});

        copyMap(po.getProductinfo(), "maininfo", content, new String[]{"isonline","serverfrom","clientid"});

        copyMap(po.getProductinfo(), "hotelgroupinfo", content, ImmutableMap.of("productid", "productid", "productname", "productnamed", "city", "city"));

        setValue(content,"deviceid", "");		        //	DeviceID
        setValue(content,"fuzzydeviceid", "");		//	FuzzyID
        setValue(content,"trueip", "");		        //	真实IP地址
        setValue(content,"trueipgeo", "");		    //	真实IP国籍
        setValue(content,"proxyip", "");		        //	代理IP地址
        setValue(content,"proxyipgeo", "");		    //	代理IP国籍

        copyMap(po.getProductinfo(), "deviceid", content, new String[]{"did"});

        return fact;
    }

    protected void fillPaymentInfoToMap(PO po,Map<String, Object> target) {
        List<Map<String, Object>> payInfos = MapX.getList(po.getPaymentinfo(), "paymentinfolist");
        String prepayType = po.getPrepaytype().toUpperCase();

        if (prepayType.equals("CASH")) {
            setValue(target, "prepaytypedetails", "X");
        } else if (prepayType.equals("PAYPL")) {
            setValue(target, "prepaytypedetails", "P");
        } else if (prepayType.equals("DCARD")) {
            setValue(target, "prepaytypedetails", "D");
        }

        if (payInfos != null && payInfos.size() > 0) {
            for (Map<String, Object> payInfo : payInfos) {
                Map<String, Object> payment = (Map<String, Object>) MapX.getMap(payInfo, "payment");
                if (payInfo != null) {
                    prepayType = MapX.getStringEmpty(payInfo, "prepaytype").toUpperCase();
                    if (prepayType.equals("CCARD") || prepayType.equals("DCARD")) {
                        List<Map<String, Object>> cardInfoList = (List<Map<String, Object>>) MapX.getList(payInfo, "cardInfoList");
                        if (cardInfoList != null && cardInfoList.size() > 0) {
                            Map<String, Object> cardInfo0 = cardInfoList.get(0);
                            setValue(target, "bankofcardissue", getString(cardInfo0, "bankofcardissue"));
                            setValue(target, "cardbin", getString(cardInfo0, "cardbin"));
                            setValue(target, "cardbinissue", getString(cardInfo0, "cardbinissue"));
                            setValue(target, "cardholder", getString(cardInfo0, "cardholder"));
                            setValue(target, "ccardnocode", getString(cardInfo0, "ccardnocode"));
                            setValue(target, "cardnorefid", getString(cardInfo0, "cardnorefid"));
                            setValue(target, "nationality", getString(cardInfo0, "nationality"));
                            setValue(target, "nationalityofisuue", getString(cardInfo0, "nationalityofisuue"));
                            setValue(target, "ccardprenocode", getString(cardInfo0, "ccardprenocode"));
                            if (prepayType.equals("CCARD")) {
                                if ("T".equals(getString(cardInfo0, "isforigencard"))) {
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
