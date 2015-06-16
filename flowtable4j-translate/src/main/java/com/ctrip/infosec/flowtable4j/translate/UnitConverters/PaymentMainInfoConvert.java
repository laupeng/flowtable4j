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
public class PaymentMainInfoConvert implements Convert
{
    private static Logger logger = LoggerFactory.getLogger(PaymentMainInfoConvert.class);

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
        if(checkType.equals("0") || checkType.equals("2"))
        {
            dataFact.paymentMainInfo.put(Common.BankValidationMethod,getValue(data,Common.BankValidationMethod));
            dataFact.paymentMainInfo.put(Common.ClientIDOrIP,getValue(data,Common.ClientIDOrIP));
            dataFact.paymentMainInfo.put(Common.ClientOS,getValue(data,Common.ClientOS));
            dataFact.paymentMainInfo.put(Common.DeductType,getValue(data,Common.DeductType));
            dataFact.paymentMainInfo.put(Common.IsPrepaID,getValue(data,Common.IsPrepaID));
            dataFact.paymentMainInfo.put(Common.PayMethod,getValue(data,Common.PayMethod));
            dataFact.paymentMainInfo.put(Common.PayValidationMethod,getValue(data,Common.PayValidationMethod));
            dataFact.paymentMainInfo.put(Common.ValidationFailsReason,getValue(data,Common.ValidationFailsReason));
        }else if(checkType.equals("1"))
        {
            final String reqIdStr = getValue(data,Common.OldReqID);
            commonOperation.fillPaymentMainInfo(dataFact,reqIdStr);
        }

    }

    @Override
    public void writeData(DataFact dataFact,final String reqId, boolean isWrite, boolean isCheck)
    {
        final Map paymentMainInfo = dataFact.paymentMainInfo;
        try
        {
            if(isCheck)
            {
                Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_paymentMainInfo where reqid=?",reqId);
                Set<Map.Entry> entries = oldMainInfo.entrySet();
                for(Map.Entry<String,Object> entry : entries)
                {
                    if(!entry.getValue().equals(getValue(paymentMainInfo,entry.getKey())))
                    {
                        logger.info("cardInfo信息比对结果    "+entry.getKey()+": "+"老系统的值:"+entry.getValue()+"\t"+"新系统的值:"+getValue(paymentMainInfo,entry.getKey()));
                    }
                }
            }
        }catch (Exception exp)
        {
            logger.warn("insertPaymentMainInfo比对数据的时候出现异常"+exp.getMessage());
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
}
