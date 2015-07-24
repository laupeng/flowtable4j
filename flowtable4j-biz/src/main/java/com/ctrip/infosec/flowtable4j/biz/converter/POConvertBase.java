package com.ctrip.infosec.flowtable4j.biz.converter;

import com.ctrip.infosec.flowtable4j.biz.ConverterBase;
import com.ctrip.infosec.flowtable4j.biz.processor.Crypto;
import com.ctrip.infosec.flowtable4j.model.CtripOrderType;
import com.ctrip.infosec.flowtable4j.model.RequestBody;
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
    private static List<String> foreignCardType = Arrays.asList(new String[]{"6", "7", "8", "9", "10"});

    public List<String> getPaymentModule(int orderType){

        if(CtripOrderType.Flights.getCode()==orderType){
            return  Arrays.asList(new String[]{"getcardinfo","getforeigncardinfo","getidprovince","getcardbankinfo","paymentineventbody"});
        }
        if(CtripOrderType.TTD.getCode()==orderType){
            return  Arrays.asList(new String[]{"getcardinfo","getforeigncardinfo","getidprovince","getcardbankinfo"});
        }
        return  Arrays.asList(new String[]{"getcardinfo","getforeigncardinfo","getcardbankinfo"});
    }
    /**
     * 检查 MobilePhone、UserIP,Email,ContactTel的合法性
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
            if (invalidIP.contains(userIp)) {
                userIp = "";
            }
        }
        setValue(requestBody.getEventBody(), "userip", userIp);

        String email = getString(requestBody.getEventBody(), "contactemail");
        //非法Email
        if (!Strings.isNullOrEmpty(email)) {
            if (invalidEmail.contains(email)) {
                email = "";
            }
        }
        setValue(requestBody.getEventBody(), "contactemail", email);

        String contacttel = getString(requestBody.getEventBody(), "contacttel");
        //非法Email
        if (!Strings.isNullOrEmpty(contacttel)) {
            if (invalidTel.contains(contacttel)) {
                contacttel = "";
            }
        }
        setValue(requestBody.getEventBody(), "contacttel", contacttel);


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
            Map<String, Object> didInfo = new HashMap<String, Object>();
            setValue(didInfo, "did", getString(DIDInfo, "did"));
            setValue(target, "didinfo", didInfo);
        }
    }


    /**
     * 填充 支付信息
     *
     * @param eventBody
     * @param paymentinfo
     */
    public void fillPaymentInfo(Map<String, Object> eventBody, Map<String, Object> paymentinfo, int orderType) {

        List<Map<String, Object>> paymentInfoList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> paymentInfoListSrc =  getList(eventBody, "paymentinfos");
        List<String> modules = getPaymentModule(orderType);
        if (paymentInfoListSrc == null || paymentInfoListSrc.size() == 0) {
            if (modules.contains("paymentineventbody")) {
                 findPaymentInfo(paymentInfoList, eventBody, modules);
            }
        } else {
            for (Map<String, Object> paymentSrc : paymentInfoListSrc) {
                 findPaymentInfo(paymentInfoList, paymentSrc, modules);
            }
        }
        setValue(paymentinfo, "paymentinfolist", paymentInfoList);
    }

    private void findPaymentInfo(List<Map<String, Object>> paymentInfoList, Map<String, Object> paymentSrc, List<String> hints) {
        Map<String, Object> paymentInfo = new HashMap<String, Object>();
        Map<String, Object> payment = new HashMap<String, Object>();
        //获取PaymentInfo信息
        copyMap(paymentSrc, payment, "infosecurity_paymentinfo");
        setValue(paymentInfo, "payment", payment);

        String prepayType = getString(payment,"prepaytype","").toUpperCase();

        String isForigin = null;

        List<Map<String, Object>> cardInfoList = new ArrayList<Map<String, Object>>();
        setValue(paymentInfo, "cardinfolist", cardInfoList);

        if("CCARD".equals(prepayType)||"DCARD".equals(prepayType)||"DQPAY".equals(prepayType)) {
            Map<String, Object> cardInfo = new HashMap<String, Object>();
            copyMap(paymentSrc,cardInfo, "infosecurity_cardinfo");
            String cardInfoId = getString(paymentSrc, new String[]{"creditcardinfo", "cardinfoid"});
            Long cardId = 0L;
            if (StringUtils.isNumeric(cardInfoId)) {
                cardId = Long.parseLong(cardInfoId);
            }
            //调用接口获取信用卡信息
            if (hints != null && hints.contains("getcardinfo") && cardId > 0) {
                Map<String, Object> cardInfoResult = (Map<String, Object>) esbClient.getCardInfo(cardInfoId);//从esb取出相关数据
                if (cardInfoResult != null && cardInfoResult.size() > 0) {
                    copyMap(cardInfoResult, cardInfo, "infosecurity_cardinfo");
                    copyValue(cardInfoResult,"isforeigncard",cardInfo,"isforigencard");
                    copyValueIfNotNull(cardInfoResult, "cardrisknolastcode", cardInfo, "ccardlastnocode");
                    copyValueIfNotNull(cardInfoResult, "cardrisknoprecode", cardInfo, "ccardprenocode");

                    setValue(cardInfo, "infoid", 0);
                    //中国公民，取省份
                    isForigin = getString(cardInfoResult, "isforigencard");
                    if (hints.contains("getidprovince") && "1".equals(getString(cardInfoResult, "idcardtype")) && "F".equals(isForigin)) {
                        Map<String, Object> id = checkRiskDAO.getIDCardProvince(getString(cardInfoResult, "idnumber"));
                        if (id != null && id.size() > 0) {
                            setValue(cardInfo, "idnumberprovince", getString(id, "provincename"));
                            setValue(cardInfo, "idnumbercity", getString(id, "cityname"));
                        }
                    }
                }
            }
            //如果是外卡，获取卡发行组织、银行
            if (hints != null && hints.contains("getforeigncardinfo")) {
                String creditCardType = getString(cardInfo, "creditcardtype", "");
                String cardBin = getString(cardInfo, "cardbin");
                if ("T".equals(isForigin) || foreignCardType.contains(creditCardType)) {
                    Map<String, Object> subCardInfo = checkRiskDAO.getForeignCardInfo(creditCardType, cardBin);
                    if (subCardInfo != null && subCardInfo.size() > 0) {
                        setValue(cardInfo, "cardbinissue", getString(subCardInfo, "nationality"));
                        setValue(cardInfo, "cardbinbankofcardissue", getString(subCardInfo, "bankofcardissue"));
                    }
                }
            }
            //获取发卡银行城市、省份信息
            if (hints != null && hints.contains("getcardbankinfo")) {
                String creditCardType = getString(cardInfo, "creditcardtype", "0");
                //取出branchCity 和 branchProvince
                String creditCardNumber = getString(cardInfo, "creditcardnumber");
                if ("3".equals(creditCardType) && !Strings.isNullOrEmpty(creditCardNumber))//这里只针对类型为3的卡进行处理
                {
                    String decryptText = null;
                    try {
                        decryptText = Crypto.decrypt(creditCardNumber);
                    } catch (Exception exp) {
                        logger.warn("解密卡号异常" + exp.getMessage());
                    }
                    if (!Strings.isNullOrEmpty(decryptText) && decryptText.length() > 12) {
                        String branchNo = decryptText.substring(6,9);
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
            cardInfoList.add(cardInfo);
        }
        paymentInfoList.add(paymentInfo);
    }


    /**
     * 填充用户信息
     *
     * @param eventBody
     * @param target
     * @param uid
     * @return
     */
    protected void fillUserInfo(Map<String, Object> eventBody, Map<String, Object> target, String uid,int orderType) {
        Map<String, Object> userInfo = new HashMap<String, Object>();

        //机票会抛用户信息,先接收信息
        if(CtripOrderType.Flights.getCode()== orderType) {
            copyMap(eventBody, userInfo, "infosecurity_userinfo");
        }
        setValue(userInfo,"uid",uid);
        try {
            Map<String, Object> crmInfo = esbClient.getMemberInfo(uid);
            if (crmInfo != null) {
                copyMap(crmInfo,userInfo,"infosecurity_userinfo");
                setValue(userInfo, "cuscharacter", "NEW");
                setValue(userInfo, "relatedemail", getString(crmInfo, "email"));
                setValue(userInfo, "relatedmobilephone", getString(crmInfo, "mobilephone"));
                setValue(userInfo, "userpassword", getString(crmInfo, "md5password"));
                String signupDate = getString(userInfo,"signupdate");
                if(!Strings.isNullOrEmpty(signupDate)){
                    setValue(userInfo,"signupdate",signupDate.replace("T"," ")+".000");
                }
                if ("T".equals(getString(crmInfo, "vip"))) {
                    setValue(userInfo, "cuscharacter", "VIP");
                } else {
                    Map<String,Object> customer = esbClient.getCustomerInfo(uid);
                    if (customer != null && "1900-01-01".compareTo(getString(customer, "customerdate")) < 0) {
                        setValue(userInfo, "cuscharacter", "REPEAT");
                    }
                }
                fillMobileProvince(userInfo,getString(userInfo,"bindedmobilephone"),getString(userInfo,"relatedmobilephone"));
            }
        } catch (Exception e) {
            logger.warn("查询用户" + uid + "的userInfo的信息异常" + e.getMessage());
        }
        setValue(target, "userinfo", userInfo);
    }

    private void fillMobileProvince(Map<String, Object> target, String bindedMobilePhone, String relatedMobilephone) {
        Map<String,Object> map = checkRiskDAO.getMobileCityAndProv(bindedMobilePhone);
        if(map!=null){
            setValue(target,"bindedmobilephonecity",getString(map,"cityname"));
            setValue(target,"bindedmobilephoneprovince",getString(map,"provincename"));
        }
        if(!StringUtils.equals(bindedMobilePhone,relatedMobilephone)) {
            map = checkRiskDAO.getMobileCityAndProv(relatedMobilephone);
        }
        if(map!=null){
            setValue(target,"relatedmobilephonecity",getString(map,"cityname"));
            setValue(target,"relatedmobilephoneprovince",getString(map,"provincename"));
        }
    }

    /**
     * 填充OtherInfo
     */
    protected void fillOtherInfo(PO po,Map<String, Object> eventBody) {
        Map<String, Object> otherInfo = new HashMap<String, Object>();
        copyMap(eventBody,otherInfo,"infosecurity_otherinfo");

        String orderDate = getString(po.getProductinfo(),new String[]{"maininfo","orderdate"});
        String signupDate = getString(po.getProductinfo(),new String[]{"userinfo","signupdate"});

        setValue(otherInfo, "ordertosignupdate",dateDiffHour(orderDate, signupDate));
        setValue(otherInfo, "takeofftoorderdate",0);

        setValue(po.getProductinfo(), "otherinfo", otherInfo);
    }

    /**
     * 填充联系信息
     *
     * @param eventBody
     * @param root
     */
    protected void fillContactInfo(Map<String, Object> eventBody, Map<String, Object> root) {
        Map<String, Object> contactInfo = new HashMap<String, Object>();
        copyMap(eventBody, contactInfo, "infosecurity_contactinfo");
        Map<String, Object> city = checkRiskDAO.getMobileCityAndProv(getString(contactInfo, "mobilephone"));
        if (city != null) {
            setValue(contactInfo, "mobilephonecity", getString(city, "cityname"));
            setValue(contactInfo, "mobilephoneprovince", getString(city, "provincename"));
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
        Map<String, Object> ipInfo = new HashMap<String, Object>();
        setValue(ipInfo, "useripadd", userIP);
        long ipValue = ipConvertToValue(userIP);
        setValue(ipInfo, "useripvalue", ipValue);
        Map<String, Object> ip = checkRiskDAO.getIpCountryCity(ipValue);
        if (ip != null) {
            setValue(ipInfo, "continent", getObject(ip, "continentid"));
            setValue(ipInfo, "ipcity", getObject(ip, "cityid"));
            setValue(ipInfo, "ipcountry", getObject(ip, "countrycode"));
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
        List<Map<String, Object>> paymentInfoList =  getList(paymentInfos, "paymentinfolist");
        String prePay = "";
        if (paymentInfoList != null) {
            for (Map<String, Object> p : paymentInfoList) {
                String tmpprePay =  getString(p, new String[]{"payment", "prepaytype"},"").toUpperCase();
                if (tmpprePay.equals("CCARD") || tmpprePay.equals("DCARD")) {
                    prePay = tmpprePay;
                    break;
                }
            }
        }
        return prePay;
    }

}
