package com.ctrip.infosec.flowtable4j.v2m.converter;

import com.ctrip.infosec.flowtable4j.dal.CheckRiskDAO;
import com.ctrip.infosec.flowtable4j.dal.ESBClient;
import com.ctrip.infosec.flowtable4j.dal.RedisProvider;
import com.ctrip.infosec.flowtable4j.model.RequestBody;
import com.ctrip.infosec.flowtable4j.model.persist.PO;
import com.ctrip.infosec.flowtable4j.model.persist.PaymentInfo;
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
    protected RedisProvider redisProvider;
    /**
     * 根据字段映射从RequestBody取数据
     *
     * @param requestBody 请求报文
     * @param targetMap   目标Map
     * @param dbEntity    表名
     */
    protected void fillEntity(RequestBody requestBody, Map<String, Object> targetMap, String dbEntity) {
        Map<String, String> dbMeta = new HashMap<String, String>();
        //Fetch DbMeta by dbEntity
        for (String field : dbMeta.keySet()) {
            targetMap.put(field, requestBody.getString(field));
        }
    }

    public String getValue(Map data, String key) {
        Object obj = data.get(key);
        return obj == null ? "" : String.valueOf(obj);
    }

    /**
     * 根据字段映射从RequestBody取数据
     *
     * @param requestBody 请求报文
     * @param targetMap   目标Map
     * @param fieldMap    entity的字段，对应eventBody的路径，用A.B分层级
     */
    protected void fillEntity(RequestBody requestBody, Map<String, Object> targetMap, Map<String, String> fieldMap) {
        //有字段映射
        for (String field : fieldMap.keySet()) {
            targetMap.put(field, requestBody.getString(fieldMap.get(field).split("[.]")));
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
        Map<String, String> dbMeta = new HashMap<String, String>();
        //Fetch DbMeta by dbEntity
        for (String field : dbMeta.keySet()) {
            targetMap.put(field, srcMap.get(field));
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
            targetMap.put(field, srcMap.get(fieldMap.get(field)));
        }
    }

    /**
     * 检查 MobilePhone、UserIP的合法性
     */
    protected void validateMobilePhoneUserIP(RequestBody requestBody) {
        String mobile = requestBody.getString("MobilePhone");
        if (!Strings.isNullOrEmpty(mobile)) {
            while (mobile.startsWith("0")) {
                mobile = mobile.substring(1);
            }
            //去掉默认为13000000000及13800000000的手机号
            if (mobile.indexOf("13000000000") >= 0 || mobile.indexOf("13800000000") >= 0) {
                mobile = "";
            }
        }
        requestBody.getEventBody().put("MobilePhone", mobile);

        String userIp = requestBody.getString("UserIP");
        //去掉非法IP
        if (!Strings.isNullOrEmpty(userIp)) {
            if (userIp.startsWith("10.168.26.11") || userIp.startsWith("10.168.154.11") || userIp.startsWith("69.28.59.7")) {
                userIp = "";
            }
        }
        requestBody.getEventBody().put("UserIP", userIp);

        String email = requestBody.getString("ContactEMail");
        //非法Email
        if (!Strings.isNullOrEmpty(email)) {
            if (email.equals("noemail@ctrip.com") || email.equals("10.168.154.11") || email.equals("69.28.59.7")) {
                email = "";
            }
        }
        requestBody.getEventBody().put("ContactEMail", email);
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

    public void fillDIDInfo(PO po, String orderId, String orderType) {
        po.deviceId = new HashMap<String, Object>();
        Map DIDInfo = checkRiskDAO.getDIDInfo(orderId, orderType);
        if (DIDInfo != null && DIDInfo.size() > 0) {
            po.deviceId.put("DID", DIDInfo.get("Did"));
        }
    }


    /**
     * 填充 订单信息
     *
     * @param requestBody
     * @param po
     */
    public void fillPaymentInfo(RequestBody requestBody, PO po) {
        po.paymentInfoList = new ArrayList<PaymentInfo>();
        List<Map<String, Object>> paymentInfos = (List<Map<String, Object>>) requestBody.getList("PaymentInfos");
        if (paymentInfos == null || paymentInfos.size() == 0) {
            return;
        }
        for (Map paymentEntity : paymentInfos) {
            PaymentInfo paymentInfo = new PaymentInfo();
            Map<String, Object> payment = new HashMap<String, Object>();
            String prepayType = getValue(paymentEntity, "PrepayType");
            payment.put("PrepayType", prepayType);
            payment.put("Amount", getValue(paymentEntity, "Amount"));
            payment.put("RefNo", getValue(paymentEntity, "RefNo"));

            String cardInfoId = getValue(paymentEntity, "CardInfoID");
            payment.put("CardInfoID", cardInfoId);

            List<Map<String, Object>> cardInfoList = new ArrayList<Map<String, Object>>();

            Map<String, Object> cardInfo = new HashMap<String, Object>();

            if (prepayType.toUpperCase().equals("CCARD") || prepayType.toUpperCase().equals("DCARD")) {
                cardInfo.put("CardInfoID", cardInfoId);
                cardInfo.put("InfoID", 0);
                if (!Strings.isNullOrEmpty(cardInfoId)) {
                    Map cardInfoResult = esbClient.getCardInfo(cardInfoId);//从esb取出相关数据
                    if (cardInfoResult != null && cardInfoResult.size() > 0) {
                        String creditCardType = getValue(cardInfoResult, "CreditCardType");
                        String cardBin = getValue(cardInfoResult, "CardBin");
                        cardInfo.put("BillingAddress", getValue(cardInfoResult, "BillingAddress"));
                        cardInfo.put("CardBin", cardBin);
                        cardInfo.put("CardHolder", getValue(cardInfoResult, "CardHolder"));
                        cardInfo.put("CCardLastNoCode", getValue(cardInfoResult, "CardRiskNoLastCode"));
                        cardInfo.put("CCardNoCode", getValue(cardInfoResult, "CCardNoCode"));
                        cardInfo.put("CardNoRefID", getValue(cardInfoResult, "CardNoRefID"));
                        cardInfo.put("CCardPreNoCode", getValue(cardInfoResult, "CardRiskNoPreCode"));
                        cardInfo.put("CreditCardType", creditCardType);
                        cardInfo.put("CValidityCode", getValue(cardInfoResult, "CValidityCode"));
                        cardInfo.put("IsForigenCard", getValue(cardInfoResult, "IsForeignCard"));
                        cardInfo.put("Nationality", getValue(cardInfoResult, "Nationality"));
                        cardInfo.put("Nationalityofisuue", getValue(cardInfoResult, "Nationalityofisuue"));
                        cardInfo.put("BankOfCardIssue", getValue(cardInfoResult, "BankOfCardIssue"));
                        cardInfo.put("StateName", getValue(cardInfoResult, "StateName"));
                        cardInfo.put("CardNoRefID", getValue(cardInfoResult, "CardNoRefID"));
                        //取出branchCity 和 branchProvince
                        String creditCardNumber = getValue(cardInfoResult, "CreditCardNumber");
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
                                        cardInfo.put("BranchCity", getValue(cardBankInfo, "BranchCity"));
                                        cardInfo.put("BranchProvince", getValue(cardBankInfo, "BranchProvince"));
                                    }
                                }
                            }
                            Map subCardInfo = checkRiskDAO.getForeignCardInfo(creditCardType, cardBin);
                            if (subCardInfo != null && subCardInfo.size() > 0) {
                                cardInfo.put("CardBinIssue", getValue(subCardInfo, "Nationality"));
                                cardInfo.put("CardBinBankOfCardIssue", getValue(subCardInfo, "BankOfCardIssue"));
                            }
                        }
                    }
                }
                cardInfoList.add(cardInfo);
            }
            paymentInfo.setPayment(payment);
            paymentInfo.setCardInfoList(cardInfoList);
            po.paymentInfoList.add(paymentInfo);
        }
    }


    //通过uid补充用户信息
    protected String fillUserInfo(RequestBody requestBody, PO po, String uid) {
        String signupDate="";
        po.userInfo = new HashMap<String, Object>();
        po.userInfo.put("Uid", uid);
        po.userInfo.put("CusCharacter", "NEW");
        try {
            Map crmInfo = esbClient.getMemberInfo(uid);
            if (crmInfo != null) {
                po.userInfo.put("RelatedEMail", getValue(crmInfo, "Email"));
                po.userInfo.put("RelatedMobilephone", getValue(crmInfo, "MobilePhone"));
                po.userInfo.put("BindedEmail", getValue(crmInfo, "BindedEmail"));
                po.userInfo.put("BindedMobilePhone", getValue(crmInfo, "BindedMobilePhone"));
                String experience = getValue(crmInfo, "Experience");
                if (experience.isEmpty()) {
                    experience = "0";
                }
                po.userInfo.put("Experience", experience);
                signupDate = getValue(crmInfo, "Signupdate");
                po.userInfo.put("SignUpDate",signupDate);

                po.userInfo.put("UserPassword", getValue(crmInfo, "MD5Password"));
                po.userInfo.put("VipGrade", getValue(crmInfo, "VipGrade"));
                if (!"T".equals(getValue(crmInfo, "Vip"))) {
                    Map customer = esbClient.getCustomerInfo(uid);
                    if (customer != null && "1900-01-01".compareTo(getValue(customer, "CustomerDate")) > 0) {
                        po.userInfo.put("CusCharacter", "REPEAT");
                    }
                } else {
                    po.userInfo.put("CusCharacter", "VIP");
                }
            }
        } catch (Exception e) {
            logger.warn("查询用户" + uid + "的userInfo的信息异常" + e.getMessage());
        }
        return signupDate;
    }

    public void fillOtherInfo(PO po, String orderDate, String signupDate,String takeOffTime) {
        po.otherInfo = new HashMap<String, Object>();
        po.otherInfo.put("OrderToSignUpDate",dateDiffHour(orderDate,signupDate));
        po.otherInfo.put("TakeOffToOrderDate",dateDiffHour(takeOffTime,orderDate));
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

    protected void fillContactInfo(RequestBody requestBody, PO po) {
        po.contactInfo = new HashMap<String, Object>();
        fillEntity(requestBody, po.contactInfo, "InfoSecurity_ContactInfo");
        Map city = checkRiskDAO.getMobileCityAndProv(requestBody.getString("MobilePhone"));
        if (city != null) {
            po.contactInfo.put("MobilePhoneCity", city.get("CityName"));
            po.contactInfo.put("MobilePhoneProvince", city.get("ProvinceName"));
        }
    }

    protected void fillIPInfo(Map<String, Object> ipInfo, String userIP) {
        ipInfo.put("UserIPAdd", userIP);
        long ipValue = ipConvertToValue(userIP);
        ipInfo.put("UserIPValue", ipValue);
        Map ip = checkRiskDAO.getIpCountryCity(ipValue);
        if (ip != null) {
            ipInfo.put("Continent", ip.get("ContinentID"));
            ipInfo.put("IPCity", ip.get("CityId"));
            ipInfo.put("IPCountry", ip.get("NationCode"));
        }
    }
}
