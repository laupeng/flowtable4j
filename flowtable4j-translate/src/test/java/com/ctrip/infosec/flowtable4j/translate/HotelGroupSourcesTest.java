package com.ctrip.infosec.flowtable4j.translate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by lpxie on 15-4-24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/preprocess-datasource-test.xml"})
public class HotelGroupSourcesTest
{
    @Resource(name="CardRiskDB")
    private JdbcTemplate cardRiskDBTemplate;

    @Resource(name = "RiskCtrlPreProcDB")
    private JdbcTemplate riskCtrlPreProcDBTemplate;

    @Resource(name="FltProductDB")
    private JdbcTemplate fltProductDBDataSource;

    /*@Resource(name="riskCtrlPreProcDBNamedTemplate")
    private JdbcTemplate riskCtrlPreProcDBNamedTemplate*/

    @Test
    public void testGetDIDInfo()
    {
        Map DIDInfo = null;
        try{
            String sqlCommand = "SELECT TOP 10 * FROM CacheData_DeviceIDInfo";
            DIDInfo = riskCtrlPreProcDBTemplate.queryForMap(sqlCommand);
            Assert.assertNotNull(DIDInfo);
        }catch (Exception exp)
        {
            exp.printStackTrace();
            //log for warn
        }
    }
}
