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
public class IPInfoConvert implements Convert
{
    private static Logger logger = LoggerFactory.getLogger(IPInfoConvert.class);

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
        final String userIp = getValue(data, Common.UserIP);
        commonOperation.fillIpInfo(dataFact, userIp);//做到并发里面去
    }

    @Override
    public void writeData(DataFact dataFact,final String reqId, boolean isWrite, boolean isCheck)
    {
        final Map ipInfo = dataFact.ipInfo;
        try{
            if(isCheck)
            {
                Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_ipinfo where reqid=?",reqId);
                Set<Map.Entry> entries = oldMainInfo.entrySet();
                for(Map.Entry<String,Object> entry : entries)
                {
                    if(!entry.getValue().equals(getValue(ipInfo,entry.getKey())))
                    {
                        logger.info("ipInfo信息比对结果    "+entry.getKey()+": "+"老系统的值:"+entry.getValue()+"\t"+"新系统的值:"+getValue(ipInfo,entry.getKey()));
                    }
                }
            }
        }catch (Exception exp)
        {
            logger.warn("insertIpInfo比对数据的时候出现异常"+exp.getMessage());
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
}
