//package com.ctrip.infosec.flowtable4j.translate.flight;
//
//import org.junit.Assert;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by lpxie on 15-4-7.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath*:spring/preprocess-datasource-test.xml"})
//public class FlightSourcesTest
//{
//    @Resource(name="CardRiskDB")
//    private JdbcTemplate cardRiskDBTemplate;
//
//    @Resource(name = "RiskCtrlPreProcDB")
//    private JdbcTemplate riskCtrlPreProcDBTemplate;
//
//    @Resource(name="FltProductDB")
//    private JdbcTemplate fltProductDBDataSource;
//    /**
//     * 通过手机号查询对应的城和市
//     */
//    @Test
//    @Ignore
//    public void testGetCityAndProv()
//    {
//        String mobilePhone = "13917863756";//13482188219
//        String subMobileNum = mobilePhone.substring(0,7);
//        String sqlCommand = "SELECT Top 1 *" + " FROM BaseData_MobilePhoneInfo with (nolock) WHERE MobileNumber = "+subMobileNum;
//        Map mobileInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(mobileInfo);
//    }
//
//    /**
//     * 1034938368
//     */
//    @Test
//    @Ignore
//    public void testGetIpCountryCity()
//    {
//        String ipValue = "1034938368";
//        String sqlCommand = "SELECT Top 1 * FROM IpCountryCity with (nolock) WHERE IpStart <= "+ipValue +" ORDER BY IpStart DESC ";//FIXME 这里问徐洪修正
//        Map ipInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(ipInfo);
//    }
//
//    @Test
//    @Ignore
//    public void testGetCityCode()
//    {
//        String airPort = "北京";
//        String sqlCommand = "select City from Fltproductdb..AirPort with (nolock) where AirPort = "+"'"+airPort+"'";
//        int cityCode = 0;
//
//        //FIXME 这里连接机票的库？ ERROR
//        Map cityInfo = fltProductDBDataSource.queryForMap(sqlCommand);
//        System.out.println(cityInfo.size());
//    }
//
//    @Test
//    @Ignore
//    public void testGetMainInfo()
//    {
//        String orderType = "1";
//        String orderId = "602100001";
//        String sqlCommand = "SELECT top 1 * from InfoSecurity_MainInfo with (nolock) where [InfoSecurity_MainInfo].OrderType = " +
//                orderType + " and [InfoSecurity_MainInfo].OrderId = "+orderId +" order by [InfoSecurity_MainInfo].ReqID desc";
//        Map mainInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(mainInfo);
//    }
//
//    @Test
//    @Ignore
//    public void testGetContactInfo()
//    {
//        long reqId = 35487;
//        String sqlCommand = "SELECT top 1 * from InfoSecurity_ContactInfo with (nolock) where [InfoSecurity_ContactInfo].[ReqID] = "+reqId;
//        Map contactInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(contactInfo);
//    }
//
//    @Test
//    @Ignore
//    public void testGetUserInfo()
//    {
//        long reqId = 35487;
//        String sqlCommand = "SELECT top 1 * from InfoSecurity_UserInfo with (nolock) where [InfoSecurity_UserInfo].[ReqID] = "+reqId;
//        Map userInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(userInfo);
//    }
//
//    @Test
//    @Ignore
//    public void testGetIpInfo()
//    {
//        long reqId = 35487;
//        String sqlCommand = "SELECT top 1 * from InfoSecurity_IPInfo with (nolock) where [InfoSecurity_IPInfo].[ReqID] = "+reqId;
//        Map ipInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(ipInfo);
//    }
//
//    @Test
//    public void testGetFlightsOrderInfo()
//    {
//        long reqId = 35487;
//        String sqlCommand = "SELECT top 1 * from InfoSecurity_FlightsOrderInfo with (nolock) where [InfoSecurity_FlightsOrderInfo].[ReqID] = "+reqId;
//        Map flightsOrderInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(flightsOrderInfo);
//    }
//
//    @Test
//    public void testGetPassengerInfo()
//    {
//        long flightsOrderId = 23920;
//        String sqlCommand = "SELECT top 1 * from InfoSecurity_PassengerInfo with (nolock) where [InfoSecurity_PassengerInfo].[FlightsOrderID] = "+flightsOrderId;
//        Map passengerInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(passengerInfo);
//    }
//
//    @Test
//    public void testGetSegmentInfo()
//    {
//        try{
//        long flightsOrderId = 23920;
//        String sqlCommand = "SELECT top 1 * from InfoSecurity_SegmentInfo with (nolock) where [InfoSecurity_SegmentInfo].[FlightsOrderID] = "+flightsOrderId;
//        Map segmentInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(segmentInfo);
//        }catch (Exception exp)
//        {
//            System.out.println("size is 0");
//        }
//    }
//
//    @Test
//    public void getOtherInfo()
//    {
//        long reqId = 35487;
//        String sqlCommand = "SELECT top 1 * from InfoSecurity_OtherInfo with (nolock) where [InfoSecurity_OtherInfo].[ReqID] = "+reqId;
//        Map otherInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(otherInfo);
//    }
//
//    @Test
//    public void getCorporationInfo()
//    {
//        long reqId = 35487;
//        String sqlCommand = "SELECT top 1 * from InfoSecurity_CorporationInfo with (nolock) where [InfoSecurity_CorporationInfo].[ReqID] = "+reqId;
//        Map corporationInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(corporationInfo);
//    }
//
//    @Test
//    public void getAppInfo()
//    {
//        long reqId = 35487;
//        String sqlCommand = "SELECT top 1 * from InfoSecurity_AppInfo with (nolock) where [InfoSecurity_AppInfo].[ReqID] = "+reqId;
//        Map appInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(appInfo);
//    }
//
//    @Test
//    public void getCardInfo()
//    {
//        String cardTypeId = "7";
//        String cardBin = "410748";
//        String sqlCommand = "SELECT top 1 * from CreditCardRule_ForeignCard with (nolock) where CardTypeID = "+cardTypeId+" and CardRule" +
//                " = "+cardBin;
//        Map cardInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(cardInfo);
//    }
//
//    @Test
//    public void getTemPayInfo()
//    {
//        long paymentInfoId = 23921;
//        String sqlCommand = "SELECT top 1 * from InfoSecurity_CardInfo with (nolock) where [InfoSecurity_CardInfo].[PaymentInfoID] = "+paymentInfoId;
//        List<Map<String,Object>> temPayInfo = cardRiskDBTemplate.queryForList(sqlCommand);
//        Assert.assertNotNull(temPayInfo);
//    }
//
//    @Test
//    public void getPaymentMainInfo()
//    {
//        //FIXME 这里会有异常 Incorrect result size: expected 1, actual 0 在每次处理的时候都加上try catch
//        long reqId = 35487;
//        String sqlCommand = "SELECT top 1 * from InfoSecurity_PaymentMainInfo with (nolock) where [InfoSecurity_PaymentMainInfo].[ReqID] = "+reqId;
//        Map paymentMainInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
//        Assert.assertNotNull(paymentMainInfo);
//    }
//}
