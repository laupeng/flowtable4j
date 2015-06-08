package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Map;

import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;

/**
 * Created by lpxie on 15-5-8.
 */
//@Repository
public class TieYouSources
{
    private static Logger logger = LoggerFactory.getLogger(TieYouSources.class);

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

    public Map getExRailUserInfo(String exRailId)
    {
        Map exRailUserInfo = null;
        try
        {
            String sqlCommand = "SELECT Top 1 *" + " FROM InfoSecurity_ExRailUserInfo with (nolock) WHERE [InfoSecurity_ExRailUserInfo].[ExRailInfoID] = ?";
            exRailUserInfo = cardRiskDBTemplate.queryForMap(sqlCommand,exRailId);
        }catch(Exception exp)
        {
            logger.warn("从sql查询高铁信息异常:"+exp.getMessage());
        }
        return exRailUserInfo;
    }


}
