package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.translate.common.IpConvert;
import com.ctrip.infosec.flowtable4j.translate.dao.*;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.*;

import static com.ctrip.infosec.flowtable4j.translate.common.MyDateUtil.getDateAbs;
import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;

/**
 * Created by lpxie on 15-5-7.
 */
public class CommonOperation
{
    private Logger logger = LoggerFactory.getLogger(CommonOperation.class);

    @Autowired
    CommonSources commonSources;
    @Autowired
    RedisSources redisSources;
    @Autowired
    ESBSources esbSources;
    @Autowired
    DataProxySources dataProxySources;

    /**
     * 添加手机对应的省市信息
     */
    public void fillMobilePhone(DataFact dataFact,Map data)
    {
        logger.info(data.get("OrderID")+"获取手机相关信息");
        String mobilePhone = getValue(data,Common.MobilePhone);
        if(mobilePhone == null || mobilePhone.length() <= 6)
            return;
        Map mobileInfo = commonSources.getCityAndProv(mobilePhone);
        if(mobileInfo != null && mobileInfo.size()>0)
            dataFact.contactInfo.putAll(mobileInfo);
    }

    /**
     * 添加用户的用户等级信息
     * @param data
     */
    public void fillUserCusCharacter(DataFact dataFact,Map data)//fixme  这里的获取用户等级信息的代码有点问题
    {
        logger.info(data.get("OrderID")+"获取用户等级相关信息");
        String uid = getValue(data,Common.Uid);
        String serviceName = "UserProfileService";
        String operationName = "DataQuery";
        List tagContents = new ArrayList();
        tagContents.add("CUSCHARACTER");
        Map params = new HashMap();
        params.put("uid",uid);
        params.put("tagNames",tagContents);

        Map uidInfo = dataProxySources.queryForMap(serviceName, operationName, params);

        String CusCharacter = getValue(uidInfo,"CUSCHARACTER");
        dataFact.userInfo.put(Common.CusCharacter, CusCharacter);
    }

    /**
     * 补充ip对应的城市信息
     * @param data
     */
    public void fillIpInfo(DataFact dataFact,Map data)
    {
        logger.info(data.get("OrderID")+"获取ip相关信息");
        String userIp = getValue(data,Common.UserIP);
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

    public void getDIDInfo(DataFact dataFact,Map data)
    {
        String orderId = getValue(data,Common.OrderID);
        String orderType = getValue(data,Common.OrderType);
        Map DIDInfo = commonSources.getDIDInfo(orderId,orderType);
        if(DIDInfo !=null && DIDInfo.size()>0)
            dataFact.DIDInfo.put(Common.DID,getValue(DIDInfo,"Did"));
    }

    public long getLastReqID(Map data)
    {
        logger.info(data.get("OrderID")+"获取lastReqID");
        String orderId = getValue(data,Common.OrderID);
        String orderType = getValue(data,Common.OrderType);
        Map mainInfo = commonSources.getMainInfo(orderType, orderId);
        if(mainInfo!=null)
        {
            try{
                long reqId = Long.parseLong(mainInfo.get(Common.ReqID).toString());
                return reqId;
            }catch (Exception exp)
            {
                logger.warn("getLastReqID获取lastReqID异常:",exp);
                return 0;
            }
        }
        else
            return 0;
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
            if(prepayType.equals("CCARD") || prepayType.equals("DCARD"))
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
                    cardInfo.putAll(cardInfoResult);
                }
                //通过卡种和卡BIN获取系统中维护的信用卡信息
                String cardTypeId = getValue(cardInfoResult,Common.CreditCardType);
                String cardBin = getValue(cardInfoResult,Common.CardBin);
                Map subCardInfo = commonSources.getCardInfo(cardTypeId,cardBin);
                if(subCardInfo != null && subCardInfo.size()>0)
                {
                    cardInfo.putAll(subCardInfo);
                }
                CardInfoList.add(cardInfo);
            }
            subPaymentInfoList.put(Common.PaymentInfo,PaymentInfo);
            subPaymentInfoList.put(Common.CardInfoList,CardInfoList);
            dataFact.paymentInfoList.add(subPaymentInfoList);
        }
    }

    //同上解释
    public void fillPaymentInfo1(DataFact dataFact,Map data)//reqId :7186418
    {
        logger.info(data.get("OrderID")+"通过LastReqID获取支付信息");
        long lastReqID = Long.parseLong(getValue(data, Common.ReqID));
        List<Map<String, Object>> paymentInfos = commonSources.getListPaymentInfo(lastReqID);
        for(Map payment : paymentInfos)
        {
            Map subPayInfo = new HashMap();
            subPayInfo.put(Common.PaymentInfo,payment);
            String paymentInfoId = getValue(payment,"PaymentInfoID");
            subPayInfo.put(Common.CardInfoList, commonSources.getListCardInfo(paymentInfoId));
            dataFact.paymentInfoList.add(subPayInfo);
        }
        Map paymentMainInfo = commonSources.getPaymentMainInfo(lastReqID);
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

    //补充产品信息
    public void fillProductInfo(Map data,long lastReqID)
    {
        if(lastReqID>0)
        {
            Map contactInfo = commonSources.getContactInfo(lastReqID);
            Map userInfo = commonSources.getContactInfo(lastReqID);
            Map ipInfo = commonSources.getContactInfo(lastReqID);
            Map otherInfo = commonSources.getContactInfo(lastReqID);
            if(contactInfo!=null)
                data.putAll(contactInfo);
            if(userInfo!=null)
                data.putAll(userInfo);
            if(ipInfo!=null)
                data.putAll(ipInfo);
            if(otherInfo!=null)
                data.putAll(otherInfo);
        }
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
}
