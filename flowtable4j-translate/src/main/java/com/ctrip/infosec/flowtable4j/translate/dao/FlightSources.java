package com.ctrip.infosec.flowtable4j.translate.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by lpxie on 15-3-31.
 */
public class FlightSources
{
    @Resource(name="CardRiskDB")
    private static JdbcTemplate cardRiskDBTemplate;

    @Resource(name = "RiskCtrlPreProcDB")
    private static JdbcTemplate riskCtrlPreProcDBTemplate;

    //通过手机号查询对应的城和市
    public static Map getCityAndProv(String mobilePhone)
    {
        String subMobileNum = mobilePhone.substring(0,7);
        String sqlCommand = "SELECT *" + " FROM BaseData_MobilePhoneInfo with (nolock) WHERE MobileNumber = "+subMobileNum;
        Map mobileInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return mobileInfo;//这里取出里面的 CityName 和 ProvinceName 这两个字段
    }

    //获取当前IP所在地信息(Top 1 OrderBy IpStart Desc)  IpCountryCity
    public static Map getIpCountryCity(long ipValue)
    {
        String sqlCommand = "SELECT Top 1 FROM IpCountryCity with (nolock) WHERE IpStart <= @IpValue and IpStart <= @IpValue ORDER BY IpStart DESC ";//FIXME 这里问徐洪修正
        Map ipInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return ipInfo;
    }

    //获取CityCode根据airPort
    public static int getCityCode(String airPort)
    {
        String sqlCommand = "select City from Fltproductdb..AirPort (nolock) where AirPort = "+"'"+airPort+"'";
        int cityCode = 0;

        //FIXME 这里连接机票的库？
        //Map cityInfo = cardRiskDBTemplate

        return cityCode;
    }

    //获取主要的信息（？） 根据订单类型和订单id 数据库表：InfoSecurity_MainInfo
    public static Map getMainInfo(String orderType,String orderId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_MainInfo with (nolock) where [InfoSecurity_MainInfo].OrderType = " +
                orderType + " and [InfoSecurity_MainInfo].OrderId = "+orderId +" order by [InfoSecurity_MainInfo].ReqID desc";
        Map mainInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return mainInfo;
    }

    //获取用户联系信息根据ReqID InfoSecurity_ContactInfo
    public static Map getContactInfo(long reqId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_ContactInfo with (nolock) where [InfoSecurity_ContactInfo]" +
                "[ReqID] = "+reqId;
        Map contactInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return contactInfo;
    }

    //获取用户基本信息ReqID InfoSecurity_UserInfo
    public static Map getUserInfo(long reqId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_ContactInfo with (nolock) where [InfoSecurity_UserInfo]" +
                "[ReqID] = "+reqId;
        Map userInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return userInfo;
    }

    //获取用户IP信息ReqID InfoSecurity_IPInfo
    public static Map getIpInfo(long reqId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_ContactInfo with (nolock) where [InfoSecurity_IPInfo]" +
                "[ReqID] = "+reqId;
        Map ipInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return ipInfo;
    }
    //获取机票订单信息 InfoSecurity_FlightsOrderInfo
    public static Map getFlightsOrderInfo(long reqId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_FlightsOrderInfo with (nolock) where [InfoSecurity_FlightsOrderInfo]" +
                ".[ReqID] = "+reqId;
        Map flightsOrderInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return flightsOrderInfo;
    }
    //获取机票乘客信息 用机票订单号来获取
    public static Map getPassengerInfo(int flightsOrderId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_PassengerInfo with (nolock) where [InfoSecurity_PassengerInfo]" +
                ".[FlightsOrderID] = "+flightsOrderId;
        Map passengerInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return passengerInfo;
    }
    //获取机票段信息 用机票订单号来获取
    public static Map getSegmentInfo(int flightsOrderId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_SegmentInfo with (nolock) where [InfoSecurity_SegmentInfo]" +
                ".[FlightsOrderID] = "+flightsOrderId;
        Map segmentInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return segmentInfo;
    }

    //获取用户其他信息ReqID InfoSecurity_OtherInfo
    public static Map getOtherInfo(long reqId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_ContactInfo with (nolock) where [InfoSecurity_OtherInfo]" +
                "[ReqID] = "+reqId;
        Map otherInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return otherInfo;
    }

    //获取用户关联信息ReqID InfoSecurity_CorporationInfo
    public static Map getCorporationInfo(long reqId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_ContactInfo with (nolock) where [InfoSecurity_CorporationInfo]" +
                "[ReqID] = "+reqId;
        Map corporationInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return corporationInfo;
    }

   //获取用户的APP信息ReqID  InfoSecurity_AppInfo
    public static Map getAppInfo(long reqId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_ContactInfo with (nolock) where [InfoSecurity_AppInfo]" +
                "[ReqID] = "+reqId;
        Map appInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return appInfo;
    }

    //获取信用卡信息 通过卡种和卡BIN获取 CreditCardRule_ForeignCard
    public static Map getCardInfo(int cardTypeId,String cardBin)
    {
        String sqlCommand = "SELECT top 1 from CreditCardRule_ForeignCard with (nolock) where CardTypeID = "+cardTypeId+" and CardRule" +
                " = "+cardBin;
        Map cardInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return cardInfo;
    }

    //获取支付信息 通过reqID
    public static List<Map<String,Object>> getPaymentInfo(long reqId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_PaymentInfo with (nolock) where [InfoSecurity_PaymentInfo]" +
                ".[ReqID] = "+reqId;
        List<Map<String,Object>> paymentInfo = cardRiskDBTemplate.queryForList(sqlCommand);
        return paymentInfo;
    }

    //获取临时支付信息 通过PaymentInfoID  InfoSecurity_CardInfo
    public static List<Map<String,Object>> getTemPayInfo(long paymentInfoId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_CardInfo with (nolock) where [InfoSecurity_CardInfo]" +
                ".[PaymentInfoID] = "+paymentInfoId;
        List<Map<String,Object>> temPayInfo = cardRiskDBTemplate.queryForList(sqlCommand);
        return temPayInfo;
    }

    //获取主支付信息 InfoSecurity_PaymentMainInfo
    public static Map getPaymentMainInfo(long reqId)
    {
        String sqlCommand = "SELECT top 1 from InfoSecurity_PaymentMainInfo with (nolock) where [InfoSecurity_PaymentMainInfo]" +
                ".[ReqID] = "+reqId;
        Map paymentMainInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        return paymentMainInfo;
    }
}
