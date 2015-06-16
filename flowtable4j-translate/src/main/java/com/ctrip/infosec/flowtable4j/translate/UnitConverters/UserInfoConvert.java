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
public class UserInfoConvert implements Convert
{
    private static Logger logger = LoggerFactory.getLogger(UserInfoConvert.class);

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
            String uid = getValue(data, Common.Uid);
            commonOperation.fillUserInfo(dataFact,uid);
        }else if(checkType.equals("2"))
        {
            final String reqIdStr = getValue(data,Common.OldReqID);
            commonOperation.fillProductUser(dataFact, reqIdStr);
        }
    }

    @Override
    public void writeData(DataFact dataFact,final String reqId, boolean isWrite, boolean isCheck)
    {
        final Map userInfo = dataFact.userInfo;
        try{
            if(isCheck)
            {
                Map oldMainInfo = cardRiskDBTemplate.queryForMap("select top 1 * from infosecurity_contactinfo where reqid=?",reqId);
                Set<Map.Entry> entries = oldMainInfo.entrySet();
                for(Map.Entry<String,Object> entry : entries)
                {
                    if(!entry.getValue().equals(getValue(userInfo,entry.getKey())))
                    {
                        logger.info("userInfo信息比对结果    "+entry.getKey()+": "+"老系统的值:"+entry.getValue()+"\t"+"新系统的值:"+getValue(userInfo,entry.getKey()));
                    }
                }
            }
        }catch (Exception exp)
        {
            logger.warn("insertUserInfo比对数据的时候出现异常"+exp.getMessage());
        }

        if(!isWrite)
            return;//如果不写入就直接返回
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator()
               {
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
}
