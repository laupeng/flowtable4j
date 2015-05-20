package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
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

/**
 * 这里面负责把*Info的数据写到数据库
 * Created by lpxie on 15-5-15.
 */
public class CommonWriteSources
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

    public void insertMainInfo(final Map mainInfo)
    {
        if(mainInfo == null || mainInfo.size()<1)
            return;
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
                   String params = "";
                   for(int i=0;i<mainInfo.size()-1;i++)
                   {
                       params += "?,";
                   }
                   params += "?";
                   String storedProc = "{call sp3_InfoSecurity_MainInfo_i("+params+")}";// 调用的sqldbo.sp3_InfoSecurity_MainInfo_i
                   CallableStatement cs = con.prepareCall(storedProc);
                   Set <Map.Entry<String, Object>> entries = mainInfo.entrySet();
                   for(Map.Entry<String, Object> entry  : entries)
                   {
                       cs.setString(entry.getKey(),entry.getValue().toString());
                   }
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

    public void insertContactInfo(final Map contactInfo)
    {
        if(contactInfo == null || contactInfo.size()<1)
            return;
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
                   String params = "";
                   for(int i=0;i<contactInfo.size()-1;i++)
                   {
                       params += "?,";
                   }
                   params += "?";
                   String storedProc = "{call sp3_InfoSecurity_ContactInfo_i("+params+")}";// dbo.sp3_InfoSecurity_ContactInfo_i
                   CallableStatement cs = con.prepareCall(storedProc);
                   Set <Map.Entry<String, Object>> entries = contactInfo.entrySet();
                   for(Map.Entry<String, Object> entry  : entries)
                   {
                       cs.setString(entry.getKey(),entry.getValue().toString());
                   }
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

    public void insertUserInfo(final Map userInfo)
    {
        if(userInfo == null || userInfo.size()<1)
            return;
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
                   String params = "";
                   for(int i=0;i<userInfo.size()-1;i++)
                   {
                       params += "?,";
                   }
                   params += "?";
                   String storedProc = "{call sp3_InfoSecurity_UserInfo_i("+params+")}";// dbo.sp3_InfoSecurity_UserInfo_i
                   CallableStatement cs = con.prepareCall(storedProc);
                   Set <Map.Entry<String, Object>> entries = userInfo.entrySet();
                   for(Map.Entry<String, Object> entry  : entries)
                   {
                       cs.setString(entry.getKey(),entry.getValue().toString());
                   }
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

    public void insertIpInfo(final Map ipInfo)
    {
        if(ipInfo == null || ipInfo.size()<1)
            return;
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
                   String params = "";
                   for(int i=0;i<ipInfo.size()-1;i++)
                   {
                       params += "?,";
                   }
                   params += "?";
                   String storedProc = "{call sp3_InfoSecurity_IPInfo_i("+params+")}";// dbo.sp3_InfoSecurity_IPInfo_i
                   CallableStatement cs = con.prepareCall(storedProc);
                   Set <Map.Entry<String, Object>> entries = ipInfo.entrySet();
                   for(Map.Entry<String, Object> entry  : entries)
                   {
                       cs.setString(entry.getKey(),entry.getValue().toString());
                   }
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

    public void insertOtherInfo(final Map otherInfo)
    {
        if(otherInfo == null || otherInfo.size()<1)
            return;
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
                   String params = "";
                   for(int i=0;i<otherInfo.size()-1;i++)
                   {
                       params += "?,";
                   }
                   params += "?";
                   String storedProc = "{call sp3_InfoSecurity_OtherInfo_i("+params+")}";// dbo.sp3_InfoSecurity_OtherInfo_i
                   CallableStatement cs = con.prepareCall(storedProc);
                   Set <Map.Entry<String, Object>> entries = otherInfo.entrySet();
                   for(Map.Entry<String, Object> entry  : entries)
                   {
                       cs.setString(entry.getKey(),entry.getValue().toString());
                   }
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

    public void insertDealInfo(final Map dealInfo)
    {
        if(dealInfo == null || dealInfo.size()<1)
            return;
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
                   String params = "";
                   for(int i=0;i<dealInfo.size()-1;i++)
                   {
                       params += "?,";
                   }
                   params += "?";
                   String storedProc = "{call sp3_InfoSecurity_DealInfo_i("+params+")}";//dbo.sp3_InfoSecurity_DealInfo_i
                   CallableStatement cs = con.prepareCall(storedProc);
                   Set <Map.Entry<String, Object>> entries = dealInfo.entrySet();
                   for(Map.Entry<String, Object> entry  : entries)
                   {
                       cs.setString(entry.getKey(),entry.getValue().toString());
                   }
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

    public void insertCardInfo(final Map cardInfo)
    {
        if(cardInfo == null || cardInfo.size()<1)
            return;
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<cardInfo.size()-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_CardInfo_i("+params+")}";//dbo.sp3_InfoSecurity_CardInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               Set <Map.Entry<String, Object>> entries = cardInfo.entrySet();
               for(Map.Entry<String, Object> entry  : entries)
               {
                   cs.setString(entry.getKey(),entry.getValue().toString());
               }
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

    public void insertCorporationInfo(final Map CorporationInfo)
    {
        if(CorporationInfo == null || CorporationInfo.size()<1)
            return;
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<CorporationInfo.size()-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_CorporationInfo_i("+params+")}";//dbo.sp3_InfoSecurity_CorporationInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               Set <Map.Entry<String, Object>> entries = CorporationInfo.entrySet();
               for(Map.Entry<String, Object> entry  : entries)
               {
                   cs.setString(entry.getKey(),entry.getValue().toString());
               }
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

    public void insertDeviceIDInfo(final Map DeviceIDInfo)
    {
        if(DeviceIDInfo == null || DeviceIDInfo.size()<1)
            return;
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<DeviceIDInfo.size()-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_DeviceIDInfo_i("+params+")}";//dbo.sp3_InfoSecurity_DeviceIDInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               Set <Map.Entry<String, Object>> entries = DeviceIDInfo.entrySet();
               for(Map.Entry<String, Object> entry  : entries)
               {
                   cs.setString(entry.getKey(),entry.getValue().toString());
               }
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

    public void insertPaymentInfo(final Map PaymentInfo)
    {
        if(PaymentInfo == null || PaymentInfo.size()<1)
            return;
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<PaymentInfo.size()-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_PaymentInfo_i("+params+")}";//dbo.sp3_InfoSecurity_PaymentInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               Set <Map.Entry<String, Object>> entries = PaymentInfo.entrySet();
               for(Map.Entry<String, Object> entry  : entries)
               {
                   cs.setString(entry.getKey(),entry.getValue().toString());
               }
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

    public void insertPaymentMainInfo(final Map paymentMainInfo)
    {
        if(paymentMainInfo == null || paymentMainInfo.size()<1)
            return;
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<paymentMainInfo.size()-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_PaymentMainInfo_i("+params+")}";//dbo.sp3_InfoSecurity_PaymentMainInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               Set <Map.Entry<String, Object>> entries = paymentMainInfo.entrySet();
               for(Map.Entry<String, Object> entry  : entries)
               {
                   cs.setString(entry.getKey(),entry.getValue().toString());
               }
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
