package com.ctrip.infosec.flowtable4j.v2m.converter;

import com.ctrip.infosec.flowtable4j.dal.CheckRiskDAO;
import com.ctrip.infosec.flowtable4j.dal.ESBClient;
import com.ctrip.infosec.flowtable4j.model.MapX;
import com.ctrip.infosec.flowtable4j.model.RequestBody;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.ctrip.infosec.flowtable4j.model.persist.PaymentInfo;
import com.ctrip.infosec.flowtable4j.v2m.service.Save2DbService;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 放入工具类方法
 * Created by thyang on 2015-06-10.
 */
public class ConverterBase {

    protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private Logger logger = LoggerFactory.getLogger(ConverterBase.class);

    @Autowired
    protected CheckRiskDAO checkRiskDAO;

    @Autowired
    protected ESBClient esbClient;

    @Autowired
    protected Save2DbService dbService;


    /**
     * 根据字段映射从RequestBody取数据
     *
     * @param requestBody 请求报文
     * @param targetMap   目标Map
     * @param dbEntity    表名
     */
    protected void fillEntity(RequestBody requestBody, Map<String, Object> targetMap, String dbEntity) {
        Map<String, String> dbMeta = dbService.getDbMeta(dbEntity);
        Map<String,Object> root=requestBody.getEventBody();
        for (String field : dbMeta.keySet()) {
           setValue(targetMap, field, getObject(root, field));
        }
    }

    /**
     * 根据字段映射从RequestBody取数据     *
     * @param requestBody 请求报文
     * @param targetMap   目标Map
     * @param fieldMap    entity的字段
     */
    protected void fillEntity(RequestBody requestBody, Map<String, Object> targetMap, Map<String, String> fieldMap) {
        //有字段映射
        Map<String,Object> root = requestBody.getEventBody();
        for (String field : fieldMap.keySet()) {
           setValue(targetMap, field, getObject(root,fieldMap.get(field)));
        }
    }

    /**
     * 根据表定义，从 Src Map取数据
     *
     * @param srcMap    原始Map
     * @param targetMap 目标Map
     * @param dbEntity  数据表，读目的数据
     */
    protected void fillEntity(Map<String, Object> srcMap, Map<String, Object> targetMap, String dbEntity) {
        Map<String, String> dbMeta = dbService.getDbMeta(dbEntity);
        //Fetch DbMeta by dbEntity
        for (String field : dbMeta.keySet()) {
            setValue(targetMap, field, getObject(srcMap, field));
        }
    }

    /**
     * 根据字段映射从srcMap取数据取数据
     *
     * @param targetMap
     * @param fieldMap
     */
    protected void fillEntity(Map<String, Object> srcMap, Map<String, Object> targetMap, Map<String, String> fieldMap) {
        //有字段映射
        for (String field : fieldMap.keySet()) {
            setValue(targetMap, field, getObject(srcMap, fieldMap.get(field)));
        }
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
                 prePay = getString(p,"PrepayType").toUpperCase();
                 if(prePay.equals("CCARD")||prePay.equals("DCARD")){
                     break;
                 }
             }
         }
        return prePay;
    }

    public String getString(Map<String,Object> data, String key) {
        return MapX.getString(data,key);
    }

    public String getString(Map<String,Object> data, String[] key) {
        return MapX.getString(data,key);
    }

    public Object getObject(Map<String,Object> data, String key) {
        return MapX.getObject(data, key);
    }

    public Object getObject(Map<String,Object> data, String[] key) {
        return MapX.getObject(data, key);
    }

    public boolean setValue(Map<String,Object> target,String key,Object value){
        return MapX.setValue(target,key,value);
    }

    public boolean setValue(Map<String,Object> target,String[] key,Object value){
        return MapX.setValue(target,key,value);
    }

    /**
     * 检查 MobilePhone、UserIP的合法性
     */
    protected void validateData(RequestBody requestBody) {
        String mobile = getString(requestBody.getEventBody(), "MobilePhone");
        if (!Strings.isNullOrEmpty(mobile)) {
            while (mobile.startsWith("0")) {
                mobile = mobile.substring(1);
            }
            //去掉默认为13000000000及13800000000的手机号
            if (mobile.indexOf("13000000000") >= 0 || mobile.indexOf("13800000000") >= 0) {
                mobile = "";
            }
        }
        setValue(requestBody.getEventBody(), "MobilePhone", mobile);

        String userIp = getString(requestBody.getEventBody(), "UserIP");
        //去掉非法IP
        if (!Strings.isNullOrEmpty(userIp)) {
            if (userIp.startsWith("10.168.26.11") || userIp.startsWith("10.168.154.11") || userIp.startsWith("69.28.59.7")) {
                userIp = "";
            }
        }
        setValue(requestBody.getEventBody(), "UserIP", userIp);

        String email = getString(requestBody.getEventBody(), "ContactEMail");
        //非法Email
        if (!Strings.isNullOrEmpty(email)) {
            if (email.equals("noemail@ctrip.com") || email.equals("10.168.154.11") || email.equals("69.28.59.7")) {
                email = "";
            }
        }
        setValue(requestBody.getEventBody(), "ContactEMail", email);
    }

    private String ipConvertToStr(long Ip) {
        long a = (Ip & 0xFF000000) >> 24;
        long b = (Ip & 0x00FF0000) >> 16;
        long c = (Ip & 0x0000FF00) >> 8;
        long d = Ip & 0x000000FF;
        return a + "." + b + "." + c + "." + d;
    }

    private long ipConvertToValue(String ip) {
        long n_Ip = 0;
        if (ip != null && ip.length() > 7) {
            String[] arr = ip.split("[.]|[:]");
            if (arr.length >= 4) {
                long a = Long.parseLong(arr[0].toString());
                long b = Long.parseLong(arr[1].toString());
                long c = Long.parseLong(arr[2].toString());
                long d = Long.parseLong(arr[3].toString());
                n_Ip = (((((a << 8) | b) << 8) | c) << 8) | d;
            }
        }
        return n_Ip;
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
            setValue(deviceId,"DID",getString(DIDInfo,"Did"));
        }
        setValue(root,"deviceId",deviceId);
    }


    /**
     * 填充 支付信息
     * @param requestBody
     * @param root
     */
    public void fillPaymentInfo(RequestBody requestBody, Map<String,Object> root) {
        List<Map<String, Object>> paymentInfoList  = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> paymentInfoListSrc = (List<Map<String, Object>>) MapX.getList(requestBody.getEventBody(),"PaymentInfos");
        if (paymentInfoListSrc == null || paymentInfoListSrc.size() == 0) {
            return;
        }
        for (Map<String, Object> paymentSrc : paymentInfoListSrc) {
            Map<String, Object> paymentInfo = new HashMap<String, Object>();
            Map<String, Object> payment = new HashMap<String, Object>();
            String prepayType = getString(paymentSrc, "PrepayType");
            setValue(payment, "PrepayType", prepayType);
            setValue(payment, "Amount", getObject(paymentSrc, "Amount"));
            setValue(payment, "RefNo", getObject(paymentSrc, "RefNo"));
            String cardInfoId = getString(paymentSrc, "CardInfoID");
            setValue(payment, "CardInfoID", cardInfoId);
            setValue(paymentInfo,"payment",payment);
            List<Map<String, Object>> cardInfoList = new ArrayList<Map<String, Object>>();
            Map<String, Object> cardInfo = new HashMap<String, Object>();
            if (prepayType.toUpperCase().equals("CCARD") || prepayType.toUpperCase().equals("DCARD")) {
                setValue(cardInfo, "CardInfoID", cardInfoId);
                setValue(cardInfo, "InfoID", 0);
                if (!Strings.isNullOrEmpty(cardInfoId)) {
                    Map cardInfoResult = esbClient.getCardInfo(cardInfoId);//从esb取出相关数据
                    if (cardInfoResult != null && cardInfoResult.size() > 0) {
                        String creditCardType = getString(cardInfoResult, "CreditCardType");
                        String cardBin = getString(cardInfoResult, "CardBin");
                        setValue(cardInfo, "BillingAddress", getString(cardInfoResult, "BillingAddress"));
                        setValue(cardInfo, "CardBin", cardBin);
                        setValue(cardInfo, "CardHolder", getString(cardInfoResult, "CardHolder"));
                        setValue(cardInfo, "CCardLastNoCode", getString(cardInfoResult, "CardRiskNoLastCode"));
                        setValue(cardInfo, "CCardNoCode", getString(cardInfoResult, "CCardNoCode"));
                        setValue(cardInfo, "CardNoRefID", getString(cardInfoResult, "CardNoRefID"));
                        setValue(cardInfo, "CCardPreNoCode", getString(cardInfoResult, "CardRiskNoPreCode"));
                        setValue(cardInfo, "CreditCardType", creditCardType);
                        setValue(cardInfo, "CValidityCode", getString(cardInfoResult, "CValidityCode"));
                        setValue(cardInfo, "IsForigenCard", getString(cardInfoResult, "IsForeignCard"));
                        setValue(cardInfo, "Nationality", getString(cardInfoResult, "Nationality"));
                        setValue(cardInfo, "Nationalityofisuue", getString(cardInfoResult, "Nationalityofisuue"));
                        setValue(cardInfo, "BankOfCardIssue", getString(cardInfoResult, "BankOfCardIssue"));
                        setValue(cardInfo, "StateName", getString(cardInfoResult, "StateName"));
                        setValue(cardInfo, "CardNoRefID", getString(cardInfoResult, "CardNoRefID"));
                        //取出branchCity 和 branchProvince
                        String creditCardNumber = getString(cardInfoResult, "CreditCardNumber");
                        if (creditCardType.equals("3") && !Strings.isNullOrEmpty(creditCardNumber))//这里只针对类型为3的卡进行处理
                        {
                            String decryptText = null;
                            try {
                                decryptText = Crypto.decrypt(creditCardNumber);
                            } catch (Exception exp) {
                                logger.warn("解密卡号异常" + exp.getMessage());
                            }
                            if (decryptText != null && !decryptText.isEmpty() && decryptText.length() > 12) {
                                String branchNo = decryptText.substring(6, 9);
                                if (!branchNo.isEmpty()) {
                                    Map cardBankInfo = checkRiskDAO.getCardBankInfo(creditCardType, branchNo);
                                    if (cardBankInfo != null) {
                                        setValue(cardInfo, "BranchCity", getString(cardBankInfo, "BranchCity"));
                                        setValue(cardInfo, "BranchProvince", getString(cardBankInfo, "BranchProvince"));
                                    }
                                }
                            }
                            Map subCardInfo = checkRiskDAO.getForeignCardInfo(creditCardType, cardBin);
                            if (subCardInfo != null && subCardInfo.size() > 0) {
                                setValue(cardInfo, "CardBinIssue", getString(subCardInfo, "Nationality"));
                                setValue(cardInfo, "CardBinBankOfCardIssue", getString(subCardInfo, "BankOfCardIssue"));
                            }
                        }
                    }
                }
                cardInfoList.add(cardInfo);
            }
            setValue(paymentInfo,"cardInfoList",cardInfoList);
            paymentInfoList.add(paymentInfo);
        }
        setValue(root,"paymentInfoList",paymentInfoList);
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
        setValue(userInfo, "Uid", uid);
        setValue(userInfo, "CusCharacter", "NEW");
        try {
            Map<String,Object> crmInfo = esbClient.getMemberInfo(uid);
            if (crmInfo != null) {
                setValue(userInfo, "RelatedEMail", getString(crmInfo, "Email"));
                setValue(userInfo, "RelatedMobilephone", getString(crmInfo, "MobilePhone"));
                setValue(userInfo, "BindedEmail", getString(crmInfo, "BindedEmail"));
                setValue(userInfo, "BindedMobilePhone", getString(crmInfo, "BindedMobilePhone"));
                String experience = getString(crmInfo, "Experience");
                if (experience.isEmpty()) {
                    experience = "0";
                }
                setValue(userInfo, "Experience", experience);
                signupDate = getString(crmInfo, "Signupdate");
                setValue(userInfo, "SignUpDate", signupDate);
                setValue(userInfo, "UserPassword", getString(crmInfo, "MD5Password"));
                setValue(userInfo, "VipGrade", getString(crmInfo, "VipGrade"));
                if (!"T".equals(getString(crmInfo, "Vip"))) {
                    Map customer = esbClient.getCustomerInfo(uid);
                    if (customer != null && "1900-01-01".compareTo(getString(customer, "CustomerDate")) < 0) {
                        setValue(userInfo, "CusCharacter", "REPEAT");
                    }
                } else {
                    setValue(userInfo, "CusCharacter", "VIP");
                }
            }
        } catch (Exception e) {
            logger.warn("查询用户" + uid + "的userInfo的信息异常" + e.getMessage());
        }
        setValue(root,"userInfo",userInfo);
        return signupDate;
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
        setValue(otherInfo,"OrderToSignUpDate", dateDiffHour(orderDate, signupDate));
        setValue(otherInfo,"TakeOffToOrderDate", dateDiffHour(takeOffTime, orderDate));
        setValue(root,"otherInfo",otherInfo);
    }

    private String dateDiffHour(String startDate,String endDate) {
        if(startDate ==null || endDate ==null){
            return "0";
        }
        try {
            long S = sdf.parse(startDate).getTime();
            long E = sdf.parse(endDate).getTime();
            return  String.valueOf(Math.abs((S - E) / 1000 / 60 / 60));
        }
        catch (Exception ex){
            return "0";
        }
    }

    /**
     * 填充联系信息
     * @param requestBody
     * @param root
     */
    protected void fillContactInfo(RequestBody requestBody, Map<String,Object> root) {
        Map<String,Object> contactInfo = new HashMap<String, Object>();
        fillEntity(requestBody,contactInfo, "InfoSecurity_ContactInfo");
        Map<String,Object> city = checkRiskDAO.getMobileCityAndProv(getString(requestBody.getEventBody(), "MobilePhone"));
        if (city != null) {
            setValue(contactInfo, "MobilePhoneCity", getString(city, "CityName"));
            setValue(contactInfo, "MobilePhoneProvince", getString(city, "ProvinceName"));
        }
        setValue(root,"contactInfo",contactInfo);
    }

    /**
     * 填充IP相关信息
     * @param root
     * @param userIP
     */
    protected void fillIPInfo(Map<String, Object> root, String userIP) {
        Map<String,Object> ipInfo = new HashMap<String, Object>();
        setValue(ipInfo, "UserIPAdd", userIP);
        long ipValue = ipConvertToValue(userIP);
        setValue(ipInfo, "UserIPValue", ipValue);
        Map<String,Object> ip = checkRiskDAO.getIpCountryCity(ipValue);
        if (ip != null) {
            setValue(ipInfo, "Continent", getObject(ip, "ContinentID"));
            setValue(ipInfo, "IPCity", getObject(ip, "CityId"));
            setValue(ipInfo, "IPCountry", getObject(ip, "NationCode"));
        }
        setValue(root,"ipInfo",ipInfo);
    }
}
