package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.baseconverter.ConverterBase;
import com.ctrip.infosec.flowtable4j.biz.processor.Crypto;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.CtripSubOrderType;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by thyang on 2015-06-23.
 */
public class POConvertBase extends ConverterBase {

    private Logger logger = LoggerFactory.getLogger(POConvertBase.class);
    private static List<String> invalidIP = Arrays.asList(new String[]{"10.168.26.11", "10.168.154.11", "69.28.59.7"});
    private static List<String> invalidEmail = Arrays.asList(new String[]{"noemail@ctrip.com", "nomail@ctrip.com", "a@b.c"});
    private static List<String> invalidTel = Arrays.asList(new String[]{"400-820-6666", "400-", "4008206666", "1010-6666", "10106666", "0", "400-8206666", "13000000000"});


    /**
     * 检查 MobilePhone、UserIP,Email,ContactTel的合法性
     */
    protected void validateData(Map<String, Object> target) {
        if (target == null) {
            return;
        }

        String val = getString(target, "mobilephone");
        if (!Strings.isNullOrEmpty(val)) {
            while (val.startsWith("0")) {
                val = val.substring(1);
            }
            //去掉默认为13000000000及13800000000的手机号
            if (val.indexOf("13000000000") >= 0 || val.indexOf("13800000000") >= 0) {
                val = "";
            }
            setValue(target, "mobilephone", val);
        }

        val = getString(target, "mobile");
        if (!Strings.isNullOrEmpty(val)) {
            while (val.startsWith("0")) {
                val = val.substring(1);
            }
            //去掉默认为13000000000及13800000000的手机号
            if (val.indexOf("13000000000") >= 0 || val.indexOf("13800000000") >= 0) {
                val = "";
            }
            setValue(target, "mobile", val);
        }

        val = getString(target, "telno");
        if (!Strings.isNullOrEmpty(val)) {
            while (val.startsWith("0")) {
                val = val.substring(1);
            }
            //去掉默认为13000000000及13800000000的手机号
            if (val.indexOf("13000000000") >= 0 || val.indexOf("13800000000") >= 0) {
                val = "";
            }
            setValue(target, "telno", val);
        }

        val = getString(target, "userip");
        //去掉非法IP
        if (!Strings.isNullOrEmpty(val)) {
            val = val.trim();
            if (invalidIP.contains(val)) {
                val = "";
            }
            setValue(target, "userip", val);
        }

        val = getString(target, "contactemail");
        //非法Email
        if (!Strings.isNullOrEmpty(val) && invalidEmail.contains(val)) {
            setValue(target, "contactemail", "");
        }

        val = getString(target, "contacttel");
        //非法电话
        if (!Strings.isNullOrEmpty(val) && invalidTel.contains(val)) {
            setValue(target, "contacttel", "");
        }
    }


    /**
     * 填充DID信息
     *
     * @param target
     * @param orderId
     * @param orderType
     */
    protected void fillDIDInfo(Map<String, Object> target, String orderId, String orderType) {
        Map<String, Object> DIDInfo = checkRiskDAO.getDIDInfo(orderId, orderType);
        if (DIDInfo != null && DIDInfo.size() > 0) {
            Map<String, Object> didInfo = createMap();
            setValue(didInfo, "did", getString(DIDInfo, "did"));
            setValue(target, "didinfo", didInfo);
        }
    }

    /**
     * 填充用户信息
     *
     * @param eventBody
     * @param target
     * @param uid
     * @return
     */
    protected void fillUserInfo(Map<String, Object> eventBody, Map<String, Object> target, String uid, int orderType) {
        Map<String, Object> userInfo = createMap();
        setValue(target, "userinfo", userInfo);

        //机票会抛用户信息,先接收信息
        if (CtripOrderType.Flights.getCode() == orderType) {
            copyMap(eventBody, userInfo, "infosecurity_userinfo");
        }

        setValue(userInfo, "uid", uid);

        //HotelEBK不须取MemeberInfo的信息
        if(CtripOrderType.HotelEBK.getCode()== orderType){
            copyValue(eventBody,"signupdate",userInfo,"signupdate");
            return;
        }
        if(CtripOrderType.YongAnFlight.getCode()==orderType)
        {
            copyMap(eventBody,userInfo,"infosecurity_userinfo");
            return;
        }

        try {
            Map<String, Object> crmInfo = esbClient.getMemberInfo(uid);
            if (crmInfo != null && crmInfo.size()>0) {
                copyMap(crmInfo, userInfo, "infosecurity_userinfo");
                copyValue(crmInfo, "email", userInfo, "relatedemail");
                copyValue(crmInfo, "mobilephone", userInfo, "relatedmobilephone");
                copyValue(crmInfo, "md5password", userInfo, "userpassword");
                String signupDate = getString(userInfo, "signupdate");
                if (!Strings.isNullOrEmpty(signupDate)) {
                    setValue(userInfo, "signupdate", signupDate.replace("T", " ") + ".000");
                }
                if ("T".equals(getString(crmInfo, "vip", "").toUpperCase())) {
                    setValue(userInfo, "cuscharacter", "VIP");
                } else {
                    Map<String, Object> customer = esbClient.getCustomerInfo(uid);
                    if (customer != null && customer.size() >0 )
                        if ("1900-01-01".compareTo(getString(customer, "customerdate")) < 0) {
                            setValue(userInfo, "cuscharacter", "REPEAT");
                        } else {
                            setValue(userInfo, "cuscharacter", "NEW");
                        }
                }
           }

           //天海外部用户
           if(crmInfo==null && orderType == CtripOrderType.CruiseByTianHai.getCode()){
                copyMap(getMap(eventBody,"thuidinfo"),userInfo,"infosecurity_userinfo");
            }
            fillMobileProvince(userInfo, getString(userInfo, "bindedmobilephone"), getString(userInfo, "relatedmobilephone"));
        } catch (Exception e) {
            logger.warn("查询用户" + uid + "的userInfo的信息异常" + e.getMessage());
        }

    }

    private void fillMobileProvince(Map<String, Object> target, String bindedMobilePhone, String relatedMobilephone) {
        Map<String, Object> map = checkRiskDAO.getMobileCityAndProv(bindedMobilePhone);
        if (map != null) {
            setValue(target, "bindedmobilephonecity", getString(map, "cityname"));
            setValue(target, "bindedmobilephoneprovince", getString(map, "provincename"));
        }
        if (!StringUtils.equals(bindedMobilePhone, relatedMobilephone)) {
            map = checkRiskDAO.getMobileCityAndProv(relatedMobilephone);
        }
        if (map != null) {
            setValue(target, "relatedmobilephonecity", getString(map, "cityname"));
            setValue(target, "relatedmobilephoneprovince", getString(map, "provincename"));
        }
    }

    /**
     * 填充OtherInfo
     */
    protected void fillOtherInfo(PO po, Map<String, Object> eventBody) {
        Map<String, Object> otherInfo = createMap();
        copyMap(eventBody, otherInfo, "infosecurity_otherinfo");

        String orderDate = getString(po.getProductinfo(), new String[]{"maininfo", "orderdate"});
        String signupDate = getString(po.getProductinfo(), new String[]{"userinfo", "signupdate"});

        setValue(otherInfo, "ordertosignupdate", dateDiffHour(orderDate, signupDate));
        setValue(otherInfo, "takeofftoorderdate", 0);

        setValue(po.getProductinfo(), "otherinfo", otherInfo);
    }

    /**
     * 填充联系信息
     *
     * @param eventBody
     * @param root
     */
    protected void fillContactInfo(Map<String, Object> eventBody, Map<String, Object> root, int orderType) {
        Map<String, Object> contactInfo = createMap();
        if (orderType == CtripOrderType.JiFen.getCode()) {  //积分的数据在 ContactView上面
            Map<String, Object> contactMap = getMap(eventBody, "contactview");
            copyValue(contactMap, "name", contactInfo, "contactname");
            copyValue(contactMap, "phone", contactInfo, "contacttel");
            copyValue(contactMap, "email", contactInfo, "contactemail");
            copyValue(contactMap, "fax", contactInfo, "contactfax");
            copyValue(contactMap, "remark", contactInfo, "remark");
            copyValue(contactMap, "mobile", contactInfo, "mobilephone");
        } else {
           contactInfo =copyMap(eventBody,new String[]{"mobilephone","contactname","contacttel","contactemail","sendtickeraddr"});
        }
        Map<String, Object> city = checkRiskDAO.getMobileCityAndProv(getString(contactInfo, "mobilephone"));
        if (city != null) {
            copyValue(city, "cityname", contactInfo, "mobilephonecity");
            copyValue(city, "provincename", contactInfo, "mobilephoneprovince");
        }
        setValue(root, "contactinfo", contactInfo);
    }


    /**
     * 填充IP相关信息
     *
     * @param target
     * @param userIP
     */
    protected void fillIPInfo(Map<String, Object> target, String userIP) {
        Map<String, Object> ipInfo = createMap();
        setValue(ipInfo, "useripadd", userIP);
        long ipValue = ipConvertToValue(userIP);
        setValue(ipInfo, "useripvalue", ipValue);
        Map<String, Object> ip = checkRiskDAO.getIPInfo(ipValue);
        if (ip != null && ip.size()>0) {
            setValue(ipInfo, "continent", 0);
            setValue(ipInfo, "ipcity", getObject(ip, "cityid"));
            setValue(ipInfo, "ipcountry", getObject(ip, "nationcode"));
        }
        setValue(target, "ipinfo", ipInfo);
    }

    /**
     * 获取主要支付方式
     *
     * @param paymentInfos
     * @return
     */
    public String getPrepayType(Map<String, Object> paymentInfos) {
        List<Map<String, Object>> paymentInfoList = getList(paymentInfos, "paymentinfolist");
        String prePay = "";
        if (paymentInfoList != null) {
            for (Map<String, Object> p : paymentInfoList) {
                String tmpprePay = getString(p, new String[]{"payment", "prepaytype"}, "").toUpperCase();
                if (tmpprePay.equals("CCARD") || tmpprePay.equals("DCARD")) {
                    prePay = tmpprePay;
                    break;
                }
            }
        }
        return prePay;
    }

}
