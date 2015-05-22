package com.ctrip.infosec.flowtable4j.translate;

import com.ctrip.infosec.flowtable4j.translate.dao.Jndi.AllTemplates;
import com.ctrip.infosec.flowtable4j.translate.model.Common;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.ctrip.infosec.flowtable4j.translate.common.Utils.getValue;

/**
 * Created by lpxie on 15-5-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/allTemplates.xml"})
public class CommonWriteSourcesTest
{
    @Resource(name="allTemplates")
    private AllTemplates allTemplates;
    @Test
    public void testWrite()
    {
        JdbcTemplate cardRiskDBTemplate = allTemplates.getCardRiskDBTemplate();
        final Map<String,Object> ipInfo = new HashMap();
        ipInfo.put("CheckStatus","7788121");
        ipInfo.put("CheckNum","");
        ipInfo.put("ReferenceID",null);
        ipInfo.put("DataChange_LastTime","");
        final String  reqId = "888812";
        cardRiskDBTemplate.execute(new CallableStatementCreator() {
           public CallableStatement createCallableStatement(Connection con) throws SQLException
           {
               String params = "";
               for(int i=0;i<7-1;i++)
               {
                   params += "?,";
               }
               params += "?";
               String storedProc = "{call spA_InfoSecurity_IPInfo_i("+params+")}";//dbo.sp3_InfoSecurity_DealInfo_i
               CallableStatement cs = con.prepareCall(storedProc);
               cs.setString(Common.ReqID,reqId);
               cs.setString("UserIPValue","555");
               cs.setString("UserIPAdd","999911");//
               cs.setString("IPCountry","xieliuping de shuju");
               cs.setString("IPCity","1");
               cs.setString("Continent","");
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
