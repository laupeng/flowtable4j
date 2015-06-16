package com.ctrip.infosec.flowtable4j.translate.UnitConverters;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import com.ctrip.infosec.flowtable4j.translate.service.CommonOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Created by lpxie on 15-6-10.
 */
public class ContactInfoConvert implements Convert
{
    private static Logger logger = LoggerFactory.getLogger(ContactInfoConvert.class);

    @Resource(name="allTemplates")
    private AllTemplates allTemplates;

    JdbcTemplate cardRiskDBTemplate = null;
    JdbcTemplate riskCtrlPreProcDBTemplate = null;
    JdbcTemplate cUSRATDBTemplate = null;

    @Autowired
    CommonOperation commonOperation;
    /**
     * 初始化jndi
     */
    private void init()
    {
        cardRiskDBTemplate = allTemplates.getCardRiskDBTemplate();
        riskCtrlPreProcDBTemplate = allTemplates.getRiskCtrlPreProcDBTemplate();
        cUSRATDBTemplate = allTemplates.getcUSRATDBTemplate();
    }

    @Override
    public void completeData(DataFact dataFact, Map data)
    {
        String checkType = getValue(data,Common.CheckType);
        if(checkType.equals("0") || checkType.equals("1"))
        {
            dataFact.contactInfo.put(Common.MobilePhone,getValue(data,Common.MobilePhone));
            dataFact.contactInfo.put(Common.ContactName,getValue(data,Common.ContactName));
            dataFact.contactInfo.put(Common.ContactTel,getValue(data,Common.ContactTel));
            dataFact.contactInfo.put(Common.ContactEMail,getValue(data,Common.ContactEMail));
            dataFact.contactInfo.put(Common.SendTickerAddr,getValue(data,Common.SendTickerAddr));
        }else if(checkType.equals("2"))
        {
            final String reqIdStr = getValue(data,Common.OldReqID);
            commonOperation.fillProductContact(dataFact, reqIdStr);
        }

    }

    @Override
    public void writeData(DataFact dataFact,final String reqId, boolean isWrite, boolean isCheck)
    {
        final Map contactInfo = dataFact.contactInfo;
        try
        {
            if(isCheck)
            {
                Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_contactinfo where reqid=?",reqId);
                Set<Map.Entry> entries = oldMainInfo.entrySet();
                for(Map.Entry<String,Object> entry : entries)
                {
                    if(!entry.getValue().equals(getValue(contactInfo,entry.getKey())))
                    {
                        logger.info("contactInfo信息比对结果    "+entry.getKey()+": "+"老系统的值:"+entry.getValue()+"\t"+"新系统的值:"+getValue(contactInfo,entry.getKey()));
                    }
                }
            }
        }catch (Exception exp)
        {
            logger.warn("insertContactInfo比对数据的时候出现异常"+exp.getMessage());
        }
        if(!isWrite)
            return;//如果不写入就直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator()
           {
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
}
