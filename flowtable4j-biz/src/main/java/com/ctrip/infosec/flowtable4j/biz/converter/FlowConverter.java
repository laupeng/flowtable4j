package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.ConverterBase;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.FlowFact;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by thyang on 2015-06-10.
 */
@Component
public class FlowConverter extends ConverterBase {
    @Autowired
    FlowConverterEx flowConverterEx;

    public List<String> getModule(int orderType) {
        if (CtripOrderType.HotelGroup.getCode() == orderType) {
            return Arrays.asList(new String[]{"hotelgroupinfolist"});
        }
        if (CtripOrderType.Flights.getCode() == orderType) {
            return Arrays.asList(new String[]{"flightinfolist","fillflightprofit","flightinfo"});
        }
        if (CtripOrderType.CRH.getCode() == orderType) {
            return Arrays.asList(new String[]{"railinfolist"});
        }
        return new ArrayList<String>();
    }

    public FlowFact convert(PO po){
        FlowFact fact = new FlowFact();
        Map<String, Object> target = new HashMap<String, Object>();
        fact.setContent(target);
        fact.setOrderType(po.getOrdertype());
        fact.setOrderTypes(new ArrayList<Integer>());
        fact.getOrderTypes().add(0);
        fact.getOrderTypes().add(po.getOrdertype());

        Map<String,Object> productInfo = po.getProductinfo();

        List<String> modules= getModule(po.getOrdertype());

        //MainInfo
        copyMapIfNotNull(getMap(productInfo, "maininfo"),target,
                    new String[]{"clientid", "ordertype", "subordertype", "wirelessclientno",
                                  "orderid", "amount", "checktype", "serverfrom", "orderdate"});
        //OrderDate Hour, MergerOrderDate
        processOrderDate(target);

        setValue(target,"checktype",po.getChecktype());

        //UserInfo
        copyMapIfNotNull(getMap(productInfo, "userinfo"), target,
                new String[]{"cuscharacter", "bindedmobilephone", "userpassword", "experience",
                "bindedemail", "vipgrade", "relatedemail", "relatedmobilephone", "uid", "bindedmobilephonecity",
                "bindedmobilephoneprovince", "relatedmobilephonecity", "relatedmobilephoneprovince"});

        //ContactInfo
        copyMapIfNotNull(getMap(productInfo, "contactinfo"), target, new String[]{"mobilephone", "mobilephonecity", "contactemail", "mobilephoneprovince",
                "contactname", "contacttel", "forignmobilephone", "telcall", "sendtickeraddr", "postaddress"});


        String mobile = getString(target, "mobilephone", "");
        if(mobile.length()>7){
            setValue(target, "mobilephone7",mobile.substring(0,7)); //衍生字段中大量用到该字段，因此预先加入
        }
        //手机号含4的个数
        setValue(target, "mobilephone4count", StringUtils.countMatches(mobile, "4"));

        //IPInfo,获取IP的城市与Country
        fillIPCity(productInfo,target,po.getChecktype());

        //PaymentInfo，并设置PrepayTypes
        HashSet<String> prepayTypes= new HashSet<String>();

        fillPaymentInfoToMap(po,target,prepayTypes);

        fact.setPrepayType(new ArrayList<String>(prepayTypes));

        copyMapIfNotNull(getMap(productInfo, "otherinfo"), target, new String[]{"ordertosignupdate","takeofftoorderdate"});

        if(modules.contains("hotelgroupinfolist")) {
            List<Map<String,Object>> hotelGroups = getList(productInfo,"hotelgroupinfolist");
            if(hotelGroups!=null && hotelGroups.size()>0) {
                for(Map<String,Object> hotelGroup:hotelGroups) {
                    if("T".equals(getString(hotelGroup,"ismainproduct"))) {
                        copyMapIfNotNull(hotelGroup, target, new String[]{"quantity", "city", "productid", "productname", "producttype", "price"});
                        break;
                    }
                }
            }
        }

        if(modules.contains("flightinfolist")) {
            flowConverterEx.fillFlightInfoList(productInfo, target);
        }

        if(modules.contains("fillflightprofit")) {
            flowConverterEx.fillFlightProfit(po.getPaymentinfo(),productInfo,target);
        }

        if(modules.contains("railinfolist")){
            flowConverterEx.fillRailInfoList(productInfo,target);
        }

        if(modules.contains("flightinfo")){
            flowConverterEx.fillFlight(target);
        }

        copyMapIfNotNull(getMap(productInfo, "didinfo"), target, new String[]{"did"});

        //衍生字段 CarBin相关，CardBin在Payment处理部分已经设置
        concatKeys(target, "cardbinuid", "cardbin", "uid");                    //userinfo
        concatKeys(target, "cardbinmobilephone", "cardbin", "mobilephone"); //contactinfo

        concatKeys(target, "cardbinuseripadd", "cardbin", "useripadd");     //ipinfo
        concatKeys(target, "cardbinorderid","cardbin","orderid");           //HotelGroup


        //衍生字段 ContactEmail相关
        concatKeys(target, "contactemailcardbin", "contactemail", "cardbin"); //contactinfo
        concatKeys(target, "contactemailmergermobilephone7","contactemail","mobilephone7");

        //衍生字段 UserIPAdd相关
        concatKeys(target, "useripaddmobilenumber", "useripadd","mobilephone7");
        concatKeys(target, "useripaddmergermobilephone7", "useripadd","mobilephone7");
        concatKeys(target, "useripaddcardbin", "useripadd", "cardbin");
        concatKeys(target, "ipcardbin", "useripvalue", "cardbin");

        //衍生字段 UID相关
        concatKeys(target, "uidmobilenumber", "uid","mobilephone7");
        concatKeys(target, "uidmergermobilephone7","uid","mobilephone7");
        concatKeys(target, "uiduseripvalue","uid","useripvalue");

        //衍生字段 MobilePhone相关
        concatKeys(target, "mobilephonecardbin", "mobilephone", "cardbin");
        concatKeys(target, "mobilephonecontactemail","mobilephone","contactemail");
        concatKeys(target, "mobilephoneuid","mobilephone","uid");

        //衍生字段 ccardNoCode
        concatKeys(target,"ccardnocodemobilephone7","ccardnocode","mobilephone7");
        concatKeys(target, "ccardnocodecontactemailuseripvalue", "ccardnocode", "contactemail", "useripvalue");

        //衍生字段 CCardPreNoCode
        concatKeys(target,"ccardprenocodemobilephone","ccardprenocode","mobilephone");
        concatKeys(target,"ccardprenocodecontactemail","ccardprenocode","contactemail");
        concatKeys(target,"ccardprenocodeuid","ccardprenocode","uid");

        //衍生字段 CardNoRefID
        concatKeys(target, "cardnorefidcontactemailuseripvalue","cardnorefid", "contactemail","useripvalue");
        concatKeys(target, "cardnorefidmobilephone7","cardnorefid","mobilephone7");

        setUidActive(target);

        getOriginalRiskLevel(po,target);

        return fact;
    }

    protected static SimpleDateFormat mergeOrderDate = new SimpleDateFormat("yyyyMMdd");

    private void setUidActive(Map<String, Object> target) {
        Map<String,Object> map = checkRiskDAO.getLeakedInfo(getString(target,"uid"));
        if(map!=null){
            setValue(target,"uidactive",getString(map,"active"));
        }
    }

    private void fillIPCity(Map<String,Object> productInfo, Map<String, Object> target,Integer checkType) {
        Map<String,Object> ipinfo = getMap(productInfo, "ipinfo");
        if(ipinfo!=null && ipinfo.size()>0) {
            copyMapIfNotNull(ipinfo, target, new String[]{"useripadd", "useripvalue", "ipcity", "ipcountry", "ipcityname", "ipprovince"});
            if (checkType.equals(1)||checkType.equals(0)) {
                Map<String, Object> map = checkRiskDAO.getCityNameProvince(getString(target, "ipcity"));
                if (map != null) {
                    setValueIfNotNull(target, "ipcityname", getString(map, "cityname"));
                    setValueIfNotNull(target, "ipprovince", getString(map, "provincename"));
                    //回写
                    setValueIfNotNull(ipinfo, "ipcityname", getString(map, "cityname"));
                    setValueIfNotNull(ipinfo, "ipprovince", getString(map, "provincename"));
                }
            }
            String ip = getString(ipinfo,"useripadd","");
            if(ip.startsWith("192.")||ip.startsWith("172.")||ip.startsWith("10.")){
               setValue(target,"isextranetip",0);
            } else {
                setValue(target,"isextranetip",1);
            }
        }
    }

    private void processOrderDate(Map<String, Object> target) {
        String orderDate = getString(target,"orderdate");
        if(!Strings.isNullOrEmpty(orderDate)){
            try {
                Date date= sdf.parse(orderDate);
                setValue(target,"mergerorderdate",mergeOrderDate.format(date));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                setValue(target,"orderdatehour",calendar.get(Calendar.HOUR_OF_DAY));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    protected void fillPaymentInfoToMap(PO po,Map<String, Object> target,HashSet<String> prepayTypes) {
        List<Map<String, Object>> payInfos = getList(po.getPaymentinfo(), "paymentinfolist");
        String prepayType;
        prepayTypes.add("ALL");
        StringBuilder sb =new StringBuilder("|");
        if (payInfos != null && payInfos.size() > 0) {
            for (Map<String, Object> payInfo : payInfos) {
                Map<String, Object> payment = getMap(payInfo, "payment");
                if (payInfo != null) {
                    prepayType = getString(payment, "prepaytype", "").toUpperCase();
                    prepayTypes.add(prepayType);
                    sb.append(prepayType).append("|");
                    if (prepayType.equals("CCARD") || prepayType.equals("DCARD")||prepayType.equals("DQPAY")) {
                        List<Map<String, Object>> cardInfoList = getList(payInfo, "cardinfolist");
                        if (cardInfoList != null && cardInfoList.size() > 0) {
                            Map<String, Object> cardInfo0 = cardInfoList.get(0);
                            //if cardnorefid!=null in CRH
                            copyMapIfNotNull(cardInfo0, target, new String[]{"ccardnocode", "cardnorefid", "cvaliditycode", "creditcardtype", "isforigencard",
                                    "cardbinissue", "cardbin", "cardholder", "branchcity", "branchprovince", "idnumberprovince", "idnumbercity"});
                        }
                    }
                    if(prepayType.equals("TMPAY")){
                        setValueIfNotNull(target,"tmpayamount",getString(payInfo,"amount"));
                    }
                }
            }
        }
        setValue(target,"mergerorderprepaytype",sb.toString());
    }

    protected void getOriginalRiskLevel(PO po,Map<String, Object> target){
        Map<String,Object> dim = new HashMap<String,Object>();
        copyValueIfNotNull(target,"uid",dim,"uid");
        copyValueIfNotNull(target,"contactemail",dim,"contactemail");
        copyValueIfNotNull(target,"mobilephone",dim,"mobilephone");
        copyValueIfNotNull(target,"ccardnocode",dim,"ccardnocode");
        copyValueIfNotNull(target,"cardnorefid",dim,"cardnorefid");
        String count =checkRiskDAO.getOriginRiskLevelCount(dim, po.getOrdertype());
        if(!Strings.isNullOrEmpty(count)){
            setValue(target,"originalrisklevelcount",count);
        }
    }

}
