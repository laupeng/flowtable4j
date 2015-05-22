package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.translate.common.IpConvert;
import com.ctrip.infosec.flowtable4j.translate.dao.*;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.time.DateUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ctrip.infosec.flowtable4j.translate.common.MyDateUtil.getDateAbs;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;

/**
 * Created by lpxie on 15-5-7.
 */
//@Component
public class CommonOperation
{
    private Logger logger = LoggerFactory.getLogger(CommonOperation.class);

    @Autowired
    CommonSources commonSources;
    @Autowired
    CommonWriteSources commonWriteSources;
    @Autowired
    RedisSources redisSources;
    @Autowired
    ESBSources esbSources;
    @Autowired
    DataProxySources dataProxySources;

    /**
     * 添加手机对应的省市信息
     */
    public void fillMobilePhone(DataFact dataFact,String mobilePhone)
    {
        if(mobilePhone == null || mobilePhone.length() <= 6)
            return;
        Map mobileInfo = commonSources.getCityAndProv(mobilePhone);
        if(mobileInfo != null && mobileInfo.size()>0)
        {
            dataFact.contactInfo.put(Common.MobilePhoneCity,getValue(mobileInfo,"CityName"));
            dataFact.contactInfo.put(Common.MobilePhoneProvince,getValue(mobileInfo,"ProvinceName"));
        }
    }

    /**
     * 添加用户的用户等级信息
     * @param uid
     */
    public void fillUserCusCharacter(DataFact dataFact,String uid,String vip)//fixme  这里的获取用户等级信息的代码有点问题
    {
        String cuscharacter = "";
        String contentType = "Customer.User.GetCustomerInfo";
        String contentBody = "<GetCustomerInfoRequest><UID>" + uid + "</UID></GetCustomerInfoRequest>";
        String xpath = "/Response/GetCustomerInfoResponse";
        Map customerInfo = null;
        try
        {
            customerInfo = esbSources.getResponse(contentBody,contentType,xpath);
        } catch (DocumentException e)
        {
            logger.warn("查询用户"+uid+"的Customer的信息异常"+e.getMessage());
        }
        String FirstPkgOrderDate = customerInfo.get("FirstPkgOrderDate") == null ? "" : customerInfo.get("FirstPkgOrderDate").toString();
        String FirstHotelOrderDate = customerInfo.get("FirstHotelOrderDate") == null ? "" : customerInfo.get("FirstHotelOrderDate").toString();
        String FirstFlightOrderDate = customerInfo.get("FirstFlightOrderDate") == null ? "" : customerInfo.get("FirstFlightOrderDate").toString();
        if(FirstPkgOrderDate.equals("0001-01-01T00:00:00") && FirstHotelOrderDate.equals("0001-01-01T00:00:00") && FirstFlightOrderDate.equals("0001-01-01T00:00:00"))
        {
            cuscharacter = "NEW";
        }else
        {
            if(vip.equals("T"))
            {
                cuscharacter = "VIP";
            }else if(vip.equals("F"))
            {
                cuscharacter = "REPEAT";
            }
        }

        String serviceName = "UserProfileService";
        String operationName = "DataQuery";
        List tagContents = new ArrayList();
        tagContents.add("CUSCHARACTER");
        Map params = new HashMap();
        params.put("uid",uid);
        params.put("tagNames",tagContents);

        dataFact.userInfo.put(Common.CusCharacter, cuscharacter);
    }

    /**
     * 补充ip对应的城市信息
     * @param userIp
     */
    public void fillIpInfo(DataFact dataFact,String userIp)
    {
        dataFact.ipInfo.put(Common.UserIPAdd, userIp);
        Long userIPValue = IpConvert.ipConvertTo10(userIp);
        dataFact.ipInfo.put(Common.UserIPValue,userIPValue);

        Map ipInfo = commonSources.getIpCountryCity(userIPValue);
        if(ipInfo != null && ipInfo.size()>0)
        {
            String ContinentID = getValue(ipInfo,"ContinentID");
            String CityId = getValue(ipInfo,"CityId");
            String NationCode = getValue(ipInfo,"NationCode");
            dataFact.ipInfo.put(Common.Continent,ContinentID);
            dataFact.ipInfo.put(Common.IPCity,CityId);
            dataFact.ipInfo.put(Common.IPCountry,NationCode);
        }
    }

    public void getDIDInfo(DataFact dataFact,String orderId,String orderType)
    {
        Map DIDInfo = commonSources.getDIDInfo(orderId,orderType);
        if(DIDInfo !=null && DIDInfo.size()>0)
            dataFact.DIDInfo.put(Common.DID,getValue(DIDInfo,"Did"));
    }

    public void getLastReqID(Map data)
    {
        if(data.containsKey(Common.ReqID))
            return;
        String orderId = getValue(data,Common.OrderID);
        String orderType = getValue(data,Common.OrderType);
        Map mainInfo = commonSources.getMainInfo(orderType, orderId);
        if(mainInfo!=null)
        {
            try{
                long reqId = Long.parseLong(getValue(mainInfo, Common.ReqID));
                data.put(Common.ReqID,reqId);
            }catch (Exception exp)
            {
                logger.warn("getLastReqID获取lastReqID异常:",exp);
            }
        }
    }

    /**
     * 这个方法其实是把原来的标签PaymentInfos改成PaymentInfoList
     * 原来的结构：
     *    PaymentInfos
     *    PaymentInfo:PrepayType(String),...;CreditCardInfo(Map)
     *新的结构：
     *      PaymentInfoList
     * PaymentInfo(Map);CardInfoList(List):cardInfo(Map)
     * @param data
     */
    public void fillPaymentInfo0(DataFact dataFact,Map data)
    {
        List<Map> paymentInfos = (List<Map>)data.get(Common.PaymentInfos);
        if(paymentInfos == null || paymentInfos.size()<1)
            return;

        for(Map payment : paymentInfos)
        {
            Map<String,Object> subPaymentInfoList = new HashMap<String, Object>();

            Map<String,Object> PaymentInfo = new HashMap();
            List<Map> CardInfoList = new ArrayList<Map>();

            Map<String,Object> cardInfo = new HashMap<String, Object>();

            String prepayType = getValue(payment,Common.PrepayType);
            PaymentInfo.put(Common.PrepayType, prepayType);
            PaymentInfo.put(Common.Amount,getValue(payment, Common.Amount));
            if(prepayType.toUpperCase().equals("CCARD") || prepayType.toUpperCase().equals("DCARD"))
            {
                cardInfo.put(Common.CardInfoID,getValue(payment, Common.CardInfoID));
                cardInfo.put(Common.InfoID,"0");

                ///从wsdl里面获取卡信息
                String cardInfoId = getValue(payment,Common.CardInfoID);
                if(cardInfoId.isEmpty())
                    continue;
                Map cardInfoResult = getCardInfo(cardInfoId);
                if(cardInfoResult != null && cardInfoResult.size()>0)
                {
                    cardInfo.put(Common.BillingAddress,getValue(cardInfoResult,Common.BillingAddress));
                    cardInfo.put(Common.CardBin,getValue(cardInfoResult,Common.CardBin));
                    cardInfo.put(Common.CardHolder,getValue(cardInfoResult,Common.CardHolder));
                    cardInfo.put(Common.CCardLastNoCode,getValue(cardInfoResult,Common.CCardLastNoCode));

                    cardInfo.put(Common.CCardNoCode,getValue(cardInfoResult,Common.CCardNoCode));
                    cardInfo.put(Common.CCardPreNoCode,getValue(cardInfoResult,Common.CCardPreNoCode));
                    cardInfo.put(Common.CreditCardType,getValue(cardInfoResult,Common.CreditCardType));
                    cardInfo.put(Common.CValidityCode,getValue(cardInfoResult,Common.CValidityCode));
                    cardInfo.put(Common.IsForigenCard,getValue(cardInfoResult,Common.IsForeignCard));
                    cardInfo.put(Common.Nationality,getValue(cardInfoResult,Common.Nationality));

                    cardInfo.put(Common.Nationalityofisuue,getValue(cardInfoResult,Common.Nationalityofisuue));
                    cardInfo.put(Common.BankOfCardIssue,getValue(cardInfoResult,Common.BankOfCardIssue));
                    cardInfo.put(Common.StateName,getValue(cardInfoResult,Common.StateName));
                }
                //通过卡种和卡BIN获取系统中维护的信用卡信息
                String cardTypeId = getValue(cardInfoResult,Common.CreditCardType);
                String cardBin = getValue(cardInfoResult,Common.CardBin);
                Map subCardInfo = commonSources.getCardInfo(cardTypeId,cardBin);
                if(subCardInfo != null && subCardInfo.size()>0)
                {
                    cardInfo.put(Common.CardBinIssue,getValue(subCardInfo,Common.CardBinIssue));
                    cardInfo.put(Common.CardBinBankOfCardIssue,getValue(subCardInfo,Common.CardBinBankOfCardIssue));
                }
                CardInfoList.add(cardInfo);
            }
            subPaymentInfoList.put(Common.PaymentInfo,PaymentInfo);
            subPaymentInfoList.put(Common.CardInfoList,CardInfoList);
            dataFact.paymentInfoList.add(subPaymentInfoList);
        }
    }

    //同上解释
    public void fillPaymentInfo1(DataFact dataFact,String lastReqID)//reqId :7186418
    {
        List<Map<String, Object>> paymentInfos = commonSources.getListPaymentInfo(lastReqID);
        if(paymentInfos == null || paymentInfos.size()<1)
            return;
        for(Map payment : paymentInfos)
        {
            Map subPayInfo = new HashMap();
            subPayInfo.put(Common.PaymentInfo,payment);
            String paymentInfoId = getValue(payment,"PaymentInfoID");
            subPayInfo.put(Common.CardInfoList, commonSources.getListCardInfo(paymentInfoId));
            dataFact.paymentInfoList.add(subPayInfo);
        }
    }

    public void fillPaymentMainInfo(DataFact dataFact,String lastReq)
    {
        Map paymentMainInfo = commonSources.getPaymentMainInfo(lastReq);
        if(paymentMainInfo != null && paymentMainInfo.size()>0)
            dataFact.paymentMainInfo.putAll(paymentMainInfo);
    }

    public Map getCardInfo(String cardInfoId)
    {
        //int cardInfoId = PaymentInfo.get(Common.CardInfoID)==null ? 0 : Integer.parseInt(PaymentInfo.get(Common.CardInfoID).toString());
        //从esb获取数据 根据CardInfoID取出卡的信息
        String requestType = "AccCash.CreditCard.GetCreditCardInfo";
        String xpath = "/Response/GetCreditCardInfoResponse/CreditCardItems/CreditCardInfoResponseItem";
        StringBuffer requestXml = new StringBuffer();
        requestXml.append("<GetCreditCardInfoRequest>");
        requestXml.append("<CardInfoId>");
        requestXml.append(cardInfoId+"");
        requestXml.append("</CardInfoId>");
        requestXml.append("</GetCreditCardInfoRequest>");
        try
        {
            Map cardInfo = esbSources.getResponse(requestXml.toString(),requestType,xpath);
            return cardInfo;
        }catch (Exception exp)
        {
            return null;
        }
    }

    //补充产品信息  fixme 这里是要并发的
    public void fillProductContact(DataFact dataFact,String lastReqID)
    {
        Map contactInfo = commonSources.getContactInfo(lastReqID);
        if(contactInfo!=null)
            dataFact.contactInfo.putAll(contactInfo);
    }
    public void fillProductUser(DataFact dataFact,String lastReqID)
    {
        Map userInfo = commonSources.getUserInfo(lastReqID);
        if(userInfo!=null)
            dataFact.userInfo.putAll(userInfo);
    }
    public void fillProductIp(DataFact dataFact,String lastReqID)
    {
        Map ipInfo = commonSources.getIpInfo(lastReqID);
        if(ipInfo!=null)
            dataFact.ipInfo.putAll(ipInfo);
    }
    public void fillProductOther(DataFact dataFact,String lastReqID)
    {
        Map otherInfo = commonSources.getOtherInfo(lastReqID);
        if(otherInfo!=null)
            dataFact.otherInfo.putAll(otherInfo);
    }

    //补充主要支付方式
    public void fillMainOrderType(Map data)
    {
        String orderPrepayType = getValue(data,Common.OrderPrepayType);
        if(orderPrepayType.isEmpty())
        {
            //如果主要支付方式为空，则用订单号和订单类型到risk_levelData取上次的主要支付方式
            String orderType = getValue(data,Common.OrderType);
            String orderId = getValue(data,Common.OrderID);
            Map payInfo = commonSources.getMainPrepayType(orderType,orderId);
            if(payInfo != null && !payInfo.isEmpty())
            {
                data.put(Common.OrderPrepayType,payInfo.get(Common.PrepayType));
            }
        }

        //补充主要支付方式自动判断逻辑
        if(getValue(data,Common.OrderPrepayType).isEmpty() || getValue(data,Common.CheckType).equals("2"))
        {
            if(data.get(Common.PaymentInfos) == null)//FIXME 这里确认所有的产品支付的字段名称是PaymentInfos
                return;
            List<Map> paymentInfoList = (List<Map>)data.get(Common.PaymentInfos);
            for(Map paymentInfos : paymentInfoList)
            {
                if(paymentInfos.get(Common.PaymentInfo) == null )
                    continue;
                Map payment = (Map)paymentInfos.get(Common.PaymentInfo);
                if(payment == null || payment.get(Common.PrepayType) == null)
                    continue;
                if(payment.get(Common.PrepayType).toString().toUpperCase().equals("CCARD") || payment.get(Common.PrepayType).toString().toUpperCase().equals("DCARD"))
                {
                    data.put(Common.OrderPrepayType,payment.get(Common.PrepayType).toString().toUpperCase());
                    break;
                }else
                {
                    data.put(Common.OrderPrepayType,payment.get(Common.PrepayType).toString().toUpperCase());//这句没看懂？？？//fixme
                }
            }
        }
    }

    //通过uid补充用户信息
    public void fillUserInfo(DataFact dataFact,String uid)
    {
        String serviceName = "CRMService";
        String operationName = "getMemberInfo";
        Map params = ImmutableMap.of("uid", uid);//根据uid取值
        Map crmInfo = DataProxySources.queryForMap(serviceName, operationName, params);
        if(crmInfo !=null && crmInfo.size()>0)
        {
            dataFact.userInfo.put(Common.RelatedEMail,getValue(crmInfo,"email"));
            dataFact.userInfo.put(Common.RelatedMobilephone,getValue(crmInfo,"mobilePhone"));
            dataFact.userInfo.put(Common.BindedEmail,getValue(crmInfo,"bindedEmail"));
            dataFact.userInfo.put(Common.BindedMobilePhone,getValue(crmInfo,"bindedMobilePhone"));
            String experience = getValue(crmInfo, "experience");
            if(experience.isEmpty())
                experience = "0";
            dataFact.userInfo.put(Common.Experience,experience);
            dataFact.userInfo.put(Common.SignUpDate,getValue(crmInfo,"signupdate"));
            dataFact.userInfo.put(Common.UserPassword,getValue(crmInfo,"mD5Password"));
            dataFact.userInfo.put(Common.VipGrade,getValue(crmInfo,"vipGrade"));
            dataFact.userInfo.put("vip",getValue(crmInfo,"vip"));
        }
    }

    //写流量数据到数据库
    public void writeFlowData(final Map flowData)
    {
        final String orderType = getValue(flowData,Common.OrderType);
        if(orderType.isEmpty())
            return;
        List<Map<String,Object>> flowRules = CacheFlowRuleData.getFlowRules();//fixme 这里有bug 每个订单的订单类型是不一样的
        if(flowRules == null || flowRules.size()<1)
        {
            flowRules = commonSources.getFlowRules(orderType);
            CacheFlowRuleData.setFlowRules(flowRules);//添加到缓存中
        }
        List<Map<String,Object>> flowFilters = CacheFlowRuleData.getFlowFilters();
        if(flowFilters == null || flowFilters.size()<1)
        {
            flowFilters = commonSources.getFlowRuleFilter();
            CacheFlowRuleData.setFlowFilters(flowFilters);//添加到缓存中
        }
        String KeyFieldName1 = "", KeyFieldName2 = "", StatisticTableName = "", StatisticTableId = "";
        for(Map flowRule : flowRules)
        {
            StatisticTableId = flowRule.get("StatisticTableId").toString();
            if(isInsertToStaticTable(flowRule,StatisticTableId,flowFilters))
            {
                //写到数据库
                StatisticTableName = flowRule.get("StatisticTableName").toString();
                KeyFieldName1 = flowRule.get("KeyFieldID1").toString();
                KeyFieldName2 = flowRule.get("KeyFieldID2").toString();
                logger.info("写入流量表："+StatisticTableName+"\t"+KeyFieldName1+"\t"+KeyFieldName2);
                commonWriteSources.insertFlowInfo(flowData, KeyFieldName1, KeyFieldName2, StatisticTableName);
            }
        }
    }

    //判断是否需要写流量表数据
    public boolean isInsertToStaticTable(Map flowData,String id,List<Map<String,Object>> flowFilters)
    {
        List<Map<String,Object>> newFlowFilters = new ArrayList<Map<String, Object>>();
        boolean isInsert = true;
        for(Map flowFilter:flowFilters)
        {
            if(flowFilter.get("StatisticTableID").toString().equals(id))
                newFlowFilters.add(flowFilter);
        }
        if(newFlowFilters == null || newFlowFilters.size()<1)
            return false;
        String currentValue = "";
        for(Map flowFilter:newFlowFilters)
        {
            currentValue = getValue(flowFilter,"KeyColumnName");
            String tempMatchValue = "", tempMatchType = "";
            String matchType = getValue(flowFilter,"MatchType");
            if(matchType.toUpperCase().equals("FEQ")||matchType.toUpperCase().equals("FNE")
                    ||matchType.toUpperCase().equals("FIN")||matchType.toUpperCase().equals("FNA"))
            {
                tempMatchType = matchType.substring(1,2);
                tempMatchValue = getValue(flowFilter,"MatchValue");
            }else
            {
                tempMatchType = matchType;
                tempMatchValue = getValue(flowFilter,"MatchValue");
            }
            if(!isMatch(tempMatchType,currentValue,tempMatchValue))
                return false;
        }
        return isInsert;
    }

    //匹配值是否
    public boolean isMatch(String matchType,String currentValue,String matchValue)
    {
        matchValue = convertValueToRegValue(matchType,matchValue);
        if(currentValue.isEmpty() && !matchType.toUpperCase().equals("REGEX"))
        {
            return false;
        }

        if(matchType.toUpperCase().equals("EQ"))
        {
            return currentValue.equalsIgnoreCase(matchValue);
        }else if(matchType.toUpperCase().equals("NE"))
        {
            return !currentValue.equalsIgnoreCase(matchValue);
        }else if(matchType.toUpperCase().equals("GE"))
        {
            try
            {
                long longCurrent = Long.parseLong(currentValue);
                long longMatch = Long.parseLong(matchValue);
                if(longCurrent>longMatch)
                    return true;
                return false;
            }catch (Exception exp)
            {
                return false;
            }
        }else if(matchType.toUpperCase().equals("LE"))
        {
            try
            {
                long longCurrent = Long.parseLong(currentValue);
                long longMatch = Long.parseLong(matchValue);
                if(longCurrent <= longMatch)
                    return true;
                return false;
            }catch (Exception exp)
            {
                return false;
            }
        }else if(matchType.toUpperCase().equals("LESS"))
        {
            try
            {
                long longCurrent = Long.parseLong(currentValue);
                long longMatch = Long.parseLong(matchValue);
                if(longCurrent < longMatch)
                    return true;
                return false;
            }catch (Exception exp)
            {
                return false;
            }
        }
        else
        {
            Pattern pattern = Pattern.compile(matchValue);
            Matcher matcher = pattern.matcher(currentValue);
            if(matchType.toUpperCase().equals("IN")||matchType.toUpperCase().equals("LLIKE")||matchType.toUpperCase().equals("RLIKE"))
            {
                return matcher.find();
            }
            else if(matchType.toUpperCase().equals("NA"))
            {
                return !matcher.find();
            }
        }
        //fixme 明天完善这里的代码
        return true;
    }
    public String convertValueToRegValue(String checkType,String checkValue)
    {
        String regexValue = "";
        if(checkType.toUpperCase().equals("EQ") || checkType.toUpperCase().equals("NE"))
        {
            regexValue = checkValue;
        }else if(checkType.toUpperCase().equals("IN") || checkType.toUpperCase().equals("NA"))
        {
            regexValue = "("+checkValue.toUpperCase()+")+";
        }else if(checkType.toUpperCase().equals("LLIKE"))
        {
            regexValue = "^("+checkValue.toUpperCase()+")";
        }else if(checkType.toUpperCase().equals("RLIKE"))
        {
            regexValue = "("+checkValue.toUpperCase()+")+$";
        }else if(checkType.toUpperCase().equals("REGEX"))
        {
            regexValue = checkValue;
        }else
        {
            regexValue = checkValue;
        }
        return regexValue;
    }
}
