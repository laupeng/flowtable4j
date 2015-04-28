package com.ctrip.infosec.flowtable4j.translate.dao;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lpxie on 15-4-20.
 */
public class HotelGroupSources
{
    @Resource(name="CardRiskDB")
    private JdbcTemplate cardRiskDBTemplate;

    @Resource(name = "RiskCtrlPreProcDB")
    private JdbcTemplate riskCtrlPreProcDBTemplate;

    /**
     * 这个方法可以作为公共的方法 //Todo 下次把这个方法放到公共查询里面 或者 把从数据库查询改成从郁伟的DataProxy查询！
     * 通过手机号查询对应的城和市
     * @param mobilePhone 手机号
     * @return
     */
    public Map getCityAndProv(String mobilePhone)
    {
        Map mobileInfo = null;
        try
        {
            String subMobileNum = mobilePhone.substring(0,7);
            String sqlCommand = "SELECT Top 1 *" + " FROM BaseData_MobilePhoneInfo with (nolock) WHERE MobileNumber = "+subMobileNum;
            mobileInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        }catch(Exception exp)
        {
            //log for warn
        }
        return mobileInfo;//这里取出里面的 CityName 和 ProvinceName 这两个字段
    }

    //获取当前IP所在地信息(Top 1 OrderBy IpStart Desc)  IpCountryCity
    public Map getIpCountryCity(long ipValue)
    {
        Map ipInfo = null;
        try{
            String sqlCommand = "SELECT Top 1 * FROM IpCountryCity with (nolock) WHERE IpStart <= "+ipValue +" ORDER BY IpStart DESC ";//FIXME 这里问徐洪修正
            ipInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        }catch(Exception exp)
        {
            //log for warn
        }
        return ipInfo;
    }

    public Map getDIDInfo(String orderId,String orderType)
    {
        Map DIDInfo = null;
        try{
            String sqlCommand = "SELECT TOP 1 FROM CacheData_DeviceIDInfo with (nolock) WHERE [CacheData_DeviceIDInfo].[Oid] = "+orderId +" and " +
                    "[CacheData_DeviceIDInfo].[Payid] = "+orderType +" order by [CacheData_DeviceIDInfo].[RecordID] desc";
            DIDInfo = riskCtrlPreProcDBTemplate.queryForMap(sqlCommand);
            return DIDInfo;
        }catch (Exception exp)
        {
            //log for warn
            return DIDInfo;
        }
    }

    public Map getMainInfo(String orderType,String orderId)
    {
        Map mainInfo = null;
        try{
            String commandText = "SELECT top 1 * from InfoSecurity_MainInfo with(nolock) where [InfoSecurity_MainInfo].OrderType" +
                    " = "+orderType +" and [InfoSecurity_MainInfo].OrderId = "+orderId+" order by [InfoSecurity_MainInfo].ReqID desc";
            mainInfo = cardRiskDBTemplate.queryForMap(commandText);
            return mainInfo;
        }catch (Exception exp)
        {
            return mainInfo;
        }
    }

    public Map getCardInfo(String cardTypeId,String cardBin)
    {
        Map cardInfo = null;
        try{
            String commandText = "SELECT * from CreditCardRule_ForeignCard with (nolock) where CardTypeID = "+cardTypeId
                    +" and CardRule = "+cardBin;
            cardInfo = cardRiskDBTemplate.queryForMap(commandText);
            return cardInfo;
        }catch(Exception exp)
        {
            return cardInfo;
        }
    }

    public List getListPaymentInfo(long lastReqID)
    {
        List paymentInfoList = null;//这里的泛型类型到时根据数据库的数据来确定
        try{
            String commandText = "select * from InfoSecurity_PaymentInfo with (nolock) where [InfoSecurity_PaymentInfo].[ReqID] = " +
                    lastReqID;
            paymentInfoList = cardRiskDBTemplate.queryForList(commandText);
            return paymentInfoList;
        }catch(Exception exp)
        {
            return paymentInfoList;
        }
    }

    public List getListCardInfo(String paymentInfoId)
    {
        List cardInfoList = null;
        try
        {
            String commandText = "select * from InfoSecurity_CardInfo with (nolock) where [InfoSecurity_CardInfo].[PaymentInfoID] =" +
                    paymentInfoId;
            cardInfoList = cardRiskDBTemplate.queryForList(commandText);
            return cardInfoList;
        }catch(Exception exp)
        {
            return cardInfoList;
        }
    }

    //获取mainInfo信息
    public Map getPaymentMainInfo(long reqId)
    {
        Map paymentMainInfo = null;
        try{
            String commandText = "select * from InfoSecurity_PaymentMainInfo with (nolock) where [InfoSecurity_PaymentMainInfo].[ReqID] = " +
                    reqId;
            paymentMainInfo = cardRiskDBTemplate.queryForMap(commandText);
            return paymentMainInfo;
        }catch(Exception exp)
        {
            return paymentMainInfo;
        }
    }

    //通过lastReqId获取联系人信息
    public Map getContactInfo(long reqId)
    {
        Map contactInfo = null;
        try{
            String commandText = "select * from InfoSecurity_ContactInfo with (nolock) where [InfoSecurity_ContactInfo].[ReqID] = " +
                    reqId;
            contactInfo = cardRiskDBTemplate.queryForMap(commandText);
            return contactInfo;
        }catch(Exception exp)
        {
            return contactInfo;
        }
    }
    //通过lastReqId获取用户信息
    public Map getUserInfo(long reqId)
    {
        Map userInfo = null;
        try{
            String commandText = "select * from InfoSecurity_UserInfo with (nolock) where [InfoSecurity_UserInfo].[ReqID] = " +
                    reqId;
            userInfo = cardRiskDBTemplate.queryForMap(commandText);
            return userInfo;
        }catch(Exception exp)
        {
            return userInfo;
        }
    }
    //通过lastReqId获取ip信息
    public Map getIpInfo(long reqId)
    {
        Map userInfo = null;
        try{
            String commandText = "select * from InfoSecurity_UserInfo with (nolock) where [InfoSecurity_UserInfo].[ReqID] = " +
                    reqId;
            userInfo = cardRiskDBTemplate.queryForMap(commandText);
            return userInfo;
        }catch(Exception exp)
        {
            return userInfo;
        }
    }
    //通过lastReqId获取酒店团购信息
    public Map getHotelGroupInfo(long reqId)
    {
        Map hotelGroupInfo = null;
        try{
            String commandText = "select * from InfoSecurity_HotelGroupInfo with (nolock) where [InfoSecurity_HotelGroupInfo].[ReqID] = " +
                    reqId;
            hotelGroupInfo = cardRiskDBTemplate.queryForMap(commandText);
            return hotelGroupInfo;
        }catch(Exception exp)
        {
            return hotelGroupInfo;
        }
    }
    //通过lastReqId获取其他信息
    public Map getOtherInfo(long reqId)
    {
        Map otherInfo = null;
        try{
            String commandText = "select * from InfoSecurity_OtherInfo with (nolock) where [InfoSecurity_OtherInfo].[ReqID] = " +
                    reqId;
            otherInfo = cardRiskDBTemplate.queryForMap(commandText);
            return otherInfo;
        }catch(Exception exp)
        {
            return otherInfo;
        }
    }

    //获取上次主支付信息
    public Map getMainPrepayType(String orderType,String orderId)
    {
        Map payInfo = null;
        try{
            String commandText = "select top 1 * from InfoSecurity_RiskLevelData with (nolock) where [InfoSecurity_RiskLevelData].OrderType" +
                    " = "+orderId+" and [InfoSecurity_RiskLevelData].OrderId = "+orderId +" ORDER BY [InfoSecurity_RiskLevelData].ReqID DESC";
            payInfo = cardRiskDBTemplate.queryForMap(commandText);
            return payInfo;
        }catch(Exception exp)
        {
            return payInfo;
        }
    }

    //根据ipCity获取对应的城市名称
    public Map getCityInfo(String city)
    {
        Map cityInfo = null;
        try{
            String commandText = "select top 1 * from BaseData_City with (nolock) where city = "+city;
            cityInfo = cardRiskDBTemplate.queryForMap(commandText);
            return cityInfo;
        }catch (Exception exp)
        {
            return cityInfo;
        }
    }

    //查询CUSRATDB的CardRisk_Leaked_Uid  //todo 执行徐洪
    public Map getLeakedInfo(String uid)
    {
        Map leakInfo = null;
        try{
            String commandText = "select top 1 * from CardRisk_Leaked_Uid with (nolock) where [CardRisk_Leaked_Uid].[Uid] = " +
                    uid;
            //leakInfo = //todo 获取CUSRATDB的jndi
            return leakInfo;
        }catch(Exception exp)
        {
            return leakInfo;
        }
    }
}
