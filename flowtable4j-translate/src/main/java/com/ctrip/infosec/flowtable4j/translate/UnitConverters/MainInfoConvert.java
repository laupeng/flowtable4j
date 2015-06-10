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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;

/**
 * Created by lpxie on 15-6-10.
 */
public class MainInfoConvert implements Convert
{
    private static Logger logger = LoggerFactory.getLogger(MainInfoConvert.class);

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
    public void completeData(DataFact dataFact,Map data)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dataFact.mainInfo.put(Common.CheckType,getValue(data,Common.CheckType));
        dataFact.mainInfo.put(Common.CorporationID,"");
        try{
            long nowTime = System.currentTimeMillis();
            dataFact.mainInfo.put(Common.CreateDate,format.format(new Date(nowTime)));
        }catch (Exception exp)
        {
            logger.warn("MainInfoConvert.completeData获取当前时间异常"+exp.getMessage());
        }
        dataFact.mainInfo.put(Common.LastCheck,"T");
        dataFact.mainInfo.put(Common.OrderID,getValue(data,Common.OrderID));//OrderId
        dataFact.mainInfo.put(Common.MerchantID,getValue(data,Common.MerchantID));
        dataFact.mainInfo.put(Common.SubOrderType,getValue(data,Common.SubOrderType));
        dataFact.mainInfo.put(Common.MerchantOrderID,getValue(data,Common.MerchantOrderID));
        dataFact.mainInfo.put(Common.ClientID,getValue(data,Common.ClientID));
        dataFact.mainInfo.put(Common.Amount,getValue(data,"OrderAmount"));
        String subOrderType = getValue(data, Common.SubOrderType);
        if(subOrderType.isEmpty())
            subOrderType = "0";
        dataFact.mainInfo.put(Common.SubOrderType,subOrderType);
        dataFact.mainInfo.put(Common.OrderDate,getValue(data,Common.OrderDate));
        dataFact.mainInfo.put(Common.IsOnline,getValue(data,Common.IsOnline));
        dataFact.mainInfo.put(Common.OrderType,getValue(data,Common.OrderType));
        dataFact.mainInfo.put(Common.Serverfrom,getValue(data,Common.Serverfrom));
        dataFact.mainInfo.put(Common.CorporationID,getValue(data,Common.CorporationID));
    }

    @Override
    public void writeData(DataFact dataFact,final String reqId,boolean isWrite,boolean isCheck)
    {
        final Map mainInfo = dataFact.mainInfo;
        //查询老系统的值 用于比对
        if(isCheck)
        {
            try
            {
                Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_maininfo where reqid=?",reqId);
                Set<Map.Entry> entries = oldMainInfo.entrySet();
                for(Map.Entry<String,Object> entry : entries)
                {
                    if(!entry.getValue().equals(getValue(mainInfo,entry.getKey())))
                    {
                        logger.info("mainInfo信息比对结果    "+entry.getKey()+": "+"老系统的值:"+entry.getValue()+"\t"+"新系统的值:"+getValue(mainInfo,entry.getKey()));
                    }
                }
            }catch (Exception exp)
            {
                logger.warn("insertMainInfo比对数据的时候出现异常"+exp.getMessage());
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
                    /*String PayExpiryDate = getValue(mainInfo,"PayExpiryDate");
                    if(PayExpiryDate.isEmpty())
                    PayExpiryDate = "NULL";*/
                   cs.setString("PayExpiryDate",null);
                   cs.setString("PreAuthorizedAmount",getValue(mainInfo,"PreAuthorizedAmount"));
                    /*String RiskCountrolDeadline = getValue(mainInfo,"RiskCountrolDeadline");
                    if(PayExpiryDate.isEmpty())
                    RiskCountrolDeadline = "NULL";*/
                   cs.setString("RiskCountrolDeadline",null);
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
}
