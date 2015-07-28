package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

                if(CtripOrderType.TTD.getCode()==orderType ||CtripOrderType.CRH.getCode()==orderType||CtripOrderType.HotelGroup.getCode()==orderType){
                    copyValue(userMap,"visitorcardno",vu,"visitoridcode");
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

    private void fillADCityNameProvince(Map<String, Object> target, String acity, String dcity) {
        Map<String,Object> map = checkRiskDAO.getCityNameProvince(acity);
        if(map!=null){
            setValue(target,"acityname",getString(map,"cityname"));
            setValue(target,"acityprovince",getString(map,"provincename"));
        }
        if(!StringUtils.equals(acity,dcity)) {
            map = checkRiskDAO.getCityNameProvince(dcity);
        }
        if(map!=null){
            setValue(target,"dcityname",getString(map,"cityname"));
            setValue(target,"dcityprovince",getString(map,"provincename"));
        }
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
        fillADCityNameProvince(order,getString(order,"acity"),getString(order,"dcity"));
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
        }

        List<Map<String, Object>> segmentList = new ArrayList<Map<String, Object>>();
        setValue(flightinfo, "segmentlist", segmentList);
        List<Map<String, Object>> segmentListMap = getList(eventBody, "segmentinfolist");
        if (segmentListMap != null && segmentListMap.size() > 0) {
            for (Map<String, Object> sMap : segmentListMap) {
                Map<String, Object> s = new HashMap<String, Object>();
                copyMap(sMap, s, "infosecurity_segmentinfo");
                setValue(s, "acity", checkRiskDAO.getCityCode(getString(s, "aairport")));
                setValue(s, "dcity", checkRiskDAO.getCityCode(getString(s, "dairport")));
                segmentList.add(s);
            }
        }
        flightinfolist.add(flightinfo);
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
    public void fillFligtProfit(Map<String, Object> productInfo, Map<String, Object> paymentInfo,int checkType) {
        //CheckType!=0才计算利润
        if(checkType==0) {
            return;
        }
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

    public void fillHotelGroupInfoList(Map<String,Object> productInfo,Map<String,Object> eventBody){
        List<Map<String, Object>> hotelGroupInfos = new ArrayList<Map<String, Object>>();
        //List报文
        List<Map<String, Object>> hoteGroupInfoListMap = getList(eventBody, "groupinfos");
        if (hoteGroupInfoListMap != null && hoteGroupInfoListMap.size() > 0) {
            for (Map<String, Object> hotelGroupMap : hoteGroupInfoListMap) {
                Map<String,Object> hotelgroupInfo = new HashMap<String, Object>();
                copyMap(hotelGroupMap,hotelgroupInfo,"infosecurity_hotelgroupinfo");
                hotelGroupInfos.add(hotelgroupInfo);
            }
        } else {
            //主报文
            Map<String, Object> hotelGroup = new HashMap<String, Object>();
            copyMap(eventBody, hotelGroup, "infosecurity_hotelgroupinfo");
            setValue(hotelGroup,"ismainproduct","T");
            hotelGroupInfos.add(hotelGroup);
        }
        setValue(productInfo, "hotelgroupinfolist", hotelGroupInfos);
    }


    /**
     * TDD 特殊处理
     * @param po
     * @param eventBody
     */
    public void flightSpecial(PO po, Map<String, Object> eventBody) {

        Map<String,Object> productInfo = po.getProductinfo();
        Map<String,Object> paymentInfo = po.getPaymentinfo();

        //RefNo, CorporationId 特殊处理

        Map<String,Object> maininfo = getMap(productInfo,"maininfo");
        if(maininfo!=null){
            String refNo = getString(eventBody,"referenceno");
            if(StringUtils.isNumeric(refNo)){
                setValue(maininfo,"refno",refNo);
            }
            copyMapIfNotNull(eventBody,maininfo,new String[]{"corporationid"});
        }

        //CheckType=0, PaymentMainInfo取上次，FlightOrderInfo取上次
        if(po.getChecktype()==0){
            Map<String, Object> tmpPay = checkRiskDAO.getLastPaymentInfo(getString(eventBody, "orderid"), getString(eventBody, "ordertype"), getString(eventBody, "merchantorderid"));
            if (tmpPay != null) {
                Map<String, Object> paymentInfoOld = mapper.fromJson(getString(tmpPay, "content"), HashMap.class);
                if(paymentInfoOld!=null){
                    setValue(paymentInfo,"paymentmaininfo",getMap(paymentInfoOld,"paymentmaininfo"));
                }
            }
            Map<String, Object> tmpProduct = checkRiskDAO.getLastProductInfo(getString(eventBody, "orderid"), getString(eventBody, "ordertype"), getString(eventBody, "merchantorderid"));
            if (tmpProduct != null) {
                Map<String, Object> productInfoOld = mapper.fromJson(getString(tmpProduct, "content"), HashMap.class);
                if(productInfoOld!=null){
                    List<Map<String,Object>> flightinfolistOld = getList(productInfoOld,"flightinfolist");
                    Map<String,Object> orderOld=null;
                    if(flightinfolistOld!=null && flightinfolistOld.size()>0){
                        orderOld = getMap(flightinfolistOld.get(0),"order");
                    }
                    List<Map<String,Object>> flightsCurrent = getList(productInfo,"flightinfolist");
                    if(flightsCurrent!=null && flightsCurrent.size()>0 && orderOld!=null){
                        setValue(flightsCurrent.get(0),"order",orderOld);
                    }
                }
            }
        }
        List<Map<String, Object>> flightinfolist = getList(productInfo,"flightinfolist");
        if (flightinfolist != null && flightinfolist.size() > 0) {
            Map<String,Object> order = getMap(flightinfolist.get(0),"order");
            setValue(getMap(productInfo,"otherinfo"), "takeofftoorderdate",
                     dateDiffHour(getString(order,"takeofftime"),getString(productInfo,new String[]{"maininfo","orderdate"})));
            String amt = getString(productInfo, new String[]{"maininfo", "amount"});
            List<Map<String,Object>> passengers = getList(flightinfolist.get(0),"passengerlist");
            if (NumberUtils.isNumber(amt) && passengers!=null && passengers.size()>0) {
                setValue(order, "leafletamount", Double.parseDouble(amt) / passengers.size());
            }
        }
    }

    /**
     * TDD 特殊处理
     * @param po
     * @param eventBody
     */
    public void tddSpecial(PO po, Map<String, Object> eventBody) {

        Map<String,Object> maininfo = getMap(po.getProductinfo(),"maininfo");
        if(maininfo!=null){
            String refNo = getString(eventBody,"referenceno");
            if(StringUtils.isNumeric(refNo)){
                setValue(maininfo,"refno",refNo);
            }
            setValue(maininfo, "amount", getObject(eventBody, "orderamount"));
        }
        // CheckType =1时，带有是否预付 IsPrepaID
        // CheckType =2时，支付时带有其它信息，从上次取 IsPrepaID
        Map<String,Object> paymentMainInfo = getMap(po.getPaymentinfo(),"paymentmaininfo");
        if(paymentMainInfo==null){
            paymentMainInfo = new HashMap<String, Object>();
            setValue(po.getPaymentinfo(),"paymentmaininfo",paymentMainInfo);
        }
        if(po.getChecktype()==1){
           copyValueIfNotNull(eventBody,"isprepaid",paymentMainInfo,"isprepaid"); //取当前isprepaid
        } else if(po.getChecktype()==2) {
            Map<String, Object> tmpPay = checkRiskDAO.getLastPaymentInfo(getString(eventBody, "orderid"), getString(eventBody, "ordertype"), getString(eventBody, "merchantorderid"));
            if (tmpPay != null) {
                Map<String, Object> paymentInfo = mapper.fromJson(getString(tmpPay, "content"), HashMap.class);
                copyValueIfNotNull(getMap(paymentInfo, "paymentmaininfo"), "isprepaid", paymentMainInfo, "isprepaid"); //取上次的 isprepaid
            }
        }
    }

    /**
     * @param po
     * @param eventBody
     */
    public void hotelGroupSpecial(PO po, Map<String, Object> eventBody) {

        Map<String,Object> maininfo = getMap(po.getProductinfo(),"maininfo");
        if(maininfo!=null){
            setValue(maininfo, "amount", getObject(eventBody, "orderamount"));
        }
    }

    /**
     * @param po
     * @param eventBody
     */
    public void crhSpecial(PO po, Map<String, Object> eventBody) {

        Map<String,Object> maininfo = getMap(po.getProductinfo(),"maininfo");
        if(maininfo!=null){
            setValue(maininfo, "corporationid", getObject(eventBody, "corporationid"));
        }
    }
}
