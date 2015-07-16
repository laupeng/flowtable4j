package com.ctrip.infosec.flowtable4j.biz.subprocessor;

import com.ctrip.infosec.flowtable4j.biz.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by thyang on 2015-07-06.
 */
@Component
public class POConverterEx extends ConverterBase {
    /**
     * 填充VactionInfoList
     *
     * @param productInfo
     * @param eventBody
     */
    public void fillVacationInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody,int orderType) {
        List<Map<String, Object>> vacationinfolist = new ArrayList<Map<String, Object>>();
        Map<String, Object> order = new HashMap<String, Object>();
        copyMap(eventBody, order, "infosecurity_vacationinfo");
        List<Map<String, Object>> userlist = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> userInfolistMap = getList(eventBody, "userinfos");

        if (userInfolistMap != null && userInfolistMap.size() > 0) {
            for (Map<String, Object> userMap : userInfolistMap) {
                Map<String, Object> vu = new HashMap<String, Object>();
                copyMapIfNotNull(userMap, vu, new String[]{"visitorname", "visitorcontactinfo", "visitornationality","visitoridcardtype"});
                if(CtripOrderType.HotelGroup.getCode()==orderType){
                    copyValue(userMap,"idtype",vu,"visitoridcardtype");
                }
                userlist.add(vu);
            }
        }
        List<Map<String,Object>> optionListMap = getList(eventBody,"optionitems");
        List<Map<String, Object>> optlist = new ArrayList<Map<String, Object>>();
        if(optionListMap!=null && optionListMap.size()>0){
            for(Map<String,Object> optMap:optionListMap){
                Map<String,Object> opt = new HashMap<String, Object>();
                copyMap(optMap,opt,"infosecurity_vacationoptioninfo");
                optlist.add(opt);
            }
        }
        vacationinfolist.add(ImmutableMap.of("order", order, "userlist", userlist,"optionlist",optlist));

        setValue(productInfo, "vacationinfolist", vacationinfolist);
    }

    /**
     * 填充FlightInfoList
     *
     * @param productInfo
     * @param eventBody
     */
    public void fillFlightInfoList(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> flightinfolist = new ArrayList<Map<String, Object>>();
        Map<String, Object> flightinfo = new HashMap<String, Object>();
        Map<String, Object> order = new HashMap<String, Object>();
        copyMap(eventBody, order, "infosecurity_flightsorderinfo");
        setValue(flightinfo, "order", order);

        List<Map<String, Object>> passengerList = new ArrayList<Map<String, Object>>();
        setValue(flightinfo, "passengerlist", passengerList);

        List<Map<String, Object>> passengerListMap = getList(eventBody, "passengerinfolist");
        if (passengerListMap != null && passengerListMap.size() > 0) {
            for (Map<String, Object> pMap : passengerListMap) {
                Map<String, Object> p = new HashMap<String, Object>();
                copyMap(pMap, p, "infosecurity_passengerinfo");
                copyValue(pMap,"passengercardno",p,"passengercardid");
                passengerList.add(p);
            }
            String amt = getString(productInfo, new String[]{"maininfo", "amount"});
            if (StringUtils.isNumeric(amt)) {
                setValue(order, "leafletamount", Double.parseDouble(amt) / passengerListMap.size());
            }
        }

        List<Map<String, Object>> segmentList = new ArrayList<Map<String, Object>>();
        setValue(flightinfo, "segmentlist", segmentList);
        List<Map<String, Object>> segmentListMap = getList(eventBody, "segmentinfolist");
        if (segmentListMap != null && segmentListMap.size() > 0) {
            for (Map<String, Object> sMap : segmentListMap) {
                Map<String, Object> s = new HashMap<String, Object>();
                copyMap(sMap, s, "infosecurity_segmentinfo");
                setValue(sMap, "acity", checkRiskDAO.getCityCode(getString(s, "aairport")));
                setValue(sMap, "dcity", checkRiskDAO.getCityCode(getString(s, "dairport")));
                segmentList.add(s);
            }
        }
        setValue(productInfo, "flightinfolist", flightinfolist);
    }

    /**
     * 机票记录 Auth_CCARD_Order
     * @param product
     * @param paymentInfo
     */
    public void fillAuthCCardInfo(Map<String,Object> product,Map<String,Object> paymentInfo)
    {
        Map<String,Object> auth_ccard = new HashMap<String, Object>();
        setValue(auth_ccard,"orderid",getString(product,new String[]{"maininfo","orderid"}));
        setValue(auth_ccard,"ordertype",getString(product,new String[]{"maininfo","ordertype"}));
        setValue(auth_ccard,"createdate",getString(product,new String[]{"maininfo","createdate"}));
        setValue(auth_ccard,"authstatus", 0);
        List<Map<String, Object>> paymentInfoList = getList(paymentInfo, "paymentinfolist");
        if(paymentInfoList!=null && paymentInfoList.size()>0){
            for(Map<String,Object> pay:paymentInfoList){
                String prepayType= getString(pay,new String[]{"payment","prepaytype"});
                if("CCARD".equals(prepayType)||"DCARD".equals(prepayType)||"DQPAY".equals(prepayType)){
                    List<Map<String,Object>> cards = getList(pay, "cardinfolist");
                    if(cards!=null && cards.size()>0){
                        copyValue(cards.get(0),"isforeigncard",auth_ccard,"isforeigncard");
                        break;
                    }
                }
            }
        }
        setValue(product,"orderccard",auth_ccard);
    }

    /**
     * 计算机票负利润
     * @param productInfo
     * @param paymentInfo
     */
    public void fillFligtProfit(Map<String, Object> productInfo, Map<String, Object> paymentInfo) {
        List<Map<String, Object>> paymentInfoList =  getList(paymentInfo, "paymentinfolist");
        List<Map<String, Object>> flightInfoList =  getList(productInfo, "flightinfolist");
        Map<String,Object> maininfo = getMap(productInfo, "maininfo");
        if (paymentInfoList != null && paymentInfoList.size() > 0) {
            for (Map<String, Object> p : paymentInfoList) {
                Map<String,Object> payment = getMap(p,"payment");
                if(payment!=null && getString(payment,"prepaytype","").toUpperCase().equals("TMPAY"))
                {
                    double paymentAmount = Double.parseDouble(getString(payment,"amount", "0"));
                    if (flightInfoList != null && flightInfoList.size() > 0) {
                        Map<String, Object> flightOrder = getMap(flightInfoList.get(0), "order");
                        if (flightOrder != null && flightOrder.size() > 0) {
                            double flightPrice = Double.parseDouble(getString(flightOrder, "flightprice", "0"));
                            double flightCost = Double.parseDouble(getString(flightOrder, "flightcost", "0"));
                            double packageattachfee = Double.parseDouble(getString(flightOrder, "packageattachfee", "0"));
                            double insurance_fee = Double.parseDouble(getString(flightOrder, "insurance_fee", "0"));
                            if (flightPrice >= flightCost) {
                                setValue(flightOrder, "profit", flightPrice - flightCost + packageattachfee * 0.7 + insurance_fee * 0.9 - paymentAmount * 0.01);
                                setValue(flightOrder, "twopercentprofit", flightPrice - flightCost + packageattachfee * 0.7 + insurance_fee * 0.9 - paymentAmount * 0.02);
                                if(maininfo!=null) {
                                    setValue(flightOrder, "actualamount", Double.parseDouble(Objects.toString(getString(maininfo,"amount"),"0")) + insurance_fee + packageattachfee);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    public void fillRailInfoList(Map<String,Object> productInfo,Map<String,Object> eventBody){
        List<Map<String, Object>> railInfos = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> railInfoListMap = getList(eventBody, "crhorderinfos");
        if (railInfoListMap != null && railInfoListMap.size() > 0) {
            for (Map<String, Object> railMap : railInfoListMap) {
                Map<String, Object> rail = new HashMap<String, Object>();
                Map<String,Object> user = new HashMap<String, Object>();
                copyMap(railMap,rail,"infosecurity_exrailinfo");
                copyMap(railMap, user,"infosecurity_exrailuserinfo");
                Map<String,Object> railInfo = new HashMap<String, Object>();
                setValue(railInfo,"rail",rail);
                setValue(railInfo,"user",user);
                railInfos.add(railInfo);
            }
        }
        setValue(productInfo, "railinfolist", railInfos);
    }
}
