package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by lpxie on 15-6-9.
 */
public class VacationSources
{
    private static Logger logger = LoggerFactory.getLogger(VacationSources.class);

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

    public Map getVacationOrderInfo(String reqId)
    {
        Map vacationOrderInfo = null;
        try
        {
            String sqlCommand = "SELECT Top 1 *" + " FROM InfoSecurity_VacationInfo with (nolock) where ReqID= ?";
            vacationOrderInfo = cardRiskDBTemplate.queryForMap(sqlCommand,reqId);
        }catch(Exception exp)
        {
            logger.warn("从sql查询度假订单Order信息异常:"+exp.getMessage());
        }
        return vacationOrderInfo;
    }

    public List<Map<String,Object>> getVacationOptionInfoList(String vacationInfoID)
    {
        List<Map<String,Object>> vacationOptionInfoList = null;
        try
        {
            String sqlCommand = "SELECT Top 1 *" + " FROM InfoSecurity_VacationOptionInfo with (nolock) where VacationInfoID= ?";
            vacationOptionInfoList = cardRiskDBTemplate.queryForList(sqlCommand, vacationInfoID);
        }catch(Exception exp)
        {
            logger.warn("从sql查询度假订单Option信息异常:"+exp.getMessage());
        }
        return vacationOptionInfoList;
    }

    public List<Map<String,Object>> getVacationUserInfoList(String vacationInfoID)
    {
        List<Map<String,Object>> vacationUserInfoList = null;
        try
        {
            String sqlCommand = "SELECT Top 1 *" + " FROM InfoSecurity_VacationUserInfo with (nolock) where VacationInfoID= ?";
            vacationUserInfoList = cardRiskDBTemplate.queryForList(sqlCommand, vacationInfoID);
        }catch(Exception exp)
        {
            logger.warn("从sql查询度假订单User信息异常:"+exp.getMessage());
        }
        return vacationUserInfoList;
    }

    public Map<String,Object> getMiceInfo(String reqId)
    {
        Map miceInfo = null;
        try
        {
            String sqlCommand = "SELECT Top 1 *" + " FROM InfoSecurity_MiceInfo with (nolock) where ReqID= ?";
            miceInfo = cardRiskDBTemplate.queryForMap(sqlCommand,reqId);
        }catch(Exception exp)
        {
            logger.warn("从sql查询度假订单Mice信息异常:"+exp.getMessage());
        }
        return miceInfo;
    }
}
