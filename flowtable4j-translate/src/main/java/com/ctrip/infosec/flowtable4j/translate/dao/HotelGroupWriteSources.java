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
import java.util.Map;
import java.util.Set;

import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;

/**
 * Created by lpxie on 15-5-18.
 */
public class HotelGroupWriteSources
{
    private static Logger logger = LoggerFactory.getLogger(CommonWriteSources.class);

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

    public void insertHotelGroupInfo(final Map hotelGroupInfo,final String reqId)
    {
        if(hotelGroupInfo == null || hotelGroupInfo.size()<1)
            return;
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<9-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_HotelGroupInfo_i("+params+")}";// 调用的dbo.sp3_InfoSecurity_HotelGroupInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               cs.setString("HotelGroupOrderID","");//这个是自增主键 站位符
               cs.setString(Common.ReqID,reqId);
               cs.setString(Common.ProductID,getValue(hotelGroupInfo,Common.ProductID));
               cs.setString(Common.ProductName,getValue(hotelGroupInfo,Common.ProductName));
               cs.setString(Common.City,getValue(hotelGroupInfo,Common.City));
               cs.setString(Common.Price,getValue(hotelGroupInfo,Common.Price));
               cs.setString(Common.Quantity,getValue(hotelGroupInfo,Common.Quantity));
               cs.setString("DataChange_LastTime","");//站位符
               cs.setString(Common.ProductType,getValue(hotelGroupInfo,Common.ProductType));
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
