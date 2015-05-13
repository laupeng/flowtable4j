package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lpxie on 15-4-20.
 */
//@Repository
public class HotelGroupSources
{
    private static Logger logger = LoggerFactory.getLogger(HotelGroupSources.class);

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


    //通过lastReqId获取酒店团购信息
    public Map getHotelGroupInfo(long reqId)
    {
        Map hotelGroupInfo = null;
        try{
            String commandText = "select * from InfoSecurity_HotelGroupInfo with (nolock) where [InfoSecurity_HotelGroupInfo].[ReqID] = " +
                    reqId;
            hotelGroupInfo = cardRiskDBTemplate.queryForMap(commandText);
            return hotelGroupInfo;
        }catch(Exception exp)
        {
            return hotelGroupInfo;
        }
    }
}
