package com.ctrip.infosec.flowtable4j.translate.service;

import com.ctrip.infosec.flowtable4j.translate.common.IpConvert;
import com.ctrip.infosec.flowtable4j.translate.dao.DataProxySources;
import com.ctrip.infosec.flowtable4j.translate.dao.ESBSources;
import com.ctrip.infosec.flowtable4j.translate.dao.HotelGroupSources;
import com.ctrip.infosec.flowtable4j.translate.dao.RedisSources;
import com.ctrip.infosec.flowtable4j.translate.model.Flight;
import com.ctrip.infosec.flowtable4j.translate.model.HotelGroup;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.*;

import static com.ctrip.infosec.flowtable4j.translate.common.MyDateUtil.getDateAbs;

/**
 * Created by lpxie on 15-4-20.
 */
public class HotelGroupOperation
{
    private Logger logger = LoggerFactory.getLogger(HotelGroupOperation.class);

    @Autowired
    HotelGroupSources hotelGroupSources;
    @Autowired
    RedisSources redisSources;
    @Autowired
    ESBSources esbSources;
    @Autowired
    DataProxySources dataProxySources;

    /**
     * 添加手机对应的省市信息
     */
    public void fillMobilePhone(Map data)
    {
        logger.info("酒店团购"+data.get("OrderID")+"获取手机相关信息");
        String mobilePhone = data.get(HotelGroup.MobilePhone) == null ? "" : data.get(HotelGroup.MobilePhone).toString();
        if(mobilePhone == null || mobilePhone.length() <= 6)
            return;

        Map mobileInfo = hotelGroupSources.getCityAndProv(mobilePhone);
        data.putAll(mobileInfo);
    }

    /**
     * 添加用户的用户等级信息
     * @param data
     */
    public void fillUserCusCharacter(Map data)//fixme  这里的获取用户等级信息的代码有点问题
    {
        logger.info("酒店团购"+data.get("OrderID")+"获取用户等级相关信息");
        String uid = data.get(HotelGroup.Uid) == null ? "" : data.get(HotelGroup.Uid).toString();
        String serviceName = "UserProfileService";
        String operationName = "DataQuery";
        List tagContents = new ArrayList();
        tagContents.add("CUSCHARACTER");
        Map params = new HashMap();
        params.put("uid",uid);
        params.put("tagNames",tagContents);

        Map uidInfo = dataProxySources.queryForMap(serviceName, operationName, params);

        String CusCharacter = uidInfo.get("CUSCHARACTER") == null ? "" : uidInfo.get("CUSCHARACTER").toString();
        data.put(HotelGroup.CusCharacter,CusCharacter);
    }

    /**
     * 补充ip对应的城市信息
     * @param data
     */
    public void fillIpInfo(Map data)
    {
        logger.info("酒店团购"+data.get("OrderID")+"获取ip相关信息");
        String userIp = data.get(HotelGroup.UserIP) == null ? "" : data.get(HotelGroup.UserIP).toString();
        data.put(HotelGroup.UserIPAdd,userIp);
        Long userIPValue = IpConvert.ipConvertTo10(userIp);
        data.put(HotelGroup.UserIPValue,userIPValue);
        //
        Map ipInfo = hotelGroupSources.getIpCountryCity(userIPValue);
        if(ipInfo != null && ipInfo.size()>0)
        {
            String ContinentID = ipInfo.get("ContinentID") == null ? "" : ipInfo.get("ContinentID").toString();
            String CityId = ipInfo.get("CityId") == null ? "" : ipInfo.get("CityId").toString();
            String NationCode = ipInfo.get("NationCode") == null ? "" : ipInfo.get("NationCode").toString();
            data.put(HotelGroup.Continent,ContinentID);
            data.put(HotelGroup.IPCity,CityId);
            data.put(HotelGroup.IPCountry,NationCode);
        }
    }

    public void fillPaymentInfo(Map data)
    {
        logger.info("酒店团购"+data.get("OrderID")+"获取支付相关信息");
        if(data.get(HotelGroup.PaymentInfos) == null)
            return;
        List<Map> paymentInfo = (List<Map>)data.get(HotelGroup.PaymentInfos);
        if(paymentInfo.size()<1)
            return;

        for(Map payment : paymentInfo)
        {
            //这里层级关系是PaymentInfoList PaymentInfo CardInfoList cardInfo
            //data.put(HotelGroup.P)  //Fixme 层级关系等确认数据格式以后再做

         }
    }

    /**
     * 添加订单日期到注册日期的差值
     * 添加订单日期到起飞日期的差值
     * @param data
     * @throws java.text.ParseException
     */
    public void getTimeAbs(Map data) throws ParseException
    {
        logger.info("酒店团购"+data.get("OrderID")+"获取时间的差值相关信息");
        //订单日期
        String orderDateStr = data.get(HotelGroup.OrderDate) == null ? "": data.get(HotelGroup.OrderDate).toString();
        Date orderDate = DateUtils.parseDate(orderDateStr, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS");//yyyy-MM-dd HH:mm:ss   yyyy-MM-dd HH:mm:ss.SSS
        //注册日期
        String signUpDateStr = data.get(HotelGroup.SignUpDate) == null ? "": data.get(HotelGroup.SignUpDate).toString();
        Date signUpDate = DateUtils.parseDate(signUpDateStr,"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.SSS");
        data.put(HotelGroup.OrderToSignUpDate,getDateAbs(signUpDate, orderDate,1));
    }

    public void getDIDInfo(Map data)
    {
        String orderId = data.get(HotelGroup.OrderID) == null ? "" : data.get(HotelGroup.OrderID).toString();
        String orderType = data.get(HotelGroup.OrderType) == null ? "" : data.get(HotelGroup.OrderType).toString();
        Map DIDInfo = hotelGroupSources.getDIDInfo(orderId,orderType);
        if(DIDInfo !=null && DIDInfo.size()>0 &&DIDInfo.get("Did") != null)
            data.put(HotelGroup.DID,DIDInfo.get("Did"));
    }

    public long getLastReqID(Map data)
    {
        logger.info("酒店团购"+data.get("OrderID")+"获取lastReqID");
        String orderId = data.get(HotelGroup.OrderID) == null ? "" : data.get(HotelGroup.OrderID).toString();
        String orderType = data.get(HotelGroup.OrderType) == null ? "" : data.get(HotelGroup.OrderType).toString();
        Map mainInfo = hotelGroupSources.getMainInfo(orderType, orderId);
        if(mainInfo!=null)
            return Long.parseLong(mainInfo.get(HotelGroup.ReqID).toString());
        else
            return -1;
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
    public void fillPaymentInfo0(Map data)//fixme 这里可能有点问题 ，回头改下
    {
        List<Map> paymentInfos = (List<Map>)data.get(HotelGroup.PaymentInfos);
        if(paymentInfos == null || paymentInfos.size()<1)
            return;
        List<Object> PaymentInfoList = new ArrayList<Object>();
        for(Map payment : paymentInfos)
        {
            Map<String,Object> subPaymentInfoList = new HashMap<String, Object>();
            Map<String,Object> PaymentInfo = new HashMap();
            List<Map> CardInfoList = new ArrayList<Map>();
            Map<String,Object> cardInfo = new HashMap<String, Object>();

            String prepayType = payment.get(HotelGroup.PrepayType) == null ? "" : payment.get(HotelGroup.PrepayType).toString();
            PaymentInfo.put(HotelGroup.PrepayType,prepayType);
            PaymentInfo.put(HotelGroup.Amount,payment.get(HotelGroup.Amount));
            if(prepayType.equals("CCARD") || prepayType.equals("DCARD"))
            {
                cardInfo.put(HotelGroup.CardInfoID,payment.get(HotelGroup.CardInfoID));
                cardInfo.put(HotelGroup.InfoID,"0");

                ///从wsdl里面获取卡信息
                String cardInfoId = payment.get(HotelGroup.CardInfoID) == null ? "" : payment.get(HotelGroup.CardInfoID).toString();
                if(cardInfoId.isEmpty())
                    continue;
                Map cardInfoResult = getCardInfo(payment.get(HotelGroup.CardInfoID).toString());
                if(cardInfoResult != null && cardInfoResult.size()>0)
                {
                    cardInfo.putAll(cardInfoResult);
                }
                //通过卡种和卡BIN获取系统中维护的信用卡信息
                String cardTypeId = cardInfoResult.get(HotelGroup.CreditCardType) == null ? "" : cardInfoResult.get(HotelGroup.CreditCardType).toString();
                String cardBin = cardInfoResult.get(HotelGroup.CardBin) == null ? "" : cardInfoResult.get(HotelGroup.CardBin).toString();
                Map subCardInfo = hotelGroupSources.getCardInfo(cardTypeId,cardBin);
                if(subCardInfo != null && subCardInfo.size()>0)
                {
                    cardInfo.put(HotelGroup.CardBinIssue,subCardInfo.get(HotelGroup.Nationality));
                    cardInfo.put(HotelGroup.CardBinBankOfCardIssue,subCardInfo.get(HotelGroup.BankOfCardIssue));
                }
                CardInfoList.add(cardInfo);
            }
            subPaymentInfoList.put(HotelGroup.PaymentInfo,payment);
            subPaymentInfoList.put(HotelGroup.CardInfoList,CardInfoList);
            PaymentInfoList.add(subPaymentInfoList);
        }
        data.put(HotelGroup.PaymentInfoList,PaymentInfoList);//添加支付信息到当前的报文
    }

    //同上解释
    public void fillPaymentInfo1(Map data,long lastReqID)//reqId :7186418
    {
        logger.info("酒店团购"+data.get("OrderID")+"通过LastReqID获取支付信息");
        List<Map> PaymentInfoList = new ArrayList<Map>();
        List<Map<String, Object>> paymentInfos = hotelGroupSources.getListPaymentInfo(lastReqID);
        for(Map payment : paymentInfos)
        {
            Map subPayInfo = new HashMap();
            subPayInfo.put(HotelGroup.PaymentInfo,payment);
            String paymentInfoId = payment.get("PaymentInfoID") == null ? "" : payment.get("PaymentInfoID").toString();//PaymentInfoID数据库存储字段名称
            subPayInfo.put(HotelGroup.CardInfoList, hotelGroupSources.getListCardInfo(paymentInfoId));
            PaymentInfoList.add(subPayInfo);
        }
        data.put(HotelGroup.PaymentInfoList,PaymentInfoList);
        data.put(HotelGroup.PaymentMainInfo,hotelGroupSources.getPaymentMainInfo(lastReqID));
    }

    public Map getCardInfo(String cardInfoId)
    {
        //int cardInfoId = PaymentInfo.get(HotelGroup.CardInfoID)==null ? 0 : Integer.parseInt(PaymentInfo.get(HotelGroup.CardInfoID).toString());
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
            data.putAll(hotelGroupSources.getContactInfo(lastReqID));
            data.putAll(hotelGroupSources.getUserInfo(lastReqID));
            data.putAll(hotelGroupSources.getIpInfo(lastReqID));
            data.putAll(hotelGroupSources.getHotelGroupInfo(lastReqID));
            data.putAll(hotelGroupSources.getOtherInfo(lastReqID));
        }
    }

    //补充主要支付方式
    public void fillMainOrderType(Map data)
    {
        String orderPrepayType = data.get(HotelGroup.OrderPrepayType) == null ? "" : data.get(HotelGroup.OrderPrepayType).toString();
        if(orderPrepayType.isEmpty())
        {
            //如果主要支付方式为空，则用订单号和订单类型到risk_levelData取上次的主要支付方式
            String orderType = data.get(HotelGroup.OrderType) == null ? "" : data.get(HotelGroup.OrderType).toString();
            String orderId = data.get(HotelGroup.OrderID) == null ? "" : data.get(HotelGroup.OrderID).toString();
            Map payInfo = hotelGroupSources.getMainPrepayType(orderType,orderId);
            if(payInfo != null && !payInfo.isEmpty())
            {
                data.put(HotelGroup.OrderPrepayType,payInfo.get(HotelGroup.PrepayType));
            }
        }

        //补充主要支付方式自动判断逻辑
        if(data.get(HotelGroup.OrderPrepayType).toString().isEmpty() || data.get(HotelGroup.CheckType).toString().equals("2"))
        {
            if(data.get(HotelGroup.PaymentInfoList) == null)
            return;
            List<Map> paymentInfoList = (List<Map>)data.get(HotelGroup.PaymentInfoList);
            for(Map paymentInfos : paymentInfoList)
            {
                if(paymentInfos.get(HotelGroup.PaymentInfo) == null )
                    continue;
                Map payment = (Map)paymentInfos.get(HotelGroup.PaymentInfo);
                if(payment == null || payment.get(HotelGroup.PrepayType) == null)
                    continue;
                if(payment.get(HotelGroup.PrepayType).toString().toUpperCase().equals("CCARD") || payment.get(HotelGroup.PrepayType).toString().toUpperCase().equals("DCARD"))
                {
                    data.put(HotelGroup.OrderPrepayType,payment.get(HotelGroup.PrepayType).toString().toUpperCase());
                    break;
                }else
                {
                    data.put(HotelGroup.OrderPrepayType,payment.get(HotelGroup.PrepayType).toString().toUpperCase());//这句没看懂？？？//fixme
                }
            }
        }
    }
}
