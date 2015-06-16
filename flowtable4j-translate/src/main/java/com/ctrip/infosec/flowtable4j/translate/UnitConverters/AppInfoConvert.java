package com.ctrip.infosec.flowtable4j.translate.UnitConverters;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import com.ctrip.infosec.flowtable4j.translate.model.DataFact;
import com.ctrip.infosec.flowtable4j.translate.service.CommonOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.Map;

import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;

/**
 * Created by lpxie on 15-6-10.
 */
public class AppInfoConvert implements Convert
{
    private static Logger logger = LoggerFactory.getLogger(AppInfoConvert.class);

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
        String checkType = getValue(data, Common.CheckType);
        if(checkType.equals("0") || checkType.equals("1"))
        {
            dataFact.appInfo.put("ClientID",getValue(data,"ClientID"));
            dataFact.appInfo.put("ClientVersion",getValue(data,"ClientVersion"));
            dataFact.appInfo.put("Latitude",getValue(data,"Latitude"));
            dataFact.appInfo.put("Longitude",getValue(data,"Longitude"));
        }else if(checkType.equals("2"))
        {
            final String reqIdStr = getValue(data,Common.OldReqID);
            commonOperation.fillProductAppInfo(dataFact, reqIdStr);
        }

    }

    @Override
    public void writeData(DataFact dataFact, String reqId, boolean isWrite, boolean isCheck)
    {

    }
}
