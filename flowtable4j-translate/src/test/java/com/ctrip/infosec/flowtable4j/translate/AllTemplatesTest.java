package com.ctrip.infosec.flowtable4j.translate;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Map;

/**
* Created by lpxie on 15-4-29.
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/allTemplates.xml"})
public class AllTemplatesTest
{
    @Resource(name="allTemplates")
    private AllTemplates allTemplates;
    @Test
    public void testAllTem()
    {
        JdbcTemplate cardRiskDBTemplate = allTemplates.getCardRiskDBTemplate();
        JdbcTemplate riskCtrlPreProcDBTemplate = allTemplates.getRiskCtrlPreProcDBTemplate();
        String mobilePhone = "13917863756";//13482188219
        String subMobileNum = mobilePhone.substring(0,7);
        String sqlCommand = "SELECT Top 1 *" + " FROM CardRiskDB..BaseData_MobilePhoneInfo with (nolock) WHERE MobileNumber = ?";
        Map mobileInfo = cardRiskDBTemplate.queryForMap(sqlCommand,subMobileNum);
        Assert.assertNotNull(mobileInfo);
    }
}
