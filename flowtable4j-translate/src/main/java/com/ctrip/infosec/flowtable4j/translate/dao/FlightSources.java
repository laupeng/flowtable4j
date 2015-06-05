package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lpxie on 15-3-31.
 */
public class FlightSources
{
    private static Logger logger = LoggerFactory.getLogger(FlightSources.class);

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
        cardRiskDBTemplate = allTemplates.getCardRiskDBTemplate();
        riskCtrlPreProcDBTemplate = allTemplates.getRiskCtrlPreProcDBTemplate();
        cUSRATDBTemplate = allTemplates.getcUSRATDBTemplate();
    }

    //获取CityCode根据airPort
    public String getCityCode(String airPort)
    {
        String sqlCommand = "select City from Fltproductdb..AirPort (nolock) where AirPort = ?";
        String cityCode = "";

        //FIXME 这里连接机票的库？
        //Map cityInfo = cardRiskDBTemplate

        return cityCode;
    }

    //获取机票订单信息 InfoSecurity_FlightsOrderInfo
    public Map getFlightsOrderInfo(String reqId)
    {
        long now = System.currentTimeMillis();
        Map flightsOrderInfo = null;
        try{
            String sqlCommand = "SELECT top 1 * from InfoSecurity_FlightsOrderInfo with (nolock) where [InfoSecurity_FlightsOrderInfo].[ReqID] = ?";
            flightsOrderInfo = cardRiskDBTemplate.queryForMap(sqlCommand,reqId);
        }catch(Exception exp)
        {
            logger.warn("查询FlightsOrderInfo信息异常:"+exp.getMessage());
        }
        logger.info("getFlightsOrderInfo的查询sqlServer的时间是："+(System.currentTimeMillis()-now));
        return flightsOrderInfo;
    }
    //获取机票乘客信息 用机票订单号来获取
    public List<Map<String,Object>> getPassengerInfo(String flightsOrderId)
    {
        long now = System.currentTimeMillis();
        List<Map<String,Object>> passengerInfo = null;
        try{
            String sqlCommand = "SELECT * from InfoSecurity_PassengerInfo with (nolock) where [InfoSecurity_PassengerInfo].[FlightsOrderID] = ?";
            passengerInfo = cardRiskDBTemplate.queryForList(sqlCommand, flightsOrderId);
        }catch(Exception exp)
        {
            logger.warn("查询PassengerInfo信息异常:"+exp.getMessage());
        }
        logger.info("getPassengerInfo的查询sqlServer的时间是："+(System.currentTimeMillis()-now));
        return passengerInfo;
    }
    //获取机票段信息 用机票订单号来获取
    public List<Map<String,Object>> getSegmentInfo(String flightsOrderId)
    {
        long now = System.currentTimeMillis();
        List<Map<String,Object>> segmentInfo = null;
        try{
            String sqlCommand = "SELECT top 1 * from InfoSecurity_SegmentInfo with (nolock) where [InfoSecurity_SegmentInfo].[FlightsOrderID] = ?";
            segmentInfo = cardRiskDBTemplate.queryForList(sqlCommand, flightsOrderId);
        }catch(Exception exp)
        {
            logger.warn("查询SegmentInfo信息异常:"+exp.getMessage());
        }
        logger.info("getSegmentInfo的查询sqlServer的时间是："+(System.currentTimeMillis()-now));
        return segmentInfo;
    }

    //根据城市编码获取对应的省市
    public Map getCityNameProvince(String city)
    {
        long now = System.currentTimeMillis();
        Map<String,Object> cnpInfo = null;
        try{
            String sqlCommand = "SELECT top 1 * from BaseData_City with (nolock) where city = ?";
            cnpInfo = cardRiskDBTemplate.queryForMap(sqlCommand, city);
        }catch(Exception exp)
        {
            logger.warn("查询cnpInfo信息异常:"+exp.getMessage());
        }
        logger.info("getCityNameProvince的查询sqlServer的时间是："+(System.currentTimeMillis()-now));
        return cnpInfo;
    }
    //根据国家编号获取国家的名称和国际
    public Map getCountryNameNationality(String country)
    {
        long now = System.currentTimeMillis();
        Map<String,Object> countryInfo = null;
        try{
            String sqlCommand = "SELECT top 1 * from BaseData_CountryInfo with (nolock) where Country = ?";
            countryInfo = cardRiskDBTemplate.queryForMap(sqlCommand, country);
        }catch(Exception exp)
        {
            logger.warn("查询countryInfo信息异常:"+exp.getMessage());
        }
        logger.info("getCountryNameNationality的查询sqlServer的时间是："+(System.currentTimeMillis()-now));
        return countryInfo;
    }

    //获取省份证归属省信息  BaseData_IDCardInfo
    public Map getIDCardProvince(String iDCardNumber)
    {
        long now = System.currentTimeMillis();
        Map<String,Object> iDCardNumberInfo = null;
        try{
            String sqlCommand = "SELECT top 1 * from BaseData_IDCardInfo with (nolock) where IDCardNumber = ?";
            iDCardNumberInfo = cardRiskDBTemplate.queryForMap(sqlCommand, iDCardNumber);
        }catch(Exception exp)
        {
            logger.warn("查询iDCardNumberInfo信息异常:"+exp.getMessage());
        }
        logger.info("getIDCardProvince的查询sqlServer的时间是："+(System.currentTimeMillis()-now));
        return iDCardNumberInfo;
    }

    //获取uid过去的订单时间
    public String getUidOrderDate(String uid,String startTimeLimit,String timeLimit)
    {
        long now = System.currentTimeMillis();
        String orderDate = null;
        try{
            String sqlCommand = "select top 1 OrderDate from CTRIP_ALL_UID_OrderDate with (nolock) where " +
                    "Uid = ? and CreateDate>=? and CreateDate<=?";
            orderDate = riskCtrlPreProcDBTemplate.queryForObject(sqlCommand,String.class, uid,startTimeLimit,timeLimit);
        }catch(Exception exp)
        {
            logger.warn("查询orderDate信息异常:"+exp.getMessage());
        }
        logger.info("getUidOrderDate的查询sqlServer的时间是："+(System.currentTimeMillis()-now));
        return orderDate;
    }
    //获取七天内均值消费金额
    public String getAvgAmount7(String CCardNoCode,String startTimeLimit,String timeLimit)
    {
        long now = System.currentTimeMillis();
        String avgAmount = null;
        try{
            String sqlCommand = "select avg(Amount) from CTRIP_FLT_CCardNoCode_Amount with (nolock) where " +
                    "CCardNoCode = ? and CreateDate>=? and CreateDate<=?";
            avgAmount = riskCtrlPreProcDBTemplate.queryForObject(sqlCommand,String.class, CCardNoCode,startTimeLimit,timeLimit);
        }catch(Exception exp)
        {
            logger.warn("查询avgAmount信息异常:"+exp.getMessage());
        }
        logger.info("getAvgAmount7的查询sqlServer的时间是："+(System.currentTimeMillis()-now));
        return avgAmount;
    }
    //获取临时支付信息 通过PaymentInfoID  InfoSecurity_CardInfo
    /*public List<Map<String,Object>> getTemPayInfo(long paymentInfoId)
    {
        String sqlCommand = "SELECT top 1 * from InfoSecurity_CardInfo with (nolock) where [InfoSecurity_CardInfo].[PaymentInfoID] = "+paymentInfoId;
        List<Map<String,Object>> temPayInfo = cardRiskDBTemplate.queryForList(sqlCommand);
        return temPayInfo;
    }*/
}
