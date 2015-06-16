package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.Set;

import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;

/**
 * Created by lpxie on 15-5-29.
 */
public class TieYouWriteSources
{
    private static Logger logger = LoggerFactory.getLogger(TieYouWriteSources.class);

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

    public String insertTieYouExRailInfo(final Map tieYouExRailInfo,final String reqId,final boolean isWrite,final boolean isCheck)
    {
        try
        {
            if(isCheck)
            {
                Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from InfoSecurity_ExRailInfo where reqid=?",reqId);
                Set<Map.Entry> entries = oldMainInfo.entrySet();
                for(Map.Entry<String,Object> entry : entries)
                {
                    if(!entry.getValue().equals(getValue(tieYouExRailInfo,entry.getKey())))
                    {
                        logger.info("tieYouExRailInfo信息比对结果"+entry.getKey()+":"+"老系统的值"+entry.getValue()+"新系统的值"+getValue(tieYouExRailInfo,entry.getKey()));
                    }
                }
            }
        }catch (Exception exp)
        {
            logger.warn("insertTieYouExRailInfo比对数据的时候出现异常"+exp.getMessage());
        }
        if(!isWrite)
            return "";//如果不写入就直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<10-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_ExRailInfo_i("+params+")}";// 调用的dbo.sp3_InfoSecurity_ExRailInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               cs.setString("ExRailInfoID","");//这个是自增主键 站位符
               cs.setString(Common.ReqID,reqId);
               cs.setString(Common.MerchantOrderID,getValue(tieYouExRailInfo,Common.MerchantOrderID));
               cs.setString("DepartureDate",getValue(tieYouExRailInfo,"DepartureDate"));
               cs.setString("Dcity",getValue(tieYouExRailInfo,"Dcity"));
               cs.setString("Acity",getValue(tieYouExRailInfo,"Acity"));
               cs.setString(Common.SeatClass,getValue(tieYouExRailInfo,Common.SeatClass));
               cs.setString("TrainNo",getValue(tieYouExRailInfo,"TrainNo"));
               cs.setString("DataChange_LastTime","");//站位符
               cs.setString("FromStationName",getValue(tieYouExRailInfo,"FromStationName"));
               cs.registerOutParameter(1, Types.BIGINT);
               return cs;
           }
       }, new CallableStatementCallback() {
           public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException
           {
               boolean isSuccess = cs.execute();
               return cs.getString(1);
           }
       });
        return result.toString();
    }

    public void insertTieYouExRailUserInfo(final Map tieYouExRailUserInfo,final String ExRailInfoID,final boolean isWrite,final boolean isCheck)
    {
        try
        {
            if(isCheck)
            {
                Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from InfoSecurity_ExRailUserInfo where ExRailInfoID=?",ExRailInfoID);
                Set<Map.Entry> entries = oldMainInfo.entrySet();
                for(Map.Entry<String,Object> entry : entries)
                {
                    if(!entry.getValue().equals(getValue(tieYouExRailUserInfo,entry.getKey())))
                    {
                        logger.info("tieYouExRailUserInfo信息比对结果"+entry.getKey()+":"+"老系统的值"+entry.getValue()+"新系统的值"+getValue(tieYouExRailUserInfo,entry.getKey()));
                    }
                }
            }
        }catch (Exception exp)
        {
            logger.warn("insertTieYouExRailUserInfo比对数据的时候出现异常"+exp.getMessage());
        }
        if(!isWrite)
            return;//如果不写入就直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
                   String params = "";
                   for(int i=0;i<7-1;i++)
                   {
                       params += "?,";
                   }
                   params += "?";
                   String storedProc = "{call sp3_InfoSecurity_ExRailUserInfo_i("+params+")}";// 调用的dbo.sp3_InfoSecurity_HotelGroupInfo_i
                   CallableStatement cs = con.prepareCall(storedProc);
                   cs.setString("ExRailUserID","");//这个是自增主键 站位符
                   cs.setString("ExRailInfoID",ExRailInfoID);
                   cs.setString(Common.PassengerName,getValue(tieYouExRailUserInfo,Common.PassengerName));
                   cs.setString("PassengerIDType",getValue(tieYouExRailUserInfo,"PassengerIDType"));
                   cs.setString("PassengerIDCode",getValue(tieYouExRailUserInfo,"PassengerIDCode"));
                   cs.setString("DataChange_LastTime","");//站位符
                   cs.setString("InsuranceType",getValue(tieYouExRailUserInfo,"InsuranceType"));
                   return cs;
               }
           }, new CallableStatementCallback() {
               public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException
               {
                   boolean isSuccess = cs.execute();
                   return isSuccess;
               }
           });
    }
}
