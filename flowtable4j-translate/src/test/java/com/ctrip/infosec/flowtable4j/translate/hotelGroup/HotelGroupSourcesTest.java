package com.ctrip.infosec.flowtable4j.translate.hotelGroup;

import com.ctrip.infosec.flowtable4j.translate.dao.HotelGroupSources;
import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lpxie on 15-4-24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/allTemplates.xml","classpath*:spring/preprocess-datasource.xml"})
public class HotelGroupSourcesTest
{
    @Autowired
    HotelGroupSources hotelGroupSources;

    @Resource(name="allTemplates")
    private AllTemplates allTemplates;

    JdbcTemplate cardRiskDBTemplate = null;
    JdbcTemplate riskCtrlPreProcDBTemplate = null;
    JdbcTemplate cUSRATDBTemplate = null;


    @Before
    public void init()
    {
        cardRiskDBTemplate = allTemplates.getCardRiskDBTemplate();
        riskCtrlPreProcDBTemplate = allTemplates.getRiskCtrlPreProcDBTemplate();
    }

    @Test
    public void testGetCityAndProv()
    {
        try{
            String mobilePhone = "13917863756";
            String subMobileNum = mobilePhone.substring(0,7);
            String sqlCommand = "SELECT Top 1 *" + " FROM BaseData_MobilePhoneInfo with (nolock) WHERE MobileNumber = "+subMobileNum;
            Map mobileInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
            Assert.assertNotNull(mobileInfo);
        }catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }

    @Test
    public void getIpCountryCity()
    {
        Map ipInfo = null;
        try{
            //SELECT Top 1 * FROM IpCountryCity with (nolock) WHERE IpStart <= "+ipValue +" ORDER BY IpStart DESC
            String ipValue = "4278190082";
            String sqlCommand = "SELECT Top 1 * FROM IpCountryCity with (nolock) WHERE IpStart <= "+ipValue +" ORDER BY IpStart DESC";//FIXME 这里问徐洪修正
            ipInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
            Assert.assertNotNull(ipInfo);
        }catch(Exception exp)
        {
            //log for warn
        }
    }

    @Test
    public void testGetDIDInfo()
    {
        Map DIDInfo = null;
        try{
            String orderId = "37348379";
            String orderType = "16";
            String sqlCommand = "SELECT TOP 1 * FROM CacheData_DeviceIDInfo with (nolock) WHERE [CacheData_DeviceIDInfo].[Oid] = "+orderId +
                    " and [CacheData_DeviceIDInfo].[Payid] = "+orderType + " order by [CacheData_DeviceIDInfo].[RecordID] desc";
            DIDInfo = riskCtrlPreProcDBTemplate.queryForMap(sqlCommand);
            Assert.assertNotNull(DIDInfo);
        }catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }

    @Test
    public void testGetMainInfo()
    {
        Map mainInfo = null;
        try{
            String orderId = "37348379";
            String orderType = "16";//CardRiskDB..
            String sqlCommand = "SELECT top 1 * from InfoSecurity_MainInfo with (nolock) where [InfoSecurity_MainInfo].[OrderType] = "
                    +orderType +" and [InfoSecurity_MainInfo].[OrderId] = "+orderId+" order by [InfoSecurity_MainInfo].ReqID desc";
            mainInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
            Assert.assertNotNull(mainInfo);
        }catch (Exception exp)
        {
            exp.printStackTrace();
        }
    }

    @Test
    public void testGetCardInfo()
    {
        Map cardInfo = null;
        try{
            String cardTypeId = "6";
            String cardBin = "550903";
            String commandText = "SELECT * from CreditCardRule_ForeignCard with (nolock) where CardTypeID = "+cardTypeId
                    +" and CardRule = "+cardBin;
            cardInfo = cardRiskDBTemplate.queryForMap(commandText);
            Assert.assertNotNull(cardInfo);
        }catch(Exception exp)
        {
        }
    }

    @Test
    public void testGetListPaymentInfo()
    {
        List paymentInfoList = null;
        try{
            String lastReqID = "7183607";
            String commandText = "select * from InfoSecurity_PaymentInfo with (nolock) where [InfoSecurity_PaymentInfo].[ReqID] = " +lastReqID;
            paymentInfoList = cardRiskDBTemplate.queryForList(commandText);
            Assert.assertNotNull(paymentInfoList);
        }catch(Exception exp)
        {
        }
    }

    @Test
    public void testGetListCardInfo()
    {
        List<Map<String, Object>> cardInfoList = null;
        try{
            String paymentInfoId = "169";
            String commandText = "select * from InfoSecurity_CardInfo with (nolock) where [InfoSecurity_CardInfo].[PaymentInfoID] =" +
                    paymentInfoId;
            cardInfoList = cardRiskDBTemplate.queryForList(commandText);
            Assert.assertNotNull(cardInfoList);
        }catch(Exception exp)
        {
        }
    }

    @Test
    public void testGetPaymentMainInfo()
    {
        long reqId = 7229792;
        Map paymentMainInfo = null;
        try{
            String commandText = "select * from InfoSecurity_PaymentMainInfo with (nolock) where [InfoSecurity_PaymentMainInfo].[ReqID] = " +
                    reqId;
            paymentMainInfo = cardRiskDBTemplate.queryForMap(commandText);
            Assert.assertNotNull(paymentMainInfo);
        }catch(Exception exp)
        {
        }
    }

    @Test
    public void testGetContactInfo()
    {
        long reqId = 1362;
        Map contactInfo = null;
        try{
            String commandText = "select * from InfoSecurity_ContactInfo with (nolock) where [InfoSecurity_ContactInfo].[ReqID] = " +
                    reqId;
            contactInfo = cardRiskDBTemplate.queryForMap(commandText);
            Assert.assertNotNull(contactInfo);
        }catch(Exception exp)
        {
        }
    }

    @Test
    public void testGetUserInfo()
    {
        long reqId = 1362;
        Map userInfo = null;
        try{
            String commandText = "select * from InfoSecurity_UserInfo with (nolock) where [InfoSecurity_UserInfo].[ReqID] = " +
                    reqId;
            userInfo = cardRiskDBTemplate.queryForMap(commandText);
            Assert.assertNotNull(userInfo);
        }catch(Exception exp)
        {
        }
    }

    @Test
    public void testGetIpInfo()
    {
        long reqId = 1362;
        Map ipInfo = null;
        try{
            String commandText = "select * from InfoSecurity_IPInfo with (nolock) where [InfoSecurity_IPInfo].[ReqID] = " +
                    reqId;
            ipInfo = cardRiskDBTemplate.queryForMap(commandText);
            Assert.assertNotNull(ipInfo);
        }catch(Exception exp)
        {
        }
    }

    @Test
    public void testGetHotelGroupInfo()
    {
        long reqId = 6056524;
        Map hotelGroupInfo = null;
        try{
            String commandText = "select * from InfoSecurity_HotelGroupInfo with (nolock) where [InfoSecurity_HotelGroupInfo].[ReqID] = " +
                    reqId;
            hotelGroupInfo = cardRiskDBTemplate.queryForMap(commandText);
            Assert.assertNotNull(hotelGroupInfo);
        }catch(Exception exp)
        {
        }
    }

    @Test
    public void testGetOtherInfo()
    {
        long reqId = 1362;
        Map otherInfo = null;
        try{
            String commandText = "select * from InfoSecurity_OtherInfo with (nolock) where [InfoSecurity_OtherInfo].[ReqID] = " +
                    reqId;
            otherInfo = cardRiskDBTemplate.queryForMap(commandText);
            Assert.assertNotNull(otherInfo);
        }catch(Exception exp)
        {
        }
    }

    @Test
    public void testGetMainPrepayType()
    {
        String orderId = "1";
        String orderType = "16";
        Map payInfo = null;
        try{
            String commandText = "select top 1 * from InfoSecurity_RiskLevelData with (nolock) where [InfoSecurity_RiskLevelData].OrderType" +
                    " = "+orderType+" and [InfoSecurity_RiskLevelData].OrderId = "+orderId +" ORDER BY [InfoSecurity_RiskLevelData].ReqID DESC";
            payInfo = cardRiskDBTemplate.queryForMap(commandText);
            Assert.assertNotNull(payInfo);
        }catch(Exception exp)
        {
        }
    }

    @Test
    public void testGetCityInfo()
    {
        Map cityInfo = null;
        String city = "1";
        try{
            String commandText = "select top 1 * from BaseData_City with (nolock) where city = "+city;
            cityInfo = cardRiskDBTemplate.queryForMap(commandText);
            Assert.assertNotNull(cityInfo);
        }catch (Exception exp)
        {
        }
    }

    @Test
    public void testGetOriginalRisklevel()
    {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        String nowTimeStr = format.format(date);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        //测试的时间往前一年，真是数据是720分钟
        calendar.add(calendar.YEAR,-1);
        //calendar.add(calendar.MINUTE,-720);
        String timeLimitStr = format.format(calendar.getTime());

        Map params = new HashMap();
        params.put("uid","wwwwww");
        hotelGroupSources.getOriginalRisklevel(params,timeLimitStr,nowTimeStr);
    }

    @Test
    public void testGetLeakedInfo()
    {
        Map leakInfo = null;
        try{
            String uid = "test12";
            String commandText = "select top 1 * from CUSRATDB..CardRisk_Leaked_Uid with (nolock) where [CardRisk_Leaked_Uid].[Uid] = '" +
                    uid+"'";
            cUSRATDBTemplate.queryForMap(commandText);
            Assert.assertNotNull(leakInfo);
        }catch(Exception exp)
        {

        }
    }
}
