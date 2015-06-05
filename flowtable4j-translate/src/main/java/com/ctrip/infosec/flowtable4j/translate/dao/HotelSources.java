package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by lpxie on 15-6-4.
 */
public class HotelSources
{
    private static Logger logger = LoggerFactory.getLogger(HotelSources.class);

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

    //通过lastReqId获取酒店团购信息
    public Map getHotelInfo(String reqId)
    {
        Map hotelInfo = null;
        try{
            String commandText = "select * from InfoSecurity_HotelInfo with (nolock) where [InfoSecurity_HotelGroupInfo].[ReqID] = ?";
            hotelInfo = cardRiskDBTemplate.queryForMap(commandText,reqId);
            return hotelInfo;
        }catch(Exception exp)
        {
            return hotelInfo;
        }
    }

    //获取相同的reqId的数量
    public List<Map<String,Object>> getSumReqIdInfo(String singleGuestName,String hotelNameArrivalTime,String uid,String mobilePhone,String startTimeLimit,String timeLimit)
    {
        long now = System.currentTimeMillis();
        List<Map<String,Object>> sumReqIdInfo = null;
        try{
            String sqlCommand = "select  a.reqid ,SumSameReqidCount=(select  count(SingleGuestName) " +
                    "from  CTRIP_HOTEL_SingleGuestName_HotelNameArrivalTime  (nolock) " +
                    "where reqid=a.reqid) from  CTRIP_HOTEL_SingleGuestName_HotelNameArrivalTime a (nolock) " +
                    "join CTRIP_HOTEL_UID_MobilePhone b (nolock)  on a.reqid=b.reqid " +
                    "where SingleGuestName=? and HotelNameArrivalTime=? " +
                    "and UId<>? and MobilePhone<>? " +
                    "and  a.CreateDate>=? and a.CreateDate<=?";
            sumReqIdInfo = riskCtrlPreProcDBTemplate.queryForList(sqlCommand,singleGuestName,hotelNameArrivalTime,uid,mobilePhone,startTimeLimit,timeLimit);
        }catch(Exception exp)
        {
            logger.warn("查询sumReqIdInfo信息异常:"+exp.getMessage());
        }
        logger.info("getSumReqIdInfo的查询sqlServer的时间是："+(System.currentTimeMillis()-now));
        return sumReqIdInfo;
    }
}
