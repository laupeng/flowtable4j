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
        final Map<String,Object> mainInfo = new HashMap();
        mainInfo.put(Common.ReqID,"1005");
        //mainInfo.put(Common.OrderType,"1003");
        Object result = cardRiskDBTemplate.execute(new CallableStatementCreator() {
               public CallableStatement createCallableStatement(Connection con) throws SQLException
               {
                   String params = "";
                   for(int i=0;i<mainInfo.size()-1;i++)
                   {
                       params += "?,";
                   }
                   params += "?";
                   String storedProc = "{call sp3_InfoSecurity_MainInfo_i("+params+")}";// 调用的sqldbo.sp3_InfoSecurity_MainInfo_i
//                   String storedProc = "{call sp3_InfoSecurity_MainInfo_i(?,?)}";// 调用的sqldbo.sp3_InfoSecurity_MainInfo_i
                   CallableStatement cs = con.prepareCall(storedProc);
                   Set<Map.Entry<String, Object>> entries = mainInfo.entrySet();
                   for(Map.Entry<String, Object> entry  : entries)
                   {
                       cs.setString(entry.getKey(),entry.getValue().toString());
                   }
                   return cs;
               }
           }, new CallableStatementCallback() {
               public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException
               {
                   boolean isSuccess = cs.execute();
                   return isSuccess;
               }
           });
        System.out.print("结果："+result.toString());
    }
}
