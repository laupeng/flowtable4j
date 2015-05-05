package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import com.sun.java.swing.plaf.windows.resources.windows_zh_HK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lpxie on 15-4-20.
 */
public class HotelGroupSources
{
    private static Logger logger = LoggerFactory.getLogger(HotelGroupSources.class);

    @Resource(name="allTemplates")
    private AllTemplates allTemplates;

    JdbcTemplate cardRiskDBTemplate = null;
    JdbcTemplate riskCtrlPreProcDBTemplate = null;
    JdbcTemplate cUSRATDBTemplate = null;

    /**
     * 初始化jndi
     */
    private void init()
    {
        logger.info("开始初始化JNDI模板");
        cardRiskDBTemplate = allTemplates.getCardRiskDBTemplate();
        riskCtrlPreProcDBTemplate = allTemplates.getRiskCtrlPreProcDBTemplate();
        cUSRATDBTemplate = allTemplates.getcUSRATDBTemplate();
        logger.info("初始化JNDI模板结束");
    }

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
            String sqlCommand = "SELECT TOP 1 * FROM RiskCtrlPreProcDB.dbo.CacheData_DeviceIDInfo with (nolock) WHERE [CacheData_DeviceIDInfo].[Oid] = "+"'"+orderId+"'" +
                    " and [CacheData_DeviceIDInfo].[Payid] = "+"'"+orderType+"'" + " order by [CacheData_DeviceIDInfo].[RecordID] desc";
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
            String commandText = "SELECT top 1 * from InfoSecurity_MainInfo with (nolock) where [InfoSecurity_MainInfo].[OrderType] = "
                    +orderType +" and [InfoSecurity_MainInfo].[OrderId] = "+orderId+" order by [InfoSecurity_MainInfo].ReqID desc";
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

    public List<Map<String, Object>> getListPaymentInfo(long lastReqID)
    {
        List<Map<String, Object>> paymentInfoList = null;//这里的泛型类型到时根据数据库的数据来确定
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

    public List<Map<String, Object>> getListCardInfo(String paymentInfoId)
    {
        List<Map<String, Object>> cardInfoList = null;
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
        Map ipInfo = null;
        try{
            String commandText = "select * from InfoSecurity_IPInfo with (nolock) where [InfoSecurity_IPInfo].[ReqID] = " +
                    reqId;
            ipInfo = cardRiskDBTemplate.queryForMap(commandText);
            return ipInfo;
        }catch(Exception exp)
        {
            return ipInfo;
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
            String commandText = "select top 1 * from CardRiskDB.dbo.InfoSecurity_RiskLevelData with (nolock) where [InfoSecurity_RiskLevelData].OrderType" +
                    " = "+orderType+" and [InfoSecurity_RiskLevelData].OrderId = "+orderId +" ORDER BY [InfoSecurity_RiskLevelData].ReqID DESC";
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

    public Map getLeakedInfo(String uid)
    {
        Map leakInfo = null;
        try{
            String commandText = "select top 1 * from CUSRATDB..CardRisk_Leaked_Uid with (nolock) where [CardRisk_Leaked_Uid].[Uid] = '" +
                    uid+"'";
            leakInfo = cUSRATDBTemplate.queryForMap(commandText);
            return leakInfo;
        }catch(Exception exp)
        {
            logger.warn("getLeakedInfo查询异常"+exp.getMessage());
            return leakInfo;
        }
    }

    /**
     * 获取流量表数据 统计分值大于195分的数据  目前是根据Uid,ContactEMail,MobilePhone,CCardNoCode
     * 感谢夏阳的帮忙
     * @param params 要统计的维度属性
     * @param timeLimitStr 过去的时间结点
     * @param nowTimeStr 当前时间
     * @return 获取的大于195分的数量
     */
    public int getOriginalRisklevel(Map params,String timeLimitStr,String nowTimeStr)
    {
        int countValue = 0;
        //遍历每一个属性
        Iterator iterator = params.keySet().iterator();
        while(iterator.hasNext())
        {
            String key = iterator.next().toString();
            String value = params.get(key) == null ? "" : params.get(key).toString();
            if(value.isEmpty())
                continue;
            try{
                List<Map<String,Object>> allTableNames = null;
                //先取出出所有的表名称
                String commandText = "select t.StatisticTableId, t.StatisticTableName ,f1.ColumnName as KeyFieldID1,f2.ColumnName as " +
                        "KeyFieldID2,t.OrderType,t.Active,t.[TableType]" +
                        "from Def_RuleStatisticTable t with (nolock)" +
                        "join Def_RuleMatchField f1 (nolock) on t.KeyFieldID1 = f1.FieldID " +
                        "join Def_RuleMatchField f2 (nolock) on t.KeyFieldID2 = f2.FieldID " +
                        "where f2.ColumnName='OriginalRisklevel' and  f1.ColumnName= '"+key+"'";//添加key来关联字段
                allTableNames = cardRiskDBTemplate.queryForList(commandText);

                Iterator iterator1 = allTableNames.iterator();
                while(iterator1.hasNext())
                {
                    Map<String,Object> columnValue = (Map)iterator1.next();
                    String active = columnValue.get("Active") == null ? "" : columnValue.get("Active").toString();
                    String orderType = columnValue.get("OrderType") == null ? "" : columnValue.get("OrderType").toString();
                    String tableType = columnValue.get("TableType") == null ? "" : columnValue.get("TableType").toString();
                    if(active.equals("T") && orderType.equals("0") && tableType.equals("1"))
                    {
                        //固定的值195分
                        String tableName = columnValue.get("StatisticTableName") == null ? "" : columnValue.get("StatisticTableName").toString();

                        String commandText1 = "select count(distinct originalrisklevel) from RiskCtrlPreProcDB.."+tableName +
                                " with (nolock) where "+key +" = '"+value+"' and originalrisklevel>=195 and CreateDate>= '"+timeLimitStr+"' and CreateDate<= '"+nowTimeStr+"'";
                        countValue = riskCtrlPreProcDBTemplate.queryForObject(commandText1, Integer.class);
                        break;
                    }
                }

                if(countValue>0)
                    break;
            }catch (Exception exp)
            {
                logger.warn("getOriginalRisklevel获取数据异常"+exp.getMessage());
            }
        }
        return countValue;
    }
}
