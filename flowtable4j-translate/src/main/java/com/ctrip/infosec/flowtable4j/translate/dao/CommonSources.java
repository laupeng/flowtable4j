package com.ctrip.infosec.flowtable4j.translate.dao;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by lpxie on 15-5-6.
 */
public class CommonSources
{
    private static Logger logger = LoggerFactory.getLogger(CommonSources.class);

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

    /**
     * 通过手机号查询对应的城和市
     * @param mobilePhone 手机号
     * @return 返回手机号对应的城市信息
     */
    public Map getCityAndProv(String mobilePhone)
    {
        Map mobileInfo = null;
        try
        {
            String subMobileNum = mobilePhone.substring(0,7);
            String sqlCommand = "SELECT Top 1 *" + " FROM BaseData_MobilePhoneInfo with (nolock) WHERE MobileNumber = "+subMobileNum;
            mobileInfo = cardRiskDBTemplate.queryForMap(sqlCommand);
        }catch(Exception exp)
        {
            logger.warn("从sql查询手机号对应的城市信息异常:",exp);
        }
        return mobileInfo;//这里取出里面的是 CityName 和 ProvinceName 这两个字段
    }
}
