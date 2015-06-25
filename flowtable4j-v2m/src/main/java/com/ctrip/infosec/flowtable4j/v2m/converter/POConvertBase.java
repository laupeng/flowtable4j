package com.ctrip.infosec.flowtable4j.v2m.converter;

import com.ctrip.infosec.flowtable4j.model.MapX;
import com.ctrip.infosec.flowtable4j.model.RequestBody;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thyang on 2015-06-23.
 */
public class POConvertBase  extends ConverterBase {

    private Logger logger = LoggerFactory.getLogger(POConvertBase.class);
    /**
     * 检查 MobilePhone、UserIP的合法性
     */
    protected void validateData(RequestBody requestBody) {
        String mobile = getString(requestBody.getEventBody(), "mobilephone");
        if (!Strings.isNullOrEmpty(mobile)) {
            while (mobile.startsWith("0")) {
                mobile = mobile.substring(1);
            }
            //去掉默认为13000000000及13800000000的手机号
            if (mobile.indexOf("13000000000") >= 0 || mobile.indexOf("13800000000") >= 0) {
                mobile = "";
            }
        }

        setValue(requestBody.getEventBody(), "mobilephone", mobile);

        String userIp = getString(requestBody.getEventBody(), "userip");
        //去掉非法IP
        if (!Strings.isNullOrEmpty(userIp)) {
            if (userIp.startsWith("10.168.26.11") || userIp.startsWith("10.168.154.11") || userIp.startsWith("69.28.59.7")) {
                userIp = "";
            }
        }
        setValue(requestBody.getEventBody(), "userip", userIp);

        String email = getString(requestBody.getEventBody(), "contactemail");
        //非法Email
        if (!Strings.isNullOrEmpty(email)) {
            if (email.equals("noemail@ctrip.com") || email.equals("10.168.154.11") || email.equals("69.28.59.7")) {
                email = "";
            }
        }
        setValue(requestBody.getEventBody(), "contactemail", email);
    }

    /**
     * 填充DID信息
     * @param root
     * @param orderId
     * @param orderType
     */
    protected void fillDIDInfo(Map<String,Object> root, String orderId, String orderType) {
        Map<String,Object> deviceId = new HashMap<String, Object>();
        Map<String,Object> DIDInfo = checkRiskDAO.getDIDInfo(orderId, orderType);
        if (DIDInfo != null && DIDInfo.size() > 0) {
            setValue(deviceId,"did",getString(DIDInfo,"did"));
        }
        setValue(root,"didinfo",deviceId);
    }


    /**
     * 填充 支付信息
     * @param requestBody
     * @param root
     */
    public String fillPaymentInfo(RequestBody requestBody, Map<String,Object> root) {
        String totalPreyType="";
        List<Map<String, Object>> paymentInfoList  = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> paymentInfoListSrc = (List<Map<String, Object>>) MapX.getList(requestBody.getEventBody(), "paymentinfos");
        if (paymentInfoListSrc == null || paymentInfoListSrc.size() == 0) {
            return "";
        }
        for (Map<String, Object> paymentSrc : paymentInfoListSrc) {
            Map<String, Object> paymentInfo = new HashMap<String, Object>();
            Map<String, Object> payment = new HashMap<String, Object>();
            String prepayType = MapX.getStringEmpty(paymentSrc, "prepaytype");
            totalPreyType =  ("CCARD".equals(totalPreyType)||("DCARD").equals(totalPreyType))? totalPreyType:prepayType;
            setValue(payment, "prepaytype", prepayType);
            setValue(payment, "amount", getObject(paymentSrc, "amount"));
            setValue(payment, "refno", getObject(paymentSrc, "refno"));
            String cardInfoId = getString(paymentSrc, "cardinfoid");
            setValue(payment, "cardinfoid", cardInfoId);
            setValue(paymentInfo,"payment",payment);
            List<Map<String, Object>> cardInfoList = new ArrayList<Map<String, Object>>();
            Map<String, Object> cardInfo = new HashMap<String, Object>();
            if (prepayType.toUpperCase().equals("CCARD") || prepayType.toUpperCase().equals("DCARD")) {
                setValue(cardInfo, "CardInfoID", cardInfoId);
                setValue(cardInfo, "InfoID", 0);
                if (!Strings.isNullOrEmpty(cardInfoId)) {
                    Map cardInfoResult = esbClient.getCardInfo(cardInfoId);//从esb取出相关数据
                    if (cardInfoResult != null && cardInfoResult.size() > 0) {
                        String creditCardType = getString(cardInfoResult, "creditcardtype");
                        String cardBin = getString(cardInfoResult, "cardbin");
                        setValue(cardInfo, "billingaddress", getString(cardInfoResult, "billingaddress"));
                        setValue(cardInfo, "cardbin", cardBin);
                        setValue(cardInfo, "cardholder", getString(cardInfoResult, "cardholder"));
                        setValue(cardInfo, "ccardlastnocode", getString(cardInfoResult, "cardrisknolastcode"));
                        setValue(cardInfo, "ccardnocode", getString(cardInfoResult, "ccardnocode"));
                        setValue(cardInfo, "cardnorefid", getString(cardInfoResult, "cardnorefid"));
                        setValue(cardInfo, "ccardprenocode", getString(cardInfoResult, "cardrisknoprecode"));
                        setValue(cardInfo, "creditcardtype", creditCardType);
                        setValue(cardInfo, "cvaliditycode", getString(cardInfoResult, "cvaliditycode"));
                        setValue(cardInfo, "isforigencard", getString(cardInfoResult, "isforeigncard"));
                        setValue(cardInfo, "nationality", getString(cardInfoResult, "nationality"));
                        setValue(cardInfo, "nationalityofisuue", getString(cardInfoResult, "nationalityofisuue"));
                        setValue(cardInfo, "bankofcardissue", getString(cardInfoResult, "bankofcardissue"));
                        setValue(cardInfo, "statename", getString(cardInfoResult, "statename"));

                        Map subCardInfo = checkRiskDAO.getForeignCardInfo(creditCardType, cardBin);
                        if (subCardInfo != null && subCardInfo.size() > 0) {
                            setValue(cardInfo, "cardbinissue", getString(subCardInfo, "nationality"));
                            setValue(cardInfo, "cardbinbankofcardissue", getString(subCardInfo, "bankofcardissue"));
                        }

                        //取出branchCity 和 branchProvince
                        String creditCardNumber = getString(cardInfoResult, "creditcardnumber");
                        if (creditCardType.equals("3") && !Strings.isNullOrEmpty(creditCardNumber))//这里只针对类型为3的卡进行处理
                        {
                            String decryptText = null;
                            try {
                                decryptText = Crypto.decrypt(creditCardNumber);
                            } catch (Exception exp) {
                                logger.warn("解密卡号异常" + exp.getMessage());
                            }
                            if (!Strings.isNullOrEmpty(decryptText) && decryptText.length() > 12) {
                                String branchNo = decryptText.substring(6, 9);
                                if (!branchNo.isEmpty()) {
                                    Map cardBankInfo = checkRiskDAO.getCardBankInfo(creditCardType, branchNo);
                                    if (cardBankInfo != null) {
                                        setValue(cardInfo, "branchcity", getString(cardBankInfo, "branchcity"));
                                        setValue(cardInfo, "branchprovince", getString(cardBankInfo, "branchprovince"));
                                    }
                                }
                            }
                        }
                    }
                }
                cardInfoList.add(cardInfo);
            }
            setValue(paymentInfo,"cardinfolist",cardInfoList);
            paymentInfoList.add(paymentInfo);
        }
        setValue(root,"paymentinfolist",paymentInfoList);
        return totalPreyType;
    }


    /**
     * 填充用户信息
     * @param requestBody
     * @param root
     * @param uid
     * @return
     */
    protected String fillUserInfo(RequestBody requestBody, Map<String,Object> root, String uid) {
        String signupDate="";
        Map<String,Object> userInfo = new HashMap<String, Object>();
        setValue(userInfo, "uid", uid);
        setValue(userInfo, "cuscharacter", "NEW");
        try {
            Map<String,Object> crmInfo = esbClient.getMemberInfo(uid);
            if (crmInfo != null) {
                setValue(userInfo, "relatedemail", getString(crmInfo, "email"));
                setValue(userInfo, "relatedmobilephone", getString(crmInfo, "mobilephone"));
                setValue(userInfo, "bindedemail", getString(crmInfo, "bindedemail"));
                setValue(userInfo, "bindedmobilephone", getString(crmInfo, "bindedmobilephone"));
                String experience = getString(crmInfo, "experience");
                if (Strings.isNullOrEmpty(experience)) {
                    experience = "0";
                }
                setValue(userInfo, "experience", experience);
                signupDate = getString(crmInfo, "signupdate");
                setValue(userInfo, "signupdate", signupDate);
                setValue(userInfo, "userpassword", getString(crmInfo, "md5password"));
                setValue(userInfo, "vipgrade", getString(crmInfo, "vipgrade"));
                if ("T".equals(getString(crmInfo, "vip"))) {
                    setValue(userInfo, "cuscharacter", "VIP");
                } else {
                    Map customer = esbClient.getCustomerInfo(uid);
                    if (customer != null && "1900-01-01".compareTo(getString(customer, "customerdate")) < 0) {
                        setValue(userInfo, "cuscharacter", "REPEAT");
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("查询用户" + uid + "的userInfo的信息异常" + e.getMessage());
        }
        setValue(root,"userinfo",userInfo);
        return  Strings.isNullOrEmpty(signupDate)? signupDate:signupDate.replace("T"," ")+".000";
    }

    /**
     * 填充OtherInfo
     * @param root
     * @param orderDate
     * @param signupDate
     * @param takeOffTime
     */
    protected void fillOtherInfo(Map<String,Object> root,String orderDate, String signupDate,String takeOffTime) {
        Map<String,Object> otherInfo = new HashMap<String, Object>();
        setValue(otherInfo,"ordertosignupdate", dateDiffHour(orderDate, signupDate));
        setValue(otherInfo,"takeofftoorderdate", dateDiffHour(takeOffTime, orderDate));
        setValue(root,"otherinfo",otherInfo);
    }

    /**
     * 填充联系信息
     * @param requestBody
     * @param root
     */
    protected void fillContactInfo(RequestBody requestBody, Map<String,Object> root) {
        Map<String,Object> contactInfo = new HashMap<String, Object>();
        copyMap(requestBody.getEventBody(),contactInfo,"infosecurity_contactinfo");
        Map<String,Object> city = checkRiskDAO.getMobileCityAndProv(getString(contactInfo,"mobilephone"));
        if (city != null) {
            setValue(contactInfo, "mobilephonecity", getString(city, "cityname"));
            setValue(contactInfo, "mobilephoneprovince", getString(city, "provincename"));
        }
        setValue(root,"contactinfo",contactInfo);
    }

    /**
     * 填充VactionInfoList
     * @param productInfo
     * @param eventBody
     */
    protected void fillVactionInfo(Map<String, Object> productInfo, Map<String, Object> eventBody) {
        List<Map<String, Object>> vacationinfolist = new ArrayList<Map<String, Object>>();
        Map<String, Object> order = new HashMap<String, Object>();
        setValue(order, "productname", getString(eventBody, "productname"));
        setValue(order, "productid", getString(eventBody, "productid"));
        List<Map<String, Object>> userlist = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> userInfos = (List<Map<String, Object>>) MapX.getList(eventBody, "userinfos");
        if (userInfos != null && userInfos.size() > 0) {
            for (Map<String, Object> u : userInfos) {
                Map<String, Object> vu = new HashMap<String, Object>();
                setValue(vu, "visitoridcardtype", getObject(u, "idtype"));
                setValue(vu, "visitorname", getObject(u, "visitorname"));
                setValue(vu, "visitorcontactinfo", getObject(u, "visitorcontactinfo"));
                setValue(vu, "visitoridcode", getObject(u, "visitorcardno"));
                userlist.add(vu);
            }
        }
        vacationinfolist.add(ImmutableMap.of("order", order, "userlist", userlist));
        setValue(productInfo,"vacationinfolist",vacationinfolist);
    }
    /**
     * 填充IP相关信息
     * @param root
     * @param userIP
     */
    protected void fillIPInfo(Map<String, Object> root, String userIP) {
        Map<String,Object> ipInfo = new HashMap<String, Object>();
        setValue(ipInfo, "useripadd", userIP);
        long ipValue = ipConvertToValue(userIP);
        setValue(ipInfo, "useripvalue", ipValue);
        Map<String,Object> ip = checkRiskDAO.getIpCountryCity(ipValue);
        if (ip != null) {
            setValue(ipInfo, "continent", getObject(ip,"continentid"));
            setValue(ipInfo, "ipcity", getObject(ip,"cityid"));
            setValue(ipInfo, "ipcountry", getObject(ip,"countrycode"));
        }
        setValue(root,"ipinfo",ipInfo);
    }

    /**
     * 获取主要支付方式
     * @param paymentInfos
     * @return
     */
    public String getPrepayType(Map<String,Object> paymentInfos){
        List<Map<String,Object>> paymentInfoList = (List<Map<String,Object>>) MapX.getList(paymentInfos,"paymentInfoList");
        String prePay="";
        if(paymentInfoList!=null){
            for(Map<String,Object> p:paymentInfoList){
                prePay = getString(p,"prepaytype").toUpperCase();
                if(prePay.equals("CCARD")||prePay.equals("DCARD")){
                    break;
                }
            }
        }
        return prePay;
    }
}
