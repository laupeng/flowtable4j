package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;

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

    public void insertMainInfo(final Map mainInfo,final String reqId,boolean isWrite,boolean isCheck)
    {
        //查询老系统的值 用于比对
        if(isCheck)
        {
            Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_maininfo where reqid=?",reqId);
            Set<Map.Entry> entries = oldMainInfo.entrySet();
            for(Map.Entry<String,Object> entry : entries)
            {
                if(!entry.getValue().equals(getValue(mainInfo,entry.getKey())))
                {
                    logger.info("mainInfo信息比对结果"+entry.getKey()+":"+"老系统的值"+entry.getValue()+"新系统的值"+getValue(mainInfo,entry.getKey()));
                }
            }
        }
        if(!isWrite)
            return;//如果不写入这直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
                   String params = "";
                   for(int i=0;i<29-1;i++)
                   {
                       params += "?,";
                   }
                   params += "?";
                   String storedProc = "{call sp3_InfoSecurity_MainInfo_i("+params+")}";// 调用的sqldbo.sp3_InfoSecurity_MainInfo_i
                   CallableStatement cs = con.prepareCall(storedProc);
                   cs.setString(Common.ReqID,reqId);
                   cs.setString(Common.OrderType,getValue(mainInfo,Common.OrderType));
                   cs.setString(Common.OrderID,getValue(mainInfo,Common.OrderID));
                   cs.setString(Common.OrderDate,getValue(mainInfo,Common.OrderDate));
                   cs.setString(Common.Amount,getValue(mainInfo,Common.Amount));
                   cs.setString(Common.IsOnline,getValue(mainInfo,Common.IsOnline));
                   cs.setString(Common.Serverfrom,getValue(mainInfo,Common.Serverfrom));
                   cs.setString(Common.CheckType,getValue(mainInfo,Common.CheckType));
                   cs.setString(Common.CreateDate,getValue(mainInfo,Common.CreateDate));
                   cs.setString(Common.LastCheck,getValue(mainInfo,Common.LastCheck));
                   cs.setString(Common.RefNo,getValue(mainInfo,Common.RefNo));
                   cs.setString(Common.WirelessClientNo,getValue(mainInfo,Common.WirelessClientNo));
                   cs.setString(Common.CorporationID,getValue(mainInfo,Common.CorporationID));
                   cs.setString("DataChange_LastTime","");
                   cs.setString(Common.MerchantID,getValue(mainInfo,Common.MerchantID));
                   cs.setString("ProcessingType",getValue(mainInfo,"ProcessingType"));
                   cs.setString(Common.SubOrderType,getValue(mainInfo,Common.SubOrderType));
                   cs.setString("ApplyRemark",getValue(mainInfo,"ApplyRemark"));
                   cs.setString(Common.ClientID,getValue(mainInfo,Common.ClientID));
                   cs.setString(Common.ClientVersion,getValue(mainInfo,Common.ClientVersion));
                   cs.setString(Common.MerchantOrderID,getValue(mainInfo,Common.MerchantOrderID));
                   cs.setString("OrderProductName",getValue(mainInfo,"OrderProductName"));
                   cs.setString("PayExpiryDate",getValue(mainInfo,"PayExpiryDate"));
                   cs.setString("PreAuthorizedAmount",getValue(mainInfo,"PreAuthorizedAmount"));
                   cs.setString("RiskCountrolDeadline",getValue(mainInfo,"RiskCountrolDeadline"));
                   cs.setString("TotalDiscountAmount",getValue(mainInfo,"TotalDiscountAmount"));
                   cs.setString("Currency",getValue(mainInfo,"Currency"));
                   cs.setString("OriginalAmount",getValue(mainInfo,"OriginalAmount"));
                   cs.setString("SalesType",getValue(mainInfo,"SalesType"));
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

    public void insertContactInfo(final Map contactInfo,final String reqId,boolean isWrite,boolean isCheck)
    {
        if(isCheck)
        {
            Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_contactinfo where reqid=?",reqId);
            Set<Map.Entry> entries = oldMainInfo.entrySet();
            for(Map.Entry<String,Object> entry : entries)
            {
                if(!entry.getValue().equals(getValue(contactInfo,entry.getKey())))
                {
                    logger.info("contactInfo信息比对结果"+entry.getKey()+":"+"老系统的值"+entry.getValue()+"新系统的值"+getValue(contactInfo,entry.getKey()));
                }
            }
        }
        if(!isWrite)
            return;//如果不写入就直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
                   String params = "";
                   for(int i=0;i<15-1;i++)
                   {
                       params += "?,";
                   }
                   params += "?";
                   String storedProc = "{call sp3_InfoSecurity_ContactInfo_i("+params+")}";// dbo.sp3_InfoSecurity_ContactInfo_i
                   CallableStatement cs = con.prepareCall(storedProc);
                   cs.setString(Common.ReqID,reqId);
                   cs.setString(Common.ContactName,getValue(contactInfo,Common.ContactName));
                   cs.setString(Common.MobilePhone,getValue(contactInfo,Common.MobilePhone));
                   cs.setString(Common.ContactEMail,getValue(contactInfo,Common.ContactEMail));
                   cs.setString(Common.ContactTel,getValue(contactInfo,Common.ContactTel));
                   cs.setString(Common.ContactFax,getValue(contactInfo,Common.ContactFax));
                   cs.setString(Common.ZipCode,getValue(contactInfo,Common.ZipCode));
                   cs.setString(Common.TelCall,getValue(contactInfo,Common.TelCall));
                   cs.setString(Common.ForeignMobilePhone,getValue(contactInfo,Common.ForeignMobilePhone));
                   cs.setString(Common.SendTickerAddr,getValue(contactInfo,Common.SendTickerAddr));
                   cs.setString(Common.PostAddress,getValue(contactInfo,Common.PostAddress));
                   cs.setString("DataChange_LastTime","");
                   cs.setString("MobilePhoneProvince",getValue(contactInfo,Common.MobilePhoneProvince));
                   cs.setString("MobilePhoneCity",getValue(contactInfo,Common.MobilePhoneCity));
                   cs.setString(Common.Remark,getValue(contactInfo,Common.Remark));
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

    public void insertUserInfo(final Map userInfo,final String reqId,boolean isWrite,boolean isCheck)
    {
        if(isCheck)
        {
            Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_contactinfo where reqid=?",reqId);
            Set<Map.Entry> entries = oldMainInfo.entrySet();
            for(Map.Entry<String,Object> entry : entries)
            {
                if(!entry.getValue().equals(getValue(userInfo,entry.getKey())))
                {
                    logger.info("userInfo信息比对结果"+entry.getKey()+":"+"老系统的值"+entry.getValue()+"新系统的值"+getValue(userInfo,entry.getKey()));
                }
            }
        }
        if(!isWrite)
            return;//如果不写入就直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
                   String params = "";
                   for(int i=0;i<20-1;i++)
                   {
                       params += "?,";
                   }
                   params += "?";
                   String storedProc = "{call sp3_InfoSecurity_UserInfo_i("+params+")}";// dbo.sp3_InfoSecurity_UserInfo_i
                   CallableStatement cs = con.prepareCall(storedProc);
                   cs.setString(Common.ReqID,reqId);
                   cs.setString(Common.Uid,getValue(userInfo,Common.Uid));
                   cs.setString(Common.UserPassword,getValue(userInfo,Common.UserPassword));
                   cs.setString(Common.SignUpDate,getValue(userInfo,Common.SignUpDate));
                   cs.setString(Common.CusCharacter,getValue(userInfo,Common.CusCharacter));
                   cs.setString(Common.Experience,getValue(userInfo,Common.Experience));
                   cs.setString(Common.VipGrade,getValue(userInfo,Common.VipGrade));
                   cs.setString(Common.IsTempUser,getValue(userInfo,Common.IsTempUser));
                   cs.setString(Common.TotalPenalty,getValue(userInfo,Common.TotalPenalty));
                   cs.setString(Common.IsUidHasBlackCard,getValue(userInfo,Common.IsUidHasBlackCard));
                   cs.setString("DataChange_LastTime","");
                   cs.setString(Common.BindedMobilePhone,getValue(userInfo,Common.BindedMobilePhone));
                   cs.setString(Common.BindedEmail,getValue(userInfo,Common.BindedEmail));
                   cs.setString(Common.RelatedMobilephone,getValue(userInfo,Common.RelatedMobilephone));
                   cs.setString(Common.RelatedEMail,getValue(userInfo,Common.RelatedEMail));
                   cs.setString("IsBindedMobilePhone",getValue(userInfo,"IsBindedMobilePhone"));
                   cs.setString("IsBindedEmail",getValue(userInfo,"IsBindedEmail"));
                   cs.setString(Common.City,getValue(userInfo,Common.City));
                   cs.setString("Address",getValue(userInfo,"Address"));
                   cs.setString("Sourceid",getValue(userInfo,"Sourceid"));
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

    public void insertIpInfo(final Map ipInfo,final String reqId,boolean isWrite,boolean isCheck)
    {
        if(isCheck)
        {
            Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_ipinfo where reqid=?",reqId);
            Set<Map.Entry> entries = oldMainInfo.entrySet();
            for(Map.Entry<String,Object> entry : entries)
            {
                if(!entry.getValue().equals(getValue(ipInfo,entry.getKey())))
                {
                    logger.info("ipInfo信息比对结果"+entry.getKey()+":"+"老系统的值"+entry.getValue()+"新系统的值"+getValue(ipInfo,entry.getKey()));
                }
            }
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
                   String storedProc = "{call sp3_InfoSecurity_IPInfo_i("+params+")}";// dbo.sp3_InfoSecurity_IPInfo_i
                   CallableStatement cs = con.prepareCall(storedProc);
                   cs.setString(Common.ReqID,reqId);
                   cs.setString(Common.UserIPValue,getValue(ipInfo,Common.UserIPValue));
                   cs.setString(Common.UserIPAdd,getValue(ipInfo,Common.UserIPAdd));
                   cs.setString(Common.IPCountry,getValue(ipInfo,Common.IPCountry));
                   cs.setString(Common.IPCity,getValue(ipInfo,Common.IPCity));
                   cs.setString(Common.Continent,getValue(ipInfo,Common.Continent));
                   cs.setString("DataChange_LastTime","");
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

    public void insertOtherInfo(final Map otherInfo,final String reqId,boolean isWrite,boolean isCheck)
    {
        if(isCheck)
        {
            Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_otherinfo where reqid=?",reqId);
            Set<Map.Entry> entries = oldMainInfo.entrySet();
            for(Map.Entry<String,Object> entry : entries)
            {
                if(!entry.getValue().equals(getValue(otherInfo,entry.getKey())))
                {
                    logger.info("otherInfo信息比对结果"+entry.getKey()+":"+"老系统的值"+entry.getValue()+"新系统的值"+getValue(otherInfo,entry.getKey()));
                }
            }
        }
        if(!isWrite)
            return;//如果不写入就直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
               String params = "";
               for(int i=0;i<6-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_OtherInfo_i("+params+")}";// dbo.sp3_InfoSecurity_OtherInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               cs.setString(Common.ReqID,reqId);
               cs.setString(Common.OrderToSignUpDate,getValue(otherInfo,Common.OrderToSignUpDate));
               cs.setString(Common.TakeOffToOrderDate,getValue(otherInfo,Common.TakeOffToOrderDate));
               cs.setString("DataChange_LastTime","");
               cs.setString("OrderInfoExternalURL",getValue(otherInfo,"OrderInfoExternalURL"));
               cs.setString("Bid",getValue(otherInfo,"Bid"));
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

    public void insertCorporationInfo(final Map corporationInfo,final  String reqId,boolean isWrite,boolean isCheck)
    {
        if(isCheck)
        {
            Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_corporationInfo where reqid=?",reqId);
            Set<Map.Entry> entries = oldMainInfo.entrySet();
            for(Map.Entry<String,Object> entry : entries)
            {
                if(!entry.getValue().equals(getValue(corporationInfo,entry.getKey())))
                {
                    logger.info("corporationInfo信息比对结果"+entry.getKey()+":"+"老系统的值"+entry.getValue()+"新系统的值"+getValue(corporationInfo,entry.getKey()));
                }
            }
        }
        if(!isWrite)
            return;//如果不写入就直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<5-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_CorporationInfo_i("+params+")}";//dbo.sp3_InfoSecurity_CorporationInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               cs.setString(Common.ReqID,reqId);
               cs.setString(Common.Corp_PayType,getValue(corporationInfo,Common.Corp_PayType));
               cs.setString(Common.CanAccountPay,getValue(corporationInfo,Common.CanAccountPay));
               cs.setString(Common.CompanyType,getValue(corporationInfo,Common.CompanyType));
               cs.setString("DataChange_LastTime","");
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

    public void insertDeviceIDInfo(final Map deviceIDInfo,final String reqId,boolean isWrite,boolean isCheck)
    {
        if(isCheck)
        {
            Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_deviceIDInfo where reqid=?",reqId);
            Set<Map.Entry> entries = oldMainInfo.entrySet();
            for(Map.Entry<String,Object> entry : entries)
            {
                if(!entry.getValue().equals(getValue(deviceIDInfo,entry.getKey())))
                {
                    logger.info("deviceIDInfo信息比对结果"+entry.getKey()+":"+"老系统的值"+entry.getValue()+"新系统的值"+getValue(deviceIDInfo,entry.getKey()));
                }
            }
        }
        if(!isWrite)
            return;//如果不写入就直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<3-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_DeviceIDInfo_i("+params+")}";//dbo.sp3_InfoSecurity_DeviceIDInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               cs.setString(Common.ReqID,reqId);
               cs.setString(Common.DID,getValue(deviceIDInfo,Common.DID));
 /*              Date now = new Date(System.currentTimeMillis());
               SimpleDateFormat format = new SimpleDateFormat("yyyy:HH:DD");
               String nowStr = format.format(now);*/
               cs.setString("DataChange_LastTime","");
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

    //这里面的PaymentInfoID是主键，考虑是否添加这个字段，数据库的配置是自增
    public String insertPaymentInfo(final Map paymentInfo,final String reqId,boolean isWrite,boolean isCheck)
    {
        if(isCheck)
        {
            Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_paymentInfo where reqid=?",reqId);
            Set<Map.Entry> entries = oldMainInfo.entrySet();
            for(Map.Entry<String,Object> entry : entries)
            {
                if(!entry.getValue().equals(getValue(paymentInfo,entry.getKey())))
                {
                    logger.info("paymentInfo信息比对结果"+entry.getKey()+":"+"老系统的值"+entry.getValue()+"新系统的值"+getValue(paymentInfo,entry.getKey()));
                }
            }
        }
        if(!isWrite)
            return "";//如果不写入就直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<7-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_PaymentInfo_i("+params+")}";//dbo.sp3_InfoSecurity_PaymentInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               cs.setString("PaymentInfoID","");
               cs.setString(Common.ReqID,reqId);
               cs.setString(Common.PrepayType,getValue(paymentInfo,Common.PrepayType));
               cs.setString(Common.IsGuarantee,getValue(paymentInfo,Common.IsGuarantee));
               cs.setString(Common.Amount,getValue(paymentInfo,Common.Amount));
               cs.setString("BillNo",getValue(paymentInfo,"BillNo"));
               cs.setString("DataChange_LastTime","");
               cs.registerOutParameter(1,Types.BIGINT);
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

    public void insertCardInfo(final Map cardInfo,final String reqId,final String paymentInfoID,boolean isWrite,boolean isCheck)
    {
        if(isCheck)
        {
            Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_cardInfo where reqid=?",reqId);
            Set<Map.Entry> entries = oldMainInfo.entrySet();
            for(Map.Entry<String,Object> entry : entries)
            {
                if(!entry.getValue().equals(getValue(cardInfo,entry.getKey())))
                {
                    logger.info("cardInfo信息比对结果"+entry.getKey()+":"+"老系统的值"+entry.getValue()+"新系统的值"+getValue(cardInfo,entry.getKey()));
                }
            }
        }
        if(!isWrite)
            return ;//如果不写入就直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<23-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_CardInfo_i("+params+")}";//dbo.sp3_InfoSecurity_CardInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               cs.setString("PaymentInfoID",paymentInfoID);
               cs.setString(Common.ReqID,reqId);
               cs.setString(Common.CardInfoID,getValue(cardInfo,Common.CardInfoID));
               cs.setString(Common.CreditCardType,getValue(cardInfo,Common.CreditCardType));
               cs.setString(Common.InfoID,getValue(cardInfo,Common.InfoID));
               cs.setString(Common.CValidityCode,getValue(cardInfo,Common.CValidityCode));
               cs.setString(Common.CCardNoCode,getValue(cardInfo,Common.CCardNoCode));
               cs.setString(Common.CardHolder,getValue(cardInfo,Common.CardHolder));
               cs.setString(Common.CardBin,getValue(cardInfo,Common.CardBin));
               cs.setString(Common.CCardLastNoCode,getValue(cardInfo,Common.CCardLastNoCode));
               cs.setString(Common.CCardPreNoCode,getValue(cardInfo,Common.CCardPreNoCode));

               cs.setString(Common.StateName,getValue(cardInfo,Common.StateName));
               cs.setString(Common.BillingAddress,getValue(cardInfo,Common.BillingAddress));
               cs.setString(Common.Nationality,getValue(cardInfo,Common.Nationality));
               cs.setString(Common.Nationalityofisuue,getValue(cardInfo,Common.Nationalityofisuue));
               cs.setString(Common.BankOfCardIssue,getValue(cardInfo,Common.BankOfCardIssue));

               cs.setString(Common.CardBinIssue,getValue(cardInfo,Common.CardBinIssue));
               cs.setString(Common.CardBinBankOfCardIssue,getValue(cardInfo,Common.CardBinBankOfCardIssue));
               cs.setString(Common.IsForigenCard,getValue(cardInfo,Common.IsForigenCard));
               cs.setString("DataChange_LastTime","");
               cs.setString("CardNoRefID",getValue(cardInfo,"CardNoRefID"));
               cs.setString("BranchCity",getValue(cardInfo,"BranchCity"));
               cs.setString("BranchProvince",getValue(cardInfo,"BranchProvince"));

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

    public void insertPaymentMainInfo(final Map paymentMainInfo,final String reqId,boolean isWrite,boolean isCheck)
    {
        if(isCheck)
        {
            Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_paymentMainInfo where reqid=?",reqId);
            Set<Map.Entry> entries = oldMainInfo.entrySet();
            for(Map.Entry<String,Object> entry : entries)
            {
                if(!entry.getValue().equals(getValue(paymentMainInfo,entry.getKey())))
                {
                    logger.info("cardInfo信息比对结果"+entry.getKey()+":"+"老系统的值"+entry.getValue()+"新系统的值"+getValue(paymentMainInfo,entry.getKey()));
                }
            }
        }
        if(!isWrite)
            return ;//如果不写入就直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<10-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call sp3_InfoSecurity_PaymentMainInfo_i("+params+")}";//dbo.sp3_InfoSecurity_PaymentMainInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               cs.setString(Common.ReqID,reqId);
               cs.setString(Common.IsPrepaID,getValue(paymentMainInfo,Common.IsPrepaID));
               cs.setString("DataChange_LastTime","");
               cs.setString(Common.PayMethod,getValue(paymentMainInfo,Common.PayMethod));
               cs.setString(Common.PayValidationMethod,getValue(paymentMainInfo,Common.PayValidationMethod));
               cs.setString(Common.BankValidationMethod,getValue(paymentMainInfo,Common.BankValidationMethod));
               cs.setString(Common.ValidationFailsReason,getValue(paymentMainInfo,Common.ValidationFailsReason));
               cs.setString(Common.ClientOS,getValue(paymentMainInfo,Common.ClientOS));
               cs.setString(Common.ClientIDOrIP,getValue(paymentMainInfo,Common.ClientIDOrIP));
               cs.setString(Common.DeductType,getValue(paymentMainInfo,Common.DeductType));

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

    public void insertFlowInfo(final Map flowInfo,final String field1,final String field2,final String tableName,boolean isWrite,boolean isCheck)
    {
        final String reqId = getValue(flowInfo, Common.ReqID);
        final String fieldValue1 = getValue(flowInfo,field1);
        final String fieldValue2 = getValue(flowInfo,field2);

        if(isCheck)
        {
            Map oldMainInfo = riskCtrlPreProcDBTemplate.queryForMap("select top 1 * from "+tableName+" where reqid=?",reqId);
            {
                if(!getValue(oldMainInfo,field1).equals(fieldValue1))
                {
                    logger.info(tableName+"信息比对结果"+field1+":"+"老系统的值"+getValue(oldMainInfo,field1)+"新系统的值"+fieldValue1);
                }
                if(!getValue(oldMainInfo,field2).equals(fieldValue2))
                {
                    logger.info(tableName+"信息比对结果"+field2+":"+"老系统的值"+getValue(oldMainInfo,field2)+"新系统的值"+fieldValue2);
                }
            }
        }
        if(!isWrite)
            return ;//如果不写入就直接返回

        logger.info("写入流量表："+tableName+"\t"+field1+"\t"+fieldValue1+"\t"+field2+"\t"+fieldValue2);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String nowTime = format.format(new Date(System.currentTimeMillis()));
        Object result = riskCtrlPreProcDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
                    String storedProc = "{call spA_"+tableName+"_i(?,?,?,?,?)}";// 调用
                    CallableStatement cs = con.prepareCall(storedProc);
                   cs.setString("ReqID",reqId);
                    cs.setString(field1,fieldValue1);
                    cs.setString(field2,fieldValue2);
                    cs.setString("CreateDate",nowTime);
                    cs.setString("DataChange_LastTime","");
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
