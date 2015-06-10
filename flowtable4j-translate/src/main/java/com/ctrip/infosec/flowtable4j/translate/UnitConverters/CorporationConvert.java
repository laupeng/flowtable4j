package com.ctrip.infosec.flowtable4j.translate.UnitConverters;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
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
 * Created by lpxie on 15-6-10.
 */
public class CorporationConvert implements Convert
{
    private static Logger logger = LoggerFactory.getLogger(CorporationConvert.class);

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

    @Override
    public void completeData(DataFact dataFact, Map data)
    {
        //这里分checkType是1和0、2的情况
        String checkType = getValue(data, Common.CheckType);
        if(checkType.equals("0") || checkType.equals("2"))
        {
            //Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_corporationInfo where reqid=?",reqId);
        }else if(checkType.equals("1"))
        {
            dataFact.corporationInfo.put(Common.CanAccountPay,getValue(data,Common.CanAccountPay));
            dataFact.corporationInfo.put(Common.CompanyType,getValue(data,Common.CompanyType));
            dataFact.corporationInfo.put(Common.Corp_PayType,getValue(data,Common.Corp_PayType));
        }
    }

    @Override
    public void writeData(DataFact dataFact,final String reqId, boolean isWrite, boolean isCheck)
    {
        final Map corporationInfo = dataFact.corporationInfo;
        try{
            if(isCheck)
            {
                Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_corporationInfo where reqid=?",reqId);
                Set<Map.Entry> entries = oldMainInfo.entrySet();
                for(Map.Entry<String,Object> entry : entries)
                {
                    if(!entry.getValue().equals(getValue(corporationInfo,entry.getKey())))
                    {
                        logger.info("corporationInfo信息比对结果    "+entry.getKey()+": "+"老系统的值:"+entry.getValue()+"\t"+"新系统的值:"+getValue(corporationInfo,entry.getKey()));
                    }
                }
            }
        }catch (Exception exp)
        {
            logger.warn("insertCorporationInfo比对数据的时候出现异常"+exp.getMessage());
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
}
